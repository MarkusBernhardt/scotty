package de.scmb.scotty.service.dto;

import java.util.Objects;

public class PaymentsUploadPaymentsExecuteResponseDTO {

    private int success;

    private String filename;

    private String message;

    public PaymentsUploadPaymentsExecuteResponseDTO(int success, String filename, String message) {
        this.success = success;
        this.filename = filename;
        this.message = message;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
        PaymentsUploadPaymentsExecuteResponseDTO that = (PaymentsUploadPaymentsExecuteResponseDTO) o;
        return success == that.success && Objects.equals(filename, that.filename) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, filename, message);
    }

    @Override
    public String toString() {
        return (
            "PaymentsUploadPaymentsExecuteResponseDTO{" +
            "success=" +
            success +
            ", filename='" +
            filename +
            '\'' +
            ", message='" +
            message +
            '\'' +
            '}'
        );
    }
}
