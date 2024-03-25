package de.scmb.scotty.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.scmb.scotty.domain.enumeration.Gateway;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Reconciliation.
 */
@Entity
@Table(name = "reconciliation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Reconciliation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 35)
    @Column(name = "mandate_id", length = 35, nullable = false)
    private String mandateId;

    @NotNull
    @Size(max = 35)
    @Column(name = "payment_id", length = 35, nullable = false)
    private String paymentId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gateway", nullable = false)
    private Gateway gateway;

    @NotNull
    @Size(min = 16, max = 34)
    @Column(name = "iban", length = 34, nullable = false)
    private String iban;

    @NotNull
    @Size(min = 8, max = 11)
    @Column(name = "bic", length = 11, nullable = false)
    private String bic;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @NotNull
    @Size(min = 3, max = 3)
    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode;

    @NotNull
    @Size(max = 140)
    @Column(name = "soft_descriptor", length = 140, nullable = false)
    private String softDescriptor;

    @NotNull
    @Size(max = 35)
    @Column(name = "first_name", length = 35, nullable = false)
    private String firstName;

    @NotNull
    @Size(max = 35)
    @Column(name = "last_name", length = 35, nullable = false)
    private String lastName;

    @NotNull
    @Size(max = 70)
    @Column(name = "street_name", length = 70, nullable = false)
    private String streetName;

    @NotNull
    @Size(max = 16)
    @Column(name = "house_number", length = 16, nullable = false)
    private String houseNumber;

    @NotNull
    @Size(max = 16)
    @Column(name = "postal_code", length = 16, nullable = false)
    private String postalCode;

    @NotNull
    @Size(max = 35)
    @Column(name = "city", length = 35, nullable = false)
    private String city;

    @NotNull
    @Size(min = 2, max = 2)
    @Column(name = "country_code", length = 2, nullable = false)
    private String countryCode;

    @NotNull
    @Size(max = 39)
    @Column(name = "remote_ip", length = 39, nullable = false)
    private String remoteIp;

    @NotNull
    @Size(max = 255)
    @Column(name = "email_address", length = 255, nullable = false)
    private String emailAddress;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @NotNull
    @Size(max = 35)
    @Column(name = "state", length = 35, nullable = false)
    private String state;

    @NotNull
    @Size(max = 35)
    @Column(name = "reason_code", length = 35, nullable = false)
    private String reasonCode;

    @NotNull
    @Size(max = 255)
    @Column(name = "message", length = 255, nullable = false)
    private String message;

    @Size(max = 35)
    @Column(name = "gateway_id", length = 35)
    private String gatewayId;

    @Size(max = 35)
    @Column(name = "mode", length = 35)
    private String mode;

    @Size(max = 255)
    @Column(name = "file_name", length = 255)
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "reconciliations" }, allowSetters = true)
    private Payment scottyPayment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Reconciliation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMandateId() {
        return this.mandateId;
    }

    public Reconciliation mandateId(String mandateId) {
        this.setMandateId(mandateId);
        return this;
    }

    public void setMandateId(String mandateId) {
        this.mandateId = mandateId;
    }

    public String getPaymentId() {
        return this.paymentId;
    }

    public Reconciliation paymentId(String paymentId) {
        this.setPaymentId(paymentId);
        return this;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Gateway getGateway() {
        return this.gateway;
    }

    public Reconciliation gateway(Gateway gateway) {
        this.setGateway(gateway);
        return this;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
    }

    public String getIban() {
        return this.iban;
    }

    public Reconciliation iban(String iban) {
        this.setIban(iban);
        return this;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return this.bic;
    }

    public Reconciliation bic(String bic) {
        this.setBic(bic);
        return this;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public Integer getAmount() {
        return this.amount;
    }

    public Reconciliation amount(Integer amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public Reconciliation currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getSoftDescriptor() {
        return this.softDescriptor;
    }

    public Reconciliation softDescriptor(String softDescriptor) {
        this.setSoftDescriptor(softDescriptor);
        return this;
    }

    public void setSoftDescriptor(String softDescriptor) {
        this.softDescriptor = softDescriptor;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Reconciliation firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Reconciliation lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreetName() {
        return this.streetName;
    }

    public Reconciliation streetName(String streetName) {
        this.setStreetName(streetName);
        return this;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getHouseNumber() {
        return this.houseNumber;
    }

    public Reconciliation houseNumber(String houseNumber) {
        this.setHouseNumber(houseNumber);
        return this;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public Reconciliation postalCode(String postalCode) {
        this.setPostalCode(postalCode);
        return this;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return this.city;
    }

    public Reconciliation city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public Reconciliation countryCode(String countryCode) {
        this.setCountryCode(countryCode);
        return this;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRemoteIp() {
        return this.remoteIp;
    }

    public Reconciliation remoteIp(String remoteIp) {
        this.setRemoteIp(remoteIp);
        return this;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public Reconciliation emailAddress(String emailAddress) {
        this.setEmailAddress(emailAddress);
        return this;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public Reconciliation timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getState() {
        return this.state;
    }

    public Reconciliation state(String state) {
        this.setState(state);
        return this;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReasonCode() {
        return this.reasonCode;
    }

    public Reconciliation reasonCode(String reasonCode) {
        this.setReasonCode(reasonCode);
        return this;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getMessage() {
        return this.message;
    }

    public Reconciliation message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGatewayId() {
        return this.gatewayId;
    }

    public Reconciliation gatewayId(String gatewayId) {
        this.setGatewayId(gatewayId);
        return this;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getMode() {
        return this.mode;
    }

    public Reconciliation mode(String mode) {
        this.setMode(mode);
        return this;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getFileName() {
        return this.fileName;
    }

    public Reconciliation fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Payment getScottyPayment() {
        return this.scottyPayment;
    }

    public void setScottyPayment(Payment payment) {
        this.scottyPayment = payment;
    }

    public Reconciliation scottyPayment(Payment payment) {
        this.setScottyPayment(payment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reconciliation)) {
            return false;
        }
        return getId() != null && getId().equals(((Reconciliation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reconciliation{" +
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
            "}";
    }
}
