package com.fanap.podchat.mainmodel;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.fanap.podchat.model.ForwardInfo;
import com.fanap.podchat.model.ReplyInfoVO;

@Entity(foreignKeys = {@ForeignKey(entity = Participant.class, parentColumns = "id", childColumns = "participantId"),
        @ForeignKey(entity = ReplyInfoVO.class, parentColumns = "replyInfoVO_Id", childColumns = "replyInfoVOId"),
        @ForeignKey(entity = ForwardInfo.class, parentColumns = "forwardInfo_Id", childColumns = "forwardInfoId")})
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

    @ForeignKey(entity = Participant.class, parentColumns = "id", childColumns = "participantId")
    private Long participantId;

    @Ignore
    @Nullable
    private ReplyInfoVO replyInfoVO;

    @ForeignKey(entity = ReplyInfoVO.class, parentColumns = "ReplyInfoVO_Id", childColumns = "replyInfoVOId")
    private Long replyInfoVOId;

    @Ignore
    private ForwardInfo forwardInfo;

    @ForeignKey(entity = ForwardInfo.class, parentColumns = "forwardInfo_Id", childColumns = "forwardInfoId")
    @Nullable
    private Long forwardInfoId;

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

    @Nullable
    public ReplyInfoVO getReplyInfoVO() {
        return replyInfoVO;
    }

    @Nullable
    public void setReplyInfoVO(ReplyInfoVO replyInfoVO) {
        this.replyInfoVO = replyInfoVO;
    }

    public ForwardInfo getForwardInfo() {
        return forwardInfo;
    }

    public void setForwardInfo(ForwardInfo forwardInfo) {
        this.forwardInfo = forwardInfo;
    }

    @Nullable
    public Long getParticipantId() {
        return participantId;
    }

    @Nullable
    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    @Nullable
    public Long getReplyInfoVOId() {
        return replyInfoVOId;
    }

    @Nullable
    public void setReplyInfoVOId(Long replyInfoVOId) {
        this.replyInfoVOId = replyInfoVOId;
    }

    @Nullable
    public Long getForwardInfoId() {
        return forwardInfoId;
    }

    @Nullable
    public void setForwardInfoId(Long forwardInfoId) {
        this.forwardInfoId = forwardInfoId;
    }
}
