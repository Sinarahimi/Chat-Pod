package com.fanap.podchat.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.LastMessageVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.ThreadVo;
import com.fanap.podchat.model.ForwardInfo;
import com.fanap.podchat.model.MessageVO;
import com.fanap.podchat.model.ReplyInfoVO;
import com.fanap.podchat.util.ThreadCallbacks;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageDao {

    @Insert(onConflict = REPLACE)
    void insertHistory(MessageVO messageVO);

    @Insert(onConflict = REPLACE)
    void insertHistories(List<MessageVO> messageVOS);

    @Query("select * from MessageVO")
    List<MessageVO> getHistories();


    @Insert(onConflict = REPLACE)
    void insertContact(List<Contact> t);

    @Query("select * from Contact")
    List<Contact> getContacts();

    @Query("select * from ThreadVo")
    List<ThreadVo> getThreads();

    @Query("select  * from ThreadVo where id = :id")
    ThreadVo getThread(long id);

    @Insert(onConflict = REPLACE)
    void insertThreads(List<ThreadVo> ThreadVo);

    @Insert(onConflict = REPLACE)
    void insertThread(ThreadVo threadVo);

    @Query("select * from Inviter where id = :inviterId ")
    Inviter getInviter(long inviterId);

    @Insert(onConflict = REPLACE)
    void insertInviter(Inviter inviter);

    @Insert(onConflict = REPLACE)
    void insertLastMessageVO(LastMessageVO lastMessageVO);

    @Query("select * from LastMessageVO where id = :LastMessageVOId")
    LastMessageVO getLastMessageVO(long LastMessageVOId);

    @Insert(onConflict = REPLACE)
    void insertParticipant(Participant participant);

    @Query("select * from Participant where id = :participantId")
    Participant getParticipant(long participantId);

    @Insert(onConflict = REPLACE)
    void insertReplyInfoVO(ReplyInfoVO replyInfoVO);

    @Query("select * from ReplyInfoVO where replyInfoVO_Id = :replyInfoVOId")
    ReplyInfoVO getReplyInfo(long replyInfoVOId);

    @Insert(onConflict = REPLACE)
    void insertForwardInfo(ForwardInfo forwardInfo);

    @Query("select * from ForwardInfo where forwardInfo_Id = :forwardInfoId ")
    ForwardInfo getForwardInfo(long forwardInfoId);

}
