package com.fanap.podchat.persistance;

import android.content.Context;

import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.ThreadVo;
import com.fanap.podchat.model.FileMetaDataContent;
import com.fanap.podchat.model.MessageVO;
import com.fanap.podchat.persistance.dao.MessageDao;

import java.util.ArrayList;
import java.util.List;

public class MessageDatabaseHelper extends BaseDatabaseHelper {

    public MessageDao messageDao;

    public MessageDatabaseHelper(Context context) {
        super(context);
        messageDao = appDatabase.getMessageDao();
    }

    public List<MessageVO> getHistories() {
        List<MessageVO> messageVOS = messageDao.getHistories();
        for (MessageVO messageVO : messageVOS) {
            if (messageVO.getThreadVoId() != null) {
                messageVO.setConversation(messageDao.getThread(messageVO.getThreadVoId()));
            }
            if (messageVO.getForwardInfoId() != null) {
                messageVO.setForwardInfo(messageDao.getForwardInfo(messageVO.getForwardInfoId()));
            }
            if (messageVO.getParticipantId() != null) {
                messageVO.setParticipant(messageDao.getParticipant(messageVO.getParticipantId()));
            }
            if (messageVO.getReplyInfoVOId() != null) {
                messageVO.setReplyInfoVO(messageDao.getReplyInfo(messageVO.getReplyInfoVOId()));
            }
        }

        return messageDao.getHistories();
    }

    public void saveHistory(List<MessageVO> messageVOS) {
        for (MessageVO messageVO : messageVOS) {
            if (messageVO.getParticipant() != null) {
                messageVO.setParticipantId(messageVO.getParticipant().getId());
                messageDao.insertParticipant(messageVO.getParticipant());
            }

            if (messageVO.getConversation() != null) {
                messageVO.setThreadVoId(messageVO.getConversation().getId());
                messageDao.insertThread(messageVO.getConversation());
            }

            if (messageVO.getForwardInfo() != null) {
                messageVO.setForwardInfoId(messageVO.getForwardInfo().getId());
                messageDao.insertForwardInfo(messageVO.getForwardInfo());
                if (messageVO.getForwardInfo().getParticipant() != null) {
                    messageVO.getForwardInfo().setParticipantId(messageVO.getForwardInfo().getParticipant().getId());
                    messageDao.insertParticipant(messageVO.getForwardInfo().getParticipant());
                }
            }

            if (messageVO.getReplyInfoVO() != null) {
                messageVO.setReplyInfoVOId(messageVO.getReplyInfoVO().getId());
                messageDao.insertReplyInfoVO(messageVO.getReplyInfoVO());
                if (messageVO.getReplyInfoVO().getParticipant() != null) {
                    messageVO.getReplyInfoVO().setParticipantId(messageVO.getReplyInfoVO().getParticipant().getId());
                    messageDao.insertParticipant(messageVO.getReplyInfoVO().getParticipant());
                }
            }
        }
    }

    public List<Contact> getContacts() {
        return messageDao.getContacts();
    }

    public void save(List<Contact> contacts) {
        messageDao.insertContact(contacts);
    }

    public List<ThreadVo> getThreads() {
        List<ThreadVo> threadVos = messageDao.getThreads();
        for (ThreadVo threadVo : threadVos) {
            if (threadVo.getInviterId() != null) {
                threadVo.setInviter(messageDao.getInviter(threadVo.getInviterId()));
            }
            if (threadVo.getLastMessageVOId() != null) {
                threadVo.setLastMessageVO(messageDao.getLastMessageVO(threadVo.getLastMessageVOId()));
            }
        }
        return threadVos;
    }

    public void saveThreads(List<ThreadVo> threadVos) {
        for (ThreadVo threadVo : threadVos) {
            if (threadVo.getInviter() != null) {
                threadVo.setInviterId(threadVo.getInviter().getId());
                messageDao.insertInviter(threadVo.getInviter());
            }
            if (threadVo.getLastMessageVO() != null) {
                threadVo.setLastMessageVOId(threadVo.getLastMessageVO().getId());
                messageDao.insertLastMessageVO(threadVo.getLastMessageVO());
            }
            if (threadVo.getLastMessageVO().getParticipant() != null) {
                threadVo.getLastMessageVO().setParticipantId(threadVo.getLastMessageVO().getParticipant().getId());
                messageDao.insertParticipant(threadVo.getLastMessageVO().getParticipant());
            }
            if (threadVo.getLastMessageVO().getReplyInfoVO() != null) {
                threadVo.getLastMessageVO().setReplyInfoVOId(threadVo.getLastMessageVO().getReplyInfoVO().getId());
                messageDao.insertReplyInfoVO(threadVo.getLastMessageVO().getReplyInfoVO());
                if (threadVo.getLastMessageVO().getReplyInfoVO().getParticipant() != null) {
                    threadVo.getLastMessageVO().getReplyInfoVO().setParticipantId(threadVo.getLastMessageVO().getReplyInfoVO().getParticipant().getId());
                    messageDao.insertParticipant(threadVo.getLastMessageVO().getReplyInfoVO().getParticipant());
                }
            }
            if (threadVo.getLastMessageVO().getForwardInfo() != null) {
                messageDao.insertForwardInfo(threadVo.getLastMessageVO().getForwardInfo());
                threadVo.getLastMessageVO().setForwardInfoId(threadVo.getLastMessageVO().getForwardInfo().getId());
                if (threadVo.getLastMessageVO().getForwardInfo().getParticipant() != null) {
                    threadVo.getLastMessageVO().getForwardInfo().setParticipantId(threadVo.getLastMessageVO().getForwardInfo().getParticipant().getId());
                    messageDao.insertParticipant(threadVo.getLastMessageVO().getForwardInfo().getParticipant());
                }
            }
            messageDao.insertThread(threadVo);
        }
    }

    public void saveParticipants(List<Participant> participants, long threadId) {
        for (Participant participant : participants) {
            participant.setThreadId(threadId);
            messageDao.insertParticipant(participant);
        }
    }

    public List<Participant> getThreadParticipant(long offset, long count, long threadId) {
        if (messageDao.geParticipants(offset, count, threadId) == null) {
            List<Participant> participants = new ArrayList<>();
            return participants;
        }
        return messageDao.geParticipants(offset, count, threadId);
    }

    public long getParticipantCount(long threadId){
        return messageDao.getParticipantCount(threadId);
    }

    public Contact getContactById(long id){
        return messageDao.getContactById(id);
    }
    public List<Contact> getContactsByFirst(String firstName){
        return messageDao.getContactsByFirst(firstName);
    }

    public List<Contact> getContactsByLast(String lastName){
        return messageDao.getContactsByLast(lastName);
    }

    public List<Contact> getContactsByFirstAndLast(String firstName, String lastName){
        return messageDao.getContactsByFirstAndLast(firstName, lastName);
    }

    public List<Contact> getContactByCell(String cellphoneNumber){
        return messageDao.getContactByCell(cellphoneNumber);
    }

    public List<Contact> getContactsByEmail(String email){
        return messageDao.getContactsByEmail(email);
    }

    //Cache file
    public void saveFile(FileMetaDataContent file){
        messageDao.insertFile(file);
    }

    public FileMetaDataContent getFile(long id){
        return messageDao.getFile(id);
    }
}
