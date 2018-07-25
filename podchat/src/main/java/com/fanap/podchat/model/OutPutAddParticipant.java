package com.fanap.podchat.model;

public class OutPutAddParticipant {
    private boolean hasError;
    private String errorMessage;
    private long errorCode;
    private ResultAddParticipant result;

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

    public ResultAddParticipant getAddParticipant() {
        return result;
    }

    public void setAddParticipant(ResultAddParticipant addParticipant) {
        this.result = addParticipant;
    }
}
