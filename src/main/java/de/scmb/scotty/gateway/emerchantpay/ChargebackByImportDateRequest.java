package de.scmb.scotty.gateway.emerchantpay;

import com.emerchantpay.gateway.api.Request;
import com.emerchantpay.gateway.api.RequestBuilder;
import com.emerchantpay.gateway.api.validation.GenesisValidator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChargebackByImportDateRequest extends Request {

    private String importDate;
    private Integer page;
    private HashMap<String, String> requiredParams = new HashMap();
    private GenesisValidator validator = new GenesisValidator();

    public ChargebackByImportDateRequest setImportDate(String importDate) {
        this.importDate = importDate;
        return this;
    }

    public ChargebackByImportDateRequest setPage(Integer page) {
        this.page = page;
        return this;
    }

    public String getTransactionType() {
        return "chargeback_by_date";
    }

    public String toXML() {
        return this.buildRequest("chargeback_request").toXML();
    }

    public String toQueryString(String root) {
        return this.buildRequest(root).toQueryString();
    }

    protected RequestBuilder buildRequest(String root) {
        this.requiredParams.put("import_date", this.importDate);
        this.validator.isValidRequest(this.requiredParams);
        return (new RequestBuilder(root)).addElement("import_date", this.importDate).addElement("page", this.page);
    }

    public List<Map.Entry<String, Object>> getElements() {
        return this.buildRequest("payment_transaction").getElements();
    }
}
