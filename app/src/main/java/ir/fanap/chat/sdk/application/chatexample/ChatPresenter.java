package ir.fanap.chat.sdk.application.chatexample;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.Uri;

import com.fanap.podasync.util.JsonUtil;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatAdapter;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.model.MessageVO;
import com.squareup.moshi.JsonAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatPresenter extends ChatAdapter implements ChatContract.presenter {

    private Chat chat;
    private ChatContract.view view;
    private Context context;

    public ChatPresenter(Context context, ChatContract.view view) {
        chat = Chat.init(context);
        chat.addListener(this);
        this.context = context;
        this.view = view;
    }

    @Override
    public void connect(String serverAddress, String appId, String severName,
                        String token, String ssoHost, String platformHost, String fileServer) {
        chat.connect(serverAddress, appId, severName, token, ssoHost, platformHost, fileServer);
    }

    @Override
    public void getThread(int count, int offset, ArrayList<Integer> threadIds, String threadName) {
        chat.getThreads(count, offset, threadIds, threadName);
    }

    @Override
    public void getUserInfo() {
        chat.getUserInfo();
    }

    @Override
    public void getHistory(int count, int offset, String order, long subjectId) {
        chat.getHistory(count, offset, order, subjectId);
    }

    @Override
    public void getContact(int count, int offset) {
        chat.getContacts(count, offset);
    }

    @Override
    public void createThread(int threadType, Invitee[] invitee, String threadTitle) {
        chat.createThread(threadType, invitee, threadTitle);
    }

    @Override
    public void sendTextMessage(String textMessage, long threadId, String metaData) {
        chat.sendTextMessage(textMessage, threadId, metaData);
    }

    @Override
    public void replyMessage(String messageContent, long threadId, long messageId) {
        chat.replyMessage(messageContent, threadId, messageId);
    }

    @Override
    public LiveData<String> getLiveState() {
        return chat.getState();
    }

    @Override
    public void muteThread(int threadId) {
        chat.muteThread(threadId);
    }

    @Override
    public void renameThread(long threadId, String title) {
        chat.renameThread(threadId, title);
    }

    @Override
    public void unMuteThread(int threadId) {
        chat.unmuteThread(threadId);
    }

    @Override
    public void editMessage(int messageId, String messageContent) {
        chat.editMessage(messageId, messageContent);
    }

    @Override
    public void getThreadParticipant(int count, int offset, long threadId) {
        chat.getThreadParticipants(count, offset, threadId);
    }

    @Override
    public void addContact(String firstName, String lastName, String cellphoneNumber, String email) {
        chat.addContact(firstName, lastName, cellphoneNumber, email);
    }

    @Override
    public void removeContact(long id) {
        chat.removeContact(id);
    }

    @Override
    public void sendFileMessage(Context context, Activity activity, String description, long threadId, Uri fileUri) {
        chat.sendFileMessage(context, activity, description, threadId, fileUri);
    }

    @Override
    public void syncContact(Activity activity) {
        chat.syncContact(context, activity);
    }

    @Override
    public void forwardMessage(long threadId, ArrayList<Long> messageIds) {
        chat.forwardMessage(threadId, messageIds);
    }

    @Override
    public void updateContact(int id, String firstName, String lastName, String cellphoneNumber, String email) {
        chat.updateContact(id, firstName, lastName, cellphoneNumber, email);
    }

    @Override
    public void uploadImage(Context context, Activity activity, Uri fileUri) {
        chat.uploadImage(context, activity, fileUri);
    }

    @Override
    public void uploadFile(Context context, Activity activity, String fileUri, Uri uri) {
        chat.uploadFile(context, activity, fileUri, uri);
    }

    @Override
    public void seenMessage(int messageId) {
        chat.seenMessage(messageId);
    }

    @Override
    public void logOut() {
        chat.logOutSocket();
    }

    @Override
    public void removeParticipants(long threadId, List<Long> contactIds) {
        chat.removeParticipants(threadId, contactIds);
    }

    @Override
    public void addParticipants(long threadId, List<Long> contactIds) {
        chat.addParticipants(threadId, contactIds);
    }

    @Override
    public void leaveThread(long threadId) {
        chat.leaveThread(threadId);
    }

    @Override
    public void deleteMessage(long messageId, Boolean deleteForAll) {
        chat.deleteMessage(messageId, deleteForAll);
    }

    @Override
    public void onDeliver(String content) {
        super.onDeliver(content);
        view.onGetDeliverMessage();
        chat.seenMessage(Integer.valueOf(content));
    }

    @Override
    public void onGetThread(String content) {
        super.onGetThread(content);
        view.onGetThreadList();
    }

    @Override
    public void onThreadInfoUpdated(String content) {

    }

    @Override
    public void onGetContacts(String content) {
        super.onGetContacts(content);
        view.onGetContacts();
    }

    @Override
    public void onSeen(String content) {
        super.onSeen(content);
        view.onGetSeenMessage();
    }

    @Override
    public void onUserInfo(String content) {
        view.onGetUserInfo();
    }

    @Override
    public void onSent(String content) {
        super.onSent(content);
        view.onSentMessage();
    }

    @Override
    public void onCreateThread(String content) {
        super.onCreateThread(content);
        view.onCreateThread();
    }

    @Override
    public void onGetThreadParticipant(String content) {
        super.onGetThreadParticipant(content);
        view.onGetThreadParticipant();
    }

    @Override
    public void onEditedMessage(String content) {
        super.onEditedMessage(content);
        view.onEditMessage();
    }

    @Override
    public void onGetHistory(String content) {
        super.onGetHistory(content);
        view.onGetThreadHistory();
    }

    @Override
    public void onMuteThread(String content) {
        super.onMuteThread(content);
        view.onMuteThread();
    }

    @Override
    public void onUnmuteThread(String content) {
        super.onUnmuteThread(content);
        view.onUnMuteThread();
    }

    @Override
    public void onRenameThread(String content) {
        super.onRenameThread(content);
        view.onRenameGroupThread();
    }

    @Override
    public void onContactAdded(String content) {
        super.onContactAdded(content);
        view.onAddContact();
    }

    @Override
    public void onUpdateContact(String content) {
        super.onUpdateContact(content);
        view.onUpdateContact();
    }

    @Override
    public void onUploadFile(String content) {
        super.onUploadFile(content);
        view.onUploadFile();
    }

    @Override
    public void onUploadImageFile(String content) {
        super.onUploadImageFile(content);
        view.onUploadImageFile();
    }

    @Override
    public void onRemoveContact(String content) {
        super.onRemoveContact(content);
        view.onRemoveContact();
    }

    @Override
    public void onThreadAddParticipant(String content) {
        super.onThreadAddParticipant(content);
        view.onAddParticipant();
    }

    @Override
    public void onThreadRemoveParticipant(String content) {
        super.onThreadRemoveParticipant(content);
        view.onRemoveParticipant();
    }

    @Override
    public void onDeleteMessage(String content) {
        super.onDeleteMessage(content);
        view.onDeleteMessage();
    }

    @Override
    public void onThreadLeaveParticipant(String content) {
        super.onThreadLeaveParticipant(content);
        view.onLeaveThread();
    }

    @Override
    public void onNewMessage(String content) {
        super.onNewMessage(content);
        MessageVO jsonMessage = JsonUtil.fromJSON(content, MessageVO.class);
        long id = jsonMessage.getId();
        chat.seenMessage(id);
    }
}
