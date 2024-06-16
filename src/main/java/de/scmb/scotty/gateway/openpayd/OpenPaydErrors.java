package de.scmb.scotty.gateway.openpayd;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenPaydErrors {

    private String errorCode;

    private List<OpenPaydError> errors;

    private String message;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public List<OpenPaydError> getErrors() {
        return errors;
    }

    public void setErrors(List<OpenPaydError> errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
