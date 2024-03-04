package de.scmb.scotty.service.dto;

import java.util.Objects;

public class PaymentsUploadPaymentsValidateResponseDTO {

    private boolean success;

    private int count;

    private int amount;

    public PaymentsUploadPaymentsValidateResponseDTO(boolean success, int count, int amount) {
        this.success = success;
        this.count = count;
        this.amount = amount;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentsUploadPaymentsValidateResponseDTO that = (PaymentsUploadPaymentsValidateResponseDTO) o;
        return success == that.success && count == that.count && amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, count, amount);
    }

    @Override
    public String toString() {
        return "PaymentsUploadPaymentsValidateResponseDTO{" + "success=" + success + ", count=" + count + ", amount=" + amount + '}';
    }
}
