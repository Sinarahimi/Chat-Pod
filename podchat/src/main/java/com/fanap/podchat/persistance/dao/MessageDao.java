package com.fanap.podchat.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.LastMessageVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ForwardInfo;
import com.fanap.podchat.model.ReplyInfoVO;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageDao {

    @Insert(onConflict = REPLACE)
    void insertContact(List<Contact> t);

    @Query("select * from Contact")
    List<Contact> getContact();

    @Query("select * from Thread")
    List<Thread> getThreads();

    @Insert(onConflict = REPLACE)
    void insertThreads(List<Thread> Thread);

    @Insert(onConflict = REPLACE)
    void insertThread(Thread thread);

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
    ForwardInfo getForwardInfoId(long forwardInfoId);

}
