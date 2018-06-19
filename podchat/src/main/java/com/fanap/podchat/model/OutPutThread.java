package com.fanap.podchat.model;

public class OutPutThread {
    private boolean hasError;
    private String errorMessage;
    private long errorCode;
    private ResultThread result;
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

    public ResultThread getResult() {
        return result;
    }

    public void setResult(ResultThread result) {
        this.result = result;
    }

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }
}
