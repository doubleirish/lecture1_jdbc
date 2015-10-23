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
 *   1) start the standalone derby service
 *      cd DERBY_HOME\bin
 *      ./startNetworkServer
 *
 *   2) populate the database using sql squirrel and the attached all.sql script
 *
 *   3) Build this application as an exectuable jar
 *           mvn clean package -DskipTests spring-boot:repackage
 *
 *   4) run the application to connect to the remote server
 *      java -jar target/lecture1_jdbc-1.0-SNAPSHOT.jar
 *
 *   5) you should see user information printed out
 */
public class App7UsersSpring
{

  static final Logger log = LoggerFactory.getLogger(App7UsersSpring.class);

  public static void mainExample(String[] args) {
    log.info("Initializing Spring context.");

    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");

    log.info("Spring context initialized.");

    UserDao userDao = (UserDao) applicationContext.getBean("userDao");
    log.info("userDao."+userDao);

    List<User> users = userDao.findAll();
    users.forEach(System.out::println);  //java 8 lamda
  }
}
