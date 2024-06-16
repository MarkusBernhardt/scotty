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
@JsonPropertyOrder({ "type", "identifier", "accountType", "accountHolderId", "identifierString", "internalAccountId" })
public class OpenPaydDestinationInfo {

    @JsonProperty("type")
    private String type;

    @JsonProperty("identifier")
    private String identifier;

    @JsonProperty("accountType")
    private String accountType;

    @JsonProperty("accountHolderId")
    private String accountHolderId;

    @JsonProperty("identifierString")
    private String identifierString;

    @JsonProperty("internalAccountId")
    private String internalAccountId;

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("identifier")
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty("identifier")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @JsonProperty("accountType")
    public String getAccountType() {
        return accountType;
    }

    @JsonProperty("accountType")
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @JsonProperty("accountHolderId")
    public String getAccountHolderId() {
        return accountHolderId;
    }

    @JsonProperty("accountHolderId")
    public void setAccountHolderId(String accountHolderId) {
        this.accountHolderId = accountHolderId;
    }

    @JsonProperty("identifierString")
    public String getIdentifierString() {
        return identifierString;
    }

    @JsonProperty("identifierString")
    public void setIdentifierString(String identifierString) {
        this.identifierString = identifierString;
    }

    @JsonProperty("internalAccountId")
    public String getInternalAccountId() {
        return internalAccountId;
    }

    @JsonProperty("internalAccountId")
    public void setInternalAccountId(String internalAccountId) {
        this.internalAccountId = internalAccountId;
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
