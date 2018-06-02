package com.fanap.podchat;

public interface ChatListener {

    void onDelivery(String content);

    void onERROR();

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

}
