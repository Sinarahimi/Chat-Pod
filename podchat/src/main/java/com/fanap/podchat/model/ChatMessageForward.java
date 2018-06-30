package com.fanap.podchat.model;

import java.util.ArrayList;

public class ChatMessageForward {
    private String token;
    private String tokenIssuer;
    private int type;
    private long subjectId;
    private ArrayList<Long> content;
    private ArrayList<String> uniqueId;
    private long time;
    private int contentCount;
    private String metadata;
    private long repliedTo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public long getRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo(long repliedTo) {
        this.repliedTo = repliedTo;
    }

    public String getTokenIssuer() {
        return tokenIssuer;
    }

    public void setTokenIssuer(String tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }

    public int getContentCount() {
        return contentCount;
    }

    public void setContentCount(int contentCount) {
        this.contentCount = contentCount;
    }

    public ArrayList<String> getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(ArrayList<String> uniqueId) {
        this.uniqueId = uniqueId;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public ArrayList<Long> getContent() {
        return content;
    }

    public void setContent(ArrayList<Long> content) {
        this.content = content;
    }
}
