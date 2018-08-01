package ir.fanap.chat.sdk.application.chatexample;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.Uri;

import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.ParticipantContent;

import java.util.ArrayList;
import java.util.List;

public interface ChatContract {
    interface view {

        void onGetUserInfo();

        void onGetThreadList();

        void onGetThreadHistory();

        void onGetContacts();

        void onGetThreadParticipant();

        void onSentMessage();

        void onGetDeliverMessage();

        void onGetSeenMessage();

        void onEditMessage();

        void onDeleteMessage();

        void onCreateThread();

        void onMuteThread();

        void onUnMuteThread();

        void onRenameGroupThread();

        void onAddContact();

        void onUpdateContact();

        void onUploadFile();

        void onUploadImageFile();

        void onRemoveContact();

        void onAddParticipant();

        void onRemoveParticipant();

        void onLeaveThread();
    }

    interface presenter {

        void connect(String serverAddress, String appId, String severName, String token, String ssoHost
                , String platformHost, String fileServer);

        void getThread(int count, int offset, ArrayList<Integer> threadIds, String threadName);

        void getUserInfo();

        void getHistory(int count, int offset, String order, long subjectId);

        void getContact(int count, int offset);

        void createThread(int threadType, Invitee[] invitee, String threadTitle);

        void sendTextMessage(String textMessage, long threadId, String metaData);

        void replyMessage(String messageContent, long threadId, long messageId);

        LiveData<String> getLiveState();

        void muteThread(int threadId);

        void renameThread(long threadId, String title);

        void unMuteThread(int threadId);

        void editMessage(int messageId, String messageContent);

        void getThreadParticipant(int count, int offset, long threadId);

        void addContact(String firstName, String lastName, String cellphoneNumber, String email);

        void removeContact(long id);

        void sendFileMessage(Context context, Activity activity, String description, long threadId, Uri fileUri);

        void syncContact(Activity activity);

        void forwardMessage(long threadId, ArrayList<Long> messageIds);

        void updateContact(int id, String firstName, String lastName, String cellphoneNumber, String email);

        void uploadImage(Context context, Activity activity, Uri fileUri);

        void uploadFile(Context context, Activity activity, String fileUri, Uri uri);

        void seenMessage(int messageId, long ownerId);

        void logOut();

        void removeParticipants(long threadId, List<Long> participantIds);

        void addParticipants(long threadId, List<Long> contactIds);

        void leaveThread(long threadId);

        void deleteMessage(long messageId, Boolean deleteForAll);
    }
}
