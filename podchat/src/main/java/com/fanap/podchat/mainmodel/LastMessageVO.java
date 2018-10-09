package com.fanap.podchat.mainmodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.fanap.podchat.model.ForwardInfo;
import com.fanap.podchat.model.ReplyInfoVO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@Entity
public class LastMessageVO {
    @PrimaryKey
    private long id;
    private String uniqueId;
    private String message;
    private boolean edited;
    private boolean editable;
    private long time;

    @Ignore
    private Participant participant;

    @ForeignKey(entity = Participant.class,parentColumns = "id",childColumns = "participantId")
    private transient long participantId;

    @Ignore
    private ReplyInfoVO replyInfoVO;

    @ForeignKey(entity = ReplyInfoVO.class,parentColumns = "ReplyInfoVO_Id",childColumns = "replyInfoVOId")
    private transient long replyInfoVOId;

    @Ignore
    private ForwardInfo forwardInfo;

    @ForeignKey(entity = ReplyInfoVO.class,parentColumns = "forwardInfo_Id",childColumns = "forwardInfoId")
    private transient long forwardInfoId;

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ReplyInfoVO getReplyInfoVO() {
        return replyInfoVO;
    }

    public void setReplyInfoVO(ReplyInfoVO replyInfoVO) {
        this.replyInfoVO = replyInfoVO;
    }

    public ForwardInfo getForwardInfo() {
        return forwardInfo;
    }

    public void setForwardInfo(ForwardInfo forwardInfo) {
        this.forwardInfo = forwardInfo;
    }

    public long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(long participantId) {
        this.participantId = participantId;
    }

    public long getReplyInfoVOId() {
        return replyInfoVOId;
    }

    public void setReplyInfoVOId(long replyInfoVOId) {
        this.replyInfoVOId = replyInfoVOId;
    }

    public long getForwardInfoId() {
        return forwardInfoId;
    }

    public void setForwardInfoId(long forwardInfoId) {
        this.forwardInfoId = forwardInfoId;
    }
}
