package ir.fanap.chat.sdk.application.chatexample;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatAdapter;
import com.fanap.podchat.model.Invitee;

import java.util.ArrayList;

public class ChatPresenter extends ChatAdapter implements ChatContract.presenter {

    private Chat chat;
    private ChatContract.view view;

    public ChatPresenter(Context context) {
        chat = Chat.init(context);
        chat.addListener(this);
    }

    @Override
    public void connect(String serverAddress, String appId, String severName,
                        String token, String ssoHost, String platformHost) {
        chat.connect(serverAddress, appId, severName, token, ssoHost, platformHost);
    }

    @Override
    public void getThread(int count, int offset) {
        chat.getThread(count, offset);
    }

    @Override
    public void getUserInfo() {
        chat.getUserInfo();
    }

    @Override
    public void getHistory(int count, int offset) {
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
    public void sendTextMessage(String textMessage, long threadId) {
        chat.sendTextMessage(textMessage, threadId);
    }

    @Override
    public void sendReplyMessage(String messageContent, long threadId, long messageId) {
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
        chat.getThreadParticipant(count, offset, threadId);
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
    public void forwardMessage(long threadId, ArrayList<Long> messageIds) {
        chat.forwardMessage(threadId, messageIds);
    }


    @Override
    public void updateContact(long id, String firstName, String lastName, String cellphoneNumber, String email) {

    }

    @Override
    public void onDeliver(String content) {
        super.onDeliver(content);
    }

    @Override
    public void onGetThread(String content) {
        super.onGetThread(content);
    }

    @Override
    public void onGetContacts(String content) {
        super.onGetContacts(content);
        view.onGetContacts(content);
    }

    @Override
    public void onInvitation(String content) {
        super.onInvitation(content);
    }

    @Override
    public void onSeen(String content) {
        super.onSeen(content);
    }

    @Override
    public void onUserInfo(String content) {
    }

    @Override
    public void onSent(String content) {
        super.onSent(content);
    }

    @Override
    public void onCreateThread(String content) {

    }
}
