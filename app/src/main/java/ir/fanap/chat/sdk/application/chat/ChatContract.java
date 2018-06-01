package ir.fanap.chat.sdk.application.chat;

import android.arch.lifecycle.LiveData;

public interface ChatContract {
    interface view {
    }

    interface presenter {

        void connect(String serverAddress, String appId, String severName, String token);

        void getThread(int count, int offset);

        void getMessage();

        void getHistory(int count, int offset);

        void getHistory(int count, int offset, String order, long subjectId);

        void getContact(int count, int offset);

        void createThread(int type, String title);

        void sendTextMessage(String textMessage, long threadId);

        LiveData<String> getLiveState();

    }
}
