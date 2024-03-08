package de.scmb.scotty.service.dto;

import java.util.Objects;

public class PaymentsUploadPaymentsProgressResponseDTO {

    private int success;

    private int count;

    public PaymentsUploadPaymentsProgressResponseDTO(int success, int count) {
        this.success = success;
        this.count = count;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentsUploadPaymentsProgressResponseDTO that = (PaymentsUploadPaymentsProgressResponseDTO) o;
        return success == that.success && count == that.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, count);
    }

    @Override
    public String toString() {
        return "PaymentsUploadPaymentsProgressResponseDTO{" + "success=" + success + ", count=" + count + '}';
    }
}
