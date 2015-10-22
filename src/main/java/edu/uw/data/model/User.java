package edu.uw.data.model;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * User can have one Adress but many Phone Numbers
 */
public class User {
 private Integer id;
 private String userName;
 private String firstName;
 private String lastName;
 private Date activeSince;
 private Address address;
 private Set<Phone> phoneNumbers = new TreeSet<>();

  public User() {
  }

  public User(Integer id) {
    this.id = id;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Date getActiveSince() {
    return activeSince;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Set<Phone> getPhoneNumbers() {
    return phoneNumbers;
  }

  public void setPhoneNumbers(Set<Phone> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

  public void addPhoneNumber(Phone phone) {
    this.phoneNumbers.add(phone);
  }


  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", userName='" + userName + '\'' +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", activeSince=" + activeSince +
        ", address=" + address +
        ", phoneNumbers=" + phoneNumbers +
        '}';
  }

  // An example of the Builder pattern as described in the book "Effective Java"  by Joshua Bloch
  // builders are a less error prone alternative to constructors and more readable than setters
  // example usage is below.
  //   User user = new User.Builder()
  //              .firstName("Ted")
  //              .lastName("Crilly")
  //              .build();
  public static class Builder {
    private User user;

    public Builder() {
      user = new User();
    }

    public Builder builder() {
      return new Builder();
    }

    public Builder id(Integer id) {
      user.setId(id);
      return this;
    }

    public Builder userName(String userName) {
      user.setUserName(userName);
      return this;
    }

    public Builder firstName(String firstName) {
      user.setFirstName(firstName);
      return this;
    }

    public Builder lastName(String lastName) {
      user.setLastName(lastName);
      return this;
    }

    public Builder activeSince(Date  activeSince) {
      user.setActiveSince(activeSince);
      return this;
    }

    public Builder address(Address address) {
      user.setAddress(address);
      return this;
    }

    public Builder phoneNumbers(Set<Phone> phoneNumbers) {
      user.setPhoneNumbers(phoneNumbers);
      return this;
    }

    public Builder phone(Phone phone) {
      user.getPhoneNumbers().add(phone);
      return this;
    }

    public User build() {
      //validate ??;
      return user;
    }
  }
}
