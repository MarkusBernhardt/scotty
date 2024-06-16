package de.scmb.scotty.gateway.openpayd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenPaydPayment {

    private String id;

    private String type = "RECURRING";

    private String paymentType = "SEPA_DD";

    private String accountId;

    private OpenPaydPaymentAmount amount;

    private OpenPaydPaymentDebtor debtor;

    private String dueDate;

    private String transactionReference;

    private String mandateDateOfSigning;

    private String mandateId;

    private String friendlyName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public OpenPaydPaymentAmount getAmount() {
        return amount;
    }

    public void setAmount(OpenPaydPaymentAmount amount) {
        this.amount = amount;
    }

    public OpenPaydPaymentDebtor getDebtor() {
        return debtor;
    }

    public void setDebtor(OpenPaydPaymentDebtor debtor) {
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
