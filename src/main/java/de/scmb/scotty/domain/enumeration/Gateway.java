package de.scmb.scotty.domain.enumeration;

/**
 * The Gateway enumeration.
 */
public enum Gateway {
    CCBILL("ccbill"),
    EMERCHANTPAY("emerchantpay"),
    NOVALNET("novalnet"),
    UNKNOWN("unknown");

    private final String value;

    Gateway(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
