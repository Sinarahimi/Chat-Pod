package com.fanap.podchat.util;

import android.support.annotation.NonNull;

import com.fanap.podchat.model.Contacts;
import com.fanap.podchat.model.OutPutAddContact;
import com.fanap.podchat.model.ResultAddContact;

public class Util {

    @NonNull
    public static OutPutAddContact getReformatOutPutAddContact(Contacts contacts) {
        OutPutAddContact outPutAddContact = new OutPutAddContact();
        outPutAddContact.setContentCount(1);
        outPutAddContact.setErrorCode(0);
        outPutAddContact.setErrorMessage("");
        outPutAddContact.setHasError(false);

        ResultAddContact resultAddContact = new ResultAddContact();
        resultAddContact.setCellphoneNumber(contacts.getResult().get(0).getCellphoneNumber());
        resultAddContact.setEmail(contacts.getResult().get(0).getEmail());
        resultAddContact.setFirstName(contacts.getResult().get(0).getFirstName());
        resultAddContact.setId(contacts.getResult().get(0).getId());
        resultAddContact.setLastName(contacts.getResult().get(0).getLastName());
        resultAddContact.setUniqueId(contacts.getResult().get(0).getUniqueId());
        outPutAddContact.setResult(resultAddContact);
        return outPutAddContact;
    }
}
