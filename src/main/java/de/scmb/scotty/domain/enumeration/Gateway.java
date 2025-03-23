package de.scmb.scotty.domain.enumeration;

/**
 * The Gateway enumeration.
 */
public enum Gateway {
    BANKINGCIRCLE("bankingcircle"),
    CCBILL("ccbill"),
    EMERCHANTPAY("emerchantpay"),
    NOVALNET("novalnet"),
    OPENPAYD("openpayd"),
    UNKNOWN("unknown");

    private final String value;

    Gateway(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
