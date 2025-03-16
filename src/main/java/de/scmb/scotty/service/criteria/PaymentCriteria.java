package de.scmb.scotty.service.criteria;

import de.scmb.scotty.domain.enumeration.Gateway;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link de.scmb.scotty.domain.Payment} entity. This class is used
 * in {@link de.scmb.scotty.web.rest.PaymentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /payments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentCriteria implements Serializable, Criteria {

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

    private StringFilter message;

    private StringFilter gatewayId;

    private StringFilter mode;

    private StringFilter fileName;

    private LongFilter reconciliationId;

    private Boolean distinct;

    public PaymentCriteria() {}

    public PaymentCriteria(PaymentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.mandateId = other.optionalMandateId().map(StringFilter::copy).orElse(null);
        this.paymentId = other.optionalPaymentId().map(StringFilter::copy).orElse(null);
        this.gateway = other.optionalGateway().map(GatewayFilter::copy).orElse(null);
        this.iban = other.optionalIban().map(StringFilter::copy).orElse(null);
        this.bic = other.optionalBic().map(StringFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(IntegerFilter::copy).orElse(null);
        this.currencyCode = other.optionalCurrencyCode().map(StringFilter::copy).orElse(null);
        this.softDescriptor = other.optionalSoftDescriptor().map(StringFilter::copy).orElse(null);
        this.firstName = other.optionalFirstName().map(StringFilter::copy).orElse(null);
        this.lastName = other.optionalLastName().map(StringFilter::copy).orElse(null);
        this.streetName = other.optionalStreetName().map(StringFilter::copy).orElse(null);
        this.houseNumber = other.optionalHouseNumber().map(StringFilter::copy).orElse(null);
        this.postalCode = other.optionalPostalCode().map(StringFilter::copy).orElse(null);
        this.city = other.optionalCity().map(StringFilter::copy).orElse(null);
        this.countryCode = other.optionalCountryCode().map(StringFilter::copy).orElse(null);
        this.remoteIp = other.optionalRemoteIp().map(StringFilter::copy).orElse(null);
        this.emailAddress = other.optionalEmailAddress().map(StringFilter::copy).orElse(null);
        this.timestamp = other.optionalTimestamp().map(InstantFilter::copy).orElse(null);
        this.state = other.optionalState().map(StringFilter::copy).orElse(null);
        this.message = other.optionalMessage().map(StringFilter::copy).orElse(null);
        this.gatewayId = other.optionalGatewayId().map(StringFilter::copy).orElse(null);
        this.mode = other.optionalMode().map(StringFilter::copy).orElse(null);
        this.fileName = other.optionalFileName().map(StringFilter::copy).orElse(null);
        this.reconciliationId = other.optionalReconciliationId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PaymentCriteria copy() {
        return new PaymentCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getMandateId() {
        return mandateId;
    }

    public Optional<StringFilter> optionalMandateId() {
        return Optional.ofNullable(mandateId);
    }

    public StringFilter mandateId() {
        if (mandateId == null) {
            setMandateId(new StringFilter());
        }
        return mandateId;
    }

    public void setMandateId(StringFilter mandateId) {
        this.mandateId = mandateId;
    }

    public StringFilter getPaymentId() {
        return paymentId;
    }

    public Optional<StringFilter> optionalPaymentId() {
        return Optional.ofNullable(paymentId);
    }

    public StringFilter paymentId() {
        if (paymentId == null) {
            setPaymentId(new StringFilter());
        }
        return paymentId;
    }

    public void setPaymentId(StringFilter paymentId) {
        this.paymentId = paymentId;
    }

    public GatewayFilter getGateway() {
        return gateway;
    }

    public Optional<GatewayFilter> optionalGateway() {
        return Optional.ofNullable(gateway);
    }

    public GatewayFilter gateway() {
        if (gateway == null) {
            setGateway(new GatewayFilter());
        }
        return gateway;
    }

    public void setGateway(GatewayFilter gateway) {
        this.gateway = gateway;
    }

    public StringFilter getIban() {
        return iban;
    }

    public Optional<StringFilter> optionalIban() {
        return Optional.ofNullable(iban);
    }

    public StringFilter iban() {
        if (iban == null) {
            setIban(new StringFilter());
        }
        return iban;
    }

    public void setIban(StringFilter iban) {
        this.iban = iban;
    }

    public StringFilter getBic() {
        return bic;
    }

    public Optional<StringFilter> optionalBic() {
        return Optional.ofNullable(bic);
    }

    public StringFilter bic() {
        if (bic == null) {
            setBic(new StringFilter());
        }
        return bic;
    }

    public void setBic(StringFilter bic) {
        this.bic = bic;
    }

    public IntegerFilter getAmount() {
        return amount;
    }

    public Optional<IntegerFilter> optionalAmount() {
        return Optional.ofNullable(amount);
    }

    public IntegerFilter amount() {
        if (amount == null) {
            setAmount(new IntegerFilter());
        }
        return amount;
    }

    public void setAmount(IntegerFilter amount) {
        this.amount = amount;
    }

    public StringFilter getCurrencyCode() {
        return currencyCode;
    }

    public Optional<StringFilter> optionalCurrencyCode() {
        return Optional.ofNullable(currencyCode);
    }

    public StringFilter currencyCode() {
        if (currencyCode == null) {
            setCurrencyCode(new StringFilter());
        }
        return currencyCode;
    }

    public void setCurrencyCode(StringFilter currencyCode) {
        this.currencyCode = currencyCode;
    }

    public StringFilter getSoftDescriptor() {
        return softDescriptor;
    }

    public Optional<StringFilter> optionalSoftDescriptor() {
        return Optional.ofNullable(softDescriptor);
    }

    public StringFilter softDescriptor() {
        if (softDescriptor == null) {
            setSoftDescriptor(new StringFilter());
        }
        return softDescriptor;
    }

    public void setSoftDescriptor(StringFilter softDescriptor) {
        this.softDescriptor = softDescriptor;
    }

    public StringFilter getFirstName() {
        return firstName;
    }

    public Optional<StringFilter> optionalFirstName() {
        return Optional.ofNullable(firstName);
    }

    public StringFilter firstName() {
        if (firstName == null) {
            setFirstName(new StringFilter());
        }
        return firstName;
    }

    public void setFirstName(StringFilter firstName) {
        this.firstName = firstName;
    }

    public StringFilter getLastName() {
        return lastName;
    }

    public Optional<StringFilter> optionalLastName() {
        return Optional.ofNullable(lastName);
    }

    public StringFilter lastName() {
        if (lastName == null) {
            setLastName(new StringFilter());
        }
        return lastName;
    }

    public void setLastName(StringFilter lastName) {
        this.lastName = lastName;
    }

    public StringFilter getStreetName() {
        return streetName;
    }

    public Optional<StringFilter> optionalStreetName() {
        return Optional.ofNullable(streetName);
    }

    public StringFilter streetName() {
        if (streetName == null) {
            setStreetName(new StringFilter());
        }
        return streetName;
    }

    public void setStreetName(StringFilter streetName) {
        this.streetName = streetName;
    }

    public StringFilter getHouseNumber() {
        return houseNumber;
    }

    public Optional<StringFilter> optionalHouseNumber() {
        return Optional.ofNullable(houseNumber);
    }

    public StringFilter houseNumber() {
        if (houseNumber == null) {
            setHouseNumber(new StringFilter());
        }
        return houseNumber;
    }

    public void setHouseNumber(StringFilter houseNumber) {
        this.houseNumber = houseNumber;
    }

    public StringFilter getPostalCode() {
        return postalCode;
    }

    public Optional<StringFilter> optionalPostalCode() {
        return Optional.ofNullable(postalCode);
    }

    public StringFilter postalCode() {
        if (postalCode == null) {
            setPostalCode(new StringFilter());
        }
        return postalCode;
    }

    public void setPostalCode(StringFilter postalCode) {
        this.postalCode = postalCode;
    }

    public StringFilter getCity() {
        return city;
    }

    public Optional<StringFilter> optionalCity() {
        return Optional.ofNullable(city);
    }

    public StringFilter city() {
        if (city == null) {
            setCity(new StringFilter());
        }
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public StringFilter getCountryCode() {
        return countryCode;
    }

    public Optional<StringFilter> optionalCountryCode() {
        return Optional.ofNullable(countryCode);
    }

    public StringFilter countryCode() {
        if (countryCode == null) {
            setCountryCode(new StringFilter());
        }
        return countryCode;
    }

    public void setCountryCode(StringFilter countryCode) {
        this.countryCode = countryCode;
    }

    public StringFilter getRemoteIp() {
        return remoteIp;
    }

    public Optional<StringFilter> optionalRemoteIp() {
        return Optional.ofNullable(remoteIp);
    }

    public StringFilter remoteIp() {
        if (remoteIp == null) {
            setRemoteIp(new StringFilter());
        }
        return remoteIp;
    }

    public void setRemoteIp(StringFilter remoteIp) {
        this.remoteIp = remoteIp;
    }

    public StringFilter getEmailAddress() {
        return emailAddress;
    }

    public Optional<StringFilter> optionalEmailAddress() {
        return Optional.ofNullable(emailAddress);
    }

    public StringFilter emailAddress() {
        if (emailAddress == null) {
            setEmailAddress(new StringFilter());
        }
        return emailAddress;
    }

    public void setEmailAddress(StringFilter emailAddress) {
        this.emailAddress = emailAddress;
    }

    public InstantFilter getTimestamp() {
        return timestamp;
    }

    public Optional<InstantFilter> optionalTimestamp() {
        return Optional.ofNullable(timestamp);
    }

    public InstantFilter timestamp() {
        if (timestamp == null) {
            setTimestamp(new InstantFilter());
        }
        return timestamp;
    }

    public void setTimestamp(InstantFilter timestamp) {
        this.timestamp = timestamp;
    }

    public StringFilter getState() {
        return state;
    }

    public Optional<StringFilter> optionalState() {
        return Optional.ofNullable(state);
    }

    public StringFilter state() {
        if (state == null) {
            setState(new StringFilter());
        }
        return state;
    }

    public void setState(StringFilter state) {
        this.state = state;
    }

    public StringFilter getMessage() {
        return message;
    }

    public Optional<StringFilter> optionalMessage() {
        return Optional.ofNullable(message);
    }

    public StringFilter message() {
        if (message == null) {
            setMessage(new StringFilter());
        }
        return message;
    }

    public void setMessage(StringFilter message) {
        this.message = message;
    }

    public StringFilter getGatewayId() {
        return gatewayId;
    }

    public Optional<StringFilter> optionalGatewayId() {
        return Optional.ofNullable(gatewayId);
    }

    public StringFilter gatewayId() {
        if (gatewayId == null) {
            setGatewayId(new StringFilter());
        }
        return gatewayId;
    }

    public void setGatewayId(StringFilter gatewayId) {
        this.gatewayId = gatewayId;
    }

    public StringFilter getMode() {
        return mode;
    }

    public Optional<StringFilter> optionalMode() {
        return Optional.ofNullable(mode);
    }

    public StringFilter mode() {
        if (mode == null) {
            setMode(new StringFilter());
        }
        return mode;
    }

    public void setMode(StringFilter mode) {
        this.mode = mode;
    }

    public StringFilter getFileName() {
        return fileName;
    }

    public Optional<StringFilter> optionalFileName() {
        return Optional.ofNullable(fileName);
    }

    public StringFilter fileName() {
        if (fileName == null) {
            setFileName(new StringFilter());
        }
        return fileName;
    }

    public void setFileName(StringFilter fileName) {
        this.fileName = fileName;
    }

    public LongFilter getReconciliationId() {
        return reconciliationId;
    }

    public Optional<LongFilter> optionalReconciliationId() {
        return Optional.ofNullable(reconciliationId);
    }

    public LongFilter reconciliationId() {
        if (reconciliationId == null) {
            setReconciliationId(new LongFilter());
        }
        return reconciliationId;
    }

    public void setReconciliationId(LongFilter reconciliationId) {
        this.reconciliationId = reconciliationId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
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
        final PaymentCriteria that = (PaymentCriteria) o;
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
            Objects.equals(message, that.message) &&
            Objects.equals(gatewayId, that.gatewayId) &&
            Objects.equals(mode, that.mode) &&
            Objects.equals(fileName, that.fileName) &&
            Objects.equals(reconciliationId, that.reconciliationId) &&
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
            message,
            gatewayId,
            mode,
            fileName,
            reconciliationId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalMandateId().map(f -> "mandateId=" + f + ", ").orElse("") +
            optionalPaymentId().map(f -> "paymentId=" + f + ", ").orElse("") +
            optionalGateway().map(f -> "gateway=" + f + ", ").orElse("") +
            optionalIban().map(f -> "iban=" + f + ", ").orElse("") +
            optionalBic().map(f -> "bic=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalCurrencyCode().map(f -> "currencyCode=" + f + ", ").orElse("") +
            optionalSoftDescriptor().map(f -> "softDescriptor=" + f + ", ").orElse("") +
            optionalFirstName().map(f -> "firstName=" + f + ", ").orElse("") +
            optionalLastName().map(f -> "lastName=" + f + ", ").orElse("") +
            optionalStreetName().map(f -> "streetName=" + f + ", ").orElse("") +
            optionalHouseNumber().map(f -> "houseNumber=" + f + ", ").orElse("") +
            optionalPostalCode().map(f -> "postalCode=" + f + ", ").orElse("") +
            optionalCity().map(f -> "city=" + f + ", ").orElse("") +
            optionalCountryCode().map(f -> "countryCode=" + f + ", ").orElse("") +
            optionalRemoteIp().map(f -> "remoteIp=" + f + ", ").orElse("") +
            optionalEmailAddress().map(f -> "emailAddress=" + f + ", ").orElse("") +
            optionalTimestamp().map(f -> "timestamp=" + f + ", ").orElse("") +
            optionalState().map(f -> "state=" + f + ", ").orElse("") +
            optionalMessage().map(f -> "message=" + f + ", ").orElse("") +
            optionalGatewayId().map(f -> "gatewayId=" + f + ", ").orElse("") +
            optionalMode().map(f -> "mode=" + f + ", ").orElse("") +
            optionalFileName().map(f -> "fileName=" + f + ", ").orElse("") +
            optionalReconciliationId().map(f -> "reconciliationId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
