package edu.uw.data.dao;

import edu.uw.data.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC old,old school with embedded connection params and plaintext passwords . never use in production !!
 * notice all the painful boiler plate to clean up resources.
 *
 */
public class UserDao1Orig extends AbstractUserDao implements UserDao {

    static final Logger log = LoggerFactory.getLogger(UserDao1Orig.class);

    public  List<User>  findAll()
    {
      String dbDir = "c:/derbydata";
      String databaseName = "lecture1";
      String dbUser = "app";
      String dbPassword = "app";
      String url = "jdbc:derby://localhost:1527/"
              + dbDir + "/" + databaseName + ";create=true;user="
              +dbUser+";password="+dbPassword;
      log.info("dburl="+url);
      Connection connection = null;
      PreparedStatement ps = null;
      ResultSet rs = null;
      List<User> users = new ArrayList<>();
      try {
        connection = DriverManager.getConnection(url);

        ps = connection.prepareStatement("SELECT id, username,firstname" +
                " ,lastname, active_since FROM users");

        rs = ps.executeQuery();
        while (rs.next()) {
          User user = new User();
          user.setId(rs.getInt(1));
          user.setUserName(rs.getString(2));
          user.setFirstName(rs.getString(3));
          user.setLastName(rs.getString(4));
          user.setActiveSince(rs.getDate(5));
          users.add(user);
        }
        // eagerly free resources
        rs.close();
        ps.close();
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException(e); //convert checked exception into unchecked
      } finally {
        try {
          if (rs != null && !rs.isClosed()) {
            rs.close();
          }
        } catch (SQLException ignored) {
        }
        try {
          if (ps != null && !ps.isClosed()) {
            ps.close();
          }
        } catch (SQLException ignored) {
        }
        try {
          if (connection != null && !connection.isClosed()) {
            connection.close();
          }
        } catch (SQLException ignored) {
        }
      }
      return users;
    }

}
