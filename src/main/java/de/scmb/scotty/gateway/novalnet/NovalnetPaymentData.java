package de.scmb.scotty.gateway.novalnet;

import java.util.Objects;

public class NovalnetPaymentData {

    private String iban;

    private String bic;

    public NovalnetPaymentData() {}

    public NovalnetPaymentData(String iban, String bic) {
        this.iban = iban;
        this.bic = bic;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NovalnetPaymentData that = (NovalnetPaymentData) o;
        return Objects.equals(iban, that.iban) && Objects.equals(bic, that.bic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iban, bic);
    }

    @Override
    public String toString() {
        return "NovalnetPaymentData{" + "iban='" + iban + '\'' + ", bic='" + bic + '\'' + '}';
    }
}
