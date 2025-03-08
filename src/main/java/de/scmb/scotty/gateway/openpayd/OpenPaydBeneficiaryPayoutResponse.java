package de.scmb.scotty.gateway.openpayd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenPaydBeneficiaryPayoutResponse {

    @JsonProperty("shortId")
    private String shortId;

    @JsonProperty("accountHolderId")
    private String accountHolderId;

    @JsonProperty("externalCustomerId")
    private String externalCustomerId;

    @JsonProperty("amount")
    private OpenPaydBeneficiaryPayoutAmount amount;

    @JsonProperty("metadata")
    private OpenPaydBeneficiaryPayoutMetadata metadata;

    @JsonProperty("type")
    private String type;

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("paymentType")
    private String paymentType;

    @JsonProperty("reference")
    private String reference;

    @JsonProperty("destinationInfo")
    private OpenPaydBeneficiaryPayoutDestinationInfo destinationInfo;

    @JsonProperty("sourceInfo")
    private OpenPaydBeneficiaryPayoutSourceInfo sourceInfo;

    @JsonProperty("beneficiary")
    private OpenPaydBeneficiaryPayoutBeneficiary beneficiary;

    @JsonProperty("reasonCode")
    private String reasonCode;

    @JsonProperty("bulkPaymentId")
    private Object bulkPaymentId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("beneficiaryId")
    private String beneficiaryId;

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getAccountHolderId() {
        return accountHolderId;
    }

    public void setAccountHolderId(String accountHolderId) {
        this.accountHolderId = accountHolderId;
    }

    public String getExternalCustomerId() {
        return externalCustomerId;
    }

    public void setExternalCustomerId(String externalCustomerId) {
        this.externalCustomerId = externalCustomerId;
    }

    public OpenPaydBeneficiaryPayoutAmount getAmount() {
        return amount;
    }

    public void setAmount(OpenPaydBeneficiaryPayoutAmount amount) {
        this.amount = amount;
    }

    public OpenPaydBeneficiaryPayoutMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(OpenPaydBeneficiaryPayoutMetadata metadata) {
        this.metadata = metadata;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public OpenPaydBeneficiaryPayoutDestinationInfo getDestinationInfo() {
        return destinationInfo;
    }

    public void setDestinationInfo(OpenPaydBeneficiaryPayoutDestinationInfo destinationInfo) {
        this.destinationInfo = destinationInfo;
    }

    public OpenPaydBeneficiaryPayoutSourceInfo getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(OpenPaydBeneficiaryPayoutSourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public OpenPaydBeneficiaryPayoutBeneficiary getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(OpenPaydBeneficiaryPayoutBeneficiary beneficiary) {
        this.beneficiary = beneficiary;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public Object getBulkPaymentId() {
        return bulkPaymentId;
    }

    public void setBulkPaymentId(Object bulkPaymentId) {
        this.bulkPaymentId = bulkPaymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(String beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    @Override
    public String toString() {
        return (
            "OpenPaydBeneficiaryPayoutResponse{" +
            "shortId='" +
            shortId +
            '\'' +
            ", accountHolderId='" +
            accountHolderId +
            '\'' +
            ", externalCustomerId='" +
            externalCustomerId +
            '\'' +
            ", amount=" +
            amount +
            ", metadata=" +
            metadata +
            ", type='" +
            type +
            '\'' +
            ", transactionId='" +
            transactionId +
            '\'' +
            ", paymentType='" +
            paymentType +
            '\'' +
            ", reference='" +
            reference +
            '\'' +
            ", destinationInfo=" +
            destinationInfo +
            ", sourceInfo=" +
            sourceInfo +
            ", beneficiary=" +
            beneficiary +
            ", reasonCode='" +
            reasonCode +
            '\'' +
            ", bulkPaymentId=" +
            bulkPaymentId +
            ", status='" +
            status +
            '\'' +
            ", beneficiaryId='" +
            beneficiaryId +
            '\'' +
            '}'
        );
    }
}
