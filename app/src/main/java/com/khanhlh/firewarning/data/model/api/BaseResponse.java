package com.khanhlh.firewarning.data.model.api;

import java.io.Serializable;

public class BaseResponse implements Serializable {
    private String errorCode;
    private String result;
    private String message;

    public BaseResponse() {
    }

    public BaseResponse(String errorCode, String result, String message) {
        this.errorCode = errorCode;
        this.result = result;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
