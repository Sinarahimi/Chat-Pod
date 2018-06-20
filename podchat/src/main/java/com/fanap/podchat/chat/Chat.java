package com.fanap.podchat.chat;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fanap.podasync.Async;
import com.fanap.podasync.AsyncAdapter;
import com.fanap.podasync.networking.RetrofitHelper;
import com.fanap.podasync.util.JsonUtil;
import com.fanap.podchat.BuildConfig;
import com.fanap.podchat.model.AddContact;
import com.fanap.podchat.model.ChatMessage;
import com.fanap.podchat.model.ChatMessageContent;
import com.fanap.podchat.model.Contact;
import com.fanap.podchat.model.OutPutThread;
import com.fanap.podchat.model.OutPutUserInfo;
import com.fanap.podchat.model.ResultThread;
import com.fanap.podchat.model.ResultUserInfo;
import com.fanap.podchat.model.Thread;
import com.fanap.podchat.model.Error;
import com.fanap.podchat.model.OutPut;
import com.fanap.podchat.model.OutPutContact;
import com.fanap.podchat.model.OutPutThreads;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.model.Results;
import com.fanap.podchat.networking.api.ContactApi;
import com.fanap.podchat.util.CallBack;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.ChatMessageType.Constants;
import com.fanap.podchat.model.ChatThread;
import com.fanap.podchat.model.Invitee;
import com.fanap.podchat.model.Message;
import com.fanap.podchat.model.UserInfo;
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
    private List<String> conversations;
    private String token;
    private static ChatListenerManager listenerManager;
    private int userId;
    private ArrayList<CallBack> uniquesArrayList = new ArrayList<>();
    private CallBack callBack = new CallBack();
    private String platformHost;
    private ContactApi contactApi;
    private static HashMap<String, Integer> messageCallbacks;
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
        //TOdO remove this log before release
        if (BuildConfig.DEBUG) Log.d("RAW_MESSAGE", textMessage);
        super.onReceivedMessage(textMessage);
        int messageType = 0;
        JsonAdapter<ChatMessage> jsonAdapter = moshi.adapter(ChatMessage.class);
        ChatMessage chatMessage = jsonAdapter.fromJson(textMessage);
        String messageUniqueId = chatMessage.getUniqueId();
        int requestType = messageCallbacks.get(messageUniqueId);
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
                handleResponseMessage(requestType, true, 0, "", chatMessage);
                break;
            case Constants.FORWARD_MESSAGE:
                break;
            case Constants.GET_CONTACTS:
                handleResponseMessage(requestType, false, 0, "", chatMessage);
                break;
            case Constants.GET_HISTORY:
                handleResponseMessage(requestType, false, 0, "", chatMessage);
                break;
            case Constants.GET_STATUS:
                break;
            case Constants.GET_THREADS:
                handleResponseMessage(requestType, false, 0, "", chatMessage);
                break;
            case Constants.INVITATION:
                handleResponseMessage(requestType, false, 0, "", chatMessage);
                break;
            case Constants.LAST_SEEN_TYPE:
                break;
            case Constants.LEAVE_THREAD:
                break;
            case Constants.MESSAGE:
                handleOnMessage(chatMessage);
                break;
            case Constants.MUTE_THREAD:
                handleResponseMessage(requestType, false, 0, "", chatMessage);
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
                handleOnSeenMessage(chatMessage);
                break;
            case Constants.SENT:
                handleOnSentMessage(chatMessage);
                break;
            case Constants.THREAD_PARTICIPANTS:
                handleOnGetThreadParticipant(chatMessage);
                break;
            case Constants.UNBLOCK:
                break;
            case Constants.UN_MUTE_THREAD:
                handleResponseMessage(requestType, false, 0, "", chatMessage);
                break;
            case Constants.UPDATE_METADATA:
                break;
            case Constants.USER_INFO:
                handleResponseMessage(requestType, false, 0, "", chatMessage);
                break;
            case Constants.USER_STATUS:
                break;
            case Constants.USER_S_STATUS:
                break;
            case Constants.DELETE_MESSAGE:
                break;
            case Constants.EDIT_MESSAGE:
                handleResponseMessage(requestType, false, 0, "", chatMessage);
                break;
        }
    }

    private void handleResponseMessage(int type, boolean hasError, int errorCode, String errorMessage, ChatMessage chatMessage) {
        Results results = new Results();
        OutPut outPut = new OutPut();
        switch (type) {
            case Constants.DELIVERY:
                Log.i("RECEIVE_DELIVER_MESSAGE", chatMessage.getContent());
                listenerManager.callOnDeliveryMessage(chatMessage.getContent());
                break;
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
                for (CallBack callBack : uniquesArrayList) {
                    if (callBack.getUniqueId().equals(chatMessage.getUniqueId())) {
                        Error error = JsonUtil.fromJSON(chatMessage.getContent(), Error.class);
                        Log.e("RECEIVED_ERROR", chatMessage.getContent());
                        Log.e("ErrorMessage", error.getMessage());
                        Log.e("ErrorCode", String.valueOf(error.getCode()));
                        outPut.setErrorMessage(error.getMessage());
                        outPut.setErrorCode(error.getCode());
                        String errorJson = JsonUtil.getJson(outPut);
                        listenerManager.callOnError(errorJson);
                    }
                }
                break;
            case Constants.GET_CONTACTS:
                OutPutContact outPutContact = new OutPutContact();
                if (hasError) {
                    String errorJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorJson);
                } else {
                    String contactJson = reformatGetContactResponse(chatMessage, outPutContact);
                    listenerManager.callOnGetContacts(contactJson);
                }
                break;
            case Constants.GET_THREADS:
                OutPutThreads outPutThreads = new OutPutThreads();
                if (hasError) {
                    String errorJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorJson);
                } else {
                    String threadJson = reformatGetThreadsResponse(chatMessage, outPutThreads);
                    listenerManager.callOnGetThread(threadJson);
                }
                break;
            case Constants.INVITATION:
                if (hasError) {
                    String errorJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorJson);
                } else {
                    OutPutThread outPutThread = new OutPutThread();
                    String inviteJson = reformatCreateThread(chatMessage, outPutThread);
                    listenerManager.callOnCreateThread(inviteJson);
                }
                break;
            case Constants.MUTE_THREAD:
                if (hasError) {
                    String errorJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorJson);
                } else {
                    String muteThreadJson = reformatMuteThread(chatMessage, outPut);
                    listenerManager.callOnMuteThread(muteThreadJson);
                }
                break;
            case Constants.UN_MUTE_THREAD:
                if (hasError) {
                    String errorJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorJson);
                } else {
                    String muteThreadJson = reformatMuteThread(chatMessage, outPut);
                    listenerManager.callOnUnmuteThread(muteThreadJson);
                }
            case Constants.EDIT_MESSAGE:
                if (hasError) {
                    String errorJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorJson);
                } else {
                    if (BuildConfig.DEBUG) Log.i("RECEIVE_EDIT_MESSAGE", chatMessage.getContent());
                    listenerManager.callOnEditedMessage(chatMessage.getContent());
                }
                break;
            case Constants.USER_INFO:
                if (hasError) {
                    if (BuildConfig.DEBUG) Log.e("USER_INFO", "RECEIVE_ERROR");
                    String errorJson = reformatError(true, chatMessage, outPut);
                    listenerManager.callOnError(errorJson);
                } else {
                    String userInfoJson = reformatUserInfo(chatMessage);
                    listenerManager.callOnUserInfo(userInfoJson);
                }
                break;
        }
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

    private void handleOnGetThreadParticipant(ChatMessage chatMessage) {
        Log.i("Participant", chatMessage.getContent());
        listenerManager.callOnGetThreadParticipant(chatMessage.getContent());
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
    private void handleOnMessage(ChatMessage chatMessage) throws IOException {
        Log.i("RECEIVED_MESSAGE", chatMessage.getContent());
        JsonAdapter<Message> jsonHistoryAdapter = moshi.adapter(Message.class);
        Message jsonMessage = jsonHistoryAdapter.fromJson(chatMessage.getContent());

        long ownerId = jsonMessage.getParticipant().getId();

        if (ownerId != getUserId()) {
            ChatMessage message = new ChatMessage();
            message.setType(Constants.DELIVERY);
            message.setContent(String.valueOf(jsonMessage.getId()));
            message.setTokenIssuer("1");
            message.setToken(getToken());
            message.setUniqueId(getUniqueId());
            message.setTime(1000);

            JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
            String asyncContent = chatMessageJsonAdapter.toJson(message);

            async.sendMessage(asyncContent, 4);
        }
    }

    private void handleOnSentMessage(ChatMessage chatMessage) {
        for (CallBack callBack : uniquesArrayList) {
            if (callBack.getUniqueId().equals(chatMessage.getUniqueId())) {
                Log.i("Received :Sent Message", chatMessage.getContent());
                listenerManager.callOnSentMessage(chatMessage.getContent());
            }
        }
    }

    private void handleOnSeenMessage(ChatMessage chatMessage) {
        for (CallBack callBack : uniquesArrayList) {
            if (callBack.getUniqueId().equals(chatMessage.getUniqueId())) {
                Log.i("RECEIVE_SEEN_MESSAGE", chatMessage.getContent());
                listenerManager.callOnSeenMessage(chatMessage.getContent());
            }
        }
    }

    public void sendTextMessage(String textMessage, long threadId) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(textMessage);
        chatMessage.setType(Constants.MESSAGE);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(getUniqueId());
        chatMessage.setTime(1000);
        chatMessage.setSubjectId(threadId);
        addUniqueIdsToArray(getUniqueId(), true, true, true);

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);
        Log.d("Message send", asyncContent);

        async.sendMessage(asyncContent, 4);
    }

    //TODO forward Message
    public void forwardMessage() {

    }

    public void sendReplyMessage(String messageContent, long threadId, long messageId) {
        String uniqueId = getUniqueId();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUniqueId(uniqueId);
        addUniqueIdsToArray(uniqueId, true, true, true);
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

    /**
     * We add all of the unique Ides to the arrayList so that we identified which message was
     * received
     */
    private void addUniqueIdsToArray(String uniqueId, boolean delivery, boolean seen, boolean sent) {
        callBack.setUniqueId(uniqueId);
        callBack.setSeen(seen);
        callBack.setDelivery(delivery);
        callBack.setSent(sent);
        uniquesArrayList.add(callBack);
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
        chatMessage.setType(ChatMessageType.Constants.GET_THREADS);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(uniqueId);

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);
        Log.d("Get thread send", asyncContent);

        sendChatMessage(asyncContent, 3, uniqueId, Constants.GET_THREADS);
    }

    public void getHistory(int count, int offset, String order, long threadId) {
        ChatMessageContent chatMessageContent = new ChatMessageContent();
        chatMessageContent.setCount(count);
        chatMessageContent.setOffset(offset);
        chatMessageContent.setOrder(order);

        JsonAdapter<ChatMessageContent> messageContentJsonAdapter = moshi.adapter(ChatMessageContent.class);
        String content = messageContentJsonAdapter.toJson(chatMessageContent);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setType(Constants.GET_HISTORY);
        chatMessage.setUniqueId(getUniqueId());
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");
        chatMessage.setSubjectId(threadId);

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);

        async.sendMessage(asyncContent, 3);
    }

    /**
     * Get history of the
     */
    public void getHistory(int count, int offset, long threadId) {
        ChatMessageContent chatMessageContent = new ChatMessageContent();
        chatMessageContent.setCount(count);
        chatMessageContent.setOffset(offset);

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

        sendChatMessage(asyncContent, 3, uniqueId, Constants.GET_HISTORY);
    }


    private void sendChatMessage(String asyncContent, int asyncMessageType, String uniqueId, int chatType) {
        messageCallbacks.put(uniqueId, chatType);
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

        Log.d("GET_CONTACT_SEND", asyncContent);
        sendChatMessage(asyncContent, 3, uniqueId, Constants.GET_CONTACTS);
    }

    /**
     * Add contact
     */
    public void addContact(String firstName, String lastName, String cellphoneNumber, String email) {
        AddContact addContact = new AddContact();
        Contact contact = new Contact();
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setCellphoneNumber(cellphoneNumber);
        contact.setEmail(email);
        contact.setUniqueId(getUniqueId());
        Observable<Response<AddContact>> addContactObservable = contactApi.addContact(getToken(), 1, contact);
        addContactObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(contactResponse -> {
            if (contactResponse.isSuccessful()) {
                if (BuildConfig.DEBUG)
                    Log.i("AddContactResponse", contactResponse.body().toString());
                listenerManager.callOnContactAdded(contactResponse.body().toString());
            }
        }, throwable ->
                Log.e("Error on add contact", throwable.toString()));
    }

    public void removeContact(String userId) {
        Observable<Response> removeContactObservable = contactApi.removeContact(getToken(), 1, userId);
        removeContactObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {

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

        }, throwable -> Log.e("Error on update contact", throwable.toString()));
    }

    /**
     * Create the thread. The list below is showing all of the thread type
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
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(contentThreadChat);
        chatMessage.setType(Constants.INVITATION);
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTokenIssuer("1");

        String asyncContent = JsonUtil.getJson(chatMessage);
        sendChatMessage(asyncContent, 4, uniqueId, Constants.INVITATION);
        if (BuildConfig.DEBUG) Log.d("createThread called", String.valueOf(threadType));
    }

    public void getThreadParticipant(int count, int offset, long threadId) {
        ChatMessageContent chatMessageContent = new ChatMessageContent();
        chatMessageContent.setCount(count);
        chatMessageContent.setOffset(offset);

        JsonAdapter<ChatMessageContent> messageContentJsonAdapter = moshi.adapter(ChatMessageContent.class);
        String content = messageContentJsonAdapter.toJson(chatMessageContent);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setType(Constants.THREAD_PARTICIPANTS);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(getUniqueId());
        chatMessage.setSubjectId(threadId);

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);
        Log.i("Thread Participant send", asyncContent);

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
        chatMessage.setToken(uniqueId);
        String asyncContent = JsonUtil.getJson(chatMessage);
        sendChatMessage(asyncContent, 3, uniqueId, Constants.USER_INFO);
    }

    public void muteThread(int threadId) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.MUTE_THREAD);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");
        chatMessage.setSubjectId(threadId);
        String uniqueId = getUniqueId();
        chatMessage.setUniqueId(uniqueId);

        String asyncContent = JsonUtil.getJson(chatMessage);
        sendChatMessage(asyncContent, 4, uniqueId, Constants.MUTE_THREAD);
    }

    public void unmuteThread(int threadId) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.UN_MUTE_THREAD);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");
        chatMessage.setSubjectId(threadId);
        String uniqueId = getUniqueId();
        chatMessage.setUniqueId(uniqueId);

        String asyncContent = JsonUtil.getJson(chatMessage);
        sendChatMessage(asyncContent, 4, uniqueId, Constants.UN_MUTE_THREAD);
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

        sendChatMessage(asyncContent, 4, uniqueId, Constants.EDIT_MESSAGE);
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

    /**
     * Get list of conversations
     */
    public List<String> getConversation() {
        return conversations;
    }

    private void setConversations(List<String> conversations) {
        this.conversations = conversations;
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

    private void setPlatformHost(String platformHost) {
        this.platformHost = platformHost;
    }

    private String getPlatformHost() {
        return platformHost;
    }
}
