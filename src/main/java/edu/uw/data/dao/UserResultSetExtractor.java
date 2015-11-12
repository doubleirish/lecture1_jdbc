package edu.uw.data.dao;

import edu.uw.data.model.Address;
import edu.uw.data.model.Phone;
import edu.uw.data.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * "SELECT u.id, u.user_name, u.first_name ,u.last_name, u.active_since " +
 * "       , p.phone, p.label " +
 * "       , a.street, a.city , a.state , a.zip " +
 * " FROM Users u \n" +
 * " LEFT OUTER JOIN Address a on  a.id      = u.address_id " +
 * " LEFT OUTER JOIN phone   p on  p.user_Id = u.id " +
 * " ORDER BY u.id ";
 */
public class UserResultSetExtractor implements ResultSetExtractor {

  public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
    Map<Integer, User> map = new HashMap<>();

    while (rs.next()) {
      Integer id = rs.getInt("ID");
      User user = map.get(id);
      if (user == null) {
        user = new User.Builder()
            .id(id)
            .userName(rs.getString("USER_NAME"))
            .firstName(rs.getString("FIRST_NAME"))
            .lastName(rs.getString("LAST_NAME"))
            .activeSince(rs.getDate("ACTIVE_SINCE"))
            .build();

        map.put(id, user);
      }

      //add optional address
      String street = rs.getString("street");
      if (StringUtils.isNotBlank(street)) {
        Address address = new Address.Builder()
            .street(street)
            .city(rs.getString("CITY"))
            .state(rs.getString("STATE"))
            .zip(rs.getString("ZIP"))
            .build();
        user.setAddress(address);
      }

      //add optional phone numbers
      String phoneNumber = rs.getString("PHONE");
      if (StringUtils.isNotBlank(phoneNumber)) {
        String phoneLabel = rs.getString("LABEL");
        Phone phone = new Phone.Builder()
            .label(phoneLabel)
            .number(phoneNumber)
            .build();
        user.getPhoneNumbers().add(phone);
      }
    }
    return new ArrayList<>(map.values());
  }
}