package com.fanap.podchat.model;

import java.util.ArrayList;

public class OutPut {
    private boolean hasError;
    private String errorMessage;
    private long errorCode;
    private ArrayList<Results> result;
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

    public ArrayList<Results> getResult() {
        return result;
    }

    public void setResult(ArrayList<Results> result) {
        this.result = result;
    }

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }
}
