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
 *
 */
public class App7UsersSpring
{

  static final Logger log = LoggerFactory.getLogger(App7UsersSpring.class);

  public static void main(String[] args) {
    log.info("Initializing Spring context.");

    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");

    log.info("Spring context initialized.");

    UserDao userDao = (UserDao) applicationContext.getBean("userDao");
    log.info("userDao."+userDao);

    List<User> users = userDao.findAll();
    users.forEach(System.out::println);
  }
}
