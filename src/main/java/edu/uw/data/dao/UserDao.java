package edu.uw.data.dao;

import edu.uw.data.model.User;

import java.util.List;

/**
 * Created by credmond on 03/03/15.
 */
public interface UserDao {


   void createUser(User user) ;
   User readUser(Integer id) ;
   void updateUser(User user) ;
   void deleteUser(User user) ;

    User findUserByUsername(String username) ;

    List<User> findAll();

    int countUsers();

}
