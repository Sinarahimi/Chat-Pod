package com.fanap.podchat.mainmodel;

public class NosqlListMessageCriteriaVO {
    private long messageThreadId;
    private long userId;
    private Long firstMessageId;
    private Long lastMessageId;
    private Long id;
    private String query;
    private NosqlSearchMetadataCriteria metadataCriteria;
    private int offset;
    private int count;
    private String order;

    public long getMessageThreadId() {
        return messageThreadId;
    }

    public void setMessageThreadId(long messageThreadId) {
        this.messageThreadId = messageThreadId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Long getFirstMessageId() {
        return firstMessageId;
    }

    public void setFirstMessageId(Long firstMessageId) {
        this.firstMessageId = firstMessageId;
    }

    public Long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public NosqlSearchMetadataCriteria getMetadataCriteria() {
        return metadataCriteria;
    }

    public void setMetadataCriteria(NosqlSearchMetadataCriteria metadataCriteria) {
        this.metadataCriteria = metadataCriteria;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
