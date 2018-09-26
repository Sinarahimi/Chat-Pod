package com.fanap.podchat.mainmodel;

public class History {
    private long offset;
    private long count;
    private String order;
    private long firstMessageId;
    private long lastMessageId;

    public History(Builder builder){
        this.count = builder.count;
        this.offset = builder.offset;
        this.order = builder.order;
        this.firstMessageId = builder.firstMessageId;
        this.lastMessageId = builder.lastMessageId;
    }

    public static class Builder{
        private long offset;
        private long count;
        private String order;
        private long firstMessageId;
        private long lastMessageId;


        public Builder offset(long offset){
           this.offset = offset;
           return this;
        }

        public Builder count(long count){
            this.count = count;
            return this;
        }

        public Builder order(String order){
            this.order = order;
            return this;
        }

        public Builder firstMessageId(long firstMessageId){
            this.firstMessageId = firstMessageId;
            return this;
        }

        public Builder lastMessageId (long lastMessageId){
            this.lastMessageId = lastMessageId;
            return this;
        }

        public History build(){
            return new History(this);
        }
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public long getFirstMessageId() {
        return firstMessageId;
    }

    public void setFirstMessageId(long firstMessageId) {
        this.firstMessageId = firstMessageId;
    }

    public long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }
}

//{"type":15,"token":"7cba09ff83554fc98726430c30afcfc6","tokenIssuer":1,"subjectId":293,"content":"{\"count\":50,\"offset\":0,\"\":15113,\"lastMessageId\":15150}","uniqueId":"f1bf0790-a96f-48c3-b5d0-2b019f1a50ad"}