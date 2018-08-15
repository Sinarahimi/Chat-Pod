package ir.fanap.chat.sdk.application.chatexample;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.ParticipantContent;
import com.fanap.podchat.mainmodel.SearchContact;

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

        void onBlock();

        void onUnblock();

        void onSearchContact();

        void onSearchHisory();

        void ongetBlockList();

        void onMapSearch();

        void onMapRouting();
    }

    interface presenter {

        void connect(String serverAddress, String appId, String severName, String token, String ssoHost
                , String platformHost, String fileServer);

        void mapSearch(String searchTerm, Double latitude, Double longitude);

        void mapRouting(String originLat, String originLng);

        void getThread(int count, int offset, ArrayList<Integer> threadIds, String threadName);

        void getUserInfo();

        void getHistory(int count, int offset, String order, long subjectId);

        void searchHistory(NosqlListMessageCriteriaVO messageCriteriaVO);

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

        void searchContact(SearchContact searchContact);

        void block(Long contactId);

        void unBlock(long contactId);

        void getBlockList(Integer count, Integer offset);

        void sendFileMessage(Context context, Activity activity, String description, long threadId, Uri fileUri, String metaData);

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
