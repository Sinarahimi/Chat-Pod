package com.fanap.podchat.persistance;

import android.content.Context;

import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.persistance.dao.MessageDao;

import java.util.List;

public class MessageDatabaseHelper extends BaseDatabaseHelper {

    public MessageDao messageDao;

    public MessageDatabaseHelper(Context context) {
        super(context);
        messageDao = appDatabase.getMessageDao();
    }

    public List<Contact> getContacts() {
        return messageDao.getContact();
    }

    public void save(List<Contact> contacts) {
        messageDao.insertContact(contacts);
    }

    public List<Thread> getThreads() {
        List<Thread> threads = messageDao.getThreads();
        for (Thread thread : threads) {
            thread.setInviter(messageDao.getInviter(thread.getInviterId()));
            thread.setLastMessageVO(messageDao.getLastMessageVO(thread.getLastMessageVOId()));
        }
        return threads;
    }

    public void saveThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            if (thread.getInviter() != null) {
                messageDao.insertInviter(thread.getInviter());
            }
            if (thread.getLastMessageVO() != null) {
                messageDao.insertLastMessageVO(thread.getLastMessageVO());
            }
            if (thread.getLastMessageVO().getParticipant() != null) {
                messageDao.insertParticipant(thread.getLastMessageVO().getParticipant());
            }
            if (thread.getLastMessageVO().getReplyInfoVO().getParticipant() != null) {
                messageDao.insertParticipant(thread.getLastMessageVO().getReplyInfoVO().getParticipant());
            }

            if (thread.getLastMessageVO().getForwardInfo().getParticipant() != null) {
                messageDao.insertParticipant(thread.getLastMessageVO().getForwardInfo().getParticipant());
            }
            if (thread.getLastMessageVO().getForwardInfo() != null) {
                messageDao.insertForwardInfo(thread.getLastMessageVO().getForwardInfo());
            }
            if (thread.getLastMessageVO().getReplyInfoVO() != null) {
                messageDao.insertReplyInfoVO(thread.getLastMessageVO().getReplyInfoVO());
            }

        }
        messageDao.insertThreads(threads);
    }
}
