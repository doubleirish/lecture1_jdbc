package edu.uw.data;


import edu.uw.data.dao.UserDao;
import edu.uw.data.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * this test explictly cleans out and sets up test data in the database using the setup() call before calling each test method.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/userapp-spring.xml",
    "classpath:/datasource-embedded-test.xml"})
@Transactional(transactionManager = "txManager")
@Rollback
public class UserDao8Embedded_SLOW_Test extends AbstractTransactionalJUnit4SpringContextTests {

  static final Logger log = LoggerFactory.getLogger(UserDao8Embedded_SLOW_Test.class);

  @Resource
  private UserDao userDao;

  @Override
  @Resource(name = "dataSource")
  public void setDataSource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  @Before
  public void setup() throws Exception {
    boolean continueOnErrorTrue = true;
    boolean continueOnErrorFalse = false;
    executeSqlScript("classpath:user_1drop.sql", continueOnErrorTrue);
    executeSqlScript("classpath:user_2create.sql", continueOnErrorFalse);
    executeSqlScript("classpath:user_3insert.sql", continueOnErrorFalse);
  }

  @After
  public void tearDown() throws Exception {

    //executeSqlScript("classpath:user_1drop.sql", false);
  }


  @Test
  public void findUserByUsername() {

    User credmond = userDao.findUserByUsername("credmond");
    log.info("foundUser " + credmond);
    assertNotNull(credmond);
    assertEquals(1L, credmond.getId().longValue());
  }

  @Test
  public void createUser() {
    User user = new User();
    String expectedUserName = "btest";
    user.setUserName(expectedUserName);
    user.setFirstName("Bob");
    user.setLastName("Test");
    user.setActiveSince(new Date());

    userDao.createUser(user);

    User foundUser = userDao.findUserByUsername(expectedUserName);
    log.info("foundUser " + foundUser);
    assertNotNull(foundUser);
    assertEquals(expectedUserName, foundUser.getUserName());


  }

  @Test
  public void readUser() {

    User user = userDao.readUser(1);
    log.info("foundUser " + user);
    assertNotNull(user);
    assertNotNull(user.getId());


  }


  @Test
  public void updateJohnSmithUser() {


    User user = new User();

    int jsmithId = 2;
    user.setId(jsmithId);
    user.setUserName("jsmith2");
    user.setFirstName("John2");
    user.setLastName("Smith2");
    user.setActiveSince(new Date());
    log.info("user ob " + user);
    userDao.updateUser(user);

    User updatedUser = userDao.readUser(jsmithId);

    assertNotNull(updatedUser);
    assertEquals("John2", updatedUser.getFirstName());

  }


  @Test
  public void deleteTempUser() {

    // 1 create a user
    userDao.createUser(new User.Builder().userName("temp").firstName("tem").lastName("porary").build());

    // 2 verify it's created
    User tempBefore = userDao.findUserByUsername("temp");
    assertEquals("temp", tempBefore.getUserName());

    // 3 delete that user
    userDao.deleteUser(tempBefore);

    // 4 verify it's gone
    User tempAfter = userDao.findUserByUsername("temp");

    assertNull(tempAfter);


  }


  @Test
  public void findAll() {
    log.info("userDao " + userDao);
    List<User> users = userDao.findAll();
    assertNotNull(users);
    assertTrue(users.size() > 0);
    for (User user : users) {
      System.out.println("User " + user);
    }
  }


}
