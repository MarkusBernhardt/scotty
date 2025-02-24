package de.scmb.scotty.gateway.openpayd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenPaydBeneficiaryPayoutResponse {

    public String transactionId;
    public String shortId;
    public String externalCustomerId;
    public String reference;
    public String paymentType;
    public String reasonCode;
    public Amount amount;
    public Beneficiary beneficiary;
    public Object bulkPaymentId;
    public Metadata metadata;
    public String status;
    public Object beneficiaryId;
    public SourceInfo sourceInfo;
    public DestinationInfo destinationInfo;
    public String accountHolderId;
    public String type;

    public static class RoutingCodes {}

    public static class SourceInfo {

        public String type;
        public String identifier;
    }

    public static class Amount {

        public double value;
        public String currency;
    }

    public static class Beneficiary {

        public String customerType;
        public String firstName;
        public String lastName;
        public Object companyName;
        public Object beneficiaryCountry;
        public Object beneficiaryPostalCode;
        public Object beneficiaryCity;
        public Object beneficiaryState;
        public Object beneficiaryAddressLine;
        public Object beneficiaryBirthDate;
        public String bankAccountCountry;
        public Object bankName;
        public Object taxId;
        public Object phoneNumber;
        public Object bankAccountType;
        public String bankAccountHolderName;
        public Object bic;
        public String iban;
        public Object accountNumber;
        public Object documentId;
        public Object bankAccountAlias;
        public Object intermediaryBankAccountCountry;
        public Object intermediaryName;
        public Object intermediaryTownName;
        public Object intermediaryPostalCode;
        public Object bban;
        public RoutingCodes routingCodes;
        public Metadata metadata;
    }

    public static class DestinationInfo {

        public String type;
        public String identifier;
    }

    public static class Metadata {}
}
