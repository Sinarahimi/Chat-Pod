package com.fanap.podchat.chat;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import com.fanap.podasync.Async;
import com.fanap.podasync.AsyncAdapter;
import com.fanap.podasync.util.JsonUtil;
import com.fanap.podchat.model.ChatMessage;
import com.fanap.podchat.model.ChatMessageContent;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.ChatMessageType.Constants;
import com.fanap.podchat.model.ChatThread;
import com.fanap.podchat.model.Invite;
import com.fanap.podchat.model.InviteType;
import com.fanap.podchat.model.Message;
import com.fanap.podchat.model.UserInfo;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Chat extends AsyncAdapter {

    private Async async;
    private Moshi moshi;
    private List<String> conversations;
    private static String TAG = "Chat";
    private String token;
    private ChatListenerManager listenerManager;
    private int userId;

    public void init(Context context) {
        async = Async.getInstance(context).addListener(this);
        moshi = new Moshi.Builder().build();
        listenerManager = new ChatListenerManager();
    }

    /**
     * Connect to the Async
     */
    public void connect(String serverAddress, String appId, String severName, String token, String ssoHost) {
        async.connect(serverAddress, appId, severName, token, ssoHost);
        setToken(token);
        getUserInfo();
    }


    /**
     * First we check the message type and then we set the
     * the  specific callback for that
     */
    @Override
    public void OnTextMessage(String textMessage) throws IOException {
        super.OnTextMessage(textMessage);
        int messageType = 0;

        JsonAdapter<ChatMessage> jsonAdapter = moshi.adapter(ChatMessage.class);
        ChatMessage chatMessage = jsonAdapter.fromJson(textMessage);
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
                handleOnDelivery(chatMessage);
                break;
            case Constants.ERROR:
                handleError(chatMessage);
                break;
            case Constants.FORWARD_MESSAGE:
                break;
            case Constants.GET_CONTACTS:
                handleOnGetContacts(chatMessage);
                break;
            case Constants.GET_HISTORY:
                handleOnGetHistory(chatMessage);
                break;
            case Constants.GET_STATUS:
                break;
            case Constants.GET_THREADS:
                handleOnGetThread(chatMessage);
                break;
            case Constants.INVITATION:
                handleOnInvitation(chatMessage);
                break;
            case Constants.LAST_SEEN_TYPE:
                break;
            case Constants.LEAVE_THREAD:
                break;
            case Constants.MESSAGE:
                handleOnMessage(chatMessage);
                break;
            case Constants.MUTE_THREAD:
                handleMuteThread(chatMessage);
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
                handleUnMuteThread(chatMessage);
                break;
            case Constants.UPDATE_METADATA:
                break;
            case Constants.USER_INFO:
                handleOnUserInfoMessage(chatMessage);
                break;
            case Constants.USER_STATUS:
                break;
            case Constants.USER_S_STATUS:
                break;
            case Constants.DELETE_MESSAGE:
                break;
            case Constants.EDIT_MESSAGE:
                handleOnEditedMessage(chatMessage);
                Log.i("EDIT_MESSAGE", chatMessage.getContent());
                break;
        }
    }

    private void handleOnEditedMessage(ChatMessage chatMessage) {
        Log.i("EditedMessage", chatMessage.getContent());
        listenerManager.callOnEditedMessage(chatMessage.getContent());
    }

    private void handleOnGetThreadParticipant(ChatMessage chatMessage) {
        Log.i("Participant", chatMessage.getContent());
        listenerManager.callOnGetThreadParticipant(chatMessage.getContent());
    }

    private void handleError(ChatMessage chatMessage) {
        Log.e("Chat: Error", chatMessage.getContent());
        listenerManager.callOnError(chatMessage.getContent());
    }

    /**
     * Remove the peerId and send ping again but this time
     * peerId that was set in the server was removed
     */
    public void logOutSocket() {
        async.logOutSocket();
    }

    private void handleUnMuteThread(ChatMessage chatMessage) {
        Log.i("UN_MUTE_THREAD", chatMessage.getContent());
    }

    private void handleMuteThread(ChatMessage chatMessage) {
        Log.i("MUTE_THREAD", chatMessage.getContent());
    }

    private void handleOnMessage(ChatMessage chatMessage) throws IOException {
        Log.i("Chat:Message", chatMessage.getContent());
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

    private void handleOnUserInfoMessage(ChatMessage chatMessage) {
        Log.i("USER_INFO", chatMessage.getContent());
        UserInfo userInfo = new UserInfo();
        setUserId(userInfo.getId());
    }

    private void handleOnSentMessage(ChatMessage chatMessage) {
        Log.i("Sent:Message", chatMessage.getContent());
        listenerManager.callOnSentMessage(chatMessage.getContent());
    }

    private void handleOnSeenMessage(ChatMessage chatMessage) {
        Log.i("Seen:Message", chatMessage.getContent());
        listenerManager.callOnSeenMessage(chatMessage.getContent());
    }

    private void handleOnInvitation(ChatMessage chatMessage) {
        Log.i("INVITATION ", chatMessage.getContent());
        listenerManager.callOnInvitation(chatMessage.getContent());
    }

    private void handleOnDelivery(ChatMessage chatMessage) {
        Log.i("DeliveryMessage", chatMessage.getContent());
        listenerManager.callOnDeliveryMessage(chatMessage.getContent());
    }

    private void handleOnGetContacts(ChatMessage chatMessage) {
        listenerManager.callOnGetContacts(chatMessage.getContent());
    }

    private void handleOnGetHistory(ChatMessage chatMessage) throws IOException {
        Log.i("GET_THREAD_HISTORY", chatMessage.getContent());
        JsonAdapter<Message> jsonHistoryAdapter = moshi.adapter(Message.class);
        Message jsonHistoryMessage = jsonHistoryAdapter.fromJson(chatMessage.getContent());
        listenerManager.callOnGetThreadHistory(chatMessage.getContent());
        jsonHistoryMessage.getId();
        jsonHistoryMessage.getUniqueId();
        jsonHistoryMessage.getPreviousId();
        jsonHistoryMessage.getMessage();
        jsonHistoryMessage.getParticipant();
    }

    private void handleOnGetThread(ChatMessage chatMessage) {
        Log.i("GET_THREADS", chatMessage.getContent());
        chatMessage.getUniqueId();
        chatMessage.getTime();
        conversations = new ArrayList<>(Arrays.asList(chatMessage.getContent().split(",")));
        setConversations(conversations);
        listenerManager.callOnGetThread(chatMessage.getContent());
    }

    public void sendTextMessage(String textMessage, long threadId) {
        Log.i("send Message called", textMessage);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(textMessage);
        chatMessage.setType(Constants.MESSAGE);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(getUniqueId());
        chatMessage.setTime(1000);
        chatMessage.setSubjectId(threadId);

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);

        async.sendMessage(asyncContent, 4);
    }

    public void getThread(int count, int offset) {
        ChatMessageContent chatMessageContent = new ChatMessageContent();
        chatMessageContent.setCount(count);
        chatMessageContent.setOffset(offset);

        JsonAdapter<ChatMessageContent> messageContentJsonAdapter = moshi.adapter(ChatMessageContent.class);
        String content = messageContentJsonAdapter.toJson(chatMessageContent);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setType(ChatMessageType.Constants.GET_THREADS);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(getUniqueId());

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);

        async.sendMessage(asyncContent, 3);
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

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setType(Constants.GET_HISTORY);
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(getUniqueId());
        chatMessage.setSubjectId(threadId);

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);

        async.sendMessage(asyncContent, 3);
    }

    /**
     * Get all of the contacts of the user
     */
    public void getContact(int count, int offset) {
        ChatMessageContent chatMessageContent = new ChatMessageContent();
        chatMessageContent.setCount(count);
        chatMessageContent.setOffset(offset);

        JsonAdapter<ChatMessageContent> messageContentJsonAdapter = moshi.adapter(ChatMessageContent.class);
        String content = messageContentJsonAdapter.toJson(chatMessageContent);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setType(Constants.GET_CONTACTS);
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(getUniqueId());

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);

        async.sendMessage(asyncContent, 3);
    }

    /**
     * Create the thread for chat with another user
     */
    public void createThread(int chatThreadType, int contactId) {
        Log.i("createThread called", "count" + chatThreadType);

        List<Invite> invites = new ArrayList<>();
        invites.add(new Invite(contactId, InviteType.Constants.TO_BE_USER_CONTACT_ID));

        ChatThread chatThread = new ChatThread();
        chatThread.setType(chatThreadType);
        chatThread.setInvitees(invites);

        String contentThreadChat = JsonUtil.getJson(chatThread);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(contentThreadChat);
        chatMessage.setType(Constants.INVITATION);
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(getUniqueId());
        chatMessage.setTokenIssuer("1");

        String asyncContent = JsonUtil.getJson(chatMessage);

        async.sendMessage(asyncContent, 4);
        Log.i("Create thread", asyncContent);
    }

