package com.fanap.podchat.mainmodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.model.ConversationSummery;

@Entity
public class ForwardInfo {

    //This field is just for using cache
    @PrimaryKey
    @ColumnInfo(name = "forwardInfo_Id")
    private long id;

    @Ignore
    @Nullable
    private Participant participant;

    @Nullable
    private Long participantId;

    @Ignore
    private ConversationSummery conversation;

    public Participant getParticipant() {
        return participant;
    }

    @Nullable
    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public ConversationSummery getConversation() {
        return conversation;
    }

    public void setConversation(ConversationSummery conversation) {
        this.conversation = conversation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Nullable
    public Long getParticipantId() {
        return participantId;
    }

    @Nullable
    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }
}
