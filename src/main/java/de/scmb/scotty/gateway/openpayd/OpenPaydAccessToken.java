package de.scmb.scotty.gateway.openpayd;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenPaydAccessToken {

    private Instant createdAt = Instant.now();

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private String expiresIn;

    private String scope;

    private String accountHolderId;

    private String clientType;

    private String parentAccountHolderId;

    private String clientId;

    private String referralId;

    private String accountHolderStatus;

    private String tenantId;

    private String clientTenantId;

    private JsonNode accountHolders;

    private List<String> authorities;

    private String jti;

    private String accountHolderType;

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAccountHolderId() {
        return accountHolderId;
    }

    public void setAccountHolderId(String accountHolderId) {
        this.accountHolderId = accountHolderId;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getParentAccountHolderId() {
        return parentAccountHolderId;
    }

    public void setParentAccountHolderId(String parentAccountHolderId) {
        this.parentAccountHolderId = parentAccountHolderId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public String getAccountHolderStatus() {
        return accountHolderStatus;
    }

    public void setAccountHolderStatus(String accountHolderStatus) {
        this.accountHolderStatus = accountHolderStatus;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getClientTenantId() {
        return clientTenantId;
    }

    public void setClientTenantId(String clientTenantId) {
        this.clientTenantId = clientTenantId;
    }

    public JsonNode getAccountHolders() {
        return accountHolders;
    }

    public void setAccountHolders(JsonNode accountHolders) {
        this.accountHolders = accountHolders;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getAccountHolderType() {
        return accountHolderType;
    }

    public void setAccountHolderType(String accountHolderType) {
        this.accountHolderType = accountHolderType;
    }
}
