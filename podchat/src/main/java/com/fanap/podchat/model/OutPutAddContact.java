package com.fanap.podchat.model;

public class OutPutAddContact {
    private boolean hasError;
    private String errorMessage;
    private long errorCode;
    private ResultAddContact result;

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(long errorCode) {
        this.errorCode = errorCode;
    }

    public ResultAddContact getResult() {
        return result;
    }

    public void setResult(ResultAddContact result) {
        this.result = result;
    }
}
