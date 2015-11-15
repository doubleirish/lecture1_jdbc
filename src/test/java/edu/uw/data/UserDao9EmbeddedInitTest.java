package edu.uw.data;


import edu.uw.config.EmbeddedTestDataSourceInit;
import edu.uw.data.config.AppConfig;
import edu.uw.data.dao.UserDao;
import edu.uw.data.dao.UserDao6SpringJdbcTemplate;
import edu.uw.data.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Date;
import java.util.List;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

/**
 * this test implictly cleans out and sets up test data in the database using the <jdbc:embedded-database> datasource properties
 * This happens once per test class , not once per test method , so it is quite a bit faster.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
    classes = {
        AppConfig.class
        ,EmbeddedTestDataSourceInit.class
    })
@Transactional(transactionManager = "transactionManager")
@Rollback(false)   // don't really need to rollback as we reinitialize on test startup
@ActiveProfiles("dev")
public class UserDao9EmbeddedInitTest extends AbstractTransactionalJUnit4SpringContextTests {

  static final Logger log = LoggerFactory.getLogger(UserDao9EmbeddedInitTest.class);

  @Resource
private UserDao userDao;

  @Override
  @Resource(name = "dataSource")
  public void setDataSource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }


  @Test
  public void findUserByUsername()    {

    User credmond =userDao.findUserByUsername("credmond");
    log.info("foundUser "+credmond);
    assertNotNull(credmond);
    assertEquals(1L, credmond.getId().longValue());
  }

  @Test
  public void createUser()    {
    User user = new User();
    String expectedUserName = ("btest"+System.currentTimeMillis()).substring(0,15);
    user.setUserName(expectedUserName);
    user.setFirstName("Bob");
    user.setLastName("Test");
    user.setActiveSince(new Date());

    userDao.createUser(user);

    User foundUser =userDao.findUserByUsername(expectedUserName);  //TODO using an  method to test another method is a code smell
    log.info("foundUser "+foundUser);
    assertNotNull(foundUser);
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
  public void updateNameOfUser()    {
    int jsmithId = 2;

    String originalLastname = jdbcTemplate.queryForObject(
        "select LAST_NAME  from USERS  where ID = ?" ,String.class,jsmithId);

    //
    // check initial state
    //
    assertThat(originalLastname, is("Smith"));

    User user = new User.Builder().id(jsmithId)
        .firstName("John2").lastName("Smith2").build();
    //
    // update !
    //
     userDao.updateUser(user);

    String updatedUsername = jdbcTemplate.queryForObject( //TODO much less smelly
        "select LAST_NAME  from USERS  where ID = ?" ,String.class,jsmithId);

    //
    // verify results
    //

    assertThat(updatedUsername, is("Smith2"));
  }


  @Test
  public void deleteTempUser()    {

    //
    // 1. Create a user
    //
    String tempUsername = ("test"+System.currentTimeMillis()).substring(0,15);
    User user  = new User.Builder().userName(tempUsername).firstName("delete").lastName("me").build();
    userDao.createUser(user);

    //
    // 2. verify it was persisted
    //
    Integer tempUserId = jdbcTemplate.queryForObject(
        "select ID  from USERS  where USER_NAME = ?", Integer.class, tempUsername);
    assertThat(tempUserId, notNullValue());


    // 3. delete that user
    userDao.deleteUser(new User(tempUserId));

    // 4. verify it's gone
    User tempAfter = userDao.findUserByUsername("temp");
    assertNull(tempAfter);
  }


  @Test
  public void findAll()    {
    List<User> users = userDao.findAll();
    assertNotNull(users);
    assertTrue(users.size() > 0);
    for (User user : users) {
         System.out.println("User "+user);
      }
  }

  @Test
   public void findAll_using_ResultSetExtractor()    {
    // using a multi column join to build User object with populated Addess and Phone
     List<User> users = ((UserDao6SpringJdbcTemplate)userDao).findAll_using_ResultSetExtractor();
     assertNotNull(users);
     assertTrue(users.size() > 0);
    for (User user : users) {
         System.out.println("User "+user);
      }
      // users.forEach(System.out::println); //java 8 lamda
   }


}
