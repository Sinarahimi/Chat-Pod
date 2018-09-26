package com.fanap.podchat.model;

public class OutPutContact {
    private boolean hasError;
    private String errorMessage;
    private long errorCode;
    private ResultContact result;

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

    public ResultContact getResult() {
        return result;
    }

    public void setResult(ResultContact result) {
        this.result = result;
    }
}
