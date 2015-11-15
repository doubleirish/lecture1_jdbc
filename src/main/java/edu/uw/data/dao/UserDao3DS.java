package edu.uw.data.dao;

import edu.uw.data.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**

 * JDBC: Dao with an injected datasource bean, (but not injected using spring)
 */
public class UserDao3DS extends AbstractUserDao implements UserDao {

  private DataSource dataSource = null;

  public UserDao3DS(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public List<User> findAll() {

    List<User> users = new ArrayList<>();
    try (
        //  TODO look ma, no passwords.
        Connection connection = dataSource.getConnection();

        PreparedStatement ps = connection.prepareStatement("SELECT id, user_name,first_name ,last_name, active_since FROM Users");
        ResultSet rs = ps.executeQuery()
    ) {
      while (rs.next()) {
        User user = new User();
        user.setId(rs.getInt(1));
        user.setUserName(rs.getString(2));
        user.setFirstName(rs.getString(3));
        user.setLastName(rs.getString(4));
        user.setActiveSince(rs.getDate(5));
        users.add(user);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e); //convert checked exception into unchecked
    }
    return users;
  }

}
