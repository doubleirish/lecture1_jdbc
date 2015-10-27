package edu.uw.data.app;

import edu.uw.data.dao.UserDao;
import edu.uw.data.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Hello spring application context with spring Jdbc template!
 *   we can run this from the command line
 *   1)
 *   2)
 *   3) java -jar target/lecture1_jdbc-1.0-SNAPSHOT.jar
 */
public class App7UsersSpringEmbedded
{

  static final Logger log = LoggerFactory.getLogger(App7UsersSpringEmbedded.class);

  public static void main(String[] args) {
    log.info("Initializing Spring context, starting embedded Derby server.");

    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");

    log.info("Spring context initialized.");

    UserDao userDao = (UserDao) applicationContext.getBean("userDao");
    log.info("userDao."+userDao);

    List<User> users = userDao.findAll();
    for (User user : users) {
         System.out.println("User "+user);
      }
  }
}
