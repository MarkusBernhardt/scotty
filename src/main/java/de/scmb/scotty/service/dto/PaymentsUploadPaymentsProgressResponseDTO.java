package de.scmb.scotty.service.dto;

import java.util.Objects;

public class PaymentsUploadPaymentsProgressResponseDTO {

    private int success;

    private int count;

    private boolean stillRunning;

    public PaymentsUploadPaymentsProgressResponseDTO(int success, int count, boolean stillRunning) {
        this.success = success;
        this.count = count;
        this.stillRunning = stillRunning;
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

    public boolean isStillRunning() {
        return stillRunning;
    }

    public void setStillRunning(boolean stillRunning) {
        this.stillRunning = stillRunning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentsUploadPaymentsProgressResponseDTO that = (PaymentsUploadPaymentsProgressResponseDTO) o;
        return success == that.success && count == that.count && stillRunning == that.stillRunning;
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, count, stillRunning);
    }

    @Override
    public String toString() {
        return (
            "PaymentsUploadPaymentsProgressResponseDTO{" +
            "success=" +
            success +
            ", count=" +
            count +
            ", stillRunning=" +
            stillRunning +
            '}'
        );
    }
}
