package com.fanap.podchat.model;

public class MetaDataImageFile {
    private SdkImageFile sdk;
    private String user;

    public SdkImageFile getSdk() {
        return sdk;
    }

    public void setSdk(SdkImageFile sdk) {
        this.sdk = sdk;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