//    private void sendMessage(String token, String uniqueId, String tokenIssuer,
//                             String content, int ChatType, int AsyncType, String metadata,){
//
//    }

    public void createGroupThread(int type, String title, Invite[] invite) {
        Log.i("createThread called", "count" + type);

        List<Invite[]> invites = new ArrayList<>();
        for (Invite[] invitees : invites) {
            invites.add(invitees);
        }

        ChatThread chatThread = new ChatThread();
        chatThread.setType(type);
        chatThread.setTitle(title);
        chatThread.setArrayInvitees(invites);

        String contentThreadChat = JsonUtil.getJson(chatThread);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(contentThreadChat);
        chatMessage.setType(Constants.INVITATION);
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(getUniqueId());
        chatMessage.setTokenIssuer("1");

        String asyncContent = JsonUtil.getJson(chatMessage);

        async.sendMessage(asyncContent, 4);
        Log.i("Create thread", asyncContent);
    }

    //TODO forward Message
    public void forwardMessage() {

    }

    //TODO reply Message
    public void replyMessage() {

    }

    public void getThreadParticipant(int count, int offset, long threadId) {
        Log.i("get thread called", "count" + count);
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

    public void getUserInfo() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.USER_INFO);
        chatMessage.setToken(getToken());
        String asyncContent = JsonUtil.getJson(chatMessage);

        async.sendMessage(asyncContent, 3);
    }

    public void muteThread(int threadId) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.MUTE_THREAD);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");
        chatMessage.setSubjectId(threadId);
        chatMessage.setUniqueId(getUniqueId());

        String asyncContent = JsonUtil.getJson(chatMessage);
        async.sendMessage(asyncContent, 4);
    }

    public void unMuteThread(int threadId) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.UN_MUTE_THREAD);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");
        chatMessage.setSubjectId(threadId);
        chatMessage.setUniqueId(getUniqueId());

        String asyncContent = JsonUtil.getJson(chatMessage);
        async.sendMessage(asyncContent, 4);
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
        chatMessage.setToken(getToken());
        chatMessage.setContent(editedMessage);
        chatMessage.setTokenIssuer("1");
        String asyncContent = JsonUtil.getJson(chatMessage);

        async.sendMessage(asyncContent, 4);
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
}
