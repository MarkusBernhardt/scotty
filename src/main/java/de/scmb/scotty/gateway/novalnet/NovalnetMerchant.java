package de.scmb.scotty.gateway.novalnet;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NovalnetMerchant {

    private String signature;

    private String tariff;

    public NovalnetMerchant() {}

    public NovalnetMerchant(String signature, String tariff) {
        this.signature = signature;
        this.tariff = tariff;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovalnetMerchant that = (NovalnetMerchant) o;
        return Objects.equals(signature, that.signature) && Objects.equals(tariff, that.tariff);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signature, tariff);
    }

    @Override
    public String toString() {
        return "NovalnetMerchant{" + "signature='" + signature + '\'' + ", tariff='" + tariff + '\'' + '}';
    }
}
