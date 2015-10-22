package edu.uw.data;


import edu.uw.data.dao.*;
import edu.uw.data.model.Address;
import edu.uw.data.model.Phone;
import edu.uw.data.model.User;
import org.apache.derby.jdbc.ClientDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This Test illustrates the problems you may experience testing with a stateful resource.
 */
public class UserDao5JdbcCrud_BAD_Test {

  private static ClientDataSource remoteDerbyDataSource;

  @BeforeClass
  public static void setup () {
    remoteDerbyDataSource = new ClientDataSource();
    remoteDerbyDataSource.setServerName("localhost");
    remoteDerbyDataSource.setDatabaseName("C:/derbydata/lecture1");
    remoteDerbyDataSource.setUser("app");
    remoteDerbyDataSource.setPassword("app");
  }



  @Test
  public void createUser()    { //TODO  This works the first time I run it, but ...
    UserDao dao = new UserDao5JdbcCrud(remoteDerbyDataSource);
    //TODO don't use this , use transactional or embedded db, coming soon

    String userName = ("NEW" + System.currentTimeMillis()).substring(3);
    User user = new User.Builder()
        .userName(userName)
        .firstName("new")
        .lastName("usertest")
        .address(new Address.Builder()
                .street("1234 main st")
                .city("redmond")
                .state("WA")
                .zip("98042")
                .build()
        )
        .phone(new Phone.Builder()
            .label("HOME")
            .number("123-456-7890").build())
        .phone(new Phone.Builder()
            .label("WORK")
            .number("425-555-1212").build())
        .build();
    dao.createUser(user);
    User newUser = dao.findUserByUsername(userName);
    assertNotNull(newUser);
  }

  @Test
  public void  readUser()    {
    UserDao dao = new UserDao5JdbcCrud(remoteDerbyDataSource);
    User user = dao.readUser(1);
    assertNotNull(user);
    System.out.println(user);


  }

  @Test
  public void  updateUser()    {
    UserDao dao = new UserDao5JdbcCrud(remoteDerbyDataSource);
      dao.updateUser(new User.Builder().id(3).userName("updated").firstName("up").lastName("dated").build());
  }

  @Test
  public void deleteUser()    {
    //UserDao dao = new UserDao5JdbcCrud(remoteDerbyDataSource);
    // TODO dao.deletePhonesForUser(132);
    // TODO dao.deleteAddressForUser(132);
    // TODO dao.deleteUser(132);
  }


  @Test
  public void findAll()    {
    UserDao dao = new UserDao5JdbcCrud(remoteDerbyDataSource);
    List<User> users = dao.findAll();
    assertNotNull(users);
    assertTrue(users.size() >0);
    users.forEach(System.out::println);

  }

  @Test
  public void findUserByUsername()    {
    UserDao dao = new UserDao5JdbcCrud(remoteDerbyDataSource);
    String expectedUserName = "credmond";
    User user = dao.findUserByUsername(expectedUserName);
    assertNotNull(user);
    assertThat(user.getUserName(),is(expectedUserName));
    assertThat(user.getFirstName(),is("Conor"));


  }

  @Test
  public void countUsers()    {
    UserDao dao = new UserDao5JdbcCrud(remoteDerbyDataSource);
    int countUsers = dao.countUsers();
    assertThat(countUsers,greaterThan(0));
  }

}
