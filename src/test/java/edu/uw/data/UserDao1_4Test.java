package edu.uw.data;


import edu.uw.data.dao.AbstractUserDao;
import edu.uw.data.dao.UserDao;
import edu.uw.data.dao.UserDao1Orig;
import edu.uw.data.dao.UserDao2Try;
import edu.uw.data.dao.UserDao3DS;
import edu.uw.data.dao.UserDao4OneToOne;
import edu.uw.data.model.User;
import org.apache.derby.jdbc.ClientDataSource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.   These tests require that a separate Derby database service is running on the same host.
 */
public class UserDao1_4Test {

  private ClientDataSource remoteDerbyDataSource;



  @Test
  public void userDao1Orig_findAll() {
    UserDao dao = new UserDao1Orig();
    List<User> users = dao.findAll();
    assertNotNull(users);
    assertThat("was expecting at least one user found", users.size(), is(greaterThan(0)));

  }


  @Test
  public void userDao2_TryWithResources_returnsUsers() {
    UserDao dao = new UserDao2Try();
    List<User> users = dao.findAll();
    assertNotNull(users);
    assertTrue(users.size() > 0);

  }


  @Before
  public void setup() {
    remoteDerbyDataSource = new ClientDataSource();

    // TODO load properties from a file

    Properties props = AbstractUserDao.loadJdbcProperties("jdbc.properties");
    String url = props.getProperty("jdbc.url");
    String username = props.getProperty("jdbc.username");
    String password = props.getProperty("jdbc.password");
    String serverName = props.getProperty("server.name");
    String databaseName = props.getProperty("database.name");

    remoteDerbyDataSource.setServerName(serverName);
    remoteDerbyDataSource.setDatabaseName(databaseName);   //location of derby database file.
    remoteDerbyDataSource.setUser(username);
    remoteDerbyDataSource.setPassword(password);
  }

  @Test
  public void userDao3_ExternalDataSource_returnsUsers() {
    UserDao dao = new UserDao3DS(remoteDerbyDataSource);
    List<User> users = dao.findAll();
    assertNotNull(users);
    assertTrue(users.size() > 0);
  }


  @Test
  public void userDao4_returnsUsersAndAddresses() {
    UserDao dao = new UserDao4OneToOne(remoteDerbyDataSource);
    List<User> users = dao.findAll();
    assertNotNull(users);
    assertTrue(users.size() > 0);
    for (User user : users) {
       System.out.println("User "+user);
    }
  }


}
