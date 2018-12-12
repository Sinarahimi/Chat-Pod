package com.fanap.podchat.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.LastMessageVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.ThreadVo;
import com.fanap.podchat.model.FileImageMetaData;
import com.fanap.podchat.model.FileMetaDataContent;
import com.fanap.podchat.mainmodel.ForwardInfo;
import com.fanap.podchat.model.MessageVO;
import com.fanap.podchat.mainmodel.ReplyInfoVO;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageDao {

    @Insert(onConflict = REPLACE)
    void insertMessage(MessageVO messageVO);

    @Insert(onConflict = REPLACE)
    void insertHistories(List<MessageVO> messageVOS);

    @Query("select * from MessageVO")
    List<MessageVO> getHistories();

    @Insert(onConflict = REPLACE)
    void InsertParticipant(Participant participant);

    @Insert(onConflict = REPLACE)
    void insertParticipants(List<Participant> participants);

    @Query("select COUNT(id) FROM Participant WHERE threadId = :threadId")
    int getParticipantCount(long threadId);

    @Query("select * from Participant WHERE threadId =:threadId ORDER BY name LIMIT :count OFFSET :offset ")
    List<Participant> geParticipants(long offset, long count, long threadId);

    @Query("select * from Participant WHERE threadId = :threadId")
    List<Participant> geParticipantsWithThreadId(long threadId);

    @Insert(onConflict = REPLACE)
    void insertContact(List<Contact> t);

    @Query("select * from Contact")
    List<Contact> getContacts();


    //Cache thread

    @Query("select COUNT(id) FROM THREADVO")
    int getThreadCount();

    @Query("select * from ThreadVo")
    List<ThreadVo> getThreads();

    @Query("select  * from ThreadVo where id = :id")
    ThreadVo getThread(long id);

    @Query("select  * from ThreadVo where id = :id")
    ThreadVo getThreadByName(long id);

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

    //Search contact
    @Query("select * from Contact where id = :id")
    Contact getContactById(long id);

    @Query("select * from contact where firstName LIKE :firstName ")
    List<Contact> getContactsByFirst(String firstName);

    @Query("select * from contact where lastName LIKE :lastName ")
    List<Contact> getContactsByLast(String lastName);

    @Query("select * from contact where firstName LIKE :firstName AND lastName LIKE :lastName")
    List<Contact> getContactsByFirstAndLast(String firstName, String lastName);

    @Query("select * from Contact where cellphoneNumber LIKE :cellphoneNumber")
    List<Contact> getContactByCell(String cellphoneNumber);

    @Query("select * from contact where email LIKE :email ")
    List<Contact> getContactsByEmail(String email);

    //cache file
    @Insert(onConflict = REPLACE)
    void insertFile(FileMetaDataContent file);

    @Query("select * from FileMetaDataContent where id = :id")
    FileMetaDataContent getFile(long id );

    //Cache image file
    @Insert(onConflict = REPLACE)
    void insertImage(FileImageMetaData image);

    @Query("select * from FileImageMetaData where id = :id")
    FileImageMetaData getImageFile(long id);
}
