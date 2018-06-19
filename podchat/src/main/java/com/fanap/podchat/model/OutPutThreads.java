package com.fanap.podchat.model;

public class OutPutThreads {
    private boolean hasError;
    private String errorMessage;
    private long errorCode;
    private ResultThreads result;
    private long contentCount;

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

    public ResultThreads getResult() {
        return result;
    }

    public void setResult(ResultThreads result) {
        this.result = result;
    }

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }
}
