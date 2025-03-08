package de.scmb.scotty.gateway.openpayd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenPaydBeneficiaryPayoutBeneficiary {

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("metadata")
    private OpenPaydBeneficiaryPayoutMetadata metadata;

    @JsonProperty("bban")
    private String bban;

    @JsonProperty("bankAccountCountry")
    private String bankAccountCountry;

    @JsonProperty("bankAccountAlias")
    private String bankAccountAlias;

    @JsonProperty("companyName")
    private String companyName;

    @JsonProperty("routingCodes")
    private OpenPaydBeneficiaryPayoutRoutingCodes routingCodes;

    @JsonProperty("bankAccountCode")
    private String bankAccountCode;

    @JsonProperty("bankName")
    private String bankName;

    @JsonProperty("beneficiaryAddressLine")
    private String beneficiaryAddressLine;

    @JsonProperty("intermediaryName")
    private String intermediaryName;

    @JsonProperty("foundationDate")
    private String foundationDate;

    @JsonProperty("customerType")
    private String customerType;

    @JsonProperty("beneficiaryPostalCode")
    private String beneficiaryPostalCode;

    @JsonProperty("beneficiaryBirthDate")
    private String beneficiaryBirthDate;

    @JsonProperty("beneficiaryState")
    private String beneficiaryState;

    @JsonProperty("intermediaryPostalCode")
    private String intermediaryPostalCode;

    @JsonProperty("accountNumber")
    private String accountNumber;

    @JsonProperty("intermediaryBankAccountCountry")
    private String intermediaryBankAccountCountry;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("beneficiaryCity")
    private String beneficiaryCity;

    @JsonProperty("beneficiaryCountry")
    private String beneficiaryCountry;

    @JsonProperty("beneficiaryEmail")
    private String beneficiaryEmail;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("bankAccountType")
    private String bankAccountType;

    @JsonProperty("bankAccountHolderName")
    private String bankAccountHolderName;

    @JsonProperty("taxId")
    private String taxId;

    @JsonProperty("registrationNumber")
    private String registrationNumber;

    @JsonProperty("iban")
    private String iban;

    @JsonProperty("documentId")
    private String documentId;

    @JsonProperty("bic")
    private String bic;

    @JsonProperty("intermediaryTownName")
    private String intermediaryTownName;

    @JsonProperty("id")
    private String id;

    @JsonProperty("accountHolderId")
    private String accountHolderId;

    @JsonProperty("middleName")
    private String middleName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public OpenPaydBeneficiaryPayoutMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(OpenPaydBeneficiaryPayoutMetadata metadata) {
        this.metadata = metadata;
    }

    public String getBban() {
        return bban;
    }

    public void setBban(String bban) {
        this.bban = bban;
    }

    public String getBankAccountCountry() {
        return bankAccountCountry;
    }

    public void setBankAccountCountry(String bankAccountCountry) {
        this.bankAccountCountry = bankAccountCountry;
    }

    public String getBankAccountAlias() {
        return bankAccountAlias;
    }

    public void setBankAccountAlias(String bankAccountAlias) {
        this.bankAccountAlias = bankAccountAlias;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public OpenPaydBeneficiaryPayoutRoutingCodes getRoutingCodes() {
        return routingCodes;
    }

    public void setRoutingCodes(OpenPaydBeneficiaryPayoutRoutingCodes routingCodes) {
        this.routingCodes = routingCodes;
    }

    public String getBankAccountCode() {
        return bankAccountCode;
    }

    public void setBankAccountCode(String bankAccountCode) {
        this.bankAccountCode = bankAccountCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBeneficiaryAddressLine() {
        return beneficiaryAddressLine;
    }

    public void setBeneficiaryAddressLine(String beneficiaryAddressLine) {
        this.beneficiaryAddressLine = beneficiaryAddressLine;
    }

    public String getIntermediaryName() {
        return intermediaryName;
    }

    public void setIntermediaryName(String intermediaryName) {
        this.intermediaryName = intermediaryName;
    }

    public String getFoundationDate() {
        return foundationDate;
    }

    public void setFoundationDate(String foundationDate) {
        this.foundationDate = foundationDate;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getBeneficiaryPostalCode() {
        return beneficiaryPostalCode;
    }

    public void setBeneficiaryPostalCode(String beneficiaryPostalCode) {
        this.beneficiaryPostalCode = beneficiaryPostalCode;
    }

    public String getBeneficiaryBirthDate() {
        return beneficiaryBirthDate;
    }

    public void setBeneficiaryBirthDate(String beneficiaryBirthDate) {
        this.beneficiaryBirthDate = beneficiaryBirthDate;
    }

    public String getBeneficiaryState() {
        return beneficiaryState;
    }

    public void setBeneficiaryState(String beneficiaryState) {
        this.beneficiaryState = beneficiaryState;
    }

    public String getIntermediaryPostalCode() {
        return intermediaryPostalCode;
    }

    public void setIntermediaryPostalCode(String intermediaryPostalCode) {
        this.intermediaryPostalCode = intermediaryPostalCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIntermediaryBankAccountCountry() {
        return intermediaryBankAccountCountry;
    }

    public void setIntermediaryBankAccountCountry(String intermediaryBankAccountCountry) {
        this.intermediaryBankAccountCountry = intermediaryBankAccountCountry;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getBeneficiaryCity() {
        return beneficiaryCity;
    }

    public void setBeneficiaryCity(String beneficiaryCity) {
        this.beneficiaryCity = beneficiaryCity;
    }

    public String getBeneficiaryCountry() {
        return beneficiaryCountry;
    }

    public void setBeneficiaryCountry(String beneficiaryCountry) {
        this.beneficiaryCountry = beneficiaryCountry;
    }

    public String getBeneficiaryEmail() {
        return beneficiaryEmail;
    }

    public void setBeneficiaryEmail(String beneficiaryEmail) {
        this.beneficiaryEmail = beneficiaryEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(String bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public String getBankAccountHolderName() {
        return bankAccountHolderName;
    }

    public void setBankAccountHolderName(String bankAccountHolderName) {
        this.bankAccountHolderName = bankAccountHolderName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getIntermediaryTownName() {
        return intermediaryTownName;
    }

    public void setIntermediaryTownName(String intermediaryTownName) {
        this.intermediaryTownName = intermediaryTownName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountHolderId() {
        return accountHolderId;
    }

    public void setAccountHolderId(String accountHolderId) {
        this.accountHolderId = accountHolderId;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Override
    public String toString() {
        return (
            "OpenPaydBeneficiaryPayoutBeneficiary{" +
            "lastName='" +
            lastName +
            '\'' +
            ", metadata=" +
            metadata +
            ", bban='" +
            bban +
            '\'' +
            ", bankAccountCountry='" +
            bankAccountCountry +
            '\'' +
            ", bankAccountAlias='" +
            bankAccountAlias +
            '\'' +
            ", companyName='" +
            companyName +
            '\'' +
            ", routingCodes=" +
            routingCodes +
            ", bankAccountCode='" +
            bankAccountCode +
            '\'' +
            ", bankName='" +
            bankName +
            '\'' +
            ", beneficiaryAddressLine='" +
            beneficiaryAddressLine +
            '\'' +
            ", intermediaryName='" +
            intermediaryName +
            '\'' +
            ", foundationDate='" +
            foundationDate +
            '\'' +
            ", customerType='" +
            customerType +
            '\'' +
            ", beneficiaryPostalCode='" +
            beneficiaryPostalCode +
            '\'' +
            ", beneficiaryBirthDate='" +
            beneficiaryBirthDate +
            '\'' +
            ", beneficiaryState='" +
            beneficiaryState +
            '\'' +
            ", intermediaryPostalCode='" +
            intermediaryPostalCode +
            '\'' +
            ", accountNumber='" +
            accountNumber +
            '\'' +
            ", intermediaryBankAccountCountry='" +
            intermediaryBankAccountCountry +
            '\'' +
            ", firstName='" +
            firstName +
            '\'' +
            ", beneficiaryCity='" +
            beneficiaryCity +
            '\'' +
            ", beneficiaryCountry='" +
            beneficiaryCountry +
            '\'' +
            ", beneficiaryEmail='" +
            beneficiaryEmail +
            '\'' +
            ", phoneNumber='" +
            phoneNumber +
            '\'' +
            ", bankAccountType='" +
            bankAccountType +
            '\'' +
            ", bankAccountHolderName='" +
            bankAccountHolderName +
            '\'' +
            ", taxId='" +
            taxId +
            '\'' +
            ", registrationNumber='" +
            registrationNumber +
            '\'' +
            ", iban='" +
            iban +
            '\'' +
            ", documentId='" +
            documentId +
            '\'' +
            ", bic='" +
            bic +
            '\'' +
            ", intermediaryTownName='" +
            intermediaryTownName +
            '\'' +
            ", id='" +
            id +
            '\'' +
            ", accountHolderId='" +
            accountHolderId +
            '\'' +
            ", middleName='" +
            middleName +
            '\'' +
            '}'
        );
    }
}
