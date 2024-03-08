package de.scmb.scotty.service.dto;

import de.scmb.scotty.domain.enumeration.Gateway;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link de.scmb.scotty.domain.Payment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentDTO implements Serializable {

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
    @Min(value = 0)
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
    private String addressLine1;

    @Size(max = 70)
    private String addressLine2;

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
    private Instant timestamp;

    @NotNull
    @Size(max = 35)
    private String state;

    @NotNull
    @Size(max = 255)
    private String message;

    @Size(max = 35)
    private String gatewayId;

    @Size(max = 35)
    private String mode;

    @Size(max = 255)
    private String fileName;

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

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentDTO)) {
            return false;
        }

        PaymentDTO paymentDTO = (PaymentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paymentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentDTO{" +
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
            ", addressLine1='" + getAddressLine1() + "'" +
            ", addressLine2='" + getAddressLine2() + "'" +
            ", postalCode='" + getPostalCode() + "'" +
            ", city='" + getCity() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", remoteIp='" + getRemoteIp() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", state='" + getState() + "'" +
            ", message='" + getMessage() + "'" +
            ", gatewayId='" + getGatewayId() + "'" +
            ", mode='" + getMode() + "'" +
            ", fileName='" + getFileName() + "'" +
            "}";
    }
}
