package com.fanap.podchat.model;

public class OutPutParticipant {
    private boolean hasError;
    private String errorMessage;
    private long errorCode;
    private boolean hasNext;
    private long nextOffset;
    private ResultParticipant result;

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


    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public long getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(long nextOffset) {
        this.nextOffset = nextOffset;
    }

    public ResultParticipant getResult() {
        return result;
    }

    public void setResult(ResultParticipant result) {
        this.result = result;
    }
}
