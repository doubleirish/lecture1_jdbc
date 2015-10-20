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

      //hand build the database connection url
      String url = "jdbc:derby://localhost:1527/"  // derby database service running on host and port
          + dbDir + "/" + databaseName // location of database file
          + ";create=true"             // create the database file if it doesn't exist.
          +";user="   +dbUser          // database username
          +";password="+dbPassword;    // database password

      log.info("dburl="+url);

      Connection connection = null;
      PreparedStatement ps = null;
      ResultSet rs = null;
      List<User> users = new ArrayList<>();
      try {
        connection = DriverManager.getConnection(url); // open a connection to the database

        ps = connection.prepareStatement("SELECT id, username,firstname" +
                " ,lastname, active_since FROM users");

        rs = ps.executeQuery();   // calling the database
        while (rs.next()) {        //retrieve a result row
          User user = new User();
          user.setId(rs.getInt(1));          // the result row's columns use one-based-index i.e first column in the result row,
          user.setUserName(rs.getString(2));
          user.setFirstName(rs.getString(3));
          user.setLastName(rs.getString(4));
          user.setActiveSince(rs.getDate(5));
          users.add(user);       // add user object to list
        }
        // eagerly free resources
        rs.close();
        ps.close();
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException(e);   //convert checked exception into unchecked
      } finally {
        try {
          if (rs != null && !rs.isClosed()) {
            rs.close();
          }
        } catch (SQLException ignored) {   // there's usually no recovery from sql exceptions, so they are ignored
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
