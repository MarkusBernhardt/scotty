package de.scmb.scotty.gateway.novalnet;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class NovalnetResult {

    private String status;

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("status_text")
    private String statusText;

    public NovalnetResult() {}

    public NovalnetResult(String status, int statusCode, String statusText) {
        this.status = status;
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovalnetResult that = (NovalnetResult) o;
        return statusCode == that.statusCode && Objects.equals(status, that.status) && Objects.equals(statusText, that.statusText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, statusCode, statusText);
    }

    @Override
    public String toString() {
        return "NovalnetResult{" + "status='" + status + '\'' + ", statusCode=" + statusCode + ", statusText='" + statusText + '\'' + '}';
    }
}
