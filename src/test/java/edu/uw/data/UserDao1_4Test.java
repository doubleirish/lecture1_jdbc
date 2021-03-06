package edu.uw.data;


import edu.uw.data.dao.*;
import edu.uw.data.model.User;
import org.apache.derby.jdbc.ClientDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for our first 4 Dao findAll() implementations.
 * These tests require that a separate Derby database service is running on the same host.
 */
public class UserDao1_4Test {

  private static ClientDataSource remoteDerbyDataSource;

  @BeforeClass  // once per test class
    public static void setup() {
      remoteDerbyDataSource = new ClientDataSource();

      // load properties from a file
      Properties props = DataSourceHelper.loadJdbcProperties("jdbc.properties");
      String username = props.getProperty("jdbc.username");
      String password = props.getProperty("jdbc.password");
      String serverName = props.getProperty("server.name");
      String databaseName = props.getProperty("database.name");

      //push into data source
      remoteDerbyDataSource.setServerName(serverName);
      remoteDerbyDataSource.setDatabaseName(databaseName);   //location of derby database file.
      remoteDerbyDataSource.setUser(username);
      remoteDerbyDataSource.setPassword(password);
    }


  @Test
  public void userDao1Orig_findAll() {
    UserDao dao = new UserDao1Orig();
    findAll(dao);
  }

  @Test
  public void userDao2_TryWithResources_returnsUsers() {
    UserDao dao = new UserDao2Try();
    findAll(dao);
  }

  @Test
  public void userDao3_ExternalDataSource_returnsUsers() {
    UserDao dao = new UserDao3DS(remoteDerbyDataSource);
    findAll(dao);
  }

  @Test
  public void userDao4_returnsUsersAndAddresses() {
    UserDao dao = new UserDao4OneToOne(remoteDerbyDataSource);
    findAll(dao);
  }


  private void findAll(UserDao dao) {
    List<User> users = dao.findAll();
    assertNotNull(users);
    for (User user : users) {
         System.out.println("User "+user);
       }
    assertThat("was expecting at least one user found", users.size(), is(greaterThan(0)));
  }



}
