package com.fanap.podchat.chat;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fanap.podasync.Async;
import com.fanap.podasync.AsyncAdapter;
import com.fanap.podasync.util.JsonUtil;
import com.fanap.podchat.BuildConfig;
import com.fanap.podchat.model.ChatMessage;
import com.fanap.podchat.model.ChatMessageContent;
import com.fanap.podchat.model.ChatThread;
import com.fanap.podchat.model.Contact;
import com.fanap.podchat.model.Error;
import com.fanap.podchat.model.Invitee;
import com.fanap.podchat.model.Message;
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
import com.fanap.podchat.networking.api.ContactApi;
import com.fanap.podchat.util.CallBacks;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.ChatMessageType.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Chat extends AsyncAdapter {

    private static Async async;
    private static Moshi moshi;
    private String token;
    private static ChatListenerManager listenerManager;
    private int userId;
    private ContactApi contactApi;
    private static HashMap<String, CallBacks> messageCallbacks;
    private static Chat instance;

    public static Chat init(Context context) {
        if (instance == null) {
            async = Async.getInstance(context);
            instance = new Chat();
            moshi = new Moshi.Builder().build();
            listenerManager = new ChatListenerManager();
            messageCallbacks = new HashMap<>();
        }
        return instance;
    }

    /**
     * Connect to the Async with params that client set to it.
     */
    public void connect(String serverAddress, String appId, String severName, String token,
                        String ssoHost, String platformHost) {
        async.addListener(this);
        async.connect(serverAddress, appId, severName, token, ssoHost);
        RetrofitHelper retrofitHelper = new RetrofitHelper(platformHost);
        contactApi = retrofitHelper.getService(ContactApi.class);
        setToken(token);
        getUserInfo();
    }

    /**
     * First we check the message type and then we set the
     * the  specific callback for that
     */
    @Override
    public void onReceivedMessage(String textMessage) throws IOException {
        if (BuildConfig.DEBUG) Log.d("onReceivedMessage", textMessage);
        super.onReceivedMessage(textMessage);
        int messageType = 0;
        JsonAdapter<ChatMessage> jsonAdapter = moshi.adapter(ChatMessage.class);
        ChatMessage chatMessage = jsonAdapter.fromJson(textMessage);
        String messageUniqueId = chatMessage.getUniqueId();
        CallBacks callBacks = messageCallbacks.get(messageUniqueId);
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
            case Constants.DELIVERY:
                break;
            case Constants.ERROR:
                handleResponseMessage(callBacks, true, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.FORWARD_MESSAGE:
                break;
            case Constants.GET_CONTACTS:
                handleResponseMessage(callBacks, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.GET_HISTORY:
                handleResponseMessage(callBacks, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.GET_STATUS:
                break;
            case Constants.GET_THREADS:
                handleResponseMessage(callBacks, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.INVITATION:
                handleResponseMessage(callBacks, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.LAST_SEEN_TYPE:
                break;
            case Constants.LEAVE_THREAD:
                break;
            case Constants.MESSAGE:
                handleResponseMessage(callBacks, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.MUTE_THREAD:
                handleResponseMessage(callBacks, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.PING:
                break;
            case Constants.RELATION_INFO:
                break;
            case Constants.REMOVE_PARTICIPANT:
                break;
            case Constants.RENAME:
                break;
            case Constants.SEEN:
                handleResponseMessage(callBacks, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.SENT:
                handleResponseMessage(callBacks, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.THREAD_PARTICIPANTS:
                handleResponseMessage(callBacks, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.UNBLOCK:
                break;
            case Constants.UN_MUTE_THREAD:
                handleResponseMessage(callBacks, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.UPDATE_METADATA:
                break;
            case Constants.USER_INFO:
                handleResponseMessage(callBacks, false, 0, "", chatMessage, messageUniqueId);
                break;
            case Constants.USER_STATUS:
                break;
            case Constants.USER_S_STATUS:
                break;
            case Constants.DELETE_MESSAGE:
                break;
            case Constants.EDIT_MESSAGE:
                handleResponseMessage(callBacks, false, 0, "", chatMessage, messageUniqueId);
                break;
        }
    }

    private void handleResponseMessage(CallBacks callBacks, boolean hasError, int errorCode, String errorMessage, ChatMessage chatMessage, String messageUniqueId) {
        Results results = new Results();
        OutPut outPut = new OutPut();
        switch (callBacks.getRequestType()) {
            case Constants.GET_HISTORY:
                if (hasError) {
                    String errorJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorJson);
                } else {
                    results.setContentCount(chatMessage.getContentCount());
                    if (chatMessage.getContent().length() + 23 < chatMessage.getContentCount()) {
                        results.setHasNext(true);
                    } else {
                        results.setHasNext(false);
                    }
                    results.setHistory(chatMessage.getContent());
                    results.setNextOffset(2 + chatMessage.getContent().length());
                    outPut.setErrorCode(errorCode);
                    outPut.setHasError(false);
                    outPut.setErrorMessage(errorMessage);

                    String json = JsonUtil.getJson(outPut);
                    listenerManager.callOnGetThreadHistory(json);
                }
                break;
            case Constants.ERROR:
                Error error = JsonUtil.fromJSON(chatMessage.getContent(), Error.class);
                if (BuildConfig.DEBUG) Log.e("RECEIVED_ERROR", chatMessage.getContent());
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
                    if (callBacks.isResult()) {
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
                    if (callBacks.isResult()) {
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
                    if (callBacks.isResult()) {
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
                    if (callBacks.isResult()) {
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
                    if (callBacks.isResult()) {
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
                    if (callBacks.isResult()) {
                        if (BuildConfig.DEBUG)
                            Log.i("RECEIVE_EDIT_MESSAGE", chatMessage.getContent());
                        listenerManager.callOnEditedMessage(chatMessage.getContent());
                    }
                }
                break;
            case Constants.USER_INFO:
                if (hasError) {
                    String errorUserInfoJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorUserInfoJson);
                } else {
                    if (callBacks.isResult()) {
                        String userInfoJson = reformatUserInfo(chatMessage);
                        listenerManager.callOnUserInfo(userInfoJson);
                        messageCallbacks.remove(messageUniqueId);
                    }
                }
                break;
            case Constants.SENT:
                if (hasError) {
                    String errorUserInfoJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorUserInfoJson);
                } else {
                    if (callBacks.isSent()) {
                        Log.i("Received :Sent Message", chatMessage.getContent());
                        listenerManager.callOnSentMessage(chatMessage.getContent());
                    }
                }
            case Constants.SEEN:
                if (hasError) {
                    String errorUserInfoJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorUserInfoJson);
                } else {
                    if (callBacks.isSeen()) {
                        Log.i("RECEIVE_SEEN_MESSAGE", chatMessage.getContent());
                        listenerManager.callOnSeenMessage(chatMessage.getContent());
                        listenerManager.callOnDeliveryMessage(chatMessage.getContent());
                        messageCallbacks.remove(messageUniqueId);
                    }
                }
                break;
            case Constants.DELIVERY:
                Log.i("RECEIVE_DELIVER_MESSAGE", chatMessage.getContent());
                listenerManager.callOnDeliveryMessage(chatMessage.getContent());
                break;
            case Constants.MESSAGE:
                if (hasError) {
                    String errorUserInfoJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorUserInfoJson);
                } else {
                    Log.i("RECEIVED_MESSAGE", chatMessage.getContent());
                    JsonAdapter<Message> jsonHistoryAdapter = moshi.adapter(Message.class);
                    Message jsonMessage = null;
                    try {
                        jsonMessage = jsonHistoryAdapter.fromJson(chatMessage.getContent());
                    } catch (IOException e) {
                        if (BuildConfig.DEBUG) Log.e("IOException", e.toString());
                    }
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
                    if (callBacks.isResult()) {
                        Log.i("Participant", chatMessage.getContent());
                        listenerManager.callOnGetThreadParticipant(chatMessage.getContent());
                        messageCallbacks.remove(messageUniqueId);
                    }
                }
                break;
        }
    }

    @NonNull
    private ChatMessage getChatMessage(Message jsonMessage) {
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
        Log.i("RECEIVE_USER_INFO", chatMessage.getContent());
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

    /**
     * Reformat the get thread response
     */
    private String reformatGetThreadsResponse(ChatMessage chatMessage, OutPutThreads outPutThreads) {
        if (BuildConfig.DEBUG) Log.i("RECEIVE_GET_THREADS", chatMessage.getContent());
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

    /**
     * Remove the peerId and send ping again but this time
     * peerId that was set in the server was removed
     */
    public void logOutSocket() {
        async.logOutSocket();
    }

    /**
     * When we received message from another user we check if we owner of the message
     * there is nothing to do but if we are not the owner we send the delivery message
     * to another user and seen message
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
        async.sendMessage(asyncContent, 4);
    }

    //TODO forward Message
    public void forwardMessage() {

    }

    public void sendReplyMessage(String messageContent, long threadId, long messageId) {
        String uniqueId = getUniqueId();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setRepliedTo(messageId);
        chatMessage.setSubjectId(threadId);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        chatMessage.setTime(1000);
        chatMessage.setType(Constants.MESSAGE);
        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);
        Log.d("Send Reply Message", asyncContent);

        async.sendMessage(asyncContent, 4);
    }

    public void getThread(int count, int offset) {
        ChatMessageContent chatMessageContent = new ChatMessageContent();
        chatMessageContent.setCount(count);
        chatMessageContent.setOffset(offset);

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
        sendChatMessage(asyncContent, 3);
    }

    /**
     * Get history of the
     * If order is empty [default = desc] and also you have two option [ asc | desc ]
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
        if (BuildConfig.DEBUG) Log.d("Get history send", asyncContent);
        setCallBacks(null, null, null, true, Constants.GET_HISTORY, offset, uniqueId);
        sendChatMessage(asyncContent, 3);
    }

    private void sendChatMessage(String asyncContent, int asyncMessageType) {
        async.sendMessage(asyncContent, asyncMessageType);
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
        Log.d("GET_CONTACT_SEND", asyncContent);
        sendChatMessage(asyncContent, 3);
    }

    /**
     * Add contact
     */
    public void addContact(String firstName, String lastName, String cellphoneNumber, String email) {
        String uniqueId = getUniqueId();
        Observable<Response> addContactObservable;
        addContactObservable = contactApi.addContact(getToken(), 1, firstName, lastName, email, uniqueId, cellphoneNumber);
        addContactObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(addContactResponse -> {
            if (addContactResponse.isSuccessful()) {
                listenerManager.callOnAddContact(addContactResponse.body().toString());
            }
        }, throwable -> Log.e("Error on add contact", throwable.toString()));
    }

    /**
     * Remove contact with the user id
     */
    public void removeContact(long userId) {
        Observable<Response> removeContactObservable = contactApi.removeContact(getToken(), 1, userId);
        removeContactObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
            if (response.isSuccessful()) {
                listenerManager.callOnRemoveContact(response.body().toString());
            }
        }, throwable -> Log.e("Error on remove contact", throwable.toString()));
    }

    public void updateContact(String firstName, String lastName, String cellphoneNumber, String email) {
        Contact contact = new Contact();
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setCellphoneNumber(cellphoneNumber);
        contact.setEmail(email);
        contact.setUniqueId(getUniqueId());
        Observable<Response> updateContactObservable = contactApi.updateContact(getToken(), 1, contact);
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
        sendChatMessage(asyncContent, 4);
        if (BuildConfig.DEBUG) Log.d("createThread called", String.valueOf(threadType));
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
        async.sendMessage(asyncContent, 3);
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

        async.sendMessage(asyncContent, 4);
    }

    /**
     * Get all the user information
     */
    public void getUserInfo() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.USER_INFO);
        String uniqueId = getUniqueId();
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setToken(getToken());

        setCallBacks(null, null, null, true, Constants.USER_INFO, null, uniqueId);
        String asyncContent = JsonUtil.getJson(chatMessage);
        sendChatMessage(asyncContent, 3);
    }

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
        sendChatMessage(asyncContent, 4);
    }

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
        sendChatMessage(asyncContent, 4);
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

        sendChatMessage(asyncContent, 4);
    }

    public String onMessage() {
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
        CallBacks callBacks = new CallBacks();
        callBacks.setDelivery(delivery);
        callBacks.setOffset(offset);
        callBacks.setSeen(seen);
        callBacks.setSent(sent);
        callBacks.setRequestType(requestType);
        callBacks.setResult(result);
        messageCallbacks.put(uniqueId, callBacks);
    }
}
