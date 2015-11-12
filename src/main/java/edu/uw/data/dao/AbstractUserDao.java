package edu.uw.data.dao;

import edu.uw.data.model.User;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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


    try (InputStream  is = AbstractUserDao.class.getClassLoader().getResourceAsStream(filename);
         BufferedReader in  = new BufferedReader(new InputStreamReader(is));
    ){

      //load the jdbc.properties reader
      PropertiesConfiguration config = new PropertiesConfiguration();
        config.load(in);

      // interpolate the variables
      config = (PropertiesConfiguration )config.interpolatedConfiguration();

      //get the property value and print it out


      properties = ConfigurationConverter.getProperties(config);


    } catch (Exception ex) {
      ex.printStackTrace();
      throw new RuntimeException(ex) ;
    }
    return properties;
  }


}
