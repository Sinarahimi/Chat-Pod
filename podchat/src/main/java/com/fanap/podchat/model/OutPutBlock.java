package com.fanap.podchat.model;


public class OutPutBlock {
    private Boolean hasError;
    private String referenceNumber;
    private Integer errorCode;
    private String ott;
    private ResultBlock result;

    public Boolean getHasError() {
        return hasError;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getOtt() {
        return ott;
    }

    public void setOtt(String ott) {
        this.ott = ott;
    }

    public ResultBlock getResult() {
        return result;
    }

    public void setResult(ResultBlock result) {
        this.result = result;
    }
}
