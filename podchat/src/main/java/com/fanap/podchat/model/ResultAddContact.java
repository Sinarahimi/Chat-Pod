package com.fanap.podchat.model;

import com.fanap.podchat.mainmodel.Contact;

public class ResultAddContact {

    private Contact Contact;
    private long contentCount;

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }

    public com.fanap.podchat.mainmodel.Contact getContact() {
        return Contact;
    }

    public void setContact(com.fanap.podchat.mainmodel.Contact contact) {
        Contact = contact;
    }
}
