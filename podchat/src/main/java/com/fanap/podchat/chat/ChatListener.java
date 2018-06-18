package com.fanap.podchat.chat;

public interface ChatListener {

    void onDelivery(String content);

    void onError(String content);

    void onForwardMessage();

    void onGetContacts(String content);

    void onGetHistory(String content);

    void onStatus(String content);

    void onGetThread(String content);

    void onInvitation(String content);

    void onLeaveThread();

    void onSeen(String content);

    void onSent(String content);

    void onMessage();

    void onGetThreadParticipant(String content);

    void onEditedMessage(String content);

    void onContactAdded(String content);
}
