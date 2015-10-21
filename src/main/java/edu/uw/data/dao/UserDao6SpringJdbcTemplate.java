package edu.uw.data.dao;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import edu.uw.data.model.Address;
import edu.uw.data.model.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import edu.uw.data.model.Phone;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * Spring JdbcTemplates eliminate low level boilerplate like Connections and prepareStaments,
 * JdbcTemplates also assist with avoiding the explict handing of checked exceptions
 *
 * the use of Spring Jdbc templates is a significant step up from raw JDBC.
 *
 * Modern Production code would tend to favour the use of Hibernate or Mybatis,
 * but JdbcTemplates are still used quite often in Test data setups in tests
 */
public class UserDao6SpringJdbcTemplate  extends AbstractUserDao implements UserDao {

  static final Logger log = LoggerFactory.getLogger(UserDao6SpringJdbcTemplate.class);

  private DataSource dataSource = null;
  private JdbcTemplate jdbcTemplate;
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate ;


  public UserDao6SpringJdbcTemplate() {
  }

  // when we inject the datasource we can setup the jdbcTemplates.
  // we would typically only need one template we're setting up both variants just for illustrative purposes.
  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
    this.jdbcTemplate = new JdbcTemplate(dataSource);
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

  }


  public List<User> listUsers_withOneOffMapping() {

    List<User> users = new ArrayList<>();
    String sql =
        "SELECT u.id, u.username, u.firstname ,u.lastname, u.active_since FROM Users u ORDER BY u.id ";

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    for (Map row : rows) {
      log.debug("ROW: "+row);

      User user = new User();
      user.setId(Integer.parseInt(String.valueOf(row.get("ID"))));
      user.setLastName((String)  row.get("LASTNAME"));  // mapping by column name instead of index is a lot less fragileand buggy
      user.setFirstName((String) row.get("FIRSTNAME"));
      user.setUserName((String)  row.get("USERNAME"));
      user.setActiveSince((Date) row.get("ACTIVE_SINCE"));

      users.add(user);
    }

    return users;
  }


  /**
   * if we have multiple queries e.g findUserByLastname() or findUserByActiveSince()
   * we may find the the mapping from the row to the user is basically the same each time
   * we can factor out this common row-object mapping into a call where it can be easily reused.
   *
   * this this especially useful if new columns get added as it means updating just the RowMapper class
   * instead of multiple queries.
   *
   */
  public List<User> listUsers_WithReusableMapping () {
      String sql =
          "SELECT u.id, u.username, u.firstname ,u.lastname, u.active_since FROM Users u ORDER BY u.id ";
      List<User> users = jdbcTemplate.query(sql,new UserRowMapper());
      return users;
    }


   /* CRUD using Spring Jdbc Templates*/

  @Override
  public void createUser(User user) {
    // TODO no boilerplate setup
    String sql = "INSERT INTO USERS  " +
            "(USERNAME, firstname, lastname, ACTIVE_SINCE) " +
            "VALUES (?, ?, ?,?)";
    jdbcTemplate.update(sql, user.getUserName(),  // varargs method
        user.getFirstName(),
        user.getLastName(),
        user.getActiveSince());
    // TODO no boilerplate teardown or exception handling
  }

  @Override
  public User readUser(Integer id) {
    User user=null;
    List<User>   userList = jdbcTemplate.query("select * from USERS where id= ?",
        new Object[]{id},
        new UserRowMapper());
    if (!userList.isEmpty()) {
      user = userList.get(0);
    }
    return user;
  }

  @Override
  public void updateUser(User user) {
    String sql = "UPDATE USERS  set firstname = ?, lastname = ?  where id = ?";
    int countUpdatedRows = jdbcTemplate.update(sql,
        user.getFirstName(),
        user.getLastName(),
        user.getId());
    log.info("countUpdatedRows "+countUpdatedRows);
  }


  @Override
  public void deleteUser(User user) {
    Integer userId = user.getId();
    if (userId==null) {
      throw new IllegalArgumentException("Delete of user requires populated Id :"+user);
    }


    jdbcTemplate.update("delete from PHONE where USER_ID= ?",userId );
    jdbcTemplate.update("delete from USERS where ID= ?",userId );

    Integer addressId =null;
    try {
      addressId = jdbcTemplate.queryForObject("SELECT ADDRESS_ID FROM USERS WHERE ID= ?",
          Integer.class,
          userId);
    } catch (DataAccessException e) {

    }

    if (addressId !=null) {
      jdbcTemplate.update("delete from ADDRESS  where ID  = ?)", addressId);
    }
  }

  public List<User> listAll() {
    String sql = "select * from USERS";
    return  jdbcTemplate.query(sql, new UserRowMapper());
  }


  @Override
  public User findUserByUsername(String username) {
     User user=null;
    String sql = "select * from USERS where username= ?"  ;
    Object[] args = new Object[] {username };
    List<User>   userList = jdbcTemplate.query(sql, args,new UserRowMapper());
    if (userList.size()>0) {
      user= userList.get(0);
    }
    return user;
  }

  // this is an example of supplying named parameters to a SQL statement.
  // Using named Parameters is usually a good thing as you can reorder parameters
  // and add new ones without breaking things
  // the example below uses a static initializer to create an populate the map.
  public void updateUser_namedParameter(User user) {

    String sql = "UPDATE USERS set firstname = :first ,lastname = :last where id = :id";

    Map<String, Object > map = new HashMap<String, Object>(){{
      put("first",user.getFirstName());
      put("last",user.getLastName());
      put("id",user.getId());
    }};

    namedParameterJdbcTemplate.update(sql,map);
  }

  // building a map of named parameters can be tedious.
  // the following method shows how to use goolge guava ImmutableMap utility to simplify the map construction further,
  public void updateUser_namedParameterGuava(User user) {
    String sql = "UPDATE user set firstname = :first ,lastname = :last where id = :id";
    Map<String, Object> map = ImmutableMap.of("first", user.getFirstName(),
                                              "last", user.getLastName(),
                                               "id", user.getId());

    namedParameterJdbcTemplate.update(sql,map);
  }










  public List<User> findAll() {
    // remember our attempt to map one-to-many relationships using the UserDao5OneTomany class

    Map<Integer, User> userMap= new TreeMap<>();
    String sql =
        "SELECT u.id, u.username, u.firstname ,u.lastname, u.active_since " +
            "       , p.phone, p.label " +
            "       , a.street, a.city , a.state , a.zip " +
            " FROM Users u \n" +
            " LEFT OUTER JOIN Address a on  a.id      = u.address_id " +
            " LEFT OUTER JOIN phone   p on  p.user_Id = u.id " +
            " ORDER BY u.id ";



    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql); //TODO replace with ResultSetExtractor

    for (Map row : rows) {
      log.debug("ROW: " + row);


      int userId = Integer.parseInt(String.valueOf(row.get("ID")));

      User user ;
      if (!userMap.containsKey(userId)) {    // if user is new then create user
        user = new User();
        user.setId(userId);
        user.setLastName((String) row.get("LASTNAME"));  //
        user.setFirstName((String) row.get("FIRSTNAME"));
        user.setUserName((String) row.get("USERNAME"));
        user.setActiveSince((Date) row.get("ACTIVE_SINCE"));


        //build address
              String street =(String) row.get("street");
              if (StringUtils.isNotBlank(street)) {
                Address address = new Address.Builder()
                    .street(street)
                    .city((String)  row.get("CITY"))
                    .state((String) row.get("STATE"))
                    .zip((String)   row.get("ZIP"))
                    .build();
                user.setAddress(address);
              }
        userMap.put(user.getId(), user);  // add user to a hash map with ID index
        log.debug("add user" + userId);

      } else {    // if user already has been loaded the just lookup so we can add additional phone number
        log.debug("lookup user"+userId);
         user = userMap.get(userId);
      }


      // build phone
      String phoneStr = (String) row.get("PHONE");
      if (StringUtils.isNotBlank(phoneStr)) {
        String label = (String) row.get("LABEL");
        Phone phone = new Phone.Builder()
            .label(label)
            .number(phoneStr)
            .build();
        user.addPhoneNumber(phone);
      }


    }

    return Lists.newArrayList(userMap.values());
  }

  // if we have lots of queries that all map user results in the same wasy
  //then we can factor this row-to-object mapping logic into common code
  // that can be used in multiple places.
  public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNumber) throws SQLException {
      User u = new User();
      u.setId(rs.getInt("id"));
      u.setUserName(rs.getString("username"));
      u.setFirstName(rs.getString("firstname"));
      u.setLastName(rs.getString("lastname"));
      u.setActiveSince(rs.getDate("active_since"));
      return u;
    }
  }

  @Override
  public int countUsers() {
    return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
  }
}
