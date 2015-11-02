package edu.uw.data.dao;

import edu.uw.data.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * convenience abstract implementation so I don't have to implement every interface method in the concrete sublclasses.
 */
public class AbstractUserDao implements UserDao {

  public void createUser(User user) {
    throw new UnsupportedOperationException();
  }

  public User readUser(Integer userId) {
    throw new UnsupportedOperationException();
  }

  public void updateUser(User user) {
    throw new UnsupportedOperationException();
  }

  public void deleteUser(User user) {
    throw new UnsupportedOperationException();
  }

  public User findUserByUsername(String username) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<User> findAll() {
    throw new UnsupportedOperationException();
  }

  @Override

  public int countUsers() {
    throw new UnsupportedOperationException();
  }


  public static Properties loadJdbcProperties(String filename) {
    Properties properties = new Properties();

    try (InputStream  input = AbstractUserDao.class.getClassLoader().getResourceAsStream(filename)){



      if (input == null) {
        System.out.println("Sorry, unable to find " + filename);
        return properties;
      }

      //load a properties file from class path, inside static method
      properties.load(input);

      //get the property value and print it out

      System.out.println(properties.getProperty("jdbc.url"));
      System.out.println(properties.getProperty("jdbc.username"));
      System.out.println(properties.getProperty("jdbc.password"));

    } catch (IOException ex) {
      ex.printStackTrace();
      throw new RuntimeException(ex) ;
    }
    return properties;
  }


}
