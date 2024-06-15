package de.scmb.scotty.gateway.openpayd;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenPaydDirectDebitRequest {

    private String type = "RECURRING";

    private String paymentType = "SEPA_DD";

    private String accountId;

    private OpenPaydDirectDebitRequestAmount amount;

    private OpenPaydDirectDebitRequestDebtor debtor;

    private String dueDate;

    private String transactionReference;

    private String mandateDateOfSigning;

    private String mandateId;

    private String friendlyName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public OpenPaydDirectDebitRequestAmount getAmount() {
        return amount;
    }

    public void setAmount(OpenPaydDirectDebitRequestAmount amount) {
        this.amount = amount;
    }

    public OpenPaydDirectDebitRequestDebtor getDebtor() {
        return debtor;
    }

    public void setDebtor(OpenPaydDirectDebitRequestDebtor debtor) {
        this.debtor = debtor;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getMandateDateOfSigning() {
        return mandateDateOfSigning;
    }

    public void setMandateDateOfSigning(String mandateDateOfSigning) {
        this.mandateDateOfSigning = mandateDateOfSigning;
    }

    public String getMandateId() {
        return mandateId;
    }

    public void setMandateId(String mandateId) {
        this.mandateId = mandateId;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
