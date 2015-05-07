package edu.uw.data.dao;

import com.google.common.collect.ImmutableMap;
import edu.uw.data.model.Address;
import edu.uw.data.model.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import edu.uw.data.model.Phone;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * simple single-table Jdbc example with try-resources and datasource
 */
public class UserDao6SpringJdbcTemplate  extends AbstractUserDao implements UserDao {

  static final Logger log = LoggerFactory.getLogger(UserDao6SpringJdbcTemplate.class);

  private DataSource dataSource = null;
  private JdbcTemplate jdbcTemplate;

  public UserDao6SpringJdbcTemplate() {
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }



  @Override
  public void createUser(User user) {
    String sql = "INSERT INTO USERS  " +
            "(USERNAME, firstname, lastname, ACTIVE_SINCE) " +
            "VALUES (?, ?, ?,?)";
    jdbcTemplate.update(sql, user.getUserName(),  // varargs method
        user.getFirstName(),
        user.getLastName(),
        user.getActiveSince());
  }

  @Override
  public User readUser(Integer id) {
    User user = null;
    Object[] args = {id};

    String sql = "select * from USERS where id= ?" ;
    List<User>   userList = jdbcTemplate.query(sql,args, new UserRowMapper()); //queryForObject throw ex if not found
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

  public void updateUser_namedParameter(User user) {
    NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

    String sql = "UPDATE USERS set firstname = :first ,lastname = :last where id = :id";

    Map<String, Object > map = new HashMap<String, Object>(){{
      put("first",user.getFirstName());
      put("last",user.getLastName());
      put("id",user.getId());
    }};

    jdbcTemplate.update(sql,map);
  }

  public void updateUser_namedParameterGuava(User user) {
    NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

    String sql = "UPDATE user set firstname = :first ,lastname = :last where id = :id";
    Map<String, Object> map = ImmutableMap.of("first", user.getFirstName(),
                                               "last", user.getLastName(),
                                                 "id", user.getId());

    jdbcTemplate.update(sql,map);
  }






  public List<User> listUsers() {

    List<User> users = new ArrayList<>();
    String sql =
        "SELECT u.id, u.username, u.firstname ,u.lastname, u.active_since FROM Users u ORDER BY u.id ";

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    for (Map row : rows) {
      log.debug("ROW: "+row);

      User user = new User();
      user.setId(Integer.parseInt(String.valueOf(row.get("ID"))));
      user.setLastName((String)  row.get("LASTNAME"));  //
      user.setFirstName((String) row.get("FIRSTNAME"));
      user.setUserName((String)  row.get("USERNAME"));
      user.setActiveSince((Date) row.get("ACTIVE_SINCE"));

      users.add(user);
    }

    return users;
  }



  public List<User> findAll() {

    List<User> users = new ArrayList<>();
    String sql =
        "SELECT u.id, u.username, u.firstname ,u.lastname, u.active_since \n" +
            "       , p.phone, p.label \n" +
            "       , a.street, a.city , a.state , a.zip \n" +
            "FROM Users u \n" +
            "LEFT OUTER JOIN Address a on  a.id      = u.address_id \n" +
            "LEFT OUTER JOIN phone   p on  p.user_Id = u.id \n" +
            "ORDER BY u.id ";



    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql); //TODO replace with ResultSetExtractor

    for (Map row : rows) {
      log.debug("ROW: "+row);

      User user = new User();
      user.setId(Integer.parseInt(String.valueOf(row.get("ID"))));
      user.setLastName((String)  row.get("LASTNAME"));  //
      user.setFirstName((String) row.get("FIRSTNAME"));
      user.setUserName((String)  row.get("USERNAME"));
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

      users.add(user);
    }

    return users;
  }

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
