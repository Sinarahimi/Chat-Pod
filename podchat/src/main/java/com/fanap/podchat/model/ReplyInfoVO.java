package com.fanap.podchat.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.fanap.podchat.mainmodel.Participant;

@Entity(foreignKeys = {@ForeignKey(entity = Participant.class, parentColumns = "id", childColumns = "participantId")})
public class ReplyInfoVO {

    //This field is just for using cache
    @PrimaryKey
    @ColumnInfo(name = "replyInfoVO_Id")
    private Long id;

    @Ignore
    @Nullable
    private Participant participant;

    @ForeignKey(entity = Participant.class, parentColumns = "id", childColumns = "participantId")
    private Long participantId;

    private long repliedToMessageId;
    private String repliedToMessage;

    @Nullable
    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public long getRepliedToMessageId() {
        return repliedToMessageId;
    }

    public void setRepliedToMessageId(long repliedToMessageId) {
        this.repliedToMessageId = repliedToMessageId;
    }

    public String getRepliedToMessage() {
        return repliedToMessage;
    }

    public void setRepliedToMessage(String repliedToMessage) {
        this.repliedToMessage = repliedToMessage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }
}
