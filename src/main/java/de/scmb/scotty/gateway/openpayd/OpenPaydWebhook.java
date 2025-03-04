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
    private OpenPaydSourceInfo sourceInfo;

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

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("fee")
    public OpenPaydAmount getFee() {
        return fee;
    }

    @JsonProperty("fee")
    public void setFee(OpenPaydAmount fee) {
        this.fee = fee;
    }

    @JsonProperty("tax")
    public Object getTax() {
        return tax;
    }

    @JsonProperty("tax")
    public void setTax(Object tax) {
        this.tax = tax;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("amount")
    public OpenPaydAmount getAmount() {
        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(OpenPaydAmount amount) {
        this.amount = amount;
    }

    @JsonProperty("fxRate")
    public Object getFxRate() {
        return fxRate;
    }

    @JsonProperty("fxRate")
    public void setFxRate(Object fxRate) {
        this.fxRate = fxRate;
    }

    @JsonProperty("source")
    public Object getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(Object source) {
        this.source = source;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("comment")
    public Object getComment() {
        return comment;
    }

    @JsonProperty("comment")
    public void setComment(Object comment) {
        this.comment = comment;
    }

    @JsonProperty("shortId")
    public String getShortId() {
        return shortId;
    }

    @JsonProperty("shortId")
    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    @JsonProperty("accountId")
    public Object getAccountId() {
        return accountId;
    }

    @JsonProperty("accountId")
    public void setAccountId(Object accountId) {
        this.accountId = accountId;
    }

    @JsonProperty("buyAmount")
    public Object getBuyAmount() {
        return buyAmount;
    }

    @JsonProperty("buyAmount")
    public void setBuyAmount(Object buyAmount) {
        this.buyAmount = buyAmount;
    }

    @JsonProperty("checkedBy")
    public Object getCheckedBy() {
        return checkedBy;
    }

    @JsonProperty("checkedBy")
    public void setCheckedBy(Object checkedBy) {
        this.checkedBy = checkedBy;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("fixedSide")
    public Object getFixedSide() {
        return fixedSide;
    }

    @JsonProperty("fixedSide")
    public void setFixedSide(Object fixedSide) {
        this.fixedSide = fixedSide;
    }

    @JsonProperty("sellAmount")
    public Object getSellAmount() {
        return sellAmount;
    }

    @JsonProperty("sellAmount")
    public void setSellAmount(Object sellAmount) {
        this.sellAmount = sellAmount;
    }

    @JsonProperty("sourceInfo")
    public OpenPaydSourceInfo getSourceInfo() {
        return sourceInfo;
    }

    @JsonProperty("sourceInfo")
    public void setSourceInfo(OpenPaydSourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    @JsonProperty("checkedDate")
    public Object getCheckedDate() {
        return checkedDate;
    }

    @JsonProperty("checkedDate")
    public void setCheckedDate(Object checkedDate) {
        this.checkedDate = checkedDate;
    }

    @JsonProperty("createdDate")
    public String getCreatedDate() {
        return createdDate;
    }

    @JsonProperty("createdDate")
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @JsonProperty("destination")
    public String getDestination() {
        return destination;
    }

    @JsonProperty("destination")
    public void setDestination(String destination) {
        this.destination = destination;
    }

    @JsonProperty("paymentDate")
    public Object getPaymentDate() {
        return paymentDate;
    }

    @JsonProperty("paymentDate")
    public void setPaymentDate(Object paymentDate) {
        this.paymentDate = paymentDate;
    }

    @JsonProperty("paymentType")
    public String getPaymentType() {
        return paymentType;
    }

    @JsonProperty("paymentType")
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @JsonProperty("totalAmount")
    public OpenPaydAmount getTotalAmount() {
        return totalAmount;
    }

    @JsonProperty("totalAmount")
    public void setTotalAmount(OpenPaydAmount totalAmount) {
        this.totalAmount = totalAmount;
    }

    @JsonProperty("updatedDate")
    public String getUpdatedDate() {
        return updatedDate;
    }

    @JsonProperty("updatedDate")
    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    @JsonProperty("feeReference")
    public Object getFeeReference() {
        return feeReference;
    }

    @JsonProperty("feeReference")
    public void setFeeReference(Object feeReference) {
        this.feeReference = feeReference;
    }

    @JsonProperty("refundReason")
    public String getRefundReason() {
        return refundReason;
    }

    @JsonProperty("refundReason")
    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    @JsonProperty("failureReason")
    public String getFailureReason() {
        return failureReason;
    }

    @JsonProperty("failureReason")
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    @JsonProperty("midMarketRate")
    public Object getMidMarketRate() {
        return midMarketRate;
    }

    @JsonProperty("midMarketRate")
    public void setMidMarketRate(Object midMarketRate) {
        this.midMarketRate = midMarketRate;
    }

    @JsonProperty("transactionId")
    public String getTransactionId() {
        return transactionId;
    }

    @JsonProperty("transactionId")
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @JsonProperty("runningBalance")
    public Object getRunningBalance() {
        return runningBalance;
    }

    @JsonProperty("runningBalance")
    public void setRunningBalance(Object runningBalance) {
        this.runningBalance = runningBalance;
    }

    @JsonProperty("accountHolderId")
    public String getAccountHolderId() {
        return accountHolderId;
    }

    @JsonProperty("accountHolderId")
    public void setAccountHolderId(String accountHolderId) {
        this.accountHolderId = accountHolderId;
    }

    @JsonProperty("destinationInfo")
    public OpenPaydDestinationInfo getDestinationInfo() {
        return destinationInfo;
    }

    @JsonProperty("destinationInfo")
    public void setDestinationInfo(OpenPaydDestinationInfo destinationInfo) {
        this.destinationInfo = destinationInfo;
    }

    @JsonProperty("referenceAmount")
    public Object getReferenceAmount() {
        return referenceAmount;
    }

    @JsonProperty("referenceAmount")
    public void setReferenceAmount(Object referenceAmount) {
        this.referenceAmount = referenceAmount;
    }

    @JsonProperty("complianceStatus")
    public String getComplianceStatus() {
        return complianceStatus;
    }

    @JsonProperty("complianceStatus")
    public void setComplianceStatus(String complianceStatus) {
        this.complianceStatus = complianceStatus;
    }

    @JsonProperty("endToEndReference")
    public String getEndToEndReference() {
        return endToEndReference;
    }

    @JsonProperty("endToEndReference")
    public void setEndToEndReference(String endToEndReference) {
        this.endToEndReference = endToEndReference;
    }

    @JsonProperty("transactionCategory")
    public String getTransactionCategory() {
        return transactionCategory;
    }

    @JsonProperty("transactionCategory")
    public void setTransactionCategory(String transactionCategory) {
        this.transactionCategory = transactionCategory;
    }

    @JsonProperty("transactionReference")
    public Object getTransactionReference() {
        return transactionReference;
    }

    @JsonProperty("transactionReference")
    public void setTransactionReference(Object transactionReference) {
        this.transactionReference = transactionReference;
    }

    @JsonProperty("originalTransactionId")
    public String getOriginalTransactionId() {
        return originalTransactionId;
    }

    @JsonProperty("originalTransactionId")
    public void setOriginalTransactionId(String originalTransactionId) {
        this.originalTransactionId = originalTransactionId;
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
