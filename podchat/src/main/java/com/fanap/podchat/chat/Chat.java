package com.fanap.podchat.chat;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.fanap.podasync.Async;
import com.fanap.podasync.AsyncAdapter;
import com.fanap.podasync.model.Device;
import com.fanap.podasync.model.DeviceResult;
import com.fanap.podasync.util.JsonUtil;
import com.fanap.podchat.BuildConfig;
import com.fanap.podchat.mainmodel.AddParticipant;
import com.fanap.podchat.mainmodel.BaseMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.ChatMessageContent;
import com.fanap.podchat.mainmodel.ChatThread;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.FileUpload;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.MapNeshan;
import com.fanap.podchat.mainmodel.MapRout;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.RemoveParticipant;
import com.fanap.podchat.mainmodel.ResultDeleteMessage;
import com.fanap.podchat.mainmodel.SearchContact;
import com.fanap.podchat.mainmodel.SearchContactVO;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.mainmodel.UpdateContact;
import com.fanap.podchat.mainmodel.UserInfo;
import com.fanap.podchat.model.AddContacts;
import com.fanap.podchat.model.ChatMessageForward;
import com.fanap.podchat.model.ContactRemove;
import com.fanap.podchat.model.Contacts;
import com.fanap.podchat.model.DeleteMessageContent;
import com.fanap.podchat.model.Error;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.FileImageMetaData;
import com.fanap.podchat.model.FileImageUpload;
import com.fanap.podchat.model.FileMetaDataContent;
import com.fanap.podchat.model.MessageVO;
import com.fanap.podchat.model.MetaDataFile;
import com.fanap.podchat.model.MetaDataImageFile;
import com.fanap.podchat.model.OutPut;
import com.fanap.podchat.model.OutPutAddContact;
import com.fanap.podchat.model.OutPutAddParticipant;
import com.fanap.podchat.model.OutPutBlock;
import com.fanap.podchat.model.OutPutBlockList;
import com.fanap.podchat.model.OutPutContact;
import com.fanap.podchat.model.OutPutDeleteMessage;
import com.fanap.podchat.model.OutPutInfoThread;
import com.fanap.podchat.model.OutPutLeaveThread;
import com.fanap.podchat.model.OutPutMapNeshan;
import com.fanap.podchat.model.OutPutMapRout;
import com.fanap.podchat.model.OutPutNewMessage;
import com.fanap.podchat.model.OutPutParticipant;
import com.fanap.podchat.model.OutPutThread;
import com.fanap.podchat.model.OutPutThreads;
import com.fanap.podchat.model.OutPutUpdateContact;
import com.fanap.podchat.model.OutPutUserInfo;
import com.fanap.podchat.model.ResultAddParticipant;
import com.fanap.podchat.model.ResultBlock;
import com.fanap.podchat.model.ResultBlockList;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.ResultFile;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.model.ResultLeaveThread;
import com.fanap.podchat.model.ResultMap;
import com.fanap.podchat.model.ResultParticipant;
import com.fanap.podchat.model.ResultThread;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.model.ResultUpdateContact;
import com.fanap.podchat.model.ResultUserInfo;
import com.fanap.podchat.model.ResultsHistory;
import com.fanap.podchat.networking.RetrofitHelperFileServer;
import com.fanap.podchat.networking.RetrofitHelperMap;
import com.fanap.podchat.networking.RetrofitHelperPlatformHost;
import com.fanap.podchat.networking.RetrofitHelperSsoHost;
import com.fanap.podchat.networking.api.ContactApi;
import com.fanap.podchat.networking.api.FileApi;
import com.fanap.podchat.networking.api.MapApi;
import com.fanap.podchat.networking.api.TokenApi;
import com.fanap.podchat.persistance.MessageDatabaseHelper;
import com.fanap.podchat.util.Callback;
import com.fanap.podchat.util.ChatConstant;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.ChatMessageType.Constants;
import com.fanap.podchat.util.ChatStateType;
import com.fanap.podchat.util.FilePick;
import com.fanap.podchat.util.Permission;
import com.fanap.podchat.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.orhanobut.logger.Logger;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CHAT_READY;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CLOSED;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CLOSING;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CONNECTING;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.OPEN;

public class Chat extends AsyncAdapter {
    private static Async async;
    private static Moshi moshi;
    private String token;
    private static Chat instance;
    private String platformHost;
    private static ChatListenerManager listenerManager;
    private static MessageDatabaseHelper messageDatabaseHelper;
    private long userId;
    private ContactApi contactApi;
    private static HashMap<String, Callback> messageCallbacks;
    private static HashMap<Long, ArrayList<Callback>> threadCallbacks;
    private boolean syncContact = false;
    private boolean state = false;
    private long lastSentMessageTime;
    private boolean chatState = false;
    private boolean chatReady = false;
    private static final int TOKEN_ISSUER = 1;
    private Handler pingHandler;
    private Context context;
    private boolean currentDeviceExist;
    private String fileServer;
    private boolean syncContacts = false;

    /**
     * Initialize the Chat
     **/
    public static Chat init(Context context) {
        if (instance == null) {
            async = Async.getInstance(context);
            instance = new Chat();
            moshi = new Moshi.Builder().build();
            listenerManager = new ChatListenerManager();
            messageDatabaseHelper = new MessageDatabaseHelper(context);
        }
        return instance;
    }

    /**
     * Connect to the Async .
     *
     * @param socketAddress {**REQUIRED**}
     * @param platformHost  {**REQUIRED**}
     * @param severName     {**REQUIRED**}
     * @param appId         {**REQUIRED**}
     * @param token         {**REQUIRED**}
     * @param fileServer    {**REQUIRED**}
     * @param ssoHost       {**REQUIRED**}
     */
    public void connect(String socketAddress, String appId, String severName, String token,
                        String ssoHost, String platformHost, String fileServer) {
//        Looper.prepare();
        if (platformHost.endsWith("/")) {
            pingHandler = new Handler();
            messageCallbacks = new HashMap<>();
            threadCallbacks = new HashMap<>();
            async.addListener(this);
            RetrofitHelperPlatformHost retrofitHelperPlatformHost = new RetrofitHelperPlatformHost(platformHost);
            contactApi = retrofitHelperPlatformHost.getService(ContactApi.class);
            setPlatformHost(platformHost);
            setToken(token);
            setFileServer(fileServer);
            deviceIdRequest(ssoHost, socketAddress, appId, severName);
            state = true;
        } else {
            String jsonError = getErrorOutPut("PlatformHost " + ChatConstant.ERROR_CHECK_URL, ChatConstant.ERROR_CODE_CHECK_URL);
            if (BuildConfig.DEBUG) Logger.e(jsonError);
        }
    }

    /**
     * When state of the Async changed then the chat ping is stopped buy (chatState)flag
     */
    @Override
    public void onStateChanged(String state) throws IOException {
        super.onStateChanged(state);
        listenerManager.callOnChatState(state);
        @ChatStateType.ChatSateConstant String currentChatState = state;
        switch (currentChatState) {
            case OPEN:
                chatState = true;
                //ping();
                break;
            case CHAT_READY:
                chatReady = true;
                getUserInfo();
                break;
            case CONNECTING:
            case CLOSING:
            case CLOSED:
                chatState = false;
                chatReady = false;
                break;
        }
    }

    /**
     * First we check the message type and then we set the
     * the  specific callback for that
     */
    @Override
    public void onReceivedMessage(String textMessage) throws IOException {
        super.onReceivedMessage(textMessage);
        int messageType = 0;
        JsonAdapter<ChatMessage> jsonAdapter = moshi.adapter(ChatMessage.class);
        ChatMessage chatMessage = jsonAdapter.fromJson(textMessage);

        String messageUniqueId = chatMessage.getUniqueId();
        long threadId = chatMessage.getSubjectId();
        Callback callback = messageCallbacks.get(messageUniqueId);

        if (chatMessage != null) {
            messageType = chatMessage.getType();
        }
        @ChatMessageType.Constants int currentMessageType = messageType;
        switch (currentMessageType) {
            case Constants.ADD_PARTICIPANT:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.UNBLOCK:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.BLOCK:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.CHANGE_TYPE:
                break;
            case Constants.SENT:
                handleSent(chatMessage, messageUniqueId, threadId);
                break;
            case Constants.DELIVERY:
                handleDelivery(chatMessage, messageUniqueId, threadId);
                break;
            case Constants.SEEN:
                handleSeen(chatMessage, messageUniqueId, threadId);
                break;
            case Constants.ERROR:
                handleError(chatMessage);
                break;
            case Constants.FORWARD_MESSAGE:
                handleForwardMessage(chatMessage);
                break;
            case Constants.GET_CONTACTS:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.GET_HISTORY:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.GET_STATUS:
                break;
            case Constants.GET_THREADS:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.INVITATION:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.REMOVED_FROM_THREAD:
                //TODO removed thread
                break;
            case Constants.LEAVE_THREAD:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.MESSAGE:
                handleNewMessage(chatMessage);
                break;
            case Constants.MUTE_THREAD:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.PING:
                if (BuildConfig.DEBUG) Logger.i("RECEIVED_CHAT_PING", chatMessage);
                break;
            case Constants.RELATION_INFO:
                break;
            case Constants.REMOVE_PARTICIPANT:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.RENAME:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.THREAD_PARTICIPANTS:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.UN_MUTE_THREAD:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.USER_INFO:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.USER_STATUS:
                break;
            case Constants.GET_BLOCKED:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.DELETE_MESSAGE:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.EDIT_MESSAGE:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.THREAD_INFO_UPDATED:
                OutPutInfoThread outPutInfoThread = new OutPutInfoThread();
                ResultThread resultThread = new ResultThread();
                Thread thread = JsonUtil.fromJSON(chatMessage.getContent(), Thread.class);
                resultThread.setThread(thread);
                outPutInfoThread.setResult(resultThread);
                listenerManager.callOnThreadInfoUpdated(chatMessage.getContent());
                if (BuildConfig.DEBUG) Logger.i("THREAD_INFO_UPDATED");
                if (BuildConfig.DEBUG) Logger.json(chatMessage.getContent());
                break;
            case Constants.LAST_SEEN_UPDATED:
                if (BuildConfig.DEBUG) Logger.i("LAST_SEEN_UPDATED");
                if (BuildConfig.DEBUG) Logger.i(chatMessage.getContent());
                listenerManager.callOnLastSeenUpdated(chatMessage.getContent());
                break;
            case Constants.UPDATE_THREAD_INFO:
                break;
        }
    }

