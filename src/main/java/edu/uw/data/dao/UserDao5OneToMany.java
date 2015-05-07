package edu.uw.data.dao;

import edu.uw.data.model.Address;
import edu.uw.data.model.Phone;
import edu.uw.data.model.User;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * simple single-table Jdbc example with try-resources and datasource
 */
public class UserDao5OneToMany extends AbstractUserDao implements UserDao {

  private DataSource dataSource = null;

  public UserDao5OneToMany(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public List<User> findAll() {

    List<User> users = new ArrayList<>();
    String sql =
        "SELECT u.id, u.username, u.firstname ,u.lastname, u.active_since \n" +
            "       , a.street, a.city , a.state , a.zip \n" +
            "       , p.phone, p.label \n" +
            "FROM Users u \n" +
            "LEFT OUTER JOIN Address a on  a.user_Id = u.id \n" +
            "LEFT OUTER JOIN phone p   on  p.user_Id = u.id \n" +
            "ORDER BY u.id ";
    try (
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()
    ) {
      while (rs.next()) {
        System.out.println(rs);
        User user = new User();
        user.setId(rs.getInt(1));
        user.setUserName(rs.getString(2));
        user.setFirstName(rs.getString(3));
        user.setLastName(rs.getString(4));
        user.setActiveSince(rs.getDate(5));

        // build address
        String street = rs.getString(6);
        if (StringUtils.isNotBlank(street)) {
          Address address = new Address.Builder()
              .street(street)
              .city(rs.getString(7))
              .state(rs.getString(8))
              .zip(rs.getString(9))
              .build();
          user.setAddress(address);
        }


        // build phone
        String phoneStr = rs.getString(10);
        if (StringUtils.isNotBlank(phoneStr)) {
          String label = rs.getString(11);
          Phone phone = new Phone.Builder()
              .label(label)
              .number(phoneStr)
              .build();
          user.addPhoneNumber(phone);
        }
        // TODO !! multiple 'credmond' users with different phone info.
        // TODO how to consolidate ???
        users.add(user);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e); //convert checked exception into unchecked
    }
    return users;
  }

}
