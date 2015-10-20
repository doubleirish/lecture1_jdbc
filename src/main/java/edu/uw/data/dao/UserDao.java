package edu.uw.data.dao;

import edu.uw.data.model.User;

import java.util.List;

/**
 * the Data Access Object interface for the USER table.
 */
public interface UserDao {

   /* CRUD */
   void createUser(User user) ;

   User readUser(Integer id) ;

   void updateUser(User user) ;

   void deleteUser(User user) ;

   /* queries */
    User findUserByUsername(String username) ;

    List<User> findAll();

    int countUsers();

}
