package de.scmb.scotty.gateway.openpayd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenPaydBeneficiaryPayout {

    private String id;
    private OpenPaydBeneficiaryPayoutAmount amount;
    private OpenPaydBeneficiaryPayoutBeneficiary beneficiary;
    private String accountId;
    private String paymentType;
    private String reference;
    private String externalCustomerId;
    private String reasonCode;
    private String purposeCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OpenPaydBeneficiaryPayoutAmount getAmount() {
        return amount;
    }

    public void setAmount(OpenPaydBeneficiaryPayoutAmount amount) {
        this.amount = amount;
    }

    public OpenPaydBeneficiaryPayoutBeneficiary getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(OpenPaydBeneficiaryPayoutBeneficiary beneficiary) {
        this.beneficiary = beneficiary;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public String getExternalCustomerId() {
        return externalCustomerId;
    }

    public void setExternalCustomerId(String externalCustomerId) {
        this.externalCustomerId = externalCustomerId;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getPurposeCode() {
        return purposeCode;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }
}
