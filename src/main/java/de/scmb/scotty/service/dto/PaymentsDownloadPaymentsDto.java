package de.scmb.scotty.service.dto;

import java.time.Instant;
import java.util.Objects;

public class PaymentsDownloadPaymentsDto {

    private String fileName;

    private Instant minTimestamp;

    private Instant maxTimestamp;

    private Long count;

    private Long amount;

    public PaymentsDownloadPaymentsDto(String fileName, Instant minTimestamp, Instant maxTimestamp, Long count, Long amount) {
        this.fileName = fileName;
        this.minTimestamp = minTimestamp;
        this.maxTimestamp = maxTimestamp;
        this.count = count;
        this.amount = amount;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Instant getMinTimestamp() {
        return minTimestamp;
    }

    public void setMinTimestamp(Instant minTimestamp) {
        this.minTimestamp = minTimestamp;
    }

    public Instant getMaxTimestamp() {
        return maxTimestamp;
    }

    public void setMaxTimestamp(Instant maxTimestamp) {
        this.maxTimestamp = maxTimestamp;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentsDownloadPaymentsDto that = (PaymentsDownloadPaymentsDto) o;
        return (
            count == that.count &&
            amount == that.amount &&
            Objects.equals(fileName, that.fileName) &&
            Objects.equals(minTimestamp, that.minTimestamp) &&
            Objects.equals(maxTimestamp, that.maxTimestamp)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, minTimestamp, maxTimestamp, count, amount);
    }

    @Override
    public String toString() {
        return (
            "PaymentsDownloadPaymentsDto{" +
            "fileName='" +
            fileName +
            '\'' +
            ", minTimestamp=" +
            minTimestamp +
            ", maxTimestamp=" +
            maxTimestamp +
            ", count=" +
            count +
            ", amount=" +
            amount +
            '}'
        );
    }
}
