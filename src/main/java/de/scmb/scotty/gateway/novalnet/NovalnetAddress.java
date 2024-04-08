package de.scmb.scotty.gateway.novalnet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NovalnetAddress {

    @JsonProperty("house_no")
    private String houseNo;

    private String street;

    private String city;

    private String zip;

    @JsonProperty("country_code")
    private String countryCode;

    public NovalnetAddress() {}

    public NovalnetAddress(String houseNo, String street, String city, String zip, String countryCode) {
        this.houseNo = houseNo;
        this.street = street;
        this.city = city;
        this.zip = zip;
        this.countryCode = countryCode;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovalnetAddress that = (NovalnetAddress) o;
        return (
            Objects.equals(houseNo, that.houseNo) &&
            Objects.equals(street, that.street) &&
            Objects.equals(city, that.city) &&
            Objects.equals(zip, that.zip) &&
            Objects.equals(countryCode, that.countryCode)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(houseNo, street, city, zip, countryCode);
    }

    @Override
    public String toString() {
        return (
            "NovalnetAddress{" +
            "houseNo='" +
            houseNo +
            '\'' +
            ", street='" +
            street +
            '\'' +
            ", city='" +
            city +
            '\'' +
            ", zip='" +
            zip +
            '\'' +
            ", countryCode='" +
            countryCode +
            '\'' +
            '}'
        );
    }
}
