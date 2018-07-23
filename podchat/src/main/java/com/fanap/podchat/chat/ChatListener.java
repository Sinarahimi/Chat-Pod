package com.fanap.podchat.chat;

public interface ChatListener {

    void onDeliver(String content);

    void onError(String content);

    void onForwardMessage();

    void onGetContacts(String content);

    void onGetHistory(String content);

    void onStatus(String content);

    void onGetThread(String content);

    void onInvitation(String content);

    void onLeaveThread();

    void onSeen(String content);

    void onMuteThread(String content);

    void onUnmuteThread(String content);

    void onUserInfo(String content);

    void onSent(String content);

    void onCreateThread(String content);

    void onMessage();

    void onGetThreadParticipant(String content);

    void onEditedMessage(String content);

    void onContactAdded(String content);

    void handleCallbackError(Throwable cause) throws Exception;

    void onRemoveContact(String content);

    void onRenameThread(String content);

    void onGetFile(String url);

    void onGetImageFile(String url);

    void onNewMessage(String content);

    void onUpdateContact(String content);

    void onUploadFile(String content);

    void onUploadImageFile(String content);

    void onSyncContact(String content);

    void onThreadAddPartcipant(String content);

    void onThreadRemovePartcipant(String content);

    void onThreadLeavePartcipant(String content);
}
