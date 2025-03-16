package de.scmb.scotty.service.dto;

import de.scmb.scotty.domain.enumeration.Gateway;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link de.scmb.scotty.domain.Reconciliation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReconciliationDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 35)
    private String mandateId;

    @NotNull
    @Size(max = 35)
    private String paymentId;

    @NotNull
    private Gateway gateway;

    @NotNull
    @Size(min = 16, max = 34)
    private String iban;

    @NotNull
    @Size(min = 8, max = 11)
    private String bic;

    @NotNull
    private Integer amount;

    @NotNull
    @Size(min = 3, max = 3)
    private String currencyCode;

    @NotNull
    @Size(max = 140)
    private String softDescriptor;

    @NotNull
    @Size(max = 35)
    private String firstName;

    @NotNull
    @Size(max = 35)
    private String lastName;

    @NotNull
    @Size(max = 70)
    private String streetName;

    @NotNull
    @Size(max = 16)
    private String houseNumber;

    @NotNull
    @Size(max = 16)
    private String postalCode;

    @NotNull
    @Size(max = 35)
    private String city;

    @NotNull
    @Size(min = 2, max = 2)
    private String countryCode;

    @NotNull
    @Size(max = 39)
    private String remoteIp;

    @NotNull
    @Size(max = 255)
    private String emailAddress;

    @NotNull
    private Instant timestamp;

    @NotNull
    @Size(max = 35)
    private String state;

    @NotNull
    @Size(max = 35)
    private String reasonCode;

    @NotNull
    @Size(max = 255)
    private String message;

    @Size(max = 255)
    private String gatewayId;

    @Size(max = 35)
    private String mode;

    @Size(max = 255)
    private String fileName;

    private PaymentDTO scottyPayment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMandateId() {
        return mandateId;
    }

    public void setMandateId(String mandateId) {
        this.mandateId = mandateId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Gateway getGateway() {
        return gateway;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getSoftDescriptor() {
        return softDescriptor;
    }

    public void setSoftDescriptor(String softDescriptor) {
        this.softDescriptor = softDescriptor;
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

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public PaymentDTO getScottyPayment() {
        return scottyPayment;
    }

    public void setScottyPayment(PaymentDTO scottyPayment) {
        this.scottyPayment = scottyPayment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReconciliationDTO)) {
            return false;
        }

        ReconciliationDTO reconciliationDTO = (ReconciliationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reconciliationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReconciliationDTO{" +
            "id=" + getId() +
            ", mandateId='" + getMandateId() + "'" +
            ", paymentId='" + getPaymentId() + "'" +
            ", gateway='" + getGateway() + "'" +
            ", iban='" + getIban() + "'" +
            ", bic='" + getBic() + "'" +
            ", amount=" + getAmount() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", softDescriptor='" + getSoftDescriptor() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", streetName='" + getStreetName() + "'" +
            ", houseNumber='" + getHouseNumber() + "'" +
            ", postalCode='" + getPostalCode() + "'" +
            ", city='" + getCity() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", remoteIp='" + getRemoteIp() + "'" +
            ", emailAddress='" + getEmailAddress() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", state='" + getState() + "'" +
            ", reasonCode='" + getReasonCode() + "'" +
            ", message='" + getMessage() + "'" +
            ", gatewayId='" + getGatewayId() + "'" +
            ", mode='" + getMode() + "'" +
            ", fileName='" + getFileName() + "'" +
            ", scottyPayment=" + getScottyPayment() +
            "}";
    }
}
