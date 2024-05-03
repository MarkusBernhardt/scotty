package de.scmb.scotty.gateway.novalnet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NovalnetMerchant {

    private String signature;

    private String tariff;

    private String project;

    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("project_url")
    private String projectUrl;

    private String vendor;

    public NovalnetMerchant() {}

    public NovalnetMerchant(String signature, String tariff, String project, String projectName, String projectUrl, String vendor) {
        this.signature = signature;
        this.tariff = tariff;
        this.project = project;
        this.projectName = projectName;
        this.projectUrl = projectUrl;
        this.vendor = vendor;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovalnetMerchant that = (NovalnetMerchant) o;
        return (
            Objects.equals(signature, that.signature) &&
            Objects.equals(tariff, that.tariff) &&
            Objects.equals(project, that.project) &&
            Objects.equals(projectName, that.projectName) &&
            Objects.equals(projectUrl, that.projectUrl) &&
            Objects.equals(vendor, that.vendor)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(signature, tariff, project, projectName, projectUrl, vendor);
    }

    @Override
    public String toString() {
        return (
            "NovalnetMerchant{" +
            "signature='" +
            signature +
            '\'' +
            ", tariff='" +
            tariff +
            '\'' +
            ", project='" +
            project +
            '\'' +
            ", projectName='" +
            projectName +
            '\'' +
            ", projectUrl='" +
            projectUrl +
            '\'' +
            ", vendor='" +
            vendor +
            '\'' +
            '}'
        );
    }
}
