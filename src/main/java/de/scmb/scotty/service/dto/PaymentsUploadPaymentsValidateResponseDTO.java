package de.scmb.scotty.service.dto;

import java.util.Objects;

public class PaymentsUploadPaymentsValidateResponseDTO {

    private boolean success;

    private int count;

    private double amount;

    private String message;

    public PaymentsUploadPaymentsValidateResponseDTO(boolean success, int count, double amount, String message) {
        this.success = success;
        this.count = count;
        this.amount = amount;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentsUploadPaymentsValidateResponseDTO that = (PaymentsUploadPaymentsValidateResponseDTO) o;
        return success == that.success && count == that.count && amount == that.amount && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, count, amount, message);
    }

    @Override
    public String toString() {
        return (
            "PaymentsUploadPaymentsValidateResponseDTO{" +
            "success=" +
            success +
            ", count=" +
            count +
            ", amount=" +
            amount +
            ", message='" +
            message +
            '\'' +
            '}'
        );
    }
}
