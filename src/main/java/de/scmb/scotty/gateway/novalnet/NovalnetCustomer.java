package de.scmb.scotty.gateway.novalnet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NovalnetCustomer {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String email;

    private String gender;

    private NovalnetAddress billing;

    @JsonProperty("customer_ip")
    private String customerIp;

    public NovalnetCustomer() {}

    public NovalnetCustomer(String firstName, String lastName, String email, String gender, NovalnetAddress billing, String customerIp) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.billing = billing;
        this.customerIp = customerIp;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public NovalnetAddress getBilling() {
        return billing;
    }

    public void setBilling(NovalnetAddress billing) {
        this.billing = billing;
    }

    public String getCustomerIp() {
        return customerIp;
    }

    public void setCustomerIp(String customerIp) {
        this.customerIp = customerIp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovalnetCustomer that = (NovalnetCustomer) o;
        return (
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(email, that.email) &&
            Objects.equals(gender, that.gender) &&
            Objects.equals(billing, that.billing) &&
            Objects.equals(customerIp, that.customerIp)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, gender, billing, customerIp);
    }

    @Override
    public String toString() {
        return (
            "NovalnetCustomer{" +
            "firstName='" +
            firstName +
            '\'' +
            ", lastName='" +
            lastName +
            '\'' +
            ", email='" +
            email +
            '\'' +
            ", gender='" +
            gender +
            '\'' +
            ", billing=" +
            billing +
            ", customerIp='" +
            customerIp +
            '\'' +
            '}'
        );
    }
}
