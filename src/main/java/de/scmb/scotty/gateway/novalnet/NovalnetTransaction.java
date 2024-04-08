package de.scmb.scotty.gateway.novalnet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NovalnetTransaction {

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("test_mode")
    private String testMode;

    private String amount;

    private String currency;

    @JsonProperty("invoice_ref")
    private String invoiceRef;

    @JsonProperty("mandate_ref")
    private String mandateRef;

    @JsonProperty("mandate_date")
    private String mandateDate;

    @JsonProperty("payment_data")
    private NovalnetPaymentData paymentData;

    @JsonProperty("debit_reason_1")
    private String debitReason1;

    @JsonProperty("debit_reason_2")
    private String debitReason2;

    @JsonProperty("debit_reason_3")
    private String debitReason3;

    @JsonProperty("debit_reason_4")
    private String debitReason4;

    @JsonProperty("debit_reason_5")
    private String debitReason5;

    public NovalnetTransaction() {}

    public NovalnetTransaction(
        String paymentType,
        String testMode,
        String amount,
        String currency,
        String invoiceRef,
        String mandateRef,
        String mandateDate,
        NovalnetPaymentData paymentData,
        String debitReason1,
        String debitReason2,
        String debitReason3,
        String debitReason4,
        String debitReason5
    ) {
        this.paymentType = paymentType;
        this.testMode = testMode;
        this.amount = amount;
        this.currency = currency;
        this.invoiceRef = invoiceRef;
        this.mandateRef = mandateRef;
        this.mandateDate = mandateDate;
        this.paymentData = paymentData;
        this.debitReason1 = debitReason1;
        this.debitReason2 = debitReason2;
        this.debitReason3 = debitReason3;
        this.debitReason4 = debitReason4;
        this.debitReason5 = debitReason5;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getTestMode() {
        return testMode;
    }

    public void setTestMode(String testMode) {
        this.testMode = testMode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getInvoiceRef() {
        return invoiceRef;
    }

    public void setInvoiceRef(String invoiceRef) {
        this.invoiceRef = invoiceRef;
    }

    public String getMandateRef() {
        return mandateRef;
    }

    public void setMandateRef(String mandateRef) {
        this.mandateRef = mandateRef;
    }

    public String getMandateDate() {
        return mandateDate;
    }

    public void setMandateDate(String mandateDate) {
        this.mandateDate = mandateDate;
    }

    public NovalnetPaymentData getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(NovalnetPaymentData paymentData) {
        this.paymentData = paymentData;
    }

    public String getDebitReason1() {
        return debitReason1;
    }

    public void setDebitReason1(String debitReason1) {
        this.debitReason1 = debitReason1;
    }

    public String getDebitReason2() {
        return debitReason2;
    }

    public void setDebitReason2(String debitReason2) {
        this.debitReason2 = debitReason2;
    }

    public String getDebitReason3() {
        return debitReason3;
    }

    public void setDebitReason3(String debitReason3) {
        this.debitReason3 = debitReason3;
    }

    public String getDebitReason4() {
        return debitReason4;
    }

    public void setDebitReason4(String debitReason4) {
        this.debitReason4 = debitReason4;
    }

    public String getDebitReason5() {
        return debitReason5;
    }

    public void setDebitReason5(String debitReason5) {
        this.debitReason5 = debitReason5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovalnetTransaction that = (NovalnetTransaction) o;
        return (
            Objects.equals(testMode, that.testMode) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(currency, that.currency) &&
            Objects.equals(invoiceRef, that.invoiceRef) &&
            Objects.equals(mandateRef, that.mandateRef) &&
            Objects.equals(mandateDate, that.mandateDate) &&
            Objects.equals(paymentData, that.paymentData) &&
            Objects.equals(debitReason1, that.debitReason1) &&
            Objects.equals(debitReason2, that.debitReason2) &&
            Objects.equals(debitReason3, that.debitReason3) &&
            Objects.equals(debitReason4, that.debitReason4) &&
            Objects.equals(debitReason5, that.debitReason5)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            testMode,
            amount,
            currency,
            invoiceRef,
            mandateRef,
            mandateDate,
            paymentData,
            debitReason1,
            debitReason2,
            debitReason3,
            debitReason4,
            debitReason5
        );
    }

    @Override
    public String toString() {
        return (
            "NovalnetTransaction{" +
            "testMode='" +
            testMode +
            '\'' +
            ", amount='" +
            amount +
            '\'' +
            ", currency='" +
            currency +
            '\'' +
            ", invoiceRef='" +
            invoiceRef +
            '\'' +
            ", mandateRef='" +
            mandateRef +
            '\'' +
            ", mandateDate='" +
            mandateDate +
            '\'' +
            ", paymentData=" +
            paymentData +
            ", debitReason1='" +
            debitReason1 +
            '\'' +
            ", debitReason2='" +
            debitReason2 +
            '\'' +
            ", debitReason3='" +
            debitReason3 +
            '\'' +
            ", debitReason4='" +
            debitReason4 +
            '\'' +
            ", debitReason5='" +
            debitReason5 +
            '\'' +
            '}'
        );
    }
}
