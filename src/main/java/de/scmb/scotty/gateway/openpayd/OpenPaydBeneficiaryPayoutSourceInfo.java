package de.scmb.scotty.gateway.openpayd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenPaydBeneficiaryPayoutSourceInfo {

    @JsonProperty("identifier")
    private String identifier;

    @JsonProperty("type")
    private String type;

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "OpenPaydBeneficiaryPayoutSourceInfo{" + "identifier='" + identifier + '\'' + ", type='" + type + '\'' + '}';
    }
}
