package edu.uw.data.dao;

import edu.uw.data.model.User;

import java.util.List;

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





}
