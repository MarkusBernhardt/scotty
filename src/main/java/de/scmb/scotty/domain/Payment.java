package de.scmb.scotty.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.scmb.scotty.domain.enumeration.Gateway;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Payment.
 */
@Entity
@Table(name = "payment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Payment implements Serializable {

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
    @Size(max = 255)
    @Column(name = "message", length = 255, nullable = false)
    private String message;

    @Size(max = 255)
    @Column(name = "gateway_id", length = 255)
    private String gatewayId;

    @Size(max = 35)
    @Column(name = "mode", length = 35)
    private String mode;

    @Size(max = 255)
    @Column(name = "file_name", length = 255)
    private String fileName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "scottyPayment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "scottyPayment" }, allowSetters = true)
    private Set<Reconciliation> reconciliations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Payment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMandateId() {
        return this.mandateId;
    }

    public Payment mandateId(String mandateId) {
        this.setMandateId(mandateId);
        return this;
    }

    public void setMandateId(String mandateId) {
        this.mandateId = mandateId;
    }

    public String getPaymentId() {
        return this.paymentId;
    }

    public Payment paymentId(String paymentId) {
        this.setPaymentId(paymentId);
        return this;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Gateway getGateway() {
        return this.gateway;
    }

    public Payment gateway(Gateway gateway) {
        this.setGateway(gateway);
        return this;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
    }

    public String getIban() {
        return this.iban;
    }

    public Payment iban(String iban) {
        this.setIban(iban);
        return this;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return this.bic;
    }

    public Payment bic(String bic) {
        this.setBic(bic);
        return this;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public Integer getAmount() {
        return this.amount;
    }

    public Payment amount(Integer amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public Payment currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getSoftDescriptor() {
        return this.softDescriptor;
    }

    public Payment softDescriptor(String softDescriptor) {
        this.setSoftDescriptor(softDescriptor);
        return this;
    }

    public void setSoftDescriptor(String softDescriptor) {
        this.softDescriptor = softDescriptor;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Payment firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Payment lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreetName() {
        return this.streetName;
    }

    public Payment streetName(String streetName) {
        this.setStreetName(streetName);
        return this;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getHouseNumber() {
        return this.houseNumber;
    }

    public Payment houseNumber(String houseNumber) {
        this.setHouseNumber(houseNumber);
        return this;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public Payment postalCode(String postalCode) {
        this.setPostalCode(postalCode);
        return this;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return this.city;
    }

    public Payment city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public Payment countryCode(String countryCode) {
        this.setCountryCode(countryCode);
        return this;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRemoteIp() {
        return this.remoteIp;
    }

    public Payment remoteIp(String remoteIp) {
        this.setRemoteIp(remoteIp);
        return this;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public Payment emailAddress(String emailAddress) {
        this.setEmailAddress(emailAddress);
        return this;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public Payment timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getState() {
        return this.state;
    }

    public Payment state(String state) {
        this.setState(state);
        return this;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return this.message;
    }

    public Payment message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGatewayId() {
        return this.gatewayId;
    }

    public Payment gatewayId(String gatewayId) {
        this.setGatewayId(gatewayId);
        return this;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getMode() {
        return this.mode;
    }

    public Payment mode(String mode) {
        this.setMode(mode);
        return this;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getFileName() {
        return this.fileName;
    }

    public Payment fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Set<Reconciliation> getReconciliations() {
        return this.reconciliations;
    }

    public void setReconciliations(Set<Reconciliation> reconciliations) {
        if (this.reconciliations != null) {
            this.reconciliations.forEach(i -> i.setScottyPayment(null));
        }
        if (reconciliations != null) {
            reconciliations.forEach(i -> i.setScottyPayment(this));
        }
        this.reconciliations = reconciliations;
    }

    public Payment reconciliations(Set<Reconciliation> reconciliations) {
        this.setReconciliations(reconciliations);
        return this;
    }

    public Payment addReconciliation(Reconciliation reconciliation) {
        this.reconciliations.add(reconciliation);
        reconciliation.setScottyPayment(this);
        return this;
    }

    public Payment removeReconciliation(Reconciliation reconciliation) {
        this.reconciliations.remove(reconciliation);
        reconciliation.setScottyPayment(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return getId() != null && getId().equals(((Payment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Payment{" +
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
            ", message='" + getMessage() + "'" +
            ", gatewayId='" + getGatewayId() + "'" +
            ", mode='" + getMode() + "'" +
            ", fileName='" + getFileName() + "'" +
            "}";
    }
}
