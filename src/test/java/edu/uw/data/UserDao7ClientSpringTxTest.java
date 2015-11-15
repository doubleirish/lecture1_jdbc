package edu.uw.data;


import edu.uw.config.DataSourceTestConfig;
import edu.uw.data.config.AppConfig;
import edu.uw.data.dao.UserDao;
import edu.uw.data.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)

//@Transactional(transactionManager = "txManager")
//@Rollback


@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
    classes = {
        AppConfig.class
        , DataSourceTestConfig.class,
        // DataSourceStandaloneConfig.class
    })

@ActiveProfiles("dev")
public class UserDao7ClientSpringTxTest extends AbstractTransactionalJUnit4SpringContextTests {

  static final Logger log = LoggerFactory.getLogger(UserDao7ClientSpringTxTest.class);

  @Resource
private UserDao userDao;


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
    String expectedUserName = "ksauce";
    user.setUserName(expectedUserName);
    user.setFirstName("Keyser");
    user.setLastName("Soze");
    user.setActiveSince(new Date());

    userDao.createUser(user);

    User foundUser =userDao.findUserByUsername(expectedUserName);
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
      assertEquals("Conor", user.getFirstName());


  }



  @Test
  public void updateConorRedmondUser()    {

    User user = new User();

    int jsmithId = 1;
    user.setId(jsmithId);
    user.setUserName("credmond2");
    user.setFirstName("Conor2");
    user.setLastName("Redmond");
    user.setActiveSince(new Date());
    log.info("user ob "+user);
     userDao.updateUser(user);

    User updatedUser =userDao.readUser(jsmithId);

    assertNotNull(updatedUser);
    assertEquals("Conor2",updatedUser.getFirstName());

  }


  @Test
  public void deleteUser()    {

    // 3 delete that user
    userDao.deleteUser(new User(1));

    // 4 verify it's gone
    User deletedUser = userDao.readUser(1);

    assertNull(deletedUser);



  }


  @Test
  public void findAll()    {
    log.info("userDao " + userDao);
    List<User> users = userDao.findAll();
    assertNotNull(users);
    assertTrue(users.size() > 0);
   // users.forEach(System.out::println);
    for (User user : users) {
         System.out.println("User "+user);
      }
  }


}
