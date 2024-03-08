package de.scmb.scotty.domain.enumeration;

/**
 * The Gateway enumeration.
 */
public enum Gateway {
    EMERCHANTPAY("emerchantpay"),
    CCBILL("ccbill"),
    UNKNOWN("unknown");

    private final String value;

    Gateway(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
