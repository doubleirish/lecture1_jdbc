package edu.uw.data.model;

/**
 * Created by credmond on 03/03/15.
 */
public class Phone implements Comparable{
  private String label;
  private String number;

  public Phone() {
  }

  public Phone(String label, String number) {
    this.label = label;
    this.number = number;
  }

  public String getLabel() {
    return label;
  }

  public String getNumber() {
    return number;
  }

  @Override
  public int compareTo(Object obj) {
    int compare =0;
    Phone that = (Phone) obj;
    return this.number.compareTo(that.number);
  }

  @Override
  public String toString() {
    return "" +label + "='" + number + "' "  ;
  }


  public static class Builder {
    private Phone phone;

    public Builder() {
      phone = new Phone();
    }

    public Builder builder() {
      return new Builder();
    }

    public Builder label(String label) {
      phone.label =label;
      return this;
    }

    public Builder number(String number) {
      phone.number=number;
      return this;
    }

    public Phone build() {
      //validate ??;
      return phone;
    }
  }
}
