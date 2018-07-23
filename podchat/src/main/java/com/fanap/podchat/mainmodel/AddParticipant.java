package com.fanap.podchat.mainmodel;

import java.util.List;

public class AddParticipant {
    private long subjectId;
    private List<ParticipantContent> content;
    private String uniqueId;

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public List<ParticipantContent> getContent() {
        return content;
    }

    public void setContent(List<ParticipantContent> content) {
        this.content = content;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
