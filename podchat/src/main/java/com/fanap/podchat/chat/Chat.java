package com.fanap.podchat.chat;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.fanap.podasync.Async;
import com.fanap.podasync.AsyncAdapter;
import com.fanap.podasync.model.Device;
import com.fanap.podasync.model.DeviceResult;
import com.fanap.podasync.util.JsonUtil;
import com.fanap.podchat.BuildConfig;
import com.fanap.podchat.model.ChatMessage;
import com.fanap.podchat.model.ChatMessageContent;
import com.fanap.podchat.model.ChatMessageForward;
import com.fanap.podchat.model.ChatThread;
import com.fanap.podchat.model.Contact;
import com.fanap.podchat.model.ContactRemove;
import com.fanap.podchat.model.Contacts;
import com.fanap.podchat.model.Error;
import com.fanap.podchat.model.Invitee;
import com.fanap.podchat.model.Message;
import com.fanap.podchat.model.MessageVO;
import com.fanap.podchat.model.OutPut;
import com.fanap.podchat.model.OutPutContact;
import com.fanap.podchat.model.OutPutThread;
import com.fanap.podchat.model.OutPutThreads;
import com.fanap.podchat.model.OutPutUserInfo;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.ResultThread;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.model.ResultUserInfo;
import com.fanap.podchat.model.Results;
import com.fanap.podchat.model.Thread;
import com.fanap.podchat.model.UserInfo;
import com.fanap.podchat.networking.RetrofitHelper;
import com.fanap.podchat.networking.RetrofitHelperFileServer;
import com.fanap.podchat.networking.RetrofitHelperSsoHost;
import com.fanap.podchat.networking.api.ContactApi;
import com.fanap.podchat.networking.api.FileApi;
import com.fanap.podchat.networking.api.TokenApi;
import com.fanap.podchat.util.Callback;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.ChatMessageType.Constants;
import com.fanap.podchat.util.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Chat extends AsyncAdapter {
    private static Async async;
    private static Moshi moshi;
    private String token;
    private static Chat instance;
    private String platformHost;
    private static ChatListenerManager listenerManager;
    private int userId;
    private ContactApi contactApi;
    private static HashMap<String, Callback> messageCallbacks;
    private static HashMap<Long, LinkedHashMap<String, Callback>> threadCallbacks;
    private HashMap<String, Callback> callbackMap;
    private boolean syncContact = false;
    private boolean state = false;
    private long lastSentMessageTime;
    private boolean chatState = false;
    private static final String CONNECTING = "CONNECTING";
    private static final String CLOSING = "CLOSING";
    private static final String CLOSED = "CLOSED";
    private static final String OPEN = "OPEN";
    private Handler pingHandler;
    private String contact;
    private List<String> indexes;
    private Context context;

    /**
     * Initialize the Chat
     **/
    public static Chat init(Context context) {
        if (instance == null) {
            async = Async.getInstance(context);
            instance = new Chat();
            moshi = new Moshi.Builder().build();
            listenerManager = new ChatListenerManager();
            messageCallbacks = new HashMap<>();
            threadCallbacks = new HashMap<>();
            Logger.addLogAdapter(new AndroidLogAdapter());
        }
        return instance;
    }

    /**
     * Connect to the Async with params that client set to it.
     */
    public void connect(String serverAddress, String appId, String severName, String token,
                        String ssoHost, String platformHost) {
        pingHandler = new Handler();
        async.addListener(this);
        RetrofitHelper retrofitHelper = new RetrofitHelper(platformHost);
        contactApi = retrofitHelper.getService(ContactApi.class);
        setPlatformHost(platformHost);
        setToken(token);
        getUserInfo();
        deviceIdRequest(ssoHost, serverAddress, appId, severName);
        state = true;
    }

    /**
     * When state of the Async changed then the chat ping is stopped buy (chatState)flag
     */
    @Override
    public void onStateChanged(String state) throws IOException {
        super.onStateChanged(state);
        switch (state) {
            case OPEN:
                chatState = true;
//                ping();
                break;
            case CONNECTING:
            case CLOSING:
            case CLOSED:
                chatState = false;
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
        if (BuildConfig.DEBUG) Logger.json(textMessage);

        JsonAdapter<ChatMessage> jsonAdapter = moshi.adapter(ChatMessage.class);
        ChatMessage chatMessage = jsonAdapter.fromJson(textMessage);

        String messageUniqueId = chatMessage.getUniqueId();
        long threadId = chatMessage.getSubjectId();
        Callback callback = messageCallbacks.get(messageUniqueId);
        if (!threadCallbacks.isEmpty()) {
            callbackMap = threadCallbacks.get(chatMessage.getSubjectId());
            indexes = new ArrayList<>(callbackMap.keySet());
        }

        if (chatMessage != null) {
            messageType = chatMessage.getType();
        }
        @ChatMessageType.Constants int currentMessageType = messageType;
        switch (currentMessageType) {
            case Constants.ADD_PARTICIPANT:
                break;
            case Constants.BLOCK:
                break;
            case Constants.CHANGE_TYPE:
                break;
            case Constants.SENT:
                Callback callbackSent = callbackMap.get(messageUniqueId);
                int msgUniqueIdSent = Integer.valueOf(messageUniqueId);
                while (indexes.indexOf(msgUniqueIdSent) > -1) {
                    if (callbackSent.isSent()) {
                        Callback updateCallback = new Callback(true, true, false);
                        LinkedHashMap<String, Callback> stringCallbackLinkedHashMap = new LinkedHashMap<>();
                        stringCallbackLinkedHashMap.put(messageUniqueId, updateCallback);
                        threadCallbacks.put(threadId, stringCallbackLinkedHashMap);
                        listenerManager.callOnDeliveryMessage(String.valueOf(msgUniqueIdSent));
                    }
                    msgUniqueIdSent--;
                }


//                if (callback.isSent()) {
//                    if (BuildConfig.DEBUG)
//                        Logger.i("RECEIVE_SENT_MESSAGE", chatMessage.getContent());
//                    listenerManager.callOnSentMessage(chatMessage.getContent());
//                    setCallBacks(false, false, true, null, Constants.MESSAGE, null, messageUniqueId);
//                }
                break;
            case Constants.DELIVERY:
                Callback callbackDelivery = callbackMap.get(messageUniqueId);
                int msgUniqueID = Integer.valueOf(messageUniqueId);
                while (indexes.indexOf(msgUniqueID) > -1) {
                    if (callbackDelivery.isDelivery()) {
                        Callback updateCallback = new Callback(false, true, true);
                        LinkedHashMap<String, Callback> stringCallbackLinkedHashMap = new LinkedHashMap<>();
                        stringCallbackLinkedHashMap.put(messageUniqueId, updateCallback);
                        threadCallbacks.put(threadId, stringCallbackLinkedHashMap);
                        listenerManager.callOnDeliveryMessage(String.valueOf(msgUniqueID));
                    }
                    msgUniqueID--;
                }

//                if (callback.isDelivery()) {
//                    if (BuildConfig.DEBUG)
//                        Logger.json(chatMessage.getContent());
//                    listenerManager.callOnDeliveryMessage(chatMessage.getContent());
//                    setCallBacks(false, true, true, null, Constants.MESSAGE, null, messageUniqueId);
//                }
                break;
            case Constants.SEEN:
                Callback callbackSeen = callbackMap.get(messageUniqueId);
                int msgUniqueIdSeen = Integer.valueOf(messageUniqueId);
                while (indexes.indexOf(msgUniqueIdSeen) > -1) {
                    if (callbackSeen.isSeen()) {
                        if (callbackSeen.isDelivery()) {
                            Callback updateCallbacks = new Callback(false, false, false);
                            LinkedHashMap<String, Callback> CallbackLinkedHashMap = new LinkedHashMap<>();
                            CallbackLinkedHashMap.put(messageUniqueId, updateCallbacks);
                            threadCallbacks.put(threadId, CallbackLinkedHashMap);
                            listenerManager.callOnDeliveryMessage(chatMessage.getContent());
                        }
                        listenerManager.callOnSeenMessage(chatMessage.getContent());
                        threadCallbacks.remove(threadId);
                    }
                    msgUniqueIdSeen--;
                }

//                if (callback.isSeen()) {
//                    if (BuildConfig.DEBUG)
//                        Logger.i("RECEIVE_SEEN_MESSAGE", chatMessage.getContent());
//                    listenerManager.callOnSeenMessage(chatMessage.getContent());
//                    listenerManager.callOnDeliveryMessage(chatMessage.getContent());
//                    messageCallbacks.remove(messageUniqueId);
//                }
                break;
            case Constants.ERROR:
                handleResponseMessage(callback, true, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.FORWARD_MESSAGE:
                break;
            case Constants.GET_CONTACTS:
                //TODO sync contact
                HandleSyncContact(chatMessage);
                handleResponseMessage(callback, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.GET_HISTORY:
                handleResponseMessage(callback, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.GET_STATUS:
                break;
            case Constants.GET_THREADS:
                handleResponseMessage(callback, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.INVITATION:
                handleResponseMessage(callback, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.LAST_SEEN_TYPE:
                break;
            case Constants.LEAVE_THREAD:
                break;
            case Constants.MESSAGE:
                handleResponseMessage(callback, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.MUTE_THREAD:
                handleResponseMessage(callback, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.PING:
                break;
            case Constants.RELATION_INFO:
                break;
            case Constants.REMOVE_PARTICIPANT:
                break;
            case Constants.RENAME:
                listenerManager.callOnRenameThread(chatMessage.getContent());
                listenerManager.callOnRenameThread(chatMessage.getContent());
                break;
            case Constants.THREAD_PARTICIPANTS:
                handleResponseMessage(callback, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.UNBLOCK:
                break;
            case Constants.UN_MUTE_THREAD:
                handleResponseMessage(callback, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.UPDATE_METADATA:
                break;
            case Constants.USER_INFO:
                handleResponseMessage(callback, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.USER_STATUS:
                break;
            case Constants.USER_S_STATUS:
                break;
            case Constants.DELETE_MESSAGE:
                break;
            case Constants.EDIT_MESSAGE:
                handleResponseMessage(callback, false, 0, "", chatMessage, messageUniqueId);
                break;
        }
    }

    //TODO
    private void HandleSyncContact(ChatMessage chatMessage) {
        if (syncContact) {
            Type type = Types.newParameterizedType(List.class, Contact.class);
            JsonAdapter<List<Contact>> adapter = moshi.adapter(type);
            try {
                List<Contact> serverContacts = adapter.fromJson(chatMessage.getContent());
                if (serverContacts != null) {
                    List<Contact> phoneContact = getPhoneContact(getContext());
                    for (int i = 0; i < serverContacts.size(); i++) {
                        for (int j = 0; j < phoneContact.size(); j++) {
                            if (phoneContact.get(j).getCellphoneNumber().equals(serverContacts.get(i).getCellphoneNumber())) {

                                Logger.i("Match Found");
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            syncContact = false;
        }
    }

    /**
     * Remove the peerId and send ping again but this time
     * peerId that was set in the server was removed
     */
    public void logOutSocket() {
        async.logOutSocket();
    }

    /**
     * Send text message with
     *
     * @param textMessage String that we want to sent to the thread
     * @param threadId    Id of the destination thread
     */
    public void sendTextMessage(String textMessage, long threadId) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(textMessage);
        chatMessage.setType(Constants.MESSAGE);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        String uniqueId = getUniqueId();
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTime(1000);
        chatMessage.setSubjectId(threadId);

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);
        Log.d("Message send", asyncContent);
        setCallBacks(true, true, true, null, Constants.MESSAGE, null, uniqueId);
        sendAsyncMessage(asyncContent, 4);
    }

    //TODO ping have problem with post delayed
    private void sendAsyncMessage(String asyncContent, int asyncMsgType) {
        async.sendMessage(asyncContent, asyncMsgType);
        long lastSentMessageTimeout = 9 * 1000;
        lastSentMessageTime = new Date().getTime();
        if (state) {
            pingHandler.postDelayed(() -> {
                long currentTime = new Date().getTime();
                if (currentTime - lastSentMessageTime > lastSentMessageTimeout) {
                    ping();
                }
            }, lastSentMessageTimeout);
        } else {
            Logger.e("Async is Close");
        }

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
            sendAsyncMessage(asyncContent, 4);
            if (BuildConfig.DEBUG) Logger.i("CHAT PING");
        }
    }

    //TODO sync contact
    public void syncContact(Context context) {
        syncContact = true;
        getContacts(50, 0);
        setContext(context);
    }

    private List<Contact> getPhoneContact(Context context) {
        ArrayList<Contact> storeContacts = new ArrayList<>();
        String name, phoneNumber;
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (cursor == null) throw new AssertionError();
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Contact contact = new Contact();
            contact.setCellphoneNumber(phoneNumber);
            contact.setFirstName(name);
            storeContacts.add(contact);
        }
        cursor.close();
        return storeContacts;
    }

    //TODO send file not completed
    // use the FileUtils to get the actual file by uri
    public void sendFile(String fileHost, Uri fileUri, String fileName, Context context) {
        RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(fileHost);
        FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
        File file = FileUtils.getFile(context, fileUri);

// create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse(context.getContentResolver().getType(fileUri)), file);

//    // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
//
        Call<ResponseBody> responseCall = fileApi.sendFile(body, getToken(), 1, fileName);
        responseCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Logger.i("file uploaded");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Logger.e(t, call.toString());
            }
        });
    }

    /**
     * You should consider that this method is for rename group and you have to be the admin
     * to change the thread name if not you don't have the permission
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
        sendAsyncMessage(asyncContent, 4);
        if (BuildConfig.DEBUG) Logger.i("SEND RENAME THREAD");
        if (BuildConfig.DEBUG) Logger.json(asyncContent);
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

        LinkedHashMap<String, Callback> uniqueIdCallback = new LinkedHashMap<>();
        for (int i = 0; i < messageIds.size(); i++) {
            String uniqueId = getUniqueId();
            uniqueIds.add(uniqueId);
            uniqueIdCallback.put(uniqueId, new Callback(uniqueId, true, true, true));
        }
        threadCallbacks.put(threadId, uniqueIdCallback);
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
        if (BuildConfig.DEBUG) Logger.i("SEND FORWARD MESSAGE");
        if (BuildConfig.DEBUG) Logger.json(asyncContent);
        sendAsyncMessage(asyncContent, 4);
    }

    /**
     * Reply the message in the current thread
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
        if (BuildConfig.DEBUG) Logger.d("Send Reply Message");
        if (BuildConfig.DEBUG) Logger.json(asyncContent);
        sendAsyncMessage(asyncContent, 4);
    }


    /**
     * Get the list of threads or you can just pass the thread id that you want
     *
     * @param count  number of thread
     * @param offset specified offset you want
     */
    public void getThreads(int count, int offset, ArrayList<Integer> threadIds) {
        ChatMessageContent chatMessageContent = new ChatMessageContent();
        chatMessageContent.setCount(count);
        chatMessageContent.setOffset(offset);
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
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);
        Log.d("Get thread send", asyncContent);
        setCallBacks(null, null, null, true, Constants.GET_THREADS, offset, uniqueId);
        sendAsyncMessage(asyncContent, 3);
    }

    /**
     * Get history of the thread
     *
     * @param count    count of the messages
     * @param order    If order is empty [default = desc] and also you have two option [ asc | desc ]
     * @param threadId Id of the thread that we want to get the history
     */
    public void getHistory(int count, int offset, String order, long threadId) {
        order = order != null ? order : "";
        ChatMessageContent chatMessageContent = new ChatMessageContent();
        chatMessageContent.setCount(count);
        chatMessageContent.setOffset(offset);
        chatMessageContent.setOrder(order);
        JsonAdapter<ChatMessageContent> messageContentJsonAdapter = moshi.adapter(ChatMessageContent.class);
        String content = messageContentJsonAdapter.toJson(chatMessageContent);

        String uniqueId = getUniqueId();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setType(Constants.GET_HISTORY);
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);
        if (BuildConfig.DEBUG) Logger.d("SEND GET THREAD HISTORY");
        if (BuildConfig.DEBUG) Logger.json(asyncContent);
        setCallBacks(null, null, null, true, Constants.GET_HISTORY, offset, uniqueId);
        sendAsyncMessage(asyncContent, 3);
    }

    /**
     * Get all of the contacts of the user
     */
    public void getContacts(int count, int offset) {
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
        if (BuildConfig.DEBUG) Logger.d("GET_CONTACT_SEND", asyncContent);
        if (BuildConfig.DEBUG) Logger.json(asyncContent);
        sendAsyncMessage(asyncContent, 3);
    }

    /**
     * Add contact to the contact list
     *
     * @param firstName
     * @param lastName
     * @param cellphoneNumber
     * @param email
     */
    public void addContact(String firstName, String lastName, String cellphoneNumber, String email) {
        String uniqueId = getUniqueId();
        Observable<Response<Contacts>> addContactObservable;
        if (getPlatformHost() != null) {
            addContactObservable = contactApi.addContact(getToken(), 1, firstName, lastName, email, uniqueId, cellphoneNumber);
            addContactObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(addContactResponse -> {
                if (addContactResponse.isSuccessful()) {
                    Contacts contacts = addContactResponse.body();
                    String contactsJson = JsonUtil.getJson(contacts);
                    listenerManager.callOnAddContact(contactsJson);
                }
            }, throwable -> Log.e("Error on add contact", throwable.toString()));
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
                    String contactRemoveJson = JsonUtil.getJson(contactRemove);
                    listenerManager.callOnRemoveContact(contactRemoveJson);
                }
            }, throwable -> Log.e("Error on remove contact", throwable.toString()));
        } else {
            Logger.e("PlatformHost address is :", "Empty");
        }
    }

    /**
     * Update contacts
     */
    public void updateContact(String userId, String firstName, String lastName, String cellphoneNumber, String email) {
        Contact contact = new Contact();
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setCellphoneNumber(cellphoneNumber);
        contact.setEmail(email);
        contact.setId(userId);
        contact.setUniqueId(getUniqueId());
        Observable<Response<ContactRemove>> updateContactObservable = contactApi.updateContact(getToken(), 1
                , firstName, lastName, email, getUniqueId(), cellphoneNumber);
        updateContactObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
            if (response.isSuccessful()) {
                listenerManager.callOnUpdateContact(response.body().toString());
            }
        }, throwable -> Log.e("Error on update contact", throwable.toString()));
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
        chatThread.setType(threadType);
        chatThread.setInvitees(invitees);
        chatThread.setTitle(threadTitle);

        String contentThreadChat = JsonUtil.getJson(chatThread);
        String uniqueId = getUniqueId();
        ChatMessage chatMessage = getChatMessage(contentThreadChat, uniqueId);

        setCallBacks(null, null, null, true, Constants.INVITATION, null, uniqueId);
        String asyncContent = JsonUtil.getJson(chatMessage);
        sendAsyncMessage(asyncContent, 4);
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

    /**
     * Get the participant list of specific thread
     *
     * @param threadId id of the thread we want to ge the participant list
     */
    public void getThreadParticipant(int count, int offset, long threadId) {
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
        Log.i("Thread Participant send", asyncContent);
        setCallBacks(null, null, null, true, Constants.THREAD_PARTICIPANTS, offset, uniqueId);
        sendAsyncMessage(asyncContent, 3);
    }

    public void seenMessage(int messageId) {
        ChatMessage message = new ChatMessage();
        message.setType(Constants.SEEN);
        message.setContent(String.valueOf(messageId));
        message.setTokenIssuer("1");
        message.setToken(getToken());
        message.setUniqueId(getUniqueId());
        message.setTime(1000);

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(message);
        sendAsyncMessage(asyncContent, 4);
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

        setCallBacks(null, null, null, true, Constants.USER_INFO, null, uniqueId);
        String asyncContent = JsonUtil.getJson(chatMessage);
        sendAsyncMessage(asyncContent, 3);
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
        sendAsyncMessage(asyncContent, 4);
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
        sendAsyncMessage(asyncContent, 4);
    }

    /**
     * Message can be edit when you pass the message id and the edited
     * content to editMessage function
     */
    public void editMessage(int messageId, String messageContent) {
        Message message = new Message();
        message.setId(messageId);
        message.setMessage(messageContent);
        String editedMessage = JsonUtil.getJson(message);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.EDIT_MESSAGE);
        String uniqueId = getUniqueId();
        chatMessage.setToken(uniqueId);
        chatMessage.setContent(editedMessage);
        chatMessage.setTokenIssuer("1");
        String asyncContent = JsonUtil.getJson(chatMessage);
        setCallBacks(null, null, null, true, Constants.EDIT_MESSAGE, null, uniqueId);
        sendAsyncMessage(asyncContent, 4);
    }

    private String onMessage() {
        return async.getMessageLiveData().getValue();
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

    /**
     * Get the manager that manages registered listeners.
     */
    ChatListenerManager getListenerManager() {
        return listenerManager;
    }

    public LiveData<String> getState() {
        return async.getStateLiveData();
    }

    private void handleResponseMessage(Callback callback, boolean hasError, int errorCode, String errorMessage, ChatMessage chatMessage, String messageUniqueId) {
        Results results = new Results();
        OutPut outPut = new OutPut();
        switch (callback.getRequestType()) {
            case Constants.GET_HISTORY:
                if (hasError) {
                    String errorJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorJson);
                } else {
                    results.setContentCount(chatMessage.getContentCount());
                    if (chatMessage.getContent().length() + callback.getOffset() < chatMessage.getContentCount()) {
                        results.setHasNext(true);
                    } else {
                        results.setHasNext(false);
                    }
                    results.setHistory(chatMessage.getContent());
                    results.setNextOffset(callback.getOffset() + chatMessage.getContent().length());
                    outPut.setErrorCode(errorCode);
                    outPut.setHasError(false);
                    outPut.setErrorMessage(errorMessage);

                    String json = JsonUtil.getJson(outPut);
                    listenerManager.callOnGetThreadHistory(json);
                    messageCallbacks.remove(messageUniqueId);
                }
                break;
            case Constants.ERROR:
                Error error = JsonUtil.fromJSON(chatMessage.getContent(), Error.class);
                if (BuildConfig.DEBUG) Log.e("ErrorMessage", error.getMessage());
                if (BuildConfig.DEBUG) Log.e("ErrorCode", String.valueOf(error.getCode()));
                outPut.setErrorMessage(error.getMessage());
                outPut.setErrorCode(error.getCode());
                String errorJson = JsonUtil.getJson(outPut);
                listenerManager.callOnError(errorJson);
                break;
            case Constants.GET_CONTACTS:
                OutPutContact outPutContact = new OutPutContact();
                if (hasError) {
                    String errorContactJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorContactJson);
                } else {
                    if (callback.isResult()) {
                        String contactJson = reformatGetContactResponse(chatMessage, outPutContact);
                        listenerManager.callOnGetContacts(contactJson);
                        messageCallbacks.remove(messageUniqueId);
                    }
                }
                break;
            case Constants.GET_THREADS:
                OutPutThreads outPutThreads = new OutPutThreads();
                if (hasError) {
                    String errorThreadJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorThreadJson);
                } else {
                    if (callback.isResult()) {
                        String threadJson = reformatGetThreadsResponse(chatMessage, outPutThreads);
                        listenerManager.callOnGetThread(threadJson);
                        messageCallbacks.remove(messageUniqueId);
                    }
                }
                break;
            case Constants.INVITATION:
                if (hasError) {
                    String errorInviteJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorInviteJson);
                } else {
                    if (callback.isResult()) {
                        OutPutThread outPutThread = new OutPutThread();
                        String inviteJson = reformatCreateThread(chatMessage, outPutThread);
                        listenerManager.callOnCreateThread(inviteJson);
                        messageCallbacks.remove(messageUniqueId);
                    }
                }
                break;
            case Constants.MUTE_THREAD:
                if (hasError) {
                    String errorMuteThreadJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorMuteThreadJson);
                } else {
                    if (callback.isResult()) {
                        String muteThreadJson = reformatMuteThread(chatMessage, outPut);
                        listenerManager.callOnMuteThread(muteThreadJson);
                        messageCallbacks.remove(messageUniqueId);
                    }
                }
                break;
            case Constants.UN_MUTE_THREAD:
                if (hasError) {
                    String errorUnMuteThreadJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorUnMuteThreadJson);
                } else {
                    if (callback.isResult()) {
                        String unmuteThreadJson = reformatMuteThread(chatMessage, outPut);
                        listenerManager.callOnUnmuteThread(unmuteThreadJson);
                        messageCallbacks.remove(messageUniqueId);
                    }
                }
            case Constants.EDIT_MESSAGE:
                if (hasError) {
                    String errorEditMessageJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorEditMessageJson);
                } else {
                    if (callback.isResult()) {
                        if (BuildConfig.DEBUG)
                            Log.i("RECEIVE_EDIT_MESSAGE", chatMessage.getContent());
                        listenerManager.callOnEditedMessage(chatMessage.getContent());
                        messageCallbacks.remove(messageUniqueId);
                    }
                }
                break;
            case Constants.USER_INFO:
                if (hasError) {
                    String errorUserInfoJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorUserInfoJson);
                } else {
                    if (callback.isResult()) {
                        String userInfoJson = reformatUserInfo(chatMessage);
                        listenerManager.callOnUserInfo(userInfoJson);
                        messageCallbacks.remove(messageUniqueId);
                    }
                }
                break;
            case Constants.MESSAGE:
                if (hasError) {
                    String errorUserInfoJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorUserInfoJson);
                } else {
                    if (BuildConfig.DEBUG) Logger.i("RECEIVED_MESSAGE", chatMessage.getContent());
                    MessageVO jsonMessage = JsonUtil.fromJSON(chatMessage.getContent(), MessageVO.class);
                    long ownerId = 0;
                    if (jsonMessage != null) {
                        ownerId = jsonMessage.getParticipant().getId();
                    }
                    if (ownerId != getUserId()) {
                        ChatMessage message = getChatMessage(jsonMessage);
                        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
                        String asyncContent = chatMessageJsonAdapter.toJson(message);
                        async.sendMessage(asyncContent, 4);
                    }
                }
                break;
            case Constants.THREAD_PARTICIPANTS:
                if (hasError) {
                    String errorUserInfoJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorUserInfoJson);
                } else {
                    if (callback.isResult()) {
                        Logger.i("Participant");
                        Logger.json(chatMessage.getContent());
                        listenerManager.callOnGetThreadParticipant(chatMessage.getContent());
                        listenerManager.callOnGetThreadParticipant(chatMessage.getContent());
                        messageCallbacks.remove(messageUniqueId);
                    }
                }
                break;
        }
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
        Logger.i("RECEIVE_USER_INFO", chatMessage.getContent());
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
        RetrofitHelperSsoHost retrofitHelperSsoHost = new RetrofitHelperSsoHost(ssoHost);
        TokenApi tokenApi = retrofitHelperSsoHost.getService(TokenApi.class);
        rx.Observable<Response<DeviceResult>> listObservable = tokenApi.getDeviceId("Bearer" + " " + getToken());
        listObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(deviceResults -> {
            if (deviceResults.isSuccessful()) {
                ArrayList<Device> devices = deviceResults.body().getDevices();
                for (Device device : devices) {
                    if (device.isCurrent()) {
                        if (BuildConfig.DEBUG) Logger.i("DEVICE_ID :" + device.getUid());
                        async.connect(serverAddress, appId, severName, token, ssoHost, device.getUid());
                        return;
                    }
                }
            }
        }, throwable -> Logger.e("Error on get devices", throwable.toString()));
    }

    /**
     * Reformat the get thread response
     */
    private String reformatGetThreadsResponse(ChatMessage chatMessage, OutPutThreads outPutThreads) {
        if (BuildConfig.DEBUG) Logger.json(chatMessage.getContent());
        ArrayList<Thread> threads = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            threads = objectMapper.readValue(
                    chatMessage.getContent(),
                    objectMapper.getTypeFactory().constructCollectionType(
                            ArrayList.class, Thread.class));
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
        ResultThreads resultThreads = new ResultThreads();
        resultThreads.setThreads(threads);
        outPutThreads.setContentCount(chatMessage.getContentCount());
        outPutThreads.setErrorCode(0);
        outPutThreads.setErrorMessage("");
        outPutThreads.setHasError(false);
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
        resultContact.setContacts(contacts);
        outPutContact.setResult(resultContact);
        outPutContact.setContentCount(chatMessage.getContentCount());
        return JsonUtil.getJson(outPutContact);
    }


    private static synchronized String getUniqueId() {
        return UUID.randomUUID().toString();
    }

    private void setToken(String token) {
        this.token = token;
    }

    private String getToken() {
        return token;
    }

    private int getUserId() {
        return userId;
    }

    private void setUserId(int userId) {
        this.userId = userId;
    }

    private void setCallBacks(Boolean delivery, Boolean sent, Boolean seen, Boolean result, int requestType, Integer offset, String uniqueId) {
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

    private void setPlatformHost(String platformHost) {
        this.platformHost = platformHost;
    }

    private String getPlatformHost() {
        return platformHost;
    }

    private void setContact(String contact) {
        this.contact = contact;
    }

    private String getContact() {
        return contact;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}