package com.fanap.podchat.model;

public class OutPutUserInfo {
    private boolean hasError;
    private String errorMessage;
    private long errorCode;
    private ResultUserInfo resultUserInfo;

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

    public ResultUserInfo getResultUserInfo() {
        return resultUserInfo;
    }

    public void setResultUserInfo(ResultUserInfo resultUserInfo) {
        this.resultUserInfo = resultUserInfo;
    }
}
