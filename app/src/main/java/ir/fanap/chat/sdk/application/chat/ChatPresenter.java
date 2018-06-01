package ir.fanap.chat.sdk.application.chat;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.fanap.podchat.Chat;

public class ChatPresenter implements ChatContract.presenter {

    private Chat chat;

    public ChatPresenter(Context context) {
        chat = new Chat();
        chat.init(context);
    }

    public ChatPresenter() {
        chat = new Chat();
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
}
