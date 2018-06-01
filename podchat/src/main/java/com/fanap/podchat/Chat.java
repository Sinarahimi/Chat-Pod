package com.fanap.podchat;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import com.fanap.podasync.Async;
import com.fanap.podasync.AsyncAdapter;
import com.fanap.podasync.JsonUtil;
import com.fanap.podchat.model.ChatMessage;
import com.fanap.podchat.model.ChatMessageContent;
import com.fanap.podchat.model.ChatMessageType;
import com.fanap.podchat.model.ChatMessageType.Constants;
import com.fanap.podchat.model.Invite;
import com.fanap.podchat.model.Message;
import com.fanap.podchat.model.ChatThread;
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

    public void init(Context context) {
        async = Async.getInstance(context).addListener(this);
        moshi = new Moshi.Builder().build();
    }

    public void connect(String serverAddress, String appId, String severName, String token) {
        setToken(token);
        async.connect(serverAddress, appId, severName, token);
    }

    @Override
    public void OnTextMessage(String textMessage) throws IOException {
        super.OnTextMessage(textMessage);
        Log.i("OnTextMessage: Chat", textMessage);
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
                break;
            case Constants.ERROR:
                break;
            case Constants.FORWARD_MESSAGE:
                break;
            case Constants.GET_CONTACTS:
                break;
            case Constants.GET_HISTORY:
                JsonAdapter<Message> jsonHistoryAdapter = moshi.adapter(Message.class);
                Message jsonHistoryMessage = jsonHistoryAdapter.fromJson(chatMessage.getContent());
                jsonHistoryMessage.getId();
                jsonHistoryMessage.getUniqueId();
                jsonHistoryMessage.getPreviousId();
                jsonHistoryMessage.getMessage();
                jsonHistoryMessage.getParticipant();
                break;
            case Constants.GET_STATUS:
                break;
            case Constants.GET_THREADS:

                Log.d(TAG, "OnTextMessage:GET_THREADS  .");
                chatMessage.getUniqueId();
                chatMessage.getTime();
                conversations = new ArrayList<>(Arrays.asList(chatMessage.getContent().split(",")));
                setConversations(conversations);

                break;
            case Constants.INVITATION:
                Log.d(TAG, "INVITATION  .");
                break;
            case Constants.LAST_SEEN_TYPE:
                break;
            case Constants.LEAVE_THREAD:
                break;
            case Constants.MESSAGE:
                Log.d(TAG, "OnTextMessage:GET_THREADS  .");
                break;
            case Constants.MUTE_THREAD:
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
                break;
            case Constants.SENT:
                break;
            case Constants.THREAD_PARTICIPANTS:
                break;
            case Constants.UNBLOCK:
                break;
            case Constants.UN_MUTE_THREAD:
                break;
            case Constants.UPDATE_METADATA:
                break;
            case Constants.USER_INFO:
                break;
            case Constants.USER_STATUS:
                break;
            case Constants.USER_S_STATUS:
                break;
        }
    }

    public void sendTextMessage(String textMessage, long threadId){

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

        //
        //      sendMessage = function(params, callbacks) {
        //        /**
        //         * + ChatMessage    {object}
        //         *    - token       {string}
        //         *    - tokenIssuer {string}
        //         *    - type        {int}
        //         *    - subjectId   {long}
        //         *    - uniqueId    {string}
        //         *    - content     {string}
        //         *    - time        {long}
        //         *    - medadata    {string}
        //         *    - repliedTo   {long}
        //         */
        //        var messageVO = {
        //          type: params.chatMessageVOType,
        //          token: params.token,
        //          tokenIssuer: 1
        //        };
        //
        //        if (params.subjectId) {
        //          messageVO.subjectId = params.subjectId;
        //        }
        //
        //        if (params.repliedTo) {
        //          messageVO.repliedTo = params.repliedTo;
        //        }
        //
        //        if (params.content) {
        //          if (typeof params.content == "object") {
        //            messageVO.content = JSON.stringify(params.content);
        //          } else {
        //            messageVO.content = params.content;
        //          }
        //        }
        //
        //        if (params.metaData) {
        //          messageVO.metadata = params.metaData;
        //        }
        //
        //        var uniqueId;
        //
        //        if (typeof params.uniqueId != "undefined") {
        //          uniqueId = params.uniqueId;
        //        } else {
        //          uniqueId = Utility.generateUUID();
        //        }
        //
        //        if (Array.isArray(uniqueId)) {
        //          messageVO.uniqueId = JSON.stringify(uniqueId);
        //        } else {
        //          messageVO.uniqueId = uniqueId;
        //        }
        //
        //        if (typeof callbacks == "object") {
        //          if (callbacks.onSeen || callbacks.onDeliver || callbacks.onSent) {
        //            sendMessageCallbacks[uniqueId] = {};
        //
        //            if (callbacks.onSent) {
        //              sendMessageCallbacks[uniqueId].onSent = callbacks.onSent;
        //            }
        //
        //            if (callbacks.onSeen) {
        //              sendMessageCallbacks[uniqueId].onSeen = callbacks.onSeen;
        //            }
        //
        //            if (callbacks.onDeliver) {
        //              sendMessageCallbacks[uniqueId].onDeliver = callbacks.onDeliver;
        //            }
        //
        //          } else if (callbacks.onResult) {
        //            messagesCallbacks[uniqueId] = callbacks.onResult;
        //          }
        //        } else if (typeof callbacks == "function") {
        //          messagesCallbacks[uniqueId] = callbacks;
        //        }
        /*
        *
        *
        * */



    }

    public void getThread(int count, int offset) {
        Log.i("get thread called", "count" + count);
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
        chatMessage.setSubjectId(threadId);

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);

        async.sendMessage(asyncContent, 3);
    }

    public void getHistory(int count, int offset) {
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

        JsonAdapter<ChatMessage> chatMessageJsonAdapter = moshi.adapter(ChatMessage.class);
        String asyncContent = chatMessageJsonAdapter.toJson(chatMessage);

        async.sendMessage(asyncContent, 3);
    }

    public void getContacts(int count, int offset) {
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

    /*/
    * inviteeVOidTypes = {
        TO_BE_USER_SSO_ID: 1,
        TO_BE_USER_CONTACT_ID: 2,
        TO_BE_USER_CELLPHONE_NUMBER: 3,
        TO_BE_USER_USERNAME: 4
      },
      createThreadTypes = {
        NORMAL: 0,
        OWNER_GROUP: 1,
        PUBLIC_GROUP: 2,
        CHANNEL_GROUP: 4,
        CHANNEL: 8
      }
    *
    * */

    public void createThread(int type, String title) {
        Log.i("createThread called", "count" + type);

        List<Invite> invites = new ArrayList<>();
        invites.add(new Invite(441,2));
//        invites.add(new Invite(442,2));

        ChatThread chatThread = new ChatThread();
        chatThread.setType(type);
        chatThread.setTitle(title);
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

    public String onMessage() {
        return async.getMessageLiveData().getValue();
    }

    /**
     * Get list of conversations
     */
    public List<String> getConversation() {
        return conversations;
    }

    public void setConversations(List<String> conversations) {
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
}
