package de.scmb.scotty.service.criteria;

import de.scmb.scotty.domain.enumeration.Gateway;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link de.scmb.scotty.domain.Reconciliation} entity. This class is used
 * in {@link de.scmb.scotty.web.rest.ReconciliationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /reconciliations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReconciliationCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Gateway
     */
    public static class GatewayFilter extends Filter<Gateway> {

        public GatewayFilter() {}

        public GatewayFilter(GatewayFilter filter) {
            super(filter);
        }

        @Override
        public GatewayFilter copy() {
            return new GatewayFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter mandateId;

    private StringFilter paymentId;

    private GatewayFilter gateway;

    private StringFilter iban;

    private StringFilter bic;

    private IntegerFilter amount;

    private StringFilter currencyCode;

    private StringFilter softDescriptor;

    private StringFilter firstName;

    private StringFilter lastName;

    private StringFilter streetName;

    private StringFilter houseNumber;

    private StringFilter postalCode;

    private StringFilter city;

    private StringFilter countryCode;

    private StringFilter remoteIp;

    private StringFilter emailAddress;

    private InstantFilter timestamp;

    private StringFilter state;

    private StringFilter reasonCode;

    private StringFilter message;

    private StringFilter gatewayId;

    private StringFilter mode;

    private StringFilter fileName;

    private LongFilter scottyPaymentId;

    private Boolean distinct;

    public ReconciliationCriteria() {}

    public ReconciliationCriteria(ReconciliationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.mandateId = other.mandateId == null ? null : other.mandateId.copy();
        this.paymentId = other.paymentId == null ? null : other.paymentId.copy();
        this.gateway = other.gateway == null ? null : other.gateway.copy();
        this.iban = other.iban == null ? null : other.iban.copy();
        this.bic = other.bic == null ? null : other.bic.copy();
        this.amount = other.amount == null ? null : other.amount.copy();
        this.currencyCode = other.currencyCode == null ? null : other.currencyCode.copy();
        this.softDescriptor = other.softDescriptor == null ? null : other.softDescriptor.copy();
        this.firstName = other.firstName == null ? null : other.firstName.copy();
        this.lastName = other.lastName == null ? null : other.lastName.copy();
        this.streetName = other.streetName == null ? null : other.streetName.copy();
        this.houseNumber = other.houseNumber == null ? null : other.houseNumber.copy();
        this.postalCode = other.postalCode == null ? null : other.postalCode.copy();
        this.city = other.city == null ? null : other.city.copy();
        this.countryCode = other.countryCode == null ? null : other.countryCode.copy();
        this.remoteIp = other.remoteIp == null ? null : other.remoteIp.copy();
        this.emailAddress = other.emailAddress == null ? null : other.emailAddress.copy();
        this.timestamp = other.timestamp == null ? null : other.timestamp.copy();
        this.state = other.state == null ? null : other.state.copy();
        this.reasonCode = other.reasonCode == null ? null : other.reasonCode.copy();
        this.message = other.message == null ? null : other.message.copy();
        this.gatewayId = other.gatewayId == null ? null : other.gatewayId.copy();
        this.mode = other.mode == null ? null : other.mode.copy();
        this.fileName = other.fileName == null ? null : other.fileName.copy();
        this.scottyPaymentId = other.scottyPaymentId == null ? null : other.scottyPaymentId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ReconciliationCriteria copy() {
        return new ReconciliationCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getMandateId() {
        return mandateId;
    }

    public StringFilter mandateId() {
        if (mandateId == null) {
            mandateId = new StringFilter();
        }
        return mandateId;
    }

    public void setMandateId(StringFilter mandateId) {
        this.mandateId = mandateId;
    }

    public StringFilter getPaymentId() {
        return paymentId;
    }

    public StringFilter paymentId() {
        if (paymentId == null) {
            paymentId = new StringFilter();
        }
        return paymentId;
    }

    public void setPaymentId(StringFilter paymentId) {
        this.paymentId = paymentId;
    }

    public GatewayFilter getGateway() {
        return gateway;
    }

    public GatewayFilter gateway() {
        if (gateway == null) {
            gateway = new GatewayFilter();
        }
        return gateway;
    }

    public void setGateway(GatewayFilter gateway) {
        this.gateway = gateway;
    }

    public StringFilter getIban() {
        return iban;
    }

    public StringFilter iban() {
        if (iban == null) {
            iban = new StringFilter();
        }
        return iban;
    }

    public void setIban(StringFilter iban) {
        this.iban = iban;
    }

    public StringFilter getBic() {
        return bic;
    }

    public StringFilter bic() {
        if (bic == null) {
            bic = new StringFilter();
        }
        return bic;
    }

    public void setBic(StringFilter bic) {
        this.bic = bic;
    }

    public IntegerFilter getAmount() {
        return amount;
    }

    public IntegerFilter amount() {
        if (amount == null) {
            amount = new IntegerFilter();
        }
        return amount;
    }

    public void setAmount(IntegerFilter amount) {
        this.amount = amount;
    }

    public StringFilter getCurrencyCode() {
        return currencyCode;
    }

    public StringFilter currencyCode() {
        if (currencyCode == null) {
            currencyCode = new StringFilter();
        }
        return currencyCode;
    }

    public void setCurrencyCode(StringFilter currencyCode) {
        this.currencyCode = currencyCode;
    }

    public StringFilter getSoftDescriptor() {
        return softDescriptor;
    }

    public StringFilter softDescriptor() {
        if (softDescriptor == null) {
            softDescriptor = new StringFilter();
        }
        return softDescriptor;
    }

    public void setSoftDescriptor(StringFilter softDescriptor) {
        this.softDescriptor = softDescriptor;
    }

    public StringFilter getFirstName() {
        return firstName;
    }

    public StringFilter firstName() {
        if (firstName == null) {
            firstName = new StringFilter();
        }
        return firstName;
    }

    public void setFirstName(StringFilter firstName) {
        this.firstName = firstName;
    }

    public StringFilter getLastName() {
        return lastName;
    }

    public StringFilter lastName() {
        if (lastName == null) {
            lastName = new StringFilter();
        }
        return lastName;
    }

    public void setLastName(StringFilter lastName) {
        this.lastName = lastName;
    }

    public StringFilter getStreetName() {
        return streetName;
    }

    public StringFilter streetName() {
        if (streetName == null) {
            streetName = new StringFilter();
        }
        return streetName;
    }

    public void setStreetName(StringFilter streetName) {
        this.streetName = streetName;
    }

    public StringFilter getHouseNumber() {
        return houseNumber;
    }

    public StringFilter houseNumber() {
        if (houseNumber == null) {
            houseNumber = new StringFilter();
        }
        return houseNumber;
    }

    public void setHouseNumber(StringFilter houseNumber) {
        this.houseNumber = houseNumber;
    }

    public StringFilter getPostalCode() {
        return postalCode;
    }

    public StringFilter postalCode() {
        if (postalCode == null) {
            postalCode = new StringFilter();
        }
        return postalCode;
    }

    public void setPostalCode(StringFilter postalCode) {
        this.postalCode = postalCode;
    }

    public StringFilter getCity() {
        return city;
    }

    public StringFilter city() {
        if (city == null) {
            city = new StringFilter();
        }
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public StringFilter getCountryCode() {
        return countryCode;
    }

    public StringFilter countryCode() {
        if (countryCode == null) {
            countryCode = new StringFilter();
        }
        return countryCode;
    }

    public void setCountryCode(StringFilter countryCode) {
        this.countryCode = countryCode;
    }

    public StringFilter getRemoteIp() {
        return remoteIp;
    }

    public StringFilter remoteIp() {
        if (remoteIp == null) {
            remoteIp = new StringFilter();
        }
        return remoteIp;
    }

    public void setRemoteIp(StringFilter remoteIp) {
        this.remoteIp = remoteIp;
    }

    public StringFilter getEmailAddress() {
        return emailAddress;
    }

    public StringFilter emailAddress() {
        if (emailAddress == null) {
            emailAddress = new StringFilter();
        }
        return emailAddress;
    }

    public void setEmailAddress(StringFilter emailAddress) {
        this.emailAddress = emailAddress;
    }

    public InstantFilter getTimestamp() {
        return timestamp;
    }

    public InstantFilter timestamp() {
        if (timestamp == null) {
            timestamp = new InstantFilter();
        }
        return timestamp;
    }

    public void setTimestamp(InstantFilter timestamp) {
        this.timestamp = timestamp;
    }

    public StringFilter getState() {
        return state;
    }

    public StringFilter state() {
        if (state == null) {
            state = new StringFilter();
        }
        return state;
    }

    public void setState(StringFilter state) {
        this.state = state;
    }

    public StringFilter getReasonCode() {
        return reasonCode;
    }

    public StringFilter reasonCode() {
        if (reasonCode == null) {
            reasonCode = new StringFilter();
        }
        return reasonCode;
    }

    public void setReasonCode(StringFilter reasonCode) {
        this.reasonCode = reasonCode;
    }

    public StringFilter getMessage() {
        return message;
    }

    public StringFilter message() {
        if (message == null) {
            message = new StringFilter();
        }
        return message;
    }

    public void setMessage(StringFilter message) {
        this.message = message;
    }

    public StringFilter getGatewayId() {
        return gatewayId;
    }

    public StringFilter gatewayId() {
        if (gatewayId == null) {
            gatewayId = new StringFilter();
        }
        return gatewayId;
    }

    public void setGatewayId(StringFilter gatewayId) {
        this.gatewayId = gatewayId;
    }

    public StringFilter getMode() {
        return mode;
    }

    public StringFilter mode() {
        if (mode == null) {
            mode = new StringFilter();
        }
        return mode;
    }

    public void setMode(StringFilter mode) {
        this.mode = mode;
    }

    public StringFilter getFileName() {
        return fileName;
    }

    public StringFilter fileName() {
        if (fileName == null) {
            fileName = new StringFilter();
        }
        return fileName;
    }

    public void setFileName(StringFilter fileName) {
        this.fileName = fileName;
    }

    public LongFilter getScottyPaymentId() {
        return scottyPaymentId;
    }

    public LongFilter scottyPaymentId() {
        if (scottyPaymentId == null) {
            scottyPaymentId = new LongFilter();
        }
        return scottyPaymentId;
    }

    public void setScottyPaymentId(LongFilter scottyPaymentId) {
        this.scottyPaymentId = scottyPaymentId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReconciliationCriteria that = (ReconciliationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(mandateId, that.mandateId) &&
            Objects.equals(paymentId, that.paymentId) &&
            Objects.equals(gateway, that.gateway) &&
            Objects.equals(iban, that.iban) &&
            Objects.equals(bic, that.bic) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(currencyCode, that.currencyCode) &&
            Objects.equals(softDescriptor, that.softDescriptor) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(streetName, that.streetName) &&
            Objects.equals(houseNumber, that.houseNumber) &&
            Objects.equals(postalCode, that.postalCode) &&
            Objects.equals(city, that.city) &&
            Objects.equals(countryCode, that.countryCode) &&
            Objects.equals(remoteIp, that.remoteIp) &&
            Objects.equals(emailAddress, that.emailAddress) &&
            Objects.equals(timestamp, that.timestamp) &&
            Objects.equals(state, that.state) &&
            Objects.equals(reasonCode, that.reasonCode) &&
            Objects.equals(message, that.message) &&
            Objects.equals(gatewayId, that.gatewayId) &&
            Objects.equals(mode, that.mode) &&
            Objects.equals(fileName, that.fileName) &&
            Objects.equals(scottyPaymentId, that.scottyPaymentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            mandateId,
            paymentId,
            gateway,
            iban,
            bic,
            amount,
            currencyCode,
            softDescriptor,
            firstName,
            lastName,
            streetName,
            houseNumber,
            postalCode,
            city,
            countryCode,
            remoteIp,
            emailAddress,
            timestamp,
            state,
            reasonCode,
            message,
            gatewayId,
            mode,
            fileName,
            scottyPaymentId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReconciliationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (mandateId != null ? "mandateId=" + mandateId + ", " : "") +
            (paymentId != null ? "paymentId=" + paymentId + ", " : "") +
            (gateway != null ? "gateway=" + gateway + ", " : "") +
            (iban != null ? "iban=" + iban + ", " : "") +
            (bic != null ? "bic=" + bic + ", " : "") +
            (amount != null ? "amount=" + amount + ", " : "") +
            (currencyCode != null ? "currencyCode=" + currencyCode + ", " : "") +
            (softDescriptor != null ? "softDescriptor=" + softDescriptor + ", " : "") +
            (firstName != null ? "firstName=" + firstName + ", " : "") +
            (lastName != null ? "lastName=" + lastName + ", " : "") +
            (streetName != null ? "streetName=" + streetName + ", " : "") +
            (houseNumber != null ? "houseNumber=" + houseNumber + ", " : "") +
            (postalCode != null ? "postalCode=" + postalCode + ", " : "") +
            (city != null ? "city=" + city + ", " : "") +
            (countryCode != null ? "countryCode=" + countryCode + ", " : "") +
            (remoteIp != null ? "remoteIp=" + remoteIp + ", " : "") +
            (emailAddress != null ? "emailAddress=" + emailAddress + ", " : "") +
            (timestamp != null ? "timestamp=" + timestamp + ", " : "") +
            (state != null ? "state=" + state + ", " : "") +
            (reasonCode != null ? "reasonCode=" + reasonCode + ", " : "") +
            (message != null ? "message=" + message + ", " : "") +
            (gatewayId != null ? "gatewayId=" + gatewayId + ", " : "") +
            (mode != null ? "mode=" + mode + ", " : "") +
            (fileName != null ? "fileName=" + fileName + ", " : "") +
            (scottyPaymentId != null ? "scottyPaymentId=" + scottyPaymentId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
