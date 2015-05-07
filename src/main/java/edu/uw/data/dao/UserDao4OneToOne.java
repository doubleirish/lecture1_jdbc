package edu.uw.data.dao;

import edu.uw.data.model.Address;
import edu.uw.data.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC: building a more complex User-Address domain object in one pass
 */
public class UserDao4OneToOne extends AbstractUserDao  implements UserDao {

  private DataSource dataSource = null;

  public UserDao4OneToOne(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public List<User> findAll() {

    List<User> users = new ArrayList<>();
    String sql =
        "SELECT u.id,    u.username, u.firstname ,u.lastname, u.active_since " +
        "     , a.street, a.city ,   a.state ,    a.zip " +
        "FROM Users u " +
        "LEFT OUTER JOIN Address a on  u.address_Id = a.id " +
        "ORDER BY u.id ";
    try (
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()
    ) {
      while (rs.next()) {
        User user = new User();
        user.setId(rs.getInt(1));
        user.setUserName(rs.getString(2));
        user.setFirstName(rs.getString(3));
        user.setLastName(rs.getString(4));
        user.setActiveSince(rs.getDate(5));
        // user-address is a 1-to-1 relationship so easy to build.
        Address address = new Address.Builder()
            .street(rs.getString(6))
            .city(rs.getString(7))
            .state(rs.getString(8))
            .zip(rs.getString(9)).build();
        user.setAddress(address);
        users.add(user);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e); //convert checked exception into unchecked
    }
    return users;
  }


}
