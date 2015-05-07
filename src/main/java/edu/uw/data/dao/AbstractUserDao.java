package edu.uw.data.dao;

import edu.uw.data.model.User;

import java.util.List;

/**
 * Created by credmond on 17/03/15.
 */
public class AbstractUserDao implements UserDao {

  public void createUser(User user) {throw new UnsupportedOperationException();}
  public User readUser(Integer userId) {throw new UnsupportedOperationException();}
  public void updateUser(User user) {throw new UnsupportedOperationException();}
  public void deleteUser(User user) {throw new UnsupportedOperationException();}

  public User findUserByUsername(String username) {throw new UnsupportedOperationException();}

  @Override
  public List<User> findAll() {
    throw new UnsupportedOperationException();
  }

  @Override

  public int countUsers() {
  throw new UnsupportedOperationException();
}

}
