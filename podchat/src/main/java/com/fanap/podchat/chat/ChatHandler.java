package com.fanap.podchat.chat;

public abstract class ChatHandler {

    public void onSent(String uniqueId, long threadId) {
    }

    public void onSentResult( String content) {
    }

    public void onDelivered(String i){}
}
