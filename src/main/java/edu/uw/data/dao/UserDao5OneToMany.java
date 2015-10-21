package edu.uw.data.dao;

import edu.uw.data.model.Address;
import edu.uw.data.model.Phone;
import edu.uw.data.model.User;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC is not great at loading one-to-many relationships e.g using USER and PHONE tables ,
 * populated a User Object where the User has one or more Phone objects attached.
 */
public class UserDao5OneToMany extends AbstractUserDao implements UserDao {

  private DataSource dataSource = null;

  public UserDao5OneToMany(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public List<User> findAll() {

    List<User> users = new ArrayList<>();
    String sql =
        "SELECT u.id, u.username, u.firstname ,u.lastname, u.active_since \n" +
            "       , a.street, a.city , a.state , a.zip \n" +
            "       , p.phone, p.label \n" +
            "FROM Users u \n" +
            "LEFT OUTER JOIN Address a on  a.user_Id = u.id \n" +
            "LEFT OUTER JOIN phone p   on  p.user_Id = u.id \n" +
            "ORDER BY u.id ";


   // This SQL will generate a query like the following
   // because the credmond user has two phones we have two rows

//    1,credmond,Conor,Redmond,2014-12-31,"9999 Belview Ave",Seattle,WA,98052,555-555-1212,CELL
//    1,credmond,Conor,Redmond,2014-12-31,"9999 Belview Ave",Seattle,WA,98052,123-555-6789,HOME
//    2,jsmith,John,Smith,2014-02-28,"123 Main St",Kirkland,WA,98034,NULL,NULL
//    3,pdiddy,Puffy,Combs,2014-07-04,NULL,NULL,NULL,NULL,NULL,NULL

    try (
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()
    ) {
      while (rs.next()) {


        // TODO FAILE! our simplistic  parser below would create two credmond users each with a single attached phone
        // TODO instead of what we want which is one user with two phones.

        // TODO how to consolidate ???
        // we could write some custom code to see if the user was already loaded and just add the phone to the existing user
        // rather than create a new user.

        // alternatively we could load the users first in one query and
        // then populate the phone informartion for those usersin a second sql query

        // in either case the point is this is a lot of work for the developer.

        System.out.println(rs);
        User user = new User.Builder()
            .id(rs.getInt(1))
            .userName(rs.getString(2))
            .firstName(rs.getString(3))
            .lastName(rs.getString(4))
            .activeSince(rs.getDate(5))
            .build();

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

        users.add(user);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e); //convert checked exception into unchecked
    }
    return users;
  }

}
