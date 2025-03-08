package de.scmb.scotty.gateway.openpayd;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
    {
        "id",
        "fee",
        "tax",
        "type",
        "amount",
        "fxRate",
        "source",
        "status",
        "comment",
        "shortId",
        "accountId",
        "buyAmount",
        "checkedBy",
        "createdBy",
        "fixedSide",
        "sellAmount",
        "sourceInfo",
        "checkedDate",
        "createdDate",
        "destination",
        "paymentDate",
        "paymentType",
        "totalAmount",
        "updatedDate",
        "feeReference",
        "refundReason",
        "failureReason",
        "midMarketRate",
        "transactionId",
        "runningBalance",
        "accountHolderId",
        "destinationInfo",
        "referenceAmount",
        "complianceStatus",
        "endToEndReference",
        "transactionCategory",
        "transactionReference",
        "originalTransactionId",
    }
)
public class OpenPaydWebhook {

    @JsonProperty("id")
    private String id;

    @JsonProperty("fee")
    private OpenPaydAmount fee;

    @JsonProperty("tax")
    private Object tax;

    @JsonProperty("type")
    private String type;

    @JsonProperty("amount")
    private OpenPaydAmount amount;

    @JsonProperty("fxRate")
    private Object fxRate;

    @JsonProperty("source")
    private Object source;

    @JsonProperty("status")
    private String status;

    @JsonProperty("comment")
    private Object comment;

    @JsonProperty("shortId")
    private String shortId;

    @JsonProperty("accountId")
    private Object accountId;

    @JsonProperty("buyAmount")
    private Object buyAmount;

    @JsonProperty("checkedBy")
    private Object checkedBy;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("fixedSide")
    private Object fixedSide;

    @JsonProperty("sellAmount")
    private Object sellAmount;

    @JsonProperty("sourceInfo")
    private OpenPaydDestinationInfo sourceInfo;

    @JsonProperty("checkedDate")
    private Object checkedDate;

    @JsonProperty("createdDate")
    private String createdDate;

    @JsonProperty("destination")
    private String destination;

    @JsonProperty("paymentDate")
    private Object paymentDate;

    @JsonProperty("paymentType")
    private String paymentType;

    @JsonProperty("totalAmount")
    private OpenPaydAmount totalAmount;

    @JsonProperty("updatedDate")
    private String updatedDate;

    @JsonProperty("feeReference")
    private Object feeReference;

    @JsonProperty("refundReason")
    private String refundReason;

    @JsonProperty("failureReason")
    private String failureReason;

    @JsonProperty("midMarketRate")
    private Object midMarketRate;

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("runningBalance")
    private Object runningBalance;

    @JsonProperty("accountHolderId")
    private String accountHolderId;

    @JsonProperty("destinationInfo")
    private OpenPaydDestinationInfo destinationInfo;

    @JsonProperty("referenceAmount")
    private Object referenceAmount;

    @JsonProperty("complianceStatus")
    private String complianceStatus;

    @JsonProperty("endToEndReference")
    private String endToEndReference;

    @JsonProperty("transactionCategory")
    private String transactionCategory;

    @JsonProperty("transactionReference")
    private Object transactionReference;

    @JsonProperty("originalTransactionId")
    private String originalTransactionId;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("originalTransactionShortId")
    private String originalTransactionShortId;

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OpenPaydAmount getFee() {
        return fee;
    }

    public void setFee(OpenPaydAmount fee) {
        this.fee = fee;
    }

    public Object getTax() {
        return tax;
    }

    public void setTax(Object tax) {
        this.tax = tax;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OpenPaydAmount getAmount() {
        return amount;
    }

    public void setAmount(OpenPaydAmount amount) {
        this.amount = amount;
    }

    public Object getFxRate() {
        return fxRate;
    }

    public void setFxRate(Object fxRate) {
        this.fxRate = fxRate;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getComment() {
        return comment;
    }

    public void setComment(Object comment) {
        this.comment = comment;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public Object getAccountId() {
        return accountId;
    }

    public void setAccountId(Object accountId) {
        this.accountId = accountId;
    }

    public Object getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(Object buyAmount) {
        this.buyAmount = buyAmount;
    }

    public Object getCheckedBy() {
        return checkedBy;
    }

    public void setCheckedBy(Object checkedBy) {
        this.checkedBy = checkedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Object getFixedSide() {
        return fixedSide;
    }

    public void setFixedSide(Object fixedSide) {
        this.fixedSide = fixedSide;
    }

    public Object getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(Object sellAmount) {
        this.sellAmount = sellAmount;
    }

    public OpenPaydDestinationInfo getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(OpenPaydDestinationInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public Object getCheckedDate() {
        return checkedDate;
    }

    public void setCheckedDate(Object checkedDate) {
        this.checkedDate = checkedDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Object getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Object paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public OpenPaydAmount getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(OpenPaydAmount totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Object getFeeReference() {
        return feeReference;
    }

    public void setFeeReference(Object feeReference) {
        this.feeReference = feeReference;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Object getMidMarketRate() {
        return midMarketRate;
    }

    public void setMidMarketRate(Object midMarketRate) {
        this.midMarketRate = midMarketRate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Object getRunningBalance() {
        return runningBalance;
    }

    public void setRunningBalance(Object runningBalance) {
        this.runningBalance = runningBalance;
    }

    public String getAccountHolderId() {
        return accountHolderId;
    }

    public void setAccountHolderId(String accountHolderId) {
        this.accountHolderId = accountHolderId;
    }

    public OpenPaydDestinationInfo getDestinationInfo() {
        return destinationInfo;
    }

    public void setDestinationInfo(OpenPaydDestinationInfo destinationInfo) {
        this.destinationInfo = destinationInfo;
    }

    public Object getReferenceAmount() {
        return referenceAmount;
    }

    public void setReferenceAmount(Object referenceAmount) {
        this.referenceAmount = referenceAmount;
    }

    public String getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(String complianceStatus) {
        this.complianceStatus = complianceStatus;
    }

    public String getEndToEndReference() {
        return endToEndReference;
    }

    public void setEndToEndReference(String endToEndReference) {
        this.endToEndReference = endToEndReference;
    }

    public String getTransactionCategory() {
        return transactionCategory;
    }

    public void setTransactionCategory(String transactionCategory) {
        this.transactionCategory = transactionCategory;
    }

    public Object getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(Object transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getOriginalTransactionId() {
        return originalTransactionId;
    }

    public void setOriginalTransactionId(String originalTransactionId) {
        this.originalTransactionId = originalTransactionId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOriginalTransactionShortId() {
        return originalTransactionShortId;
    }

    public void setOriginalTransactionShortId(String originalTransactionShortId) {
        this.originalTransactionShortId = originalTransactionShortId;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
