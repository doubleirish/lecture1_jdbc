package edu.uw.data.app;

import edu.uw.data.dao.UserDao;
import edu.uw.data.dao.UserDao1Orig;
import edu.uw.data.model.User;

import java.util.List;

/**
 * An example of how a user of the DAO might use it.
 * (see App7usersSpring for a
 */
public class App1UsersJdbc
{
  public static void main(String[] args) {
    UserDao dao = new UserDao1Orig();
    List<User> users = dao.findAll();
    for (User user : users) {
      System.out.println(user);
    }
  }
}
