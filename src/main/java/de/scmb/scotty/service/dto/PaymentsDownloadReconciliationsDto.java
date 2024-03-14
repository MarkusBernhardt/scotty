package de.scmb.scotty.service.dto;

import java.util.Objects;

public class PaymentsDownloadReconciliationsDto {

    private String fileName;

    private Long count;

    private Long amount;

    public PaymentsDownloadReconciliationsDto(String fileName, Long count, Long amount) {
        this.fileName = fileName;
        this.count = count;
        this.amount = amount;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
        PaymentsDownloadReconciliationsDto that = (PaymentsDownloadReconciliationsDto) o;
        return Objects.equals(fileName, that.fileName) && Objects.equals(count, that.count) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, count, amount);
    }

    @Override
    public String toString() {
        return "PaymentsDownloadPaymentsDto{" + "fileName='" + fileName + '\'' + ", count=" + count + ", amount=" + amount + '}';
    }
}
