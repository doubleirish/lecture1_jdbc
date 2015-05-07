package edu.uw.data.dao;

import edu.uw.data.model.Address;
import edu.uw.data.model.User;
import org.apache.commons.lang3.StringUtils;
import edu.uw.data.model.Phone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * JDBC CRUD with some attempts at building a populated USER-ADDRESS-PHONE domain object graph
 */
public class UserDao5JdbcCrud extends AbstractUserDao implements UserDao {
  static final Logger log = LoggerFactory.getLogger(UserDao5JdbcCrud.class);
  private DataSource dataSource = null;

  public UserDao5JdbcCrud(DataSource dataSource) {
    this.dataSource = dataSource;
  }




  public void createUser (User user) {
    String sqlAddr  = "INSERT INTO ADDRESS(STREET, CITY, STATE, ZIP) VALUES (?,?,?,?)";
    String sqlUsers = "INSERT INTO USERS(USERNAME, FIRSTNAME, LASTNAME, ACTIVE_SINCE,ADDRESS_ID) VALUES (?,?,?,?,?)";
    String sqlPhone = "INSERT INTO PHONE(USER_ID, LABEL, PHONE) VALUES (?,?,?)";

    try (
        Connection connection = dataSource.getConnection();
        PreparedStatement psAddr = connection.prepareStatement(sqlAddr);
        PreparedStatement psUser = connection.prepareStatement(sqlUsers);
        PreparedStatement psPhone = connection.prepareStatement(sqlPhone)
    ) {

//
      try {


        //
        //create address if needed
        //
        Address address = user.getAddress();
        if (address != null && address.getStreet() != null) {


          psAddr.setString(1, address.getStreet());
          psAddr.setString(2, address.getCity());
          psAddr.setString(3, address.getState());
          psAddr.setString(4, address.getZip());
          int rowsAffected =  psAddr.executeUpdate();

          //find Id of newly created address using generated key if possible
          if (rowsAffected == 0) {
            throw new SQLException("unable to insert address " + address);
          }
          try (ResultSet generatedKeys = psAddr.getGeneratedKeys()) {
            if (generatedKeys != null && generatedKeys.next()) {
              address.setId(generatedKeys.getInt(1));
            } else {
              log.warn("Creating address failed, no ID obtained." + user);
            }
          }

        }



        //create user
        psUser.setString(1, user.getUserName());
        psUser.setString(2, user.getFirstName());
        psUser.setString(3, user.getLastName());
        psUser.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));

        psUser.setInt(5, (address!=null)?address.getId():null);

        int rowsAffected = psUser.executeUpdate();

        //find Id of newly created user using generated key if possible
        if (rowsAffected == 0) {
          throw new SQLException("unable to insert user " + user);
        }
        try (ResultSet generatedKeys = psUser.getGeneratedKeys()) {
          if (generatedKeys != null && generatedKeys.next()) {
            user.setId(generatedKeys.getInt(1));
          } else {
            log.warn("Creating user failed, no ID obtained." + user);
          }
        }

        // find id of new user using search
        if (user.getId() == null) {
          user.setId(findUserIdByUsername(user.getUserName()));
        }





        //
        // create phones if needed
        //
        Set<Phone> phones = user.getPhoneNumbers();
        for (Phone phone : phones) {

          psPhone.setInt(1, user.getId());
          psPhone.setString(2, phone.getLabel());
          psPhone.setString(3, phone.getNumber());
          psPhone.executeUpdate();
        }
      } catch (SQLException sqlEx) {
        connection.rollback();
        connection.setAutoCommit(true);
        throw sqlEx;
      }
    } catch (SQLException e) {
      e.printStackTrace();

      throw new RuntimeException(e); //convert checked exception into unchecked
    }
  }

  public User readUser(Integer userId) {
    User user =null;
    String sqlUserAddress =
        "SELECT u.id, u.username, u.firstname ,u.lastname, u.active_since \n" +
            "       , a.street, a.city , a.state , a.zip \n" +
            "FROM Users u \n" +
            "LEFT OUTER JOIN Address a on  u.address_Id = a.id \n" +
            "WHERE u.id =? "+
            "ORDER BY u.id ";

    String sqlPhone =
        "SELECT   p.phone, p.label " +
            "FROM   phone p  " +
            "where p.user_id =?  " +
            "ORDER BY p.label ";

    try (
        Connection connection = dataSource.getConnection();
        PreparedStatement psUser = connection.prepareStatement(sqlUserAddress);
        PreparedStatement psPhone = connection.prepareStatement(sqlPhone)

    ) {

      psUser.setInt(1, userId);
      ResultSet rsUserAddr = psUser.executeQuery();


      while (rsUserAddr.next()) { //should be just one
        //build user
        System.out.println(rsUserAddr);
          user = new User();
        user.setId(rsUserAddr.getInt(1));
        user.setUserName(rsUserAddr.getString(2));
        user.setFirstName(rsUserAddr.getString(3));
        user.setLastName(rsUserAddr.getString(4));
        user.setActiveSince(rsUserAddr.getDate(5));

        // build single address
        String street = rsUserAddr.getString(6);
        if (StringUtils.isNotBlank(street)) {
          Address address = new Address.Builder()
              .street(street)
              .city(rsUserAddr.getString(7))
              .state(rsUserAddr.getString(8))
              .zip(rsUserAddr.getString(9))
              .build();
          user.setAddress(address);
        }

        // build multiple phones
        psPhone.setInt(1, userId);
        ResultSet rsPhone = psPhone.executeQuery();
          while (rsPhone.next()) {
            // build phone
            String phoneStr = rsPhone.getString(1);

              String label = rsPhone.getString(2);
              Phone phone = new Phone.Builder()
                  .label(label)
                  .number(phoneStr)
                  .build();
              user.addPhoneNumber(phone);

          }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e); //convert checked exception into unchecked
    }
    return user;
  }

  public void updateUser(User user) {
    String sql = "UPDATE USERS SET FIRSTNAME = ? , LASTNAME=? where ID=?";
    try (
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)
    ) {

    ps.setString(1, user.getFirstName());
    ps.setString(2, user.getLastName());
    ps.setInt(3, user.getId());

    // execute SQL statement
      int i = ps.executeUpdate();
      System.out.println("updated "+i+" users for update to userId "+user.getId());

    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e); //convert checked exception into unchecked
    }
  }

  public void deleteUser(Integer userId) {

    String sql = "DELETE from users  where ID=?"; //TODO this could blow up for two reasons ,why?
    try (
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)
    ) {
      ps.setInt(1, userId);

      // execute SQL statement
      int i = ps.executeUpdate();
      System.out.println("deleted "+i+" users for update to userId "+userId);

    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e); //convert checked exception into unchecked
    }
  }



  public User findUserByUsername(String username) {
    User user=null;
    String sql = "select id,username,firstname,lastname,active_since from USERS where username= ?"  ;

    try ( Connection connection = dataSource.getConnection();
          PreparedStatement ps = connection.prepareStatement(sql)
    ) {
      ps.setString(1, username);

      // execute SQL statement

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
          user = new User();
        user.setId(rs.getInt("ID"));
        user.setUserName(rs.getString("username"));
        user.setFirstName(rs.getString("firstname"));
        user.setLastName(rs.getString("lastname"));
        user.setActiveSince(rs.getDate("active_since"));
      }

    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e); //convert checked exception into unchecked
    }
    return user;
  }


  public Integer findUserIdByUsername(String username) {
    Integer userId=null;
    User user = findUserByUsername(username);
    if (user !=null ){
      userId=user.getId();
    }

    return userId;
  }

  public List<User> findAll() {

    List<User> users = new ArrayList<>();
    String sql =
        "SELECT u.id, u.username, u.firstname ,u.lastname, u.active_since \n" +
            "       , a.street, a.city , a.state , a.zip \n" +
            "       , p.phone, p.label \n" +
            "FROM Users u \n" +
            "LEFT OUTER JOIN Address a on  u.address_Id = a.id \n" +
            "LEFT OUTER JOIN phone p   on  p.user_Id = u.id \n" +
            "ORDER BY u.id ";
    try (
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()
    ) {
      while (rs.next()) {
        System.out.println(rs);
        User user = new User();
        user.setId(rs.getInt(1));
        user.setUserName(rs.getString(2));
        user.setFirstName(rs.getString(3));
        user.setLastName(rs.getString(4));
        user.setActiveSince(rs.getDate(5));

        // build address
        String street = rs.getString(6);
        if (StringUtils.isNotBlank(street)) {
          Address address = new Address.Builder()
              .street(street)
              .city(rs.getString(7))
              .state(rs.getString(8))
              .zip(rs.getString(9))
              .build();
          user.setAddress(address);
        }


        // build phone
        String phoneStr = rs.getString(10);
        if (StringUtils.isNotBlank(phoneStr)) {
          String label = rs.getString(11);
          Phone phone = new Phone.Builder()
              .label(label)
              .number(phoneStr)
              .build();
          user.addPhoneNumber(phone);
        }
        // TODO !! multiple 'credmond' users with different phone info.
        // TODO how to consolidate ???
        users.add(user);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e); //convert checked exception into unchecked
    }
    return users;
  }


  public int countUsers( ) {
    try ( Connection connection = dataSource.getConnection();
          PreparedStatement ps = connection.prepareStatement("select count(*)   from USERS ");
          ResultSet rs = ps.executeQuery()
    ) {
      rs.next();
      return rs.getInt(1);

    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e); //convert checked exception into unchecked
    }
  }


  public void createUserNoTxWithDefaultAutoCommit(User user) {
    String sqlUsers = "INSERT INTO USERS(USERNAME, FIRSTNAME, LASTNAME, ACTIVE_SINCE) VALUES (?,?,?,?)";
    String sqlAddr  = "INSERT INTO ADDRESS(USER_ID, STREET, CITY, STATE, ZIP) VALUES (?,?,?,?,?)";
    String sqlPhone = "INSERT INTO PHONE(USER_ID, LABEL, PHONE) VALUES (?,?,?)";

    try (
        Connection connection = dataSource.getConnection();
        PreparedStatement psUser = connection.prepareStatement(sqlUsers);
        PreparedStatement psAddr = connection.prepareStatement(sqlAddr);
        PreparedStatement psPhone = connection.prepareStatement(sqlPhone)
    ) {

      connection.setAutoCommit(false); // TODO begin transaction
      //
      //create address if needed
      //
      Address address = user.getAddress();
      if (address!=null && address.getStreet()!=null) {

        psAddr.setString(1,address.getStreet());
        psAddr.setString(2,address.getCity());
        psAddr.setString(3,address.getState());
        psAddr.setString(4,address.getZip());

        psAddr.executeUpdate();

        // TODO  find the ID of the address just added
      }



      //create user
      psUser.setString(1, user.getUserName());
      psUser.setString(2, user.getFirstName());
      psUser.setString(3, user.getLastName());
      psUser.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
      // TODO add address
      int rowsAffected =psUser.executeUpdate();

      //find Id of newly created user using generated key if possible
      if (rowsAffected == 0) {
        throw new SQLException("unable to insert user "+user);
      }
      try (ResultSet generatedKeys = psUser.getGeneratedKeys()) {
        if (generatedKeys !=null && generatedKeys.next()) {
          user.setId(generatedKeys.getInt(1));
        }
        else {
          log.warn("Creating user failed, no ID obtained." + user);
        }
      }

      if (user.getId() ==null) {   // couldn't find user id using GeneratedKeys()  ?
        // so fallback to find id using old fashioned search on unique username
        user.setId(findUserIdByUsername(user.getUserName()));
      }



      //
      // create phones if needed
      //
      Set<Phone> phones = user.getPhoneNumbers();
      for (Phone phone : phones) {

        psPhone.setInt(1, user.getId());
        psPhone.setString(2,phone.getLabel());
        psPhone.setString(3,phone.getNumber());
        psPhone.executeUpdate();
      }

      connection.commit(); //TODO commit
      connection.setAutoCommit(true);
    } catch (SQLException e) {
      e.printStackTrace();

      throw new RuntimeException(e); //convert checked exception into unchecked
    }
  }
}
