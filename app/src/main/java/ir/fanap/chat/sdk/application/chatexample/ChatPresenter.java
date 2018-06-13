package ir.fanap.chat.sdk.application.chatexample;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatAdapter;

import java.util.List;

public class ChatPresenter extends ChatAdapter implements ChatContract.presenter {

    private Chat chat;
    private ChatContract.view view;

    public ChatPresenter(Context context) {
        chat = new Chat();
        chat.init(context);
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
    public void getMessage() {
        chat.onMessage();
    }

    @Override
    public void getHistory(int count, int offset) {
    }

    @Override
    public void getHistory(int count, int offset, String order, long subjectId) {
        chat.getHistory(count, offset, subjectId);
    }

    @Override
    public void getContact(int count, int offset) {
        chat.getContact(count, offset);
    }

    @Override
    public void createThread(int chatThreadType, int contactId) {
        chat.createThread(chatThreadType, contactId);
    }

    @Override
    public void sendTextMessage(String textMessage, long threadId) {
        chat.sendTextMessage(textMessage, threadId);
    }

    @Override
    public void sendReplyMessage(String messageContent, long threadId, long messageId) {
        chat.sendReplyMessage(messageContent, threadId, messageId);
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
    public void unMuteThread(int threadId) {
        chat.unMuteThread(threadId);
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
    public void addContact(List<String> firstName, List<String> lastName, String cellphoneNumber, String email) {
        chat.addContact(firstName, lastName, cellphoneNumber, email);
    }

    @Override
    public void onDelivery(String content) {
        super.onDelivery(content);
    }

    @Override
    public void onGetThread(String content, int contentCount) {
        super.onGetThread(content, contentCount);
    }

    @Override
    public void onGetContacts(String content, int contentCount) {
        super.onGetContacts(content, contentCount);
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
    public void onSent(String content) {
        super.onSent(content);
    }
}
