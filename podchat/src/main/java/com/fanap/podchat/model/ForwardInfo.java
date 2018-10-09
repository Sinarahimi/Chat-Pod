package com.fanap.podchat.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.fanap.podchat.mainmodel.Participant;
@Entity
public class ForwardInfo {

    //This field is just for using cache
    @PrimaryKey
    @ColumnInfo(name = "forwardInfo_Id")
    private transient long id;

    @Ignore
    private Participant participant;

    @ForeignKey(entity = Participant.class,parentColumns = "id",childColumns = "participantId")
    private transient long participantId;

    @Ignore
    private ConversationSummery conversation;

    public Participant getParticipant() {
        return participant;
    }

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

    public long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(long participantId) {
        this.participantId = participantId;
    }
}
