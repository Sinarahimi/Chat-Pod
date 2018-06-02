package ir.fanap.chat.sdk.application.chat;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.fanap.podchat.Chat;
import com.fanap.podchat.ChatAdapter;
import com.fanap.podchat.ChatListener;

public class ChatPresenter extends ChatAdapter implements ChatContract.presenter  {

    private Chat chat;
    private ChatContract.view view;

    public ChatPresenter(Context context) {
        chat = new Chat();
        chat.init(context);
        chat.addListener(this);
    }

    @Override
    public void connect(String serverAddress, String appId, String severName, String token) {
        chat.connect(serverAddress, appId, severName, token);
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
        chat.getHistory(count, offset);
    }

    @Override
    public void getHistory(int count, int offset, String order, long subjectId) {
        chat.getHistory(count, offset, order,subjectId);
    }

    @Override
    public void getContact(int count, int offset) {
        chat.getContacts(count, offset);
    }

    @Override
    public void createThread(int type, String title) {
        chat.createThread(type,title);
    }

    @Override
    public void sendTextMessage(String textMessage, long threadId) {
        chat.sendTextMessage(textMessage, threadId);
    }

    @Override
    public LiveData<String> getLiveState() {
        return chat.getState();
    }

    @Override
    public void onDelivery(String content) {
        super.onDelivery(content);
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
    public void onSent(String content) {
        super.onSent(content);
    }
}
