package de.scmb.scotty.service.dto;

import java.util.Objects;

public class PaymentsUploadPaymentsExecuteResponseDTO {

    private boolean stillRunning;

    public PaymentsUploadPaymentsExecuteResponseDTO(boolean stillRunning) {
        this.stillRunning = stillRunning;
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
        PaymentsUploadPaymentsExecuteResponseDTO that = (PaymentsUploadPaymentsExecuteResponseDTO) o;
        return stillRunning == that.stillRunning;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stillRunning);
    }

    @Override
    public String toString() {
        return "PaymentsUploadPaymentsExecuteResponseDTO{" + "stillRunning=" + stillRunning + '}';
    }
}
