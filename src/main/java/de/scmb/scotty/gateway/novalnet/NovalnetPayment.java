package de.scmb.scotty.gateway.novalnet;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NovalnetPayment {

    private NovalnetMerchant merchant;

    private NovalnetCustomer customer;

    private NovalnetEvent event;

    private NovalnetResult result;

    private NovalnetTransaction transaction;

    public NovalnetPayment() {}

    public NovalnetPayment(
        NovalnetMerchant merchant,
        NovalnetCustomer customer,
        NovalnetEvent event,
        NovalnetResult result,
        NovalnetTransaction transaction
    ) {
        this.merchant = merchant;
        this.customer = customer;
        this.event = event;
        this.result = result;
        this.transaction = transaction;
    }

    public NovalnetMerchant getMerchant() {
        return merchant;
    }

    public void setMerchant(NovalnetMerchant merchant) {
        this.merchant = merchant;
    }

    public NovalnetCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(NovalnetCustomer customer) {
        this.customer = customer;
    }

    public NovalnetEvent getEvent() {
        return event;
    }

    public void setEvent(NovalnetEvent event) {
        this.event = event;
    }

    public NovalnetResult getResult() {
        return result;
    }

    public void setResult(NovalnetResult result) {
        this.result = result;
    }

    public NovalnetTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(NovalnetTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovalnetPayment that = (NovalnetPayment) o;
        return (
            Objects.equals(merchant, that.merchant) &&
            Objects.equals(customer, that.customer) &&
            Objects.equals(event, that.event) &&
            Objects.equals(result, that.result) &&
            Objects.equals(transaction, that.transaction)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(merchant, customer, event, result, transaction);
    }

    @Override
    public String toString() {
        return (
            "NovalnetPayment{" +
            "merchant=" +
            merchant +
            ", customer=" +
            customer +
            ", event=" +
            event +
            ", result=" +
            result +
            ", transaction=" +
            transaction +
            '}'
        );
    }
}
