package com.fanap.podchat.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.ThreadVo;

@Entity(foreignKeys = {
        @ForeignKey(entity = Participant.class, parentColumns = "id", childColumns = "participantId"),
        @ForeignKey(entity = ThreadVo.class, parentColumns = "id", childColumns = "threadVoId"),
        @ForeignKey(entity = ReplyInfoVO.class, parentColumns = "replyInfoVO_Id", childColumns = "replyInfoVOId"),
        @ForeignKey(entity = ForwardInfo.class, parentColumns = "forwardInfo_Id", childColumns = "forwardInfoId")
})
public class MessageVO {

    @PrimaryKey
    private long id;
    private long previousId;
    private long time;
    private boolean edited;
    private boolean editable;
    private boolean delivered;
    private boolean seen;
    private String uniqueId;
    private String message;
    private String metadata;
    private String systemMetadata;

    @Ignore
    private Participant participant;

    @ForeignKey(entity = Participant.class, parentColumns = "id", childColumns = "participantId")
    private Long participantId;

    @Ignore
    private ThreadVo conversation;

    @ForeignKey(entity = ThreadVo.class, parentColumns = "id", childColumns = "threadVoId")
    private Long threadVoId;

    @Ignore
    private ReplyInfoVO replyInfoVO;

    @ForeignKey(entity = ReplyInfoVO.class, parentColumns = "ReplyInfoVO_Id", childColumns = "replyInfoVOId")
    private Long replyInfoVOId;

    @Ignore
    private ForwardInfo forwardInfo;

    @ForeignKey(entity = ForwardInfo.class, parentColumns = "forwardInfo_Id", childColumns = "forwardInfoId")
    @Nullable
    private Long forwardInfoId;

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

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
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

    public long getPreviousId() {
        return previousId;
    }

    public void setPreviousId(long previousId) {
        this.previousId = previousId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public ThreadVo getConversation() {
        return conversation;
    }

    public void setConversation(ThreadVo conversation) {
        this.conversation = conversation;
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

    public String getSystemMetadata() {
        return systemMetadata;
    }

    public void setSystemMetadata(String systemMetadata) {
        this.systemMetadata = systemMetadata;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public Long getThreadVoId() {
        return threadVoId;
    }

    public void setThreadVoId(Long threadVoId) {
        this.threadVoId = threadVoId;
    }

    public Long getReplyInfoVOId() {
        return replyInfoVOId;
    }

    public void setReplyInfoVOId(Long replyInfoVOId) {
        this.replyInfoVOId = replyInfoVOId;
    }

    @Nullable
    public Long getForwardInfoId() {
        return forwardInfoId;
    }

    public void setForwardInfoId(@Nullable Long forwardInfoId) {
        this.forwardInfoId = forwardInfoId;
    }
}