    /**
     * Send text message with
     *
     * @param textMessage String that we want to sent to the thread
     * @param threadId    Id of the destination thread
     * @param metaData    if you don't have metaData you can set it to "null"
     */
    public void sendTextMessage(String textMessage, long threadId, String metaData) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(textMessage);
        chatMessage.setType(Constants.MESSAGE);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());

        if (metaData != null) {
            chatMessage.setSystemMetadata(metaData);
        }

        String uniqueId = getUniqueId();
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTime(1000);
        chatMessage.setSubjectId(threadId);

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);

        setThreadCallbacks(threadId, uniqueId);
        sendAsyncMessage(asyncContent, 4, "SEND_TEXT_MESSAGE");
    }

    /**
     * First we get the contact from server then at the respond of that
     * {@link #handleSyncContact(ChatMessage)} we add all of the PhoneContact that get from
     * {@link #getPhoneContact(Context)} that's not in the list of serverContact.
     */
    public void syncContact(Context context, Activity activity) {
        if (Permission.Check_READ_CONTACTS(activity)) {
            syncContact = true;
            getContacts(50, 0);
            setContext(context);
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_CONTACT_PERMISSION, ChatConstant.ERROR_CODE_READ_CONTACT_PERMISSION);
            if (BuildConfig.DEBUG) Logger.e(jsonError);
        }
    }

    /**
     * This method first check the type of the file and then choose the right
     * server and send that
     *
     * @param description Its the description that you want to send with file in the thread
     * @param fileUri     Uri of the file that you want to send to thread
     * @param threadId    Id of the thread that you want to send file
     * @param metaData    [optional]
     */
    public void sendFileMessage(Context context, Activity activity, String description, long threadId, Uri fileUri, String metaData) {

        String mimeType = context.getContentResolver().getType(fileUri);
        if (mimeType.equals("image/png") || mimeType.equals("image/jpeg")) {
            sendImageFileMessage(context, activity, description, threadId, fileUri, mimeType, metaData);
        } else {
            String path = FilePick.getSmartFilePath(context, fileUri);
            uploadFileMessage(activity, description, threadId, mimeType, path, metaData);
        }
    }

    public void uploadImage(Context context, Activity activity, Uri fileUri) {
        if (fileServer != null) {
            if (Permission.Check_READ_STORAGE(activity)) {
                String mimeType = context.getContentResolver().getType(fileUri);
                RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
                FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
                File file = new File(getRealPathFromURI(context, fileUri));
                if (mimeType.equals("image/png") || mimeType.equals("image/jpeg")) {

                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                    Observable<Response<FileImageUpload>> uploadObservable = fileApi.sendImageFile(body, getToken(), TOKEN_ISSUER, name);
                    uploadObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<FileImageUpload>>() {
                        @Override
                        public void call(Response<FileImageUpload> fileUploadResponse) {
                            if (fileUploadResponse.isSuccessful()) {
                                boolean hasError = fileUploadResponse.body().isHasError();
                                if (hasError) {
                                    String errorMessage = fileUploadResponse.body().getMessage();
                                    int errorCode = fileUploadResponse.body().getErrorCode();
                                    String jsonError = getErrorOutPut(errorMessage, errorCode);
                                    if (BuildConfig.DEBUG) Logger.e(jsonError);
                                } else {
                                    FileImageUpload fileImageUpload = fileUploadResponse.body();
                                    String imageJson = JsonUtil.getJson(fileImageUpload);
                                    listenerManager.callOnUploadImageFile(imageJson);
                                    if (BuildConfig.DEBUG) Logger.json(imageJson);
                                }
                            }
                        }
                    }, throwable -> Logger.e(throwable.getMessage()));
                } else {
                    String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE);
                    if (BuildConfig.DEBUG) Logger.e(jsonError);
                }
            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE);
                if (BuildConfig.DEBUG) Logger.e(jsonError);
            }
        } else {
            if (BuildConfig.DEBUG) Logger.e("FileServer url Is null");
        }

    }

    @NonNull
    private String getErrorOutPut(String errorMessage, int errorCode) {
        ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode);
        String jsonError = JsonUtil.getJson(error);
        listenerManager.callOnError(jsonError);
        return jsonError;
    }

    public void uploadFile(Context context, Activity activity, String fileUri, Uri uri) {
        if (Permission.Check_READ_STORAGE(activity)) {
            if (getFileServer() != null) {
                String mimeType = context.getContentResolver().getType(uri);
                File file = new File(fileUri);
                RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
                FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                Observable<Response<FileUpload>> uploadObservable = fileApi.sendFile(body, getToken(), TOKEN_ISSUER, name);
                uploadObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<FileUpload>>() {
                    @Override
                    public void call(Response<FileUpload> fileUploadResponse) {
                        if (fileUploadResponse.isSuccessful()) {
                            boolean hasError = fileUploadResponse.body().isHasError();
                            if (hasError) {
                                String errorMessage = fileUploadResponse.body().getMessage();
                                int errorCode = fileUploadResponse.body().getErrorCode();
                                String jsonError = getErrorOutPut(errorMessage, errorCode);
                                if (BuildConfig.DEBUG) Logger.e(jsonError);
                            } else {
                                FileUpload result = fileUploadResponse.body();
                                String json = JsonUtil.getJson(result);
                                listenerManager.callOnUploadFile(json);
                                if (BuildConfig.DEBUG) Logger.json(json);
                            }
                        }
                    }
                }, throwable -> {
                    if (BuildConfig.DEBUG) Logger.e(throwable.getMessage());
                });
            } else {
                if (BuildConfig.DEBUG) Logger.e("FileServer url Is null");
            }

        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE);
            if (BuildConfig.DEBUG) Logger.e(jsonError);
        }
    }

    /**
     * Remove the peerId and send ping again but this time
     * peerId that was set in the server was removed
     */
    public void logOutSocket() {
        async.logOut();
    }

    /**
     * Notice : You should consider that this method is for rename group and you have to be the admin
     * to change the thread name if not you don't have the Permission
     */
    public void renameThread(long threadId, String title) {
        String uniqueId = getUniqueId();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.RENAME);
        chatMessage.setSubjectId(threadId);
        chatMessage.setContent(title);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");
        chatMessage.setUniqueId(uniqueId);
        setCallBacks(null, null, null, true, Constants.RENAME, null, uniqueId);
        String asyncContent = JsonUtil.getJson(chatMessage);
        sendAsyncMessage(asyncContent, 4, "SEND_RENAME_THREAD");
    }

    /**
     * @param contactIds List of CONTACT IDs
     * @param threadId   Id of the thread that you are {*NOTICE*}admin of that and you want to
     *                   add someone as a participant.
     */
    public void addParticipants(long threadId, List<Long> contactIds) {

        AddParticipant addParticipant = new AddParticipant();
        String uniqueId = getUniqueId();
        addParticipant.setSubjectId(threadId);
        addParticipant.setUniqueId(uniqueId);
        JsonArray contacts = new JsonArray();
        for (Long p : contactIds) {
            contacts.add(p);
        }
        addParticipant.setContent(contacts.toString());
        addParticipant.setSubjectId(threadId);
        addParticipant.setToken(getToken());
        addParticipant.setTokenIssuer("1");
        addParticipant.setUniqueId(uniqueId);
        addParticipant.setType(Constants.ADD_PARTICIPANT);
        String asyncContent = JsonUtil.getJson(addParticipant);
        setCallBacks(null, null, null, true, Constants.ADD_PARTICIPANT, null, uniqueId);
        sendAsyncMessage(asyncContent, 4, "SEND_ADD_PARTICIPANTS");
    }

    /**
     * @param participantIds List of PARTICIPANT IDs from Thread's Participants object
     * @param threadId       Id of the thread that we wants to remove their participant
     */
    public void removeParticipants(long threadId, List<Long> participantIds) {

        String uniqueId = getUniqueId();
        RemoveParticipant removeParticipant = new RemoveParticipant();
        removeParticipant.setTokenIssuer("1");
        removeParticipant.setType(Constants.REMOVE_PARTICIPANT);
        removeParticipant.setSubjectId(threadId);
        removeParticipant.setToken(getToken());
        removeParticipant.setUniqueId(uniqueId);

        JsonArray contacts = new JsonArray();
        for (Long p : participantIds) {
            contacts.add(p);
        }
        removeParticipant.setContent(contacts.toString());

        String asyncContent = JsonUtil.getJson(removeParticipant);
        sendAsyncMessage(asyncContent, 4, "SEND_REMOVE_PARTICIPANT");
        setCallBacks(null, null, null, true, Constants.REMOVE_PARTICIPANT, null, uniqueId);
    }

    public void leaveThread(long threadId) {
        String uniqueId = getUniqueId();
        RemoveParticipant removeParticipant = new RemoveParticipant();

        removeParticipant.setSubjectId(threadId);
        removeParticipant.setToken(getToken());
        removeParticipant.setTokenIssuer("1");
        removeParticipant.setUniqueId(uniqueId);
        removeParticipant.setType(Constants.LEAVE_THREAD);

        setCallBacks(null, null, null, true, Constants.LEAVE_THREAD, null, uniqueId);
        String json = JsonUtil.getJson(removeParticipant);
        sendAsyncMessage(json, 4, "SEND_LEAVE_THREAD");
    }

    /**
     * forward message
     *
     * @param threadId   destination thread id
     * @param messageIds Array of message ids that we want to forward them
     */
    public void forwardMessage(long threadId, ArrayList<Long> messageIds) {
        ChatMessageForward chatMessageForward = new ChatMessageForward();
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> uniqueIds = new ArrayList<>();
        chatMessageForward.setSubjectId(threadId);
        ArrayList<Callback> callbacks = new ArrayList<>();

        for (int i = 0; i < messageIds.size(); i++) {
            String uniqueId = getUniqueId();
            uniqueIds.add(uniqueId);
            Callback callback = new Callback();
            callback.setDelivery(true);
            callback.setSeen(true);
            callback.setSent(true);
            callback.setUniqueId(uniqueId);
            callbacks.add(callback);
        }
        threadCallbacks.put(threadId, callbacks);
        try {
            chatMessageForward.setUniqueId(mapper.writeValueAsString(uniqueIds));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        chatMessageForward.setContent(messageIds.toString());
        chatMessageForward.setToken(getToken());
        chatMessageForward.setTokenIssuer("1");
        chatMessageForward.setType(Constants.FORWARD_MESSAGE);

        String asyncContent = JsonUtil.getJson(chatMessageForward);
        sendAsyncMessage(asyncContent, 4, "SEND_FORWARD_MESSAGE");
    }

    /**
     * Reply the message in the current thread and send az message and receive at the
     *
     * @param messageContent content of the reply message
     * @param threadId       id of the thread
     * @param messageId      of that message we want to reply
     */
    public void replyMessage(String messageContent, long threadId, long messageId) {
        String uniqueId = getUniqueId();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setRepliedTo(messageId);
        chatMessage.setSubjectId(threadId);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        chatMessage.setContent(messageContent);
        chatMessage.setTime(1000);
        chatMessage.setType(Constants.MESSAGE);
        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);

        setThreadCallbacks(threadId, uniqueId);
        sendAsyncMessage(asyncContent, 4, "SEND_REPLY_MESSAGE");
    }

    /**
     * DELETE MESSAGE IN THREAD
     *
     * @param messageId    Id of the message that you want to be removed.
     * @param deleteForAll If you want to delete message for everyone you can set it true if u dont want
     *                     you can set it false or even null.
     */
    public void deleteMessage(long messageId, Boolean deleteForAll) {
        deleteForAll = deleteForAll != null ? deleteForAll : false;
        String uniqueId = getUniqueId();
        BaseMessage baseMessage = new BaseMessage();
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setDeleteForAll(deleteForAll);
        String content = JsonUtil.getJson(deleteMessage);
        baseMessage.setContent(content);
        baseMessage.setSubjectId(messageId);
        baseMessage.setToken(getToken());
        baseMessage.setTokenIssuer("1");
        baseMessage.setType(Constants.DELETE_MESSAGE);
        baseMessage.setUniqueId(uniqueId);

        String asyncContent = JsonUtil.getJson(baseMessage);
        sendAsyncMessage(asyncContent, 4, "SEND_DELETE_MESSAGE");
        setCallBacks(null, null, null, true, Constants.DELETE_MESSAGE, null, uniqueId);
    }

    //TODO implement cache

    /**
     * Get the list of threads or you can just pass the thread id that you want
     *
     * @param count  number of thread
     * @param offset specified offset you want
     */
    public void getThreads(int count, int offset, ArrayList<Integer> threadIds, String threadName) {

//        OutPutThreads outPutThreads = new OutPutThreads();
//        ResultThreads resultThreads = new ResultThreads();
//        resultThreads.setThreads(messageDatabaseHelper.getThread());
//        outPutThreads.setContentCount(messageDatabaseHelper.getThread().size());
//        outPutThreads.setErrorCode(0);
//        outPutThreads.setErrorMessage("");
//        outPutThreads.setHasError(false);
//        outPutThreads.setResult(resultThreads);
//
//        String threadJson = JsonUtil.getJson(outPutThreads);
//        listenerManager.callOnGetThread(threadJson);

        String asyncContent = null;
        if (chatReady) {
            ChatMessageContent chatMessageContent = new ChatMessageContent();
            chatMessageContent.setCount(count);
            chatMessageContent.setOffset(offset);
            if (threadName != null) {
                chatMessageContent.setName(threadName);
            }
            if (threadIds != null) {
                chatMessageContent.setThreadIds(threadIds);
            }
            JsonAdapter<ChatMessageContent> messageContentJsonAdapter = moshi.adapter(ChatMessageContent.class);
            String content = messageContentJsonAdapter.toJson(chatMessageContent);

            String uniqueId = getUniqueId();
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(content);
            chatMessage.setType(Constants.GET_THREADS);
            chatMessage.setTokenIssuer("1");
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);

            JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
            asyncContent = chatMessageJsonAdapter.toJson(chatMessage);
            setCallBacks(null, null, null, true, Constants.GET_THREADS, offset, uniqueId);
        }

        sendAsyncMessage(asyncContent, 3, "Get thread send");
    }

    /**
     * Get history of the thread
     *
     * @param count    count of the messages
     * @param order    If order is empty [default = desc] and also you have two option [ asc | desc ]
     * @param threadId Id of the thread that we want to get the history
     */
    public void getHistory(int count, int offset, String order, long threadId) {
        History history = new History();
        if (order != null) {
            history.setOrder(order);
        }
        history.setCount(count);
        history.setOffset(offset);
        JsonAdapter<History> messageContentJsonAdapter = moshi.adapter(History.class);
        String content = messageContentJsonAdapter.toJson(history);

        String uniqueId = getUniqueId();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setType(Constants.GET_HISTORY);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);
        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);
        setCallBacks(null, null, null, true, Constants.GET_HISTORY, offset, uniqueId);
        sendAsyncMessage(asyncContent, 3, "SEND GET THREAD HISTORY");
    }

    public void searchHistory(NosqlListMessageCriteriaVO messageCriteriaVO) {

        JsonAdapter<NosqlListMessageCriteriaVO> messageContentJsonAdapter = moshi.adapter(NosqlListMessageCriteriaVO.class);
        String content = messageContentJsonAdapter.toJson(messageCriteriaVO);

        String uniqueId = getUniqueId();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setType(Constants.GET_HISTORY);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(messageCriteriaVO.getMessageThreadId());
        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);

        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);
        setCallBacks(null, null, null, true, Constants.GET_HISTORY, messageCriteriaVO.getOffset(), uniqueId);
        sendAsyncMessage(asyncContent, 3, "SEND SEARCH0. HISTORY");
    }

    /**
     * Get all of the contacts of the user
     */
    public void getContacts(int count, int offset) {

        ArrayList<Contact> arrayList = new ArrayList<>(messageDatabaseHelper.getContacts());
        OutPutContact outPutContact = new OutPutContact();

        ResultContact resultContact = new ResultContact();
        resultContact.setContacts(arrayList);
        outPutContact.setResult(resultContact);
        outPutContact.setContentCount(messageDatabaseHelper.getContacts().size());
        String contactJson = JsonUtil.getJson(outPutContact);

        listenerManager.callOnGetContacts(contactJson);

        if (chatReady) {
            ChatMessageContent chatMessageContent = new ChatMessageContent();
            chatMessageContent.setCount(count);
            chatMessageContent.setOffset(offset);

            JsonAdapter<ChatMessageContent> messageContentJsonAdapter = moshi.adapter(ChatMessageContent.class);
            String content = messageContentJsonAdapter.toJson(chatMessageContent);
            String uniqueId = getUniqueId();
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(content);
            chatMessage.setType(Constants.GET_CONTACTS);
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);

            JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
            String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);
            setCallBacks(null, null, null, true, Constants.GET_CONTACTS, offset, uniqueId);
            sendAsyncMessage(asyncContent, 3, "GET_CONTACT_SEND");
        }
    }

    public void searchContact(SearchContact searchContact) {
        if (chatReady) {
            Observable<Response<SearchContactVO>> observable = contactApi.searchContact(getToken(), TOKEN_ISSUER,
                    searchContact.getId()
                    , searchContact.getFirstName()
                    , searchContact.getLastName()
                    , searchContact.getEmail()
                    , getUniqueId()
                    , searchContact.getOffset()
                    , searchContact.getSize()
                    , searchContact.getTypeCode()
                    , searchContact.getQuery()
                    , searchContact.getCellphoneNumber());
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<SearchContactVO>>() {
                @Override
                public void call(Response<SearchContactVO> contactResponse) {

                    SearchContactVO contact = contactResponse.body();
                    String json = JsonUtil.getJson(contact);
                    listenerManager.callOnSearchContact(json);
                    if (BuildConfig.DEBUG) Logger.json(json);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_SEARCH_CONTACT");
                }
            }, (Throwable throwable) -> Logger.e(throwable.getMessage()));
        } else {
            if (BuildConfig.DEBUG) Logger.e("Chat is not ready");
        }
    }

    /**
     * Add one contact to the contact list
     *
     * @param firstName       Notice: if just put fistName without lastName its ok.
     * @param lastName        last name of the contact
     * @param cellphoneNumber Notice: If you just  put the cellPhoneNumber doesn't necessary to add email
     * @param email           email of the contact
     */
    public void addContact(String firstName, String lastName, String cellphoneNumber, String email) {
        String uniqueId = getUniqueId();
        Observable<Response<Contacts>> addContactObservable;
        if (getPlatformHost() != null) {
            addContactObservable = contactApi.addContact(getToken(), 1, firstName, lastName, email, uniqueId, cellphoneNumber);
            addContactObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(addContactResponse -> {
                if (addContactResponse.isSuccessful()) {
                    Contacts contacts = addContactResponse.body();
                    if (!contacts.getHasError()) {
                        OutPutAddContact outPutAddContact = Util.getReformatOutPutAddContact(contacts);

                        String contactsJson = JsonUtil.getJson(outPutAddContact);
                        listenerManager.callOnAddContact(contactsJson);
                        if (BuildConfig.DEBUG) Logger.json(contactsJson);
                        if (BuildConfig.DEBUG) Logger.i("RECEIVED_ADD_CONTACT");
                    } else {
                        String jsonError = getErrorOutPut(contacts.getMessage(), contacts.getErrorCode());
                        if (BuildConfig.DEBUG) Logger.e(jsonError);
                    }

                }
            }, (Throwable throwable) ->
            {
                Logger.e("Error on add contact", throwable.toString());
            });
        } else {
            if (BuildConfig.DEBUG) Logger.e("PlatformHost Address Is Empty!");
        }
    }

    /**
     * Remove contact with the user id
     *
     * @param userId id of the user that we want to remove from contact list
     */
    public void removeContact(long userId) {
        if (getPlatformHost() != null) {
            Observable<Response<ContactRemove>> removeContactObservable = contactApi.removeContact(getToken(), 1, userId);
            removeContactObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
                if (response.isSuccessful()) {
                    ContactRemove contactRemove = response.body();
                    if (!contactRemove.getHasError()) {
                        String contactRemoveJson = JsonUtil.getJson(contactRemove);
                        listenerManager.callOnRemoveContact(contactRemoveJson);
                        if (BuildConfig.DEBUG) Logger.json(contactRemoveJson);
                    } else {
                        String jsonError = getErrorOutPut(contactRemove.getErrorMessage(), contactRemove.getErrorCode());
                        if (BuildConfig.DEBUG) Logger.e(jsonError);
                    }
                }
            }, (Throwable throwable) -> {
                if (BuildConfig.DEBUG) Logger.e("Error on remove contact", throwable.getMessage());
                if (BuildConfig.DEBUG) Logger.e(throwable.getMessage());
            });
        } else {
            if (BuildConfig.DEBUG) Logger.e("PlatformHost address is :Empty");
        }
    }

    /**
     * Update contacts
     * all of the params all required to update
     */
    public void updateContact(long userId, String firstName, String lastName, String cellphoneNumber, String email) {
        Observable<Response<UpdateContact>> updateContactObservable = contactApi.updateContact(getToken(), 1
                , userId, firstName, lastName, email, getUniqueId(), cellphoneNumber);
        updateContactObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
            if (response.isSuccessful()) {
                UpdateContact updateContact = response.body();
                if (!response.body().getHasError()) {
                    OutPutUpdateContact outPut = new OutPutUpdateContact();
                    outPut.setMessage(updateContact.getMessage());
                    outPut.setErrorCode(updateContact.getErrorCode());
                    outPut.setHasError(updateContact.getHasError());
                    outPut.setOtt(updateContact.getOtt());
                    outPut.setReferenceNumber(updateContact.getReferenceNumber());
                    outPut.setCount(updateContact.getCount());
                    ResultUpdateContact resultUpdateContact = new ResultUpdateContact();
                    resultUpdateContact.setContacts(updateContact.getResult());
                    outPut.setResult(resultUpdateContact);
                    String json = JsonUtil.getJson(outPut);
                    listenerManager.callOnUpdateContact(json);
                    Logger.json(json);
                } else {
                    String jsonError = getErrorOutPut(response.body().getMessage(), response.body().getErrorCode());
                    if (BuildConfig.DEBUG) Logger.e(jsonError);
                }
            }
        }, (Throwable throwable) ->
        {
            if (throwable != null) {
                Logger.e("cause" + "" + throwable.getCause());
            }
        });
    }

    public void mapSearch(String searchTerm, Double latitude, Double longitude) {
        RetrofitHelperMap retrofitHelperMap = new RetrofitHelperMap("https://api.neshan.org/");
        MapApi mapApi = retrofitHelperMap.getService(MapApi.class);
        Observable<Response<MapNeshan>> observable = mapApi.mapSearch("8b77db18704aa646ee5aaea13e7370f4f88b9e8c"
                , searchTerm, latitude, longitude);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<MapNeshan>>() {
            @Override
            public void call(Response<MapNeshan> mapNeshanResponse) {
                if (mapNeshanResponse.isSuccessful()) {
                    MapNeshan mapNeshan = mapNeshanResponse.body();

                    OutPutMapNeshan outPutMapNeshan = new OutPutMapNeshan();
                    outPutMapNeshan.setCount(mapNeshan.getCount());
                    ResultMap resultMap = new ResultMap();
                    resultMap.setMaps(mapNeshan.getItems());
                    outPutMapNeshan.setResult(resultMap);
                    String json = JsonUtil.getJson(outPutMapNeshan);
                    listenerManager.callOnMapSearch(json);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_MAP_SEARCH");
                    if (BuildConfig.DEBUG) Logger.json(json);
                }
            }
        }, (Throwable throwable) -> listenerManager.callOnError(throwable.getMessage()));
    }

    public void mapRouting(String origin, String destination) {
        RetrofitHelperMap retrofitHelperMap = new RetrofitHelperMap("https://api.neshan.org/");
        MapApi mapApi = retrofitHelperMap.getService(MapApi.class);
        Observable<Response<MapRout>> responseObservable = mapApi.mapRouting("8b77db18704aa646ee5aaea13e7370f4f88b9e8c"
                , origin, destination, true);
        responseObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<MapRout>>() {
            @Override
            public void call(Response<MapRout> mapRoutResponse) {
                if (mapRoutResponse.isSuccessful()) {
                    MapRout mapRout = mapRoutResponse.body();
                    OutPutMapRout outPutMapRout = new OutPutMapRout();
                    outPutMapRout.setResult(mapRout);
                    String jsonMapRout = JsonUtil.getJson(outPutMapRout);
                    listenerManager.callOnMapRouting(jsonMapRout);
                    Logger.i("RECEIVE_MAP_ROUTING");
                    Logger.json(jsonMapRout);
                }
            }
        }, (Throwable throwable) -> {
            Logger.e(throwable, "Error on map routing");
        });
    }

    public void block(Long contactId) {
        BlockContactId blockAcount = new BlockContactId();
        blockAcount.setContactId(contactId);
        String uniqueId = getUniqueId();
        String json = JsonUtil.getJson(blockAcount);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(json);
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTokenIssuer("1");
        chatMessage.setType(Constants.BLOCK);
        setCallBacks(null, null, null, true, Constants.BLOCK, null, uniqueId);
        String asyncContent = JsonUtil.getJson(chatMessage);
        sendAsyncMessage(asyncContent, 4, "SEND_BLOCK");
    }

    public void unblock(long blockId) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSubjectId(blockId);
        String uniqueId = getUniqueId();
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTokenIssuer("1");
        chatMessage.setType(Constants.UNBLOCK);
        setCallBacks(null, null, null, true, Constants.UNBLOCK, null, uniqueId);
        String asyncContent = JsonUtil.getJson(chatMessage);
        sendAsyncMessage(asyncContent, 4, "SEND_UN_BLOCK");
    }

    public void getBlockList(Integer count, Integer offset) {

        ChatMessageContent chatMessageContent = new ChatMessageContent();
        if (offset != null) {
            chatMessageContent.setOffset(offset);
        }
        if (count != null) {
            chatMessageContent.setCount(count);
        }
        String json = JsonUtil.getJson(chatMessageContent);

        String uniqueId = getUniqueId();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(json);
        chatMessage.setType(Constants.GET_BLOCKED);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(uniqueId);
        setCallBacks(null, null, null, true, Constants.GET_BLOCKED, null, uniqueId);
        String asyncContent = JsonUtil.getJson(chatMessage);
        sendAsyncMessage(asyncContent, 4, "SEND_UN_BLOCK");
    }

    private class BlockContactId {
        private long contactId;

        public long getContactId() {
            return contactId;
        }

        public void setContactId(long contactId) {
            this.contactId = contactId;
        }
    }

    /**
     * Create the thread to p to p/channel/group. The list below is showing all of the thread type
     * int NORMAL = 0;
     * int OWNER_GROUP = 1;
     * int PUBLIC_GROUP = 2;
     * int CHANNEL_GROUP = 4;
     * int CHANNEL = 8;
     */
    public void createThread(int threadType, Invitee[] invitee, String threadTitle) {
        List<Invitee> invitees = new ArrayList<>(Arrays.asList(invitee));
        ChatThread chatThread = new ChatThread();
        chatThread.setThreadType(threadType);
        chatThread.setInvitees(invitees);
        chatThread.setTitle(threadTitle);

        String contentThreadChat = JsonUtil.getJson(chatThread);
        String uniqueId = getUniqueId();
        ChatMessage chatMessage = getChatMessage(contentThreadChat, uniqueId);

        setCallBacks(null, null, null, true, Constants.INVITATION, null, uniqueId);
        String asyncContent = JsonUtil.getJson(chatMessage);
        sendAsyncMessage(asyncContent, 4, "SEND_CREATE_THREAD");
    }


    public void updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO) {

        String content = JsonUtil.getJson(threadInfoVO);

        String uniqueId = getUniqueId();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        chatMessage.setSubjectId(threadId);
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setType(Constants.UPDATE_THREAD_INFO);
        chatMessage.setContent(content);

        String asyncContent = JsonUtil.getJson(chatMessage);
        setCallBacks(null, null, null, true, Constants.UPDATE_THREAD_INFO, null, uniqueId);
        sendAsyncMessage(asyncContent, 4, "SEND_CREATE_THREAD");

    }

    /**
     * Get the participant list of specific thread
     *
     * @param threadId id of the thread we want to ge the participant list
     */
    public void getThreadParticipants(int count, int offset, long threadId) {
        ChatMessageContent chatMessageContent = new ChatMessageContent();
        chatMessageContent.setCount(count);
        chatMessageContent.setOffset(offset);

        JsonAdapter<ChatMessageContent> messageContentJsonAdapter = moshi.adapter(ChatMessageContent.class);
        String content = messageContentJsonAdapter.toJson(chatMessageContent);
        String uniqueId = getUniqueId();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setType(Constants.THREAD_PARTICIPANTS);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);
        setCallBacks(null, null, null, true, Constants.THREAD_PARTICIPANTS, offset, uniqueId);
        sendAsyncMessage(asyncContent, 3, "SEND_THREAD_PARTICIPANT");
    }

    public void seenMessage(long messageId, long ownerId) {

        if (ownerId != getUserId()) {
            ChatMessage message = new ChatMessage();
            message.setType(Constants.SEEN);
            message.setContent(String.valueOf(messageId));
            message.setTokenIssuer("1");
            message.setToken(getToken());
            message.setUniqueId(getUniqueId());
            message.setTime(1000);

            JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
            String asyncContent = chatMessageJsonAdapter.toJson(message);
            sendAsyncMessage(asyncContent, 4, "SEND_SEEN_MESSAGE");
        }
    }

    /**
     * Get the information of the current user
     */
    public void getUserInfo() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.USER_INFO);
        String uniqueId = getUniqueId();
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");

        setCallBacks(null, null, null, true, Constants.USER_INFO, null, uniqueId);
        String asyncContent = JsonUtil.getJson(chatMessage);
        sendAsyncMessage(asyncContent, 3, "SEND_USER_INFO");
    }

    /**
     * Mute the thread so notification is off for that thread
     */
    public void muteThread(int threadId) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.MUTE_THREAD);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");
        chatMessage.setSubjectId(threadId);
        String uniqueId = getUniqueId();
        chatMessage.setUniqueId(uniqueId);
        setCallBacks(null, null, null, true, Constants.MUTE_THREAD, null, uniqueId);

        String asyncContent = JsonUtil.getJson(chatMessage);
        sendAsyncMessage(asyncContent, 4, "SEND_MUTE_THREAD");
    }

    /**
     * Unmute the thread so notification is on for that thread
     */
    public void unmuteThread(int threadId) {
        String uniqueId = getUniqueId();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.UN_MUTE_THREAD);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");
        chatMessage.setSubjectId(threadId);
        chatMessage.setUniqueId(uniqueId);

        setCallBacks(null, null, null, true, Constants.UN_MUTE_THREAD, null, uniqueId);
        String asyncContent = JsonUtil.getJson(chatMessage);
        sendAsyncMessage(asyncContent, 4, "SEND_UN_MUTE_THREAD");
    }

    /**
     * Message can be edit when you pass the message id and the edited
     * content to editMessage function
     */
    public void editMessage(int messageId, String messageContent) {
        String uniqueId = getUniqueId();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.EDIT_MESSAGE);
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(messageId);
        chatMessage.setContent(messageContent);
        chatMessage.setTokenIssuer("1");

        String asyncContent = JsonUtil.getJson(chatMessage);
        setCallBacks(null, null, null, true, Constants.EDIT_MESSAGE, null, uniqueId);
        sendAsyncMessage(asyncContent, 4, "SEND_EDIT_MESSAGE");
    }

    /**
     * Add a listener to receive events on this Chat.
     *
     * @param listener A listener to add.
     * @return {@code this} object.
     */
    public Chat addListener(ChatListener listener) {
        listenerManager.addListener(listener);
        return this;
    }

    public Chat addListeners(List<ChatListener> listeners) {
        listenerManager.addListeners(listeners);
        return this;
    }

    public Chat removeListener(ChatListener listener) {
        listenerManager.removeListener(listener);
        return this;
    }

    public LiveData<String> getState() {
        return async.getStateLiveData();
    }

    /**
     * Ping for staying chat alive
     */
    private void ping() {
        if (chatState && async.getPeerId() != null) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(Constants.PING);
            chatMessage.setTokenIssuer("1");
            chatMessage.setToken(getToken());
            JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
            String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);
            sendAsyncMessage(asyncContent, 4, "CHAT PING");
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void handleError(ChatMessage chatMessage) {
        OutPut outPut = new OutPut();
        Error error = JsonUtil.fromJSON(chatMessage.getContent(), Error.class);
        if (error.getCode() == 401) {
            pingHandler.removeCallbacksAndMessages(null);
        }
        outPut.setErrorMessage(error.getMessage());
        outPut.setErrorCode(error.getCode());
        String errorJson = JsonUtil.getJson(outPut);
        listenerManager.callOnError(errorJson);
        if (BuildConfig.DEBUG) Logger.e("ErrorMessage:" + error.getMessage());
        if (BuildConfig.DEBUG) Logger.e("ErrorCode:" + " " + String.valueOf(error.getCode()));
    }

    /**
     * When the new message arrived we send the deliver message to the sender user.
     */
    private void handleNewMessage(ChatMessage chatMessage) {
        if (BuildConfig.DEBUG) Logger.i("RECEIVED_NEW_MESSAGE");
        if (BuildConfig.DEBUG) Logger.json(chatMessage.getContent());
        MessageVO messageVO = JsonUtil.fromJSON(chatMessage.getContent(), MessageVO.class);
        OutPutNewMessage outPutNewMessage = new OutPutNewMessage();
        outPutNewMessage.setResult(messageVO);
        String json = JsonUtil.getJson(outPutNewMessage);
        listenerManager.callOnNewMessage(json);
        long ownerId = 0;
        if (messageVO != null) {
            ownerId = messageVO.getParticipant().getId();
        }
        if (ownerId != getUserId()) {
            ChatMessage message = getChatMessage(messageVO);
            JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
            String asyncContent = chatMessageJsonAdapter.toJson(message);
            async.sendMessage(asyncContent, 4);
            Logger.i("SEND_DELIVERY_MESSAGE");
            Logger.json(asyncContent);
        }
    }

    private void handleSent(ChatMessage chatMessage, String messageUniqueId, long threadId) {

        if (threadCallbacks.containsKey(threadId)) {
            ArrayList<Callback> callbacks = threadCallbacks.get(threadId);
            for (Callback callback : callbacks) {
                if (messageUniqueId.equals(callback.getUniqueId())) {
                    int indexUnique = callbacks.indexOf(callback);
                    if (callbacks.get(indexUnique).isSent()) {
                        listenerManager.callOnSentMessage(chatMessage.getContent());
                        Callback callbackUpdateSent = new Callback();
                        callbackUpdateSent.setSent(false);
                        callbackUpdateSent.setDelivery(callback.isDelivery());
                        callbackUpdateSent.setSeen(callback.isSeen());
                        callbackUpdateSent.setUniqueId(callback.getUniqueId());

                        callbacks.set(indexUnique, callbackUpdateSent);
                        threadCallbacks.put(threadId, callbacks);
                        if (BuildConfig.DEBUG) Logger.i("Is Sent" + " " + callback.getUniqueId());
                    }
                    break;
                }
            }
        }
    }

    private void handleSeen(ChatMessage chatMessage, String messageUniqueId, long threadId) {

        if (threadCallbacks.containsKey(threadId)) {
            ArrayList<Callback> callbacks = threadCallbacks.get(threadId);
            for (Callback callback : callbacks) {
                if (messageUniqueId.equals(callback.getUniqueId())) {
                    int indexUnique = callbacks.indexOf(callback);
                    while (indexUnique > -1) {
                        if (callbacks.get(indexUnique).isSeen()) {
                            if (callbacks.get(indexUnique).isDelivery()) {
                                listenerManager.callOnDeliveryMessage(callback.getUniqueId());

                                Callback callbackUpdateSent = new Callback();
                                callbackUpdateSent.setSent(callback.isSent());
                                callbackUpdateSent.setDelivery(false);
                                callbackUpdateSent.setSeen(callback.isSeen());
                                callbackUpdateSent.setUniqueId(callback.getUniqueId());
                                callbacks.set(indexUnique, callbackUpdateSent);
                                threadCallbacks.put(threadId, callbacks);
                                if (BuildConfig.DEBUG)
                                    Logger.i("Is Delivered" + " " + "Unique Id" + callback.getUniqueId());
                            }
                            listenerManager.callOnSeenMessage(callback.getUniqueId());
                            callbacks.remove(indexUnique);
                            threadCallbacks.put(threadId, callbacks);
                            if (BuildConfig.DEBUG)
                                Logger.i("Is Seen" + " " + "Unique Id" + callback.getUniqueId());
                        }
                        indexUnique--;
                    }
                    break;
                }
            }
        }
    }

    private void handleDelivery(ChatMessage chatMessage, String messageUniqueId, long threadId) {
        if (threadCallbacks.containsKey(threadId)) {
            ArrayList<Callback> callbacks = threadCallbacks.get(threadId);
            for (Callback callback : callbacks) {
                if (messageUniqueId.equals(callback.getUniqueId())) {
                    int indexUnique = callbacks.indexOf(callback);
                    while (indexUnique > -1) {
                        if (callbacks.get(indexUnique).isDelivery()) {
                            listenerManager.callOnDeliveryMessage(callback.getUniqueId());

                            Callback callbackUpdateSent = new Callback();
                            callbackUpdateSent.setSent(callback.isSent());
                            callbackUpdateSent.setDelivery(false);
                            callbackUpdateSent.setSeen(callback.isSeen());
                            callbackUpdateSent.setUniqueId(callback.getUniqueId());
                            callbacks.set(indexUnique, callbackUpdateSent);
                            threadCallbacks.put(threadId, callbacks);
                            if (BuildConfig.DEBUG) {
                                Logger.i("Is Delivered" + " " + callback.getUniqueId());
                            }
                        }
                        indexUnique--;
                    }
                    break;
                }
            }
        }
    }

    private void handleForwardMessage(ChatMessage chatMessage) {
        if (BuildConfig.DEBUG) Logger.i("RECEIVED_FORWARD_MESSAGE");
        if (BuildConfig.DEBUG) Logger.json(chatMessage.getContent());
        MessageVO jsonMessage = JsonUtil.fromJSON(chatMessage.getContent(), MessageVO.class);
        long ownerId = 0;
        if (jsonMessage != null) {
            ownerId = jsonMessage.getParticipant().getId();
        }
        if (ownerId != getUserId()) {
            ChatMessage message = getChatMessage(jsonMessage);
            JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
            String asyncContent = chatMessageJsonAdapter.toJson(message);
            if (BuildConfig.DEBUG) Logger.i("SEND_DELIVERY_MESSAGE");
            if (BuildConfig.DEBUG) Logger.json(asyncContent);
            async.sendMessage(asyncContent, 4);
        }
    }

    private void handleSyncContact(ChatMessage chatMessage) {
        Type type = Types.newParameterizedType(List.class, Contact.class);
        JsonAdapter<List<Contact>> adapter = moshi.adapter(type);
        ArrayList<String> firstNames = new ArrayList<>();
        ArrayList<String> cellphoneNumbers = new ArrayList<>();
        try {
            List<Contact> serverContacts = adapter.fromJson(chatMessage.getContent());
            if (serverContacts != null) {
                List<Contact> phoneContacts = getPhoneContact(getContext());
                HashMap<String, String> mapServerContact = new HashMap<>();
                for (int a = 0; a < serverContacts.size(); a++) {
                    mapServerContact.put(serverContacts.get(a).getCellphoneNumber(), serverContacts.get(a).getFirstName());
                }
                for (int j = 0; j < phoneContacts.size(); j++) {
                    if (!mapServerContact.containsKey(phoneContacts.get(j).getCellphoneNumber())) {
                        firstNames.add(phoneContacts.get(j).getFirstName());
                        cellphoneNumbers.add(phoneContacts.get(j).getCellphoneNumber());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!firstNames.isEmpty() || !cellphoneNumbers.isEmpty()) {
            addContacts(firstNames, cellphoneNumbers);
            syncContacts = true;
        }
        syncContact = false;
    }

    private void handleResponseMessage(Callback callback, ChatMessage chatMessage, String messageUniqueId) {
        OutPut outPut = new OutPut();
        switch (callback.getRequestType()) {
            case Constants.REMOVED_FROM_THREAD:
                listenerManager.callOnRemovedFromThread(chatMessage.getContent());
                break;
            case Constants.GET_HISTORY:

                ResultsHistory resultsHistory = new ResultsHistory();
                resultsHistory.setContentCount(chatMessage.getContentCount());
                if (chatMessage.getContent().length() + callback.getOffset() < chatMessage.getContentCount()) {
                    resultsHistory.setHasNext(true);
                } else {
                    resultsHistory.setHasNext(false);
                }
                resultsHistory.setHistory(chatMessage.getContent());
                resultsHistory.setNextOffset(callback.getOffset() + chatMessage.getContent().length());
                outPut.setErrorCode(0);
                outPut.setHasError(false);
                outPut.setErrorMessage("");

                String json = JsonUtil.getJson(outPut);
                listenerManager.callOnGetThreadHistory(json);
                messageCallbacks.remove(messageUniqueId);
                if (BuildConfig.DEBUG) Logger.i("RECEIVE_GET_HISTORY");
                if (BuildConfig.DEBUG) Logger.json(json);
                break;
            case Constants.GET_CONTACTS:
                if (syncContact) {
                    handleSyncContact(chatMessage);
                } else {
                    OutPutContact outPutContact = new OutPutContact();
                    if (callback.isResult()) {
                        String contactJson = reformatGetContactResponse(chatMessage, outPutContact);
                        listenerManager.callOnGetContacts(contactJson);
                        messageCallbacks.remove(messageUniqueId);
                        if (BuildConfig.DEBUG) Logger.i("RECEIVE_GET_CONTACT");
                        if (BuildConfig.DEBUG) Logger.json(contactJson);
                    }
                }
                break;
            case Constants.GET_THREADS:
                OutPutThreads outPutThreads = new OutPutThreads();

                if (callback.isResult()) {
                    String threadJson = reformatGetThreadsResponse(chatMessage, outPutThreads, callback);
                    listenerManager.callOnGetThread(threadJson);
                    messageCallbacks.remove(messageUniqueId);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_GET_THREAD");
                    if (BuildConfig.DEBUG) Logger.json(threadJson);
                }
                break;
            case Constants.INVITATION:

                if (callback.isResult()) {
                    OutPutThread outPutThread = new OutPutThread();
                    String inviteJson = reformatCreateThread(chatMessage, outPutThread);
                    listenerManager.callOnCreateThread(inviteJson);
                    messageCallbacks.remove(messageUniqueId);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_CREATE_THREAD");
                    if (BuildConfig.DEBUG) Logger.json(inviteJson);
                }
                break;
            case Constants.MUTE_THREAD:

                if (callback.isResult()) {
                    String muteThreadJson = reformatMuteThread(chatMessage, outPut);
                    listenerManager.callOnMuteThread(muteThreadJson);
                    messageCallbacks.remove(messageUniqueId);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_MUTE_THREAD");
                    if (BuildConfig.DEBUG) Logger.i(muteThreadJson);
                }
                break;
            case Constants.UN_MUTE_THREAD:

                if (callback.isResult()) {
                    String unmuteThreadJson = reformatMuteThread(chatMessage, outPut);
                    listenerManager.callOnUnmuteThread(unmuteThreadJson);
                    messageCallbacks.remove(messageUniqueId);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_UN_MUTE_THREAD");
                    if (BuildConfig.DEBUG) Logger.i(unmuteThreadJson);
                }
            case Constants.EDIT_MESSAGE:

                if (callback.isResult()) {
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_EDIT_MESSAGE");
                    if (BuildConfig.DEBUG) Logger.json(chatMessage.getContent());
                    listenerManager.callOnEditedMessage(chatMessage.getContent());
                    messageCallbacks.remove(messageUniqueId);
                }
                break;
            case Constants.USER_INFO:

                if (callback.isResult()) {
                    String userInfoJson = reformatUserInfo(chatMessage);
                    listenerManager.callOnUserInfo(userInfoJson);
                    messageCallbacks.remove(messageUniqueId);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_USER_INFO");
                    if (BuildConfig.DEBUG) Logger.json(userInfoJson);
                }
                break;
            case Constants.THREAD_PARTICIPANTS:

                if (callback.isResult()) {
                    reformatThreadParticipants(callback, chatMessage);

                    String jsonParticipant = JsonUtil.getJson(outPut);
                    listenerManager.callOnGetThreadParticipant(jsonParticipant);
                    messageCallbacks.remove(messageUniqueId);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_PARTICIPANT");
                    if (BuildConfig.DEBUG) Logger.json(jsonParticipant);
                }
                break;
            case Constants.ADD_PARTICIPANT:
                if (callback.isResult()) {
                    Thread thread = JsonUtil.fromJSON(chatMessage.getContent(), Thread.class);
                    ResultAddParticipant resultAddParticipant = new ResultAddParticipant();
                    resultAddParticipant.setThread(thread);
                    OutPutAddParticipant outPutAddParticipant = new OutPutAddParticipant();
                    outPutAddParticipant.setErrorCode(0);
                    outPutAddParticipant.setErrorMessage("");
                    outPutAddParticipant.setHasError(false);
                    outPutAddParticipant.setResult(resultAddParticipant);
                    String jsonAddParticipant = JsonUtil.getJson(outPutAddParticipant);
                    listenerManager.callOnThreadAddParticipant(jsonAddParticipant);
                    messageCallbacks.remove(messageUniqueId);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_PARTICIPANT");
                    if (BuildConfig.DEBUG) Logger.json(jsonAddParticipant);
                }
                break;
            case Constants.REMOVE_PARTICIPANT:
                if (callback.isResult()) {
                    reformatThreadParticipants(callback, chatMessage);

                    OutPutParticipant outPutParticipant = new OutPutParticipant();
                    outPutParticipant.setErrorCode(0);
                    outPutParticipant.setErrorMessage("");
                    outPutParticipant.setHasError(false);
                    ResultParticipant resultParticipant = new ResultParticipant();

                    List<Participant> participants = new ArrayList<>();
                    Type type = Types.newParameterizedType(List.class, Participant.class);
                    JsonAdapter<List<Participant>> adapter = moshi.adapter(type);
                    try {
                        participants = adapter.fromJson(chatMessage.getContent());
                    } catch (IOException e) {
                        if (BuildConfig.DEBUG) Logger.e(e.getMessage() + e.getCause());
                    }

                    resultParticipant.setParticipants(participants);
                    outPutParticipant.setResult(resultParticipant);
                    String jsonRmParticipant = JsonUtil.getJson(outPutParticipant);

                    listenerManager.callOnThreadRemoveParticipant(jsonRmParticipant);
                    messageCallbacks.remove(messageUniqueId);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_REMOVE_PARTICIPANT");
                    if (BuildConfig.DEBUG) Logger.json(jsonRmParticipant);
                }
                break;
            case Constants.LEAVE_THREAD:
                if (callback.isResult()) {
                    ResultLeaveThread leaveThread = JsonUtil.fromJSON(chatMessage.getContent(), ResultLeaveThread.class);
                    leaveThread.setThreadId(chatMessage.getSubjectId());
                    OutPutLeaveThread outPutLeaveThread = new OutPutLeaveThread();
                    outPutLeaveThread.setErrorCode(0);
                    outPutLeaveThread.setHasError(false);
                    outPutLeaveThread.setErrorMessage("");

                    outPutLeaveThread.setResult(leaveThread);
                    String jsonThread = JsonUtil.getJson(outPutLeaveThread);
                    listenerManager.callOnThreadLeaveParticipant(jsonThread);
                    messageCallbacks.remove(messageUniqueId);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_LEAVE_THREAD");
                    if (BuildConfig.DEBUG) Logger.json(jsonThread);
                }
                break;
            case Constants.RENAME:

                if (callback.isResult()) {

                    OutPutThread outPutThread = reformatRenameThread(chatMessage);
                    String jsonRename = JsonUtil.getJson(outPutThread);
                    listenerManager.callOnRenameThread(jsonRename);
                    messageCallbacks.remove(messageUniqueId);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_RENAME_THREAD");
                    if (BuildConfig.DEBUG) Logger.json(jsonRename);
                }
                break;
            case Constants.DELETE_MESSAGE:
                OutPutDeleteMessage outPutDeleteMessage = new OutPutDeleteMessage();
                outPutDeleteMessage.setErrorCode(0);
                outPutDeleteMessage.setErrorMessage("");
                outPutDeleteMessage.setHasError(false);
                ResultDeleteMessage resultDeleteMessage = new ResultDeleteMessage();
                DeleteMessageContent deleteMessage = new DeleteMessageContent();
                deleteMessage.setId(Long.valueOf(chatMessage.getContent()));
                resultDeleteMessage.setDeleteMessageContent(deleteMessage);
                outPutDeleteMessage.setResult(resultDeleteMessage);
                String jsonDeleteMsg = JsonUtil.getJson(outPutDeleteMessage);
                listenerManager.callOnDeleteMessage(jsonDeleteMsg);
                if (BuildConfig.DEBUG) Logger.i("RECEIVE_DELETE_MESSAGE");
                if (BuildConfig.DEBUG) Logger.json(jsonDeleteMsg);
                break;
            case Constants.BLOCK:
                if (callback.isResult()) {
                    Contact contact = JsonUtil.fromJSON(chatMessage.getContent(), Contact.class);
                    OutPutBlock outPutBlock = new OutPutBlock();
                    ResultBlock resultBlock = new ResultBlock();
                    resultBlock.setContact(contact);
                    outPutBlock.setResult(resultBlock);
                    outPutBlock.setErrorCode(0);
                    outPutBlock.setHasError(false);
                    String jsonBlock = JsonUtil.getJson(outPutBlock);
                    listenerManager.callOnBlock(jsonBlock);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_BLOCK");
                    if (BuildConfig.DEBUG) Logger.json(jsonBlock);
                    messageCallbacks.remove(messageUniqueId);
                }
                break;
            case Constants.UNBLOCK:
                if (callback.isResult()) {
                    Contact contact = JsonUtil.fromJSON(chatMessage.getContent(), Contact.class);
                    OutPutBlock outPutBlock = new OutPutBlock();
                    ResultBlock resultBlock = new ResultBlock();
                    resultBlock.setContact(contact);
                    outPutBlock.setResult(resultBlock);
                    outPutBlock.setErrorCode(0);
                    outPutBlock.setHasError(false);
                    String jsonUnBlock = JsonUtil.getJson(outPutBlock);
                    listenerManager.callOnUnBlock(jsonUnBlock);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_UN_BLOCK");
                    if (BuildConfig.DEBUG) Logger.json(jsonUnBlock);
                    messageCallbacks.remove(messageUniqueId);
                }
                break;
            case Constants.GET_BLOCKED:
                if (callback.isResult()) {
                    OutPutBlockList outPutBlockList = new OutPutBlockList();
                    outPutBlockList.setErrorCode(0);
                    outPutBlockList.setHasError(false);
                    ResultBlockList resultBlockList = new ResultBlockList();

                    List<Contact> contacts = JsonUtil.fromJSON(chatMessage.getContent(), new TypeReference<List<Contact>>() {
                    });
                    resultBlockList.setContacts(contacts);
                    outPutBlockList.setResult(resultBlockList);
                    String jsonGetBlock = JsonUtil.getJson(outPutBlockList);
                    listenerManager.callOnGetBlockList(jsonGetBlock);
                    if (BuildConfig.DEBUG) Logger.i("RECEIVE_GET_BLOCK_LIST");
                    if (BuildConfig.DEBUG) Logger.json(jsonGetBlock);
//                        listenerManager.callOnGetBlockList();
                }
                break;

        }
    }

    private void reformatThreadParticipants(Callback callback, ChatMessage chatMessage) {
        OutPutParticipant outPutParticipant = new OutPutParticipant();
        outPutParticipant.setErrorCode(0);
        outPutParticipant.setErrorMessage("");
        outPutParticipant.setHasError(false);
        if (chatMessage.getContent().length() + callback.getOffset() < chatMessage.getContentCount()) {
            outPutParticipant.setHasNext(true);
        } else {
            outPutParticipant.setHasNext(false);
        }
        if (chatMessage.getContent().length() + callback.getOffset() < chatMessage.getContentCount()) {
            outPutParticipant.setHasNext(true);
        } else {
            outPutParticipant.setHasNext(false);
        }

        List<Participant> participants = new ArrayList<>();
        Type type = Types.newParameterizedType(List.class, Participant.class);
        JsonAdapter<List<Participant>> adapter = moshi.adapter(type);
        try {
            participants = adapter.fromJson(chatMessage.getContent());
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Logger.e(e.getMessage() + e.getCause());
        }

        ResultParticipant resultParticipant = new ResultParticipant();
        resultParticipant.setParticipants(participants);
        outPutParticipant.setResult(resultParticipant);
        outPutParticipant.setNextOffset(callback.getOffset() + chatMessage.getContent().length());
    }

    @NonNull
    private OutPutThread reformatRenameThread(ChatMessage chatMessage) {
        OutPutThread outPutThread = new OutPutThread();
        outPutThread.setErrorMessage("");
        outPutThread.setErrorCode(0);
        outPutThread.setHasError(false);

        ResultThread resultThread = new ResultThread();
        Thread thread = JsonUtil.fromJSON(chatMessage.getContent(), Thread.class);
        resultThread.setThread(thread);
        outPutThread.setResult(resultThread);
        return outPutThread;
    }

    private void sendTextMessageWithFile(String description, long threadId, String metaData, String systemMetadata) {
        ChatMessage chatMessage = new ChatMessage();
        if (systemMetadata != null) {
            chatMessage.setSystemMetadata(systemMetadata);
        }
        chatMessage.setContent(description);
        chatMessage.setType(Constants.MESSAGE);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        chatMessage.setMetadata(metaData);

        String uniqueId = getUniqueId();
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTime(1000);
        chatMessage.setSubjectId(threadId);

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);

        setThreadCallbacks(threadId, uniqueId);
        sendAsyncMessage(asyncContent, 4, "SEND_TXT_MSG_WITH_FILE");
    }

    private void setThreadCallbacks(long threadId, String uniqueId) {
        if (chatReady) {
            Callback callback = new Callback();
            callback.setDelivery(true);
            callback.setSeen(true);
            callback.setSent(true);
            callback.setUniqueId(uniqueId);
            ArrayList<Callback> callbacks = new ArrayList<>();
            callbacks.add(callback);
            threadCallbacks.put(threadId, callbacks);
        }
    }

    private void sendAsyncMessage(String asyncContent, int asyncMsgType, String logMessage) {
        if (chatReady) {
            if (BuildConfig.DEBUG) Logger.i(logMessage);
            if (BuildConfig.DEBUG) Logger.json(asyncContent);
            async.sendMessage(asyncContent, asyncMsgType);
            long lastSentMessageTimeout = 9 * 1000;
            lastSentMessageTime = new Date().getTime();
            if (state) {
                pingHandler.postDelayed(() -> {
                    long currentTime = new Date().getTime();
                    if (currentTime - lastSentMessageTime > lastSentMessageTimeout) {
                        ping();
                    }
                }, 20000);
            } else {
                Error error = new Error();
//                error.setCode();
                Logger.e("Async is Close");
            }
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY);
            Logger.e(jsonError);
        }
    }

    /**
     * Get the list of the Device Contact
     */
    private List<Contact> getPhoneContact(Context context) {
        String name, phoneNumber;
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor == null) throw new AssertionError();
        ArrayList<Contact> storeContacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Contact contact = new Contact();
            contact.setCellphoneNumber(phoneNumber.replaceAll(" ", ""));
            contact.setFirstName(name.replaceAll(" ", ""));
            storeContacts.add(contact);
        }
        cursor.close();
        return storeContacts;
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void uploadFileMessage(Activity activity, String description, long threadId, String mimeType, String path, String metadata) {
        if (Permission.Check_READ_STORAGE(activity)) {
            if (getFileServer() != null) {
                File file = new File(path);
                RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
                FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                Observable<Response<FileUpload>> uploadObservable = fileApi.sendFile(body, getToken(), TOKEN_ISSUER, name);
                uploadObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<FileUpload>>() {
                    @Override
                    public void call(Response<FileUpload> fileUploadResponse) {
                        if (fileUploadResponse.isSuccessful()) {
                            boolean error = fileUploadResponse.body().isHasError();
                            if (error) {
                                String errorMessage = fileUploadResponse.body().getMessage();
                                if (BuildConfig.DEBUG) Logger.e(errorMessage);
                            } else {

                                ResultFile result = fileUploadResponse.body().getResult();
                                int fileId = result.getId();
                                String hashCode = result.getHashCode();

                                MetaDataFile metaDataFile = new MetaDataFile();
                                FileMetaDataContent metaDataContent = new FileMetaDataContent();
                                metaDataContent.setHashCode(hashCode);
                                metaDataContent.setId(fileId);
                                metaDataContent.setName(result.getName());
                                metaDataContent.setLink(fileServer + "/nzh/file/" + "?fileId=" + result.getId() + "&downloadable=" + true + "&hashCode=" + result.getHashCode());

                                metaDataFile.setFile(metaDataContent);

                                String jsonMeta = JsonUtil.getJson(metaDataFile);
                                if (BuildConfig.DEBUG) Logger.json(jsonMeta);
                                sendTextMessageWithFile(description, threadId, jsonMeta, metadata);
                            }
                        }
                    }
                }, throwable -> {
                    if (BuildConfig.DEBUG) Logger.e(throwable.getMessage());
                });
            } else {
                if (BuildConfig.DEBUG) Logger.e("FileServer url Is null");
            }
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE);
            if (BuildConfig.DEBUG) Logger.e(jsonError);
        }
    }

    private void sendImageFileMessage(Context context, Activity activity, String description, long threadId, Uri fileUri, String mimeType, String metadata) {
        if (fileServer != null) {
            if (Permission.Check_READ_STORAGE(activity)) {
                RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
                FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
                String path = FilePick.getSmartFilePath(context, fileUri);
                File file = new File(path);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                Observable<Response<FileImageUpload>> uploadObservable = fileApi.sendImageFile(body, getToken(), TOKEN_ISSUER, name);
                uploadObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<FileImageUpload>>() {
                    @Override
                    public void call(Response<FileImageUpload> fileUploadResponse) {
                        if (fileUploadResponse.isSuccessful()) {
                            boolean error = fileUploadResponse.body().isHasError();
                            if (error) {
                                String errorMessage = fileUploadResponse.body().getMessage();
                                Logger.e(errorMessage);
                            } else {

                                ResultImageFile result = fileUploadResponse.body().getResult();
                                int imageId = result.getId();
                                String hashCode = result.getHashCode();

                                MetaDataImageFile metaData = new MetaDataImageFile();
                                FileImageMetaData fileMetaData = new FileImageMetaData();
                                fileMetaData.setHashCode(hashCode);
                                fileMetaData.setId(imageId);
                                fileMetaData.setActualHeight(result.getActualHeight());
                                fileMetaData.setActualWidth(result.getActualWidth());
                                fileMetaData.setMimeType(mimeType);
                                fileMetaData.setLink(getPlatformHost() + "nzh/uploadImage" + "?imageId=" + imageId + "&downloadable=" + "true" + "&hashCode=" + hashCode);
                                metaData.setFile(fileMetaData);

                                String metaJson = JsonUtil.getJson(metaData);
                                if (BuildConfig.DEBUG) Logger.json(metaJson);
                                sendTextMessageWithFile(description, threadId, metaJson, metadata);

                                FileImageUpload fileImageUpload = fileUploadResponse.body();
                                String imageJson = JsonUtil.getJson(fileImageUpload);
                                listenerManager.callOnUploadImageFile(imageJson);
                                if (BuildConfig.DEBUG) Logger.json(imageJson);
                            }
                        }
                    }
                }, throwable -> Logger.e(throwable.getMessage()));

            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE);
                if (BuildConfig.DEBUG) Logger.e(jsonError);
            }
        } else {
            if (BuildConfig.DEBUG) Logger.e("FileServer url Is null");
        }
    }

    private void getFile(String hashCode, FileApi fileApi, int fileId) {
        Observable<Response<ResponseBody>> getFileObservable = fileApi.getFile(fileId, true, hashCode);
        getFileObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<ResponseBody>>() {
            @Override
            public void call(Response<ResponseBody> responseBody) {
                if (responseBody.isSuccessful()) {
                    responseBody.body();
                    Logger.i("respond", responseBody);
                }
            }
        }, throwable -> Logger.e(throwable.getMessage()));
    }

    //model
    private class DeleteMessage {
        private boolean deleteForAll;

        public boolean isDeleteForAll() {
            return deleteForAll;
        }

        public void setDeleteForAll(boolean deleteForAll) {
            this.deleteForAll = deleteForAll;
        }
    }

    //TODO make it public
    // Add list of contacts with their mobile numbers and their cellphoneNumbers
    private void addContacts(ArrayList<String> firstNames, ArrayList<String> cellphoneNumbers) {
        ArrayList<String> lastNames = new ArrayList<>();
        ArrayList<String> emails = new ArrayList<>();
        for (int i = 0; i < cellphoneNumbers.size(); i++) {
            emails.add("");
            lastNames.add("");
        }
        Observable<Response<AddContacts>> addContactsObservable;
        if (getPlatformHost() != null) {
            addContactsObservable = contactApi.addContacts(getToken(), TOKEN_ISSUER, firstNames, lastNames, emails, cellphoneNumbers, cellphoneNumbers);
            addContactsObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<AddContacts>>() {
                @Override
                public void call(Response<AddContacts> contactsResponse) {
                    boolean error = contactsResponse.body().getHasError();
                    if (contactsResponse.isSuccessful()) {
                        if (error) {
                            String jsonError = getErrorOutPut(contactsResponse.body().getMessage(), contactsResponse.body().getErrorCode());
                            if (BuildConfig.DEBUG) Logger.e(jsonError);
                        } else {
                            AddContacts contacts = contactsResponse.body();
                            String contactsJson = JsonUtil.getJson(contacts);
                            if (syncContacts) {
                                listenerManager.callOnSyncContact(contactsJson);
                                if (BuildConfig.DEBUG) Logger.i("SYNC_CONTACT");
                                syncContacts = false;
                            } else {
                                listenerManager.callOnAddContact(contactsJson);
                                if (BuildConfig.DEBUG) Logger.i("ADD_CONTACTS");
                            }
                            if (BuildConfig.DEBUG) Logger.json(contactsJson);
                        }
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    if (BuildConfig.DEBUG) Logger.e("Error on add contacts", throwable.toString());
                    if (BuildConfig.DEBUG) Logger.e(throwable.getCause().getMessage());
                }
            });
        }
    }

    @NonNull
    private ChatMessage getChatMessage(String contentThreadChat, String uniqueId) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(contentThreadChat);
        chatMessage.setType(Constants.INVITATION);
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTokenIssuer("1");
        return chatMessage;
    }

    private String onMessage() {
        return async.getMessageLiveData().getValue();
    }

    /**
     * Get the manager that manages registered listeners.
     */
    ChatListenerManager getListenerManager() {
        return listenerManager;
    }

    @NonNull
    private ChatMessage getChatMessage(MessageVO jsonMessage) {
        ChatMessage message = new ChatMessage();
        message.setType(Constants.DELIVERY);
        message.setContent(String.valueOf(jsonMessage.getId()));
        message.setTokenIssuer("1");
        message.setToken(getToken());
        message.setUniqueId(getUniqueId());
        message.setTime(1000);
        return message;
    }

    private String reformatUserInfo(ChatMessage chatMessage) {
        OutPutUserInfo outPutUserInfo = new OutPutUserInfo();
        ResultUserInfo resultUserInfo = new ResultUserInfo();
        ObjectMapper objectMapper = new ObjectMapper();
        UserInfo userInfo = null;
        try {
            userInfo = objectMapper.readValue(chatMessage.getContent(), UserInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setUserId(userInfo.getId());
        resultUserInfo.setUserInfo(userInfo);
        outPutUserInfo.setErrorCode(0);
        outPutUserInfo.setErrorMessage("");
        outPutUserInfo.setHasError(false);
        outPutUserInfo.setResultUserInfo(resultUserInfo);
        return JsonUtil.getJson(outPutUserInfo);
    }

    private String reformatMuteThread(ChatMessage chatMessage, OutPut outPut) {
        if (BuildConfig.DEBUG) Log.i("RECEIVED_MUTE_THREAD", chatMessage.getContent());
        outPut.setResult(chatMessage.getContent());
        outPut.setHasError(false);
        outPut.setErrorMessage("");
        return JsonUtil.getJson(outPut);
    }

    private String reformatCreateThread(ChatMessage chatMessage, OutPutThread outPutThread) {
        if (BuildConfig.DEBUG) Log.i("RECEIVE_INVITATION ", chatMessage.getContent());
        ResultThread resultThread = new ResultThread();
        ObjectMapper objectMapper = new ObjectMapper();
        Thread thread = null;
        try {
            thread = objectMapper.readValue(chatMessage.getContent(), Thread.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultThread.setThread(thread);
        outPutThread.setHasError(false);
        outPutThread.setErrorCode(0);
        outPutThread.setErrorMessage("");
        outPutThread.setResult(resultThread);
        return JsonUtil.getJson(outPutThread);
    }

    private void deviceIdRequest(String ssoHost, String serverAddress, String appId, String severName) {
        if (BuildConfig.DEBUG) Logger.i("GET_DEVICE_ID");
        currentDeviceExist = false;

        RetrofitHelperSsoHost retrofitHelperSsoHost = new RetrofitHelperSsoHost(ssoHost);
        TokenApi tokenApi = retrofitHelperSsoHost.getService(TokenApi.class);
        rx.Observable<Response<DeviceResult>> listObservable = tokenApi.getDeviceId("Bearer" + " " + getToken());
        listObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(deviceResults -> {
            if (deviceResults.isSuccessful()) {
                ArrayList<Device> devices = deviceResults.body().getDevices();
                for (Device device : devices) {
                    if (device.isCurrent()) {
                        currentDeviceExist = true;
                        if (BuildConfig.DEBUG) Logger.i("DEVICE_ID :" + device.getUid());
                        async.connect(serverAddress, appId, severName, token, ssoHost, device.getUid());
                        break;
                    }
                }
                if (!currentDeviceExist) {
                    String jsonError = getErrorOutPut(ChatConstant.ERROR_CURRENT_DEVICE, ChatConstant.ERROR_CODE_CURRENT_DEVICE);
                    if (BuildConfig.DEBUG) Logger.e(jsonError);
                }
            } else {
                String jsonError = getErrorOutPut(deviceResults.message(), deviceResults.code());
                if (BuildConfig.DEBUG) Logger.e(jsonError);
            }
        }, (Throwable throwable) -> {
            Logger.e("Error on get devices");
        });
    }

    /**
     * Reformat the get thread response
     */
    private String reformatGetThreadsResponse(ChatMessage chatMessage, OutPutThreads outPutThreads, Callback callback) {
        if (BuildConfig.DEBUG) Logger.json(chatMessage.getContent());
        List<Thread> threads = new ArrayList<>();
        Type type = Types.newParameterizedType(List.class, Thread.class);
        JsonAdapter<List<Thread>> adapter = moshi.adapter(type);
        try {
            threads = adapter.fromJson(chatMessage.getContent());
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Logger.e(e.getMessage() + e.getCause());
        }
        ResultThreads resultThreads = new ResultThreads();
        resultThreads.setThreads(threads);
        outPutThreads.setContentCount(chatMessage.getContentCount());
        outPutThreads.setErrorCode(0);
        outPutThreads.setErrorMessage("");
        outPutThreads.setHasError(false);

        if (chatMessage.getContent().length() + callback.getOffset() < chatMessage.getContentCount()) {
            outPutThreads.setHasNext(true);
        } else {
            outPutThreads.setHasNext(false);
        }
        outPutThreads.setNextOffset(callback.getOffset() + chatMessage.getContent().length());
        outPutThreads.setResult(resultThreads);
        return JsonUtil.getJson(outPutThreads);
    }

    @NonNull
    private String reformatError(boolean hasError, ChatMessage chatMessage, OutPut outPut) {
        Error error = JsonUtil.fromJSON(chatMessage.getContent(), Error.class);
        Log.e("RECEIVED_ERROR", chatMessage.getContent());
        Log.e("ErrorMessage", error.getMessage());
        Log.e("ErrorCode", String.valueOf(error.getCode()));
        outPut.setHasError(hasError);
        outPut.setErrorMessage(error.getMessage());
        outPut.setErrorCode(error.getCode());
        return JsonUtil.getJson(outPut);
    }

    //Todo need change
    @NonNull
    private String reformatGetContactResponse(ChatMessage chatMessage, OutPutContact outPutContact) {
        ResultContact resultContact = new ResultContact();
        ArrayList<Contact> contacts = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            contacts = objectMapper.readValue(
                    chatMessage.getContent(),
                    objectMapper.getTypeFactory().constructCollectionType(
                            ArrayList.class, Contact.class));
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }

        messageDatabaseHelper.save(contacts);
        ArrayList<Contact> contactsList = new ArrayList<>(messageDatabaseHelper.getContacts());
        resultContact.setContacts(contactsList);
        outPutContact.setResult(resultContact);
        outPutContact.setContentCount(chatMessage.getContentCount());
        return JsonUtil.getJson(outPutContact);
    }

    private static synchronized String getUniqueId() {
        return UUID.randomUUID().toString();
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String getToken() {
        return token;
    }

    private long getUserId() {
        return userId;
    }

    private void setUserId(long userId) {
        this.userId = userId;
    }

    private void setCallBacks(Boolean delivery, Boolean sent, Boolean seen, Boolean result, int requestType, Integer offset, String uniqueId) {
        if (chatReady) {
            delivery = delivery != null ? delivery : false;
            sent = sent != null ? sent : false;
            seen = seen != null ? seen : false;
            result = result != null ? result : false;
            offset = offset != null ? offset : 0;
            Callback callback = new Callback();
            callback.setDelivery(delivery);
            callback.setOffset(offset);
            callback.setSeen(seen);
            callback.setSent(sent);
            callback.setRequestType(requestType);
            callback.setResult(result);
            messageCallbacks.put(uniqueId, callback);
        }
    }

    private void setPlatformHost(String platformHost) {
        this.platformHost = platformHost;
    }

    private String getPlatformHost() {
        return platformHost;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    private void setFileServer(String fileServer) {
        this.fileServer = fileServer;
    }

    private String getFileServer() {
        return fileServer;
    }
}