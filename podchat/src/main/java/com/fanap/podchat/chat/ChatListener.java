package com.fanap.podchat.chat;

import com.fanap.podchat.model.OutPutThread;
import com.fanap.podchat.model.OutPutThreads;

public interface ChatListener {

    void onDeliver(String content);

    void onError(String content);

    void onGetContacts(String content);

    void onGetHistory(String content);

    void onGetThread(String content, OutPutThreads thread);

    void onThreadInfoUpdated(String content);

    void onBlock(String content);

    void onUnBlock(String content);

    void onSeen(String content);

    void onMuteThread(String content);

    void onUnmuteThread(String content);

    void onUserInfo(String content);

    void onSent(String content);

    void onCreateThread(String content);

    void onGetThreadParticipant(String content);

    void onEditedMessage(String content);

    void onContactAdded(String content);

    void handleCallbackError(Throwable cause) throws Exception;

    void onRemoveContact(String content);

    void onRenameThread(String content);

    void onMapSearch(String content);

    void onMapRouting(String content);

    void onNewMessage(String content);

    void onDeleteMessage(String content);

    void onUpdateContact(String content);

    void onUploadFile(String content);

    void onUploadImageFile(String content);

    void onSyncContact(String content);

    void onThreadAddParticipant(String content);

    void onThreadRemoveParticipant(String content);

    void onThreadLeaveParticipant(String content);

    void onLastSeenUpdated(String content);

    void onChatState(String state);

    void onGetBlockList(String content);

    void onSearchContact(String content);

}
