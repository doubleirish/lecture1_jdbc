package edu.uw.data;


import edu.uw.data.dao.*;
import org.apache.derby.jdbc.ClientDataSource;
import org.junit.Before;
import org.junit.Test;
import edu.uw.data.model.User;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class UserDao1_4Test {

  private ClientDataSource remoteDerbyDataSource;

  @Before
  public void setup () {
      remoteDerbyDataSource = new ClientDataSource();
    remoteDerbyDataSource.setServerName("localhost");
    remoteDerbyDataSource.setDatabaseName("c:/derbydata/lecture1");
    remoteDerbyDataSource.setUser("app");
    remoteDerbyDataSource.setPassword("app");
  }

    @Test
    public void userDaoOrig_findAll()    {

      UserDao dao = new UserDao1Orig();
      List<User> users = dao.findAll();
      assertNotNull(users);
      assertThat("users",users.size(),is(greaterThan(0)));

    }


  @Test
  public void userDao2_TryWithResources_returnsUsers()    {


    UserDao dao = new UserDao2Try();
    List<User> users = dao.findAll();
    assertNotNull(users);
    assertTrue(users.size() >0);

  }



  @Test
  public void userDao3_ExternalDataSource_returnsUsers()    {



    UserDao dao = new UserDao3DS(remoteDerbyDataSource);
    List<User> users = dao.findAll();
    assertNotNull(users);
    assertTrue(users.size() >0);

  }


  @Test
  public void userDao4_returnsUsersAndAddresses()    {
    UserDao dao = new UserDao4OneToOne(remoteDerbyDataSource);
    List<User> users = dao.findAll();
    assertNotNull(users);
    assertTrue(users.size() >0);
    users.forEach(System.out::println);

  }







}
