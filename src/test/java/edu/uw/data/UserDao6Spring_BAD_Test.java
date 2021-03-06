package edu.uw.data;


import edu.uw.data.dao.UserDao;
import edu.uw.data.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 *  TODO some of  tests will work once and then fail horribly. Why ?
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/userapp-spring.xml",
    "classpath:/datasource-client-test.xml"})

public class UserDao6Spring_BAD_Test extends AbstractJUnit4SpringContextTests {

  static final Logger log = LoggerFactory.getLogger(UserDao6Spring_BAD_Test.class);

  @Resource
private UserDao userDao;



  @Test
  public void createUser()    { // TODO Trying to create the same exact user twice would fail, hence the random username
    User user = new User();
    String expectedUserName = ("NEW" + System.currentTimeMillis()).substring(0,14);
    user.setUserName(expectedUserName);
    user.setFirstName("Bob");
    user.setLastName("Test");
    user.setActiveSince(new Date());

    userDao.createUser(user);

    User foundUser =userDao.findUserByUsername(expectedUserName);
    log.info("foundUser "+foundUser);
    assert foundUser !=null ;
    assertEquals(expectedUserName, foundUser.getUserName());


  }

  @Test
  public void readUser()    {

    User user =userDao.readUser(1);
    log.info("foundUser "+user);
    assertNotNull(user);
    assertNotNull(user.getId());


  }



  @Test
  public void updateJohnSmithUser()    {

    // TODO can anyone spot any problems here ?
    User user = new User();

    int jsmithId = 2;
    user.setId(jsmithId);
    user.setUserName("jsmith2");
    user.setFirstName("John2");
    user.setLastName("Smith2");
    user.setActiveSince(new Date());
    log.info("user ob "+user);
     userDao.updateUser(user);

    User updatedUser =userDao.readUser(jsmithId);

    assertNotNull(updatedUser);
    assertEquals("John2",updatedUser.getFirstName());

  }


  @Test
  public void deleteTempUser()    { //one implementation of repeatable mutable tests

    // 1 create a user
    //TODO FYI also usually not a good idea to test a method using another method for setup
    String userName = ("TEMP" + System.currentTimeMillis()).substring(0,14);  // random user name
       userDao.createUser(new User.Builder()
           .userName(userName)
           .firstName("tem")
           .lastName("porary")
           .build());

    // 2.a verify user created
    User tempUser = userDao.findUserByUsername(userName);
    assertEquals(userName, tempUser.getUserName());

    // 3 delete that user
    userDao.deleteUser(tempUser);

    // 4 verify it's gone
    User tempAfter = userDao.findUserByUsername(userName);
    assert tempAfter==null;
  }


  @Test
  public void findAll()    {
    log.info("userDao " + userDao);
    List<User> users = userDao.findAll();
    assertNotNull(users);
    assertTrue(users.size() > 0);
    for (User user : users) {
         System.out.println("User "+user);
      }
  }


  @Test
  public void findUserByUsername()    {

    User credmond =userDao.findUserByUsername("credmond");
    log.info("foundUser "+credmond);
    assertNotNull(credmond);
    assertEquals(1L, credmond.getId().longValue());
  }

  @Test
  public void countUsers()    {
    int countUsers = userDao.countUsers();
    assertThat(countUsers,greaterThan(0));
  }



}
