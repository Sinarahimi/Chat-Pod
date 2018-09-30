package com.fanap.podchat.chat;

import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.OutPutAddParticipant;
import com.fanap.podchat.model.OutPutBlock;
import com.fanap.podchat.model.OutPutBlockList;
import com.fanap.podchat.model.OutPutContact;
import com.fanap.podchat.model.OutPutDeleteMessage;
import com.fanap.podchat.model.OutPutHistory;
import com.fanap.podchat.model.OutPutLeaveThread;
import com.fanap.podchat.model.OutPutMapNeshan;
import com.fanap.podchat.model.OutPutMute;
import com.fanap.podchat.model.OutPutNewMessage;
import com.fanap.podchat.model.OutPutParticipant;
import com.fanap.podchat.model.OutPutThread;
import com.fanap.podchat.model.OutPutThreads;
import com.fanap.podchat.model.OutPutUserInfo;

public interface ChatListener {


    void onError(String content, ErrorOutPut errorOutPut);

    void onGetContacts(String content, OutPutContact outPutContact);

    void onGetHistory(String content, OutPutHistory history);

    void onGetThread(String content, OutPutThreads thread);

    void onThreadInfoUpdated(String content);

    void onBlock(String content, OutPutBlock outPutBlock);

    void onUnBlock(String content, OutPutBlock outPutBlock);

    void onSeen(String content);

    void onDeliver(String content);

    void onSent(String content);

    void onMuteThread(String content, OutPutMute outPutMute);

    void onUnmuteThread(String content, OutPutMute outPutUnMute);

    void onUserInfo(String content, OutPutUserInfo outPutUserInfo);

    void onCreateThread(String content, OutPutThread outPutThread);

    void onGetThreadParticipant(String content, OutPutParticipant outPutParticipant);

    void onEditedMessage(String content);

    void onContactAdded(String content);

    void handleCallbackError(Throwable cause) throws Exception;

    void onRemoveContact(String content);

    void onRenameThread(String content, OutPutThread outPutThread);

    void onMapSearch(String content, OutPutMapNeshan outPutMapNeshan);

    void onMapRouting(String content);

    void onNewMessage(String content, OutPutNewMessage outPutNewMessage);

    void onDeleteMessage(String content, OutPutDeleteMessage outPutDeleteMessage);

    void onUpdateContact(String content);

    void onUploadFile(String content);

    void onUploadImageFile(String content);

    void onSyncContact(String content);

    void onSearchContact(String content);

    void onThreadAddParticipant(String content, OutPutAddParticipant outPutAddParticipant);

    void onThreadRemoveParticipant(String content, OutPutParticipant outPutParticipant);

    void onThreadLeaveParticipant(String content, OutPutLeaveThread outPutLeaveThread);

    void onLastSeenUpdated(String content);

    void onChatState(String state);

    void onGetBlockList(String content, OutPutBlockList outPutBlockList);


}
