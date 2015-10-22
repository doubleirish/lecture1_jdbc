package edu.uw.data.dao;

import edu.uw.data.model.User;

import java.util.List;

/**
 * the Data Access Object (DAO) interface for the USER table.
 *
 * Rather than have bits of database access scattered through out classes in an application
 * one popular pattern is to centralize access  in a location
 * and hide the low level details of the database access implementation behind an interface
 *
 * https://en.wikipedia.org/wiki/Data_access_object
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
