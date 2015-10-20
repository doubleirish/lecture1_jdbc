package edu.uw.data.dao;

import edu.uw.data.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC with improved try-with-resources (java 7), much less clean up this time .
 * Also has a twist with an all-in-one DB connection url
 * (not for production use, no passwords in production code ever)
 *
 *
 */
public class UserDao2Try extends AbstractUserDao  implements UserDao
{

  public UserDao2Try() {
  }

  public  List<User>  findAll()
    {
      List<User> users = new ArrayList<>();
      String url = "jdbc:derby://localhost:1527/c:/derbydata/lecture1;create=true;user=app;password=app";

      // Java 7 introduced try-with-resources which auto-closes classes implementing the AutoCloseable interface
      // such as  Connection, PreparedStatement and ResultSet
      try(
          Connection connection = DriverManager.getConnection(url);
          PreparedStatement  ps = connection.prepareStatement
                  ("SELECT id, username,firstname ,lastname, active_since FROM Users");

          ResultSet rs=ps.executeQuery()
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
