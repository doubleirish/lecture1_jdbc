package edu.uw.data.model;

/**
 * Created by credmond on 03/03/15.
 */
public class Address {
  private Integer id;
  private String street;
  private String city;
  private String state;
  private String zip;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }



  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  @Override
  public String toString() {
    return "Address{" +
        "id=" + id +
        ", street='" + street + '\'' +
        ", city='" + city + '\'' +
        ", state='" + state + '\'' +
        ", zip='" + zip + '\'' +
        '}';
  }

  public static class Builder {
    private Address address;

    public Builder() {
      address = new Address();
    }

    public Builder builder() {
      return new Builder();
    }


    public Builder street(String street) {
      address.setStreet(street);
      return this;
    }


    public Builder city(String city) {
      address.setCity(city);
      return this;
    }

    public Builder state(String  state) {
      address.setState(state);
      return this;
    }

    public Builder zip(String zip) {
      address.setZip(zip);
      return this;
    }


    public Address build() {
      //validate ??;
      return address;
    }
  }

}
