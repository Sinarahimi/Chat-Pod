package com.fanap.podasync;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fanap.podasync.model.AsyncConstant;
import com.fanap.podasync.model.AsyncMessageType;
import com.fanap.podasync.model.ClientMessage;
import com.fanap.podasync.model.Message;
import com.fanap.podasync.model.MessageWrapperVo;
import com.fanap.podasync.model.PeerInfo;
import com.fanap.podasync.model.RegistrationRequest;
import com.fanap.podasync.util.JsonUtil;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.neovisionaries.ws.client.WebSocketState.OPEN;

/*
 * By default WebSocketFactory uses for non-secure WebSocket connections (ws:)
 * and for secure WebSocket connections (wss:).
 */

/**
 * Send the request from SSO host to get the device Id
 * deviceIdRequest(websocket,peerInfo);
 */
public class Async extends WebSocketAdapter {

    private WebSocket webSocket;
    private static final int TIMEOUT = 5000;
    private static final int THRESHOLD = 20000;
    private static final int SOCKET_CLOSE_TIMEOUT = 110000;
    private WebSocket webSocketReconnect;
    private static final String TAG = "Async" + " ";
    private static Async instance;
    private boolean isServerRegister;
    private boolean isDeviceRegister;
    private static SharedPreferences sharedPrefs;
    private MessageWrapperVo messageWrapperVo;
    private static AsyncListenerManager asyncListenerManager;
    private static Moshi moshi;
    private String errorMessage;
    private long lastSendMessageTime;
    private long lastReceiveMessageTime;
    private String message;
    private String state;
    private String appId;
    private String peerId;
    private String deviceID;
    private MutableLiveData<String> stateLiveData = new MutableLiveData<>();
    private MutableLiveData<String> messageLiveData = new MutableLiveData<>();
    private String serverAddress;
    private final Handler pingHandler = new Handler(Looper.getMainLooper());
    private String token;
    private String serverName;
    private String ssoHost;
    private int retryStep = 1;
    private boolean reconnectOnClose = true;

    public Async() {
        //Empty constructor
    }

    public static Async getInstance(Context context) {
        if (instance == null) {
            sharedPrefs = context.getSharedPreferences(AsyncConstant.Constants.PREFERENCE, Context.MODE_PRIVATE);
            moshi = new Moshi.Builder().build();
            instance = new Async();
            asyncListenerManager = new AsyncListenerManager();
            FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder().tag("ASYNC_LOGGER").build();
            Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        }
        return instance;
    }

    /**
     * @param textMessage that received when socket send message to Async
     */

    @Override
    public void onTextMessage(WebSocket websocket, String textMessage) throws Exception {
        super.onTextMessage(websocket, textMessage);
        int type = 0;
        lastReceiveMessageTime = new Date().getTime();

        JsonAdapter<ClientMessage> jsonAdapter = moshi.adapter(ClientMessage.class);
        ClientMessage clientMessage = jsonAdapter.fromJson(textMessage);
        if (clientMessage != null) {
            type = clientMessage.getType();
        }

//        scheduleCloseSocket();
        @AsyncMessageType.MessageType int currentMessageType = type;
        switch (currentMessageType) {
            case AsyncMessageType.MessageType.ACK:
                handleOnAck(clientMessage);
                break;
            case AsyncMessageType.MessageType.DEVICE_REGISTER:
                handleOnDeviceRegister(websocket, clientMessage);
                break;
            case AsyncMessageType.MessageType.ERROR_MESSAGE:
                handleOnErrorMessage(clientMessage);
                break;
            case AsyncMessageType.MessageType.MESSAGE_ACK_NEEDED:

                handleOnMessageAckNeeded(websocket, clientMessage);
                break;
            case AsyncMessageType.MessageType.MESSAGE_SENDER_ACK_NEEDED:
                handleOnMessageAckNeeded(websocket, clientMessage);
                break;
            case AsyncMessageType.MessageType.MESSAGE:
                handleOnMessage(clientMessage);
                break;
            case AsyncMessageType.MessageType.PEER_REMOVED:
                break;
            case AsyncMessageType.MessageType.PING:
                handleOnPing(websocket, clientMessage);
                break;
            case AsyncMessageType.MessageType.SERVER_REGISTER:
                handleOnServerRegister(textMessage);
                break;
        }
    }

    /**
     * Get the current state of this WebSocket.
     * <p>
     * <p>
     * The initial state is {@link WebSocketState#CREATED CREATED}.
     * When {@link #connect(String, String, String, String, String, String)} is called, the state is changed to
     * { CONNECTING}, and then to
     * {OPEN} after a successful opening
     * handshake. The state is changed to {CLOSING} when a closing handshake
     * is started, and then to {CLOSED}
     * when the closing handshake finished.
     * </p>
     *
     * @return The current state.
     */
    @Override
    public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
        super.onStateChanged(websocket, newState);
        asyncListenerManager.callOnStateChanged(newState.toString());
        stateLiveData.postValue(newState.toString());
        setState(newState.toString());
        if (BuildConfig.DEBUG) Logger.d("onStateChanged", newState.toString());
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        super.onError(websocket, cause);
        if (BuildConfig.DEBUG) Logger.e("onError", cause.toString());
        asyncListenerManager.callOnError(cause.toString());
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
        super.onConnectError(websocket, exception);
        if (BuildConfig.DEBUG) Logger.e("onConnected", exception.toString());
    }

    /**
     * After error event its start reconnecting again.
     * Note that you should not trigger reconnection in onError() method because onError()
     * may be called multiple times due to one error.
     * Instead, onDisconnected() is the right place to trigger reconnection.
     */
    @Override
    public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
        super.onMessageError(websocket, cause, frames);
        if (BuildConfig.DEBUG) Log.e("onMessageError", cause.toString());
    }

    /**
     * <p>
     * Before a WebSocket is closed, a closing handshake is performed. A closing handshake
     * is started (1) when the server sends a close frame to the client or (2) when the
     * client sends a close frame to the server. You can start a closing handshake by calling
     * {disconnect} method (or by sending a close frame manually).
     * </p>
     */
    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
        if (BuildConfig.DEBUG) Log.e("Disconnected", serverCloseFrame.getCloseReason());
        asyncListenerManager.callOnDisconnected(serverCloseFrame.getCloseReason());
        stopSocket();
        reConnect();
    }

    @Override
    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        super.onCloseFrame(websocket, frame);
        if (BuildConfig.DEBUG) Log.e("onCloseFrame", frame.getCloseReason());
        stopSocket();
        if (reconnectOnClose) {
            Handler handlerReconnect = new Handler();
            handlerReconnect.postDelayed(() -> {
                try {
                    reConnect();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG) Logger.e("Async: reConnect", e.getMessage());
                } catch (WebSocketException e) {
                    if (BuildConfig.DEBUG) Logger.e("Async: reConnect", e.getMessage());
                }
            }, retryStep * 1000);

            if (retryStep < 60) retryStep *= 2;

        } else {
            if (BuildConfig.DEBUG) Logger.e("Socket Closed!");
        }
    }

    public void connect(String socketServerAddress, final String appId, String serverName,
                        String token, String ssoHost, String deviceID) {
        WebSocketFactory webSocketFactory = new WebSocketFactory();
        webSocketFactory.setVerifyHostname(false);
        saveDeviceId(deviceID);
        setAppId(appId);
        setServerAddress(socketServerAddress);
        setToken(token);
        setServerName(serverName);
        setSsoHost(ssoHost);
        try {
            webSocket = webSocketFactory
                    .createSocket(socketServerAddress)
                    .addListener(this);
            webSocket.setMaxPayloadSize(100);
            webSocket.addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);
            webSocket.connectAsynchronously();
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Logger.e("Async: connect", e.getMessage());
        }
    }

    /**
     * @Param textContent
     * @Param messageType it could be 3, 4, 5
     * @Param []receiversId the Id's that we want to send
     */
    public void sendMessage(String textContent, int messageType, long[] receiversId) {
        if (getState().equals("OPEN")) {
            Message message = new Message();
            message.setContent(textContent);
            message.setReceivers(receiversId);
            JsonAdapter<Message> jsonAdapter = moshi.adapter(Message.class);
            String jsonMessage = jsonAdapter.toJson(message);
            String wrapperJsonString = getMessageWrapper(moshi, jsonMessage, messageType);
            sendData(webSocket, wrapperJsonString);
        }
    }

    /**
     * First we checking the state of the socket then we send the message
     */
    public void sendMessage(String textContent, int messageType) {
        if (getState() != null) {
            if (getState().equals("OPEN")) {
                long ttl = new Date().getTime();
                Message message = new Message();
                message.setContent(textContent);
                message.setPriority(1);
                message.setPeerName(getServerName());
                message.setTtl(ttl);

                String json = JsonUtil.getJson(message);

                messageWrapperVo = new MessageWrapperVo();
                messageWrapperVo.setContent(json);
                messageWrapperVo.setType(messageType);

                String json1 = JsonUtil.getJson(messageWrapperVo);
                sendData(webSocket, json1);
            } else {
                try {
                    asyncListenerManager.callOnError("Socket is close");
                } catch (IOException e) {
                    if (BuildConfig.DEBUG) Logger.e("Socket Is", "Closed");
                }
            }
        } else {
            if (BuildConfig.DEBUG) Logger.e("Socket Is Not Connected");
        }
    }

    public void closeSocket() {
        webSocket.sendClose();
    }

    public LiveData<String> getStateLiveData() {
        return stateLiveData;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public void logOutSocket() {
        removePeerId(AsyncConstant.Constants.PEER_ID, null);
        isServerRegister = false;
        isDeviceRegister = false;
        webSocket.sendClose();
    }

    public void setReconnectOnClose(boolean reconnectOnClosed) {
        reconnectOnClose = reconnectOnClosed;
    }

    /**
     * Add a listener to receive events on this Async.
     *
     * @param listener A listener to add.
     * @return {@code this} object.
     */
    public Async addListener(AsyncListener listener) {
        asyncListenerManager.addListener(listener);
        return this;
    }

    public Async addListeners(List<AsyncListener> listeners) {
        asyncListenerManager.addListeners(listeners);
        return this;
    }

    public Async removeListener(AsyncListener listener) {
        asyncListenerManager.removeListener(listener);
        return this;
    }

    /**
     * Connect webSocket to the Async
     *
     * @Param socketServerAddress
     * @Param appId
     */
    private void handleOnAck(ClientMessage clientMessage) throws IOException {
        setMessage(clientMessage.getContent());
        asyncListenerManager.callOnTextMessage(clientMessage.getContent());
    }

    /**
     * When socket closes by any reason
     * , server is still registered and we sent a lot of message but
     * they are still in the queue
     */
    private void handleOnDeviceRegister(WebSocket websocket, ClientMessage clientMessage) {
        isDeviceRegister = true;
        if (!peerIdExistence()) {
            String peerId = clientMessage.getContent();
            savePeerId(peerId);
        }

        //TODO handle queue message
        if (isServerRegister && peerId.equals(getPeerId())) {
            if (websocket.getState() == OPEN) {
                if (websocket.getFrameQueueSize() > 0) {

                }
            }

        } else {
            serverRegister(websocket);
        }
    }

    private void serverRegister(WebSocket websocket) {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setName(getServerName());
        JsonAdapter<RegistrationRequest> jsonRegistrationRequestVoAdapter = moshi.adapter(RegistrationRequest.class);
        String jsonRegistrationRequestVo = jsonRegistrationRequestVoAdapter.toJson(registrationRequest);
        String jsonMessageWrapperVo = getMessageWrapper(moshi, jsonRegistrationRequestVo, AsyncMessageType.MessageType.SERVER_REGISTER);
        sendData(websocket, jsonMessageWrapperVo);
    }

    private void sendData(WebSocket websocket, String jsonMessageWrapperVo) {
        lastSendMessageTime = new Date().getTime();
        websocket.sendText(jsonMessageWrapperVo);
    }

    private void handleOnErrorMessage(ClientMessage clientMessage) {
        if (BuildConfig.DEBUG) Logger.e(TAG + "OnErrorMessage", clientMessage.getContent());
        setErrorMessage(clientMessage.getContent());
    }

    private void handleOnMessage(ClientMessage clientMessage) throws IOException {
        setMessage(clientMessage.getContent());
        messageLiveData.postValue(clientMessage.getContent());
        asyncListenerManager.callOnTextMessage(clientMessage.getContent());
    }

    private void handleOnPing(WebSocket webSocket, ClientMessage clientMessage) {
        if (clientMessage.getContent() != null || !clientMessage.getContent().equals("")) {
            deviceRegister(webSocket);
        } else {
            if (BuildConfig.DEBUG) Logger.i("ASYNC_PING", String.valueOf(new Date().getTime()));
        }
    }

    private void handleOnServerRegister(String textMessage) {
        if (BuildConfig.DEBUG) Logger.i("SERVER_REGISTERED");
        if (BuildConfig.DEBUG) Logger.i("READY FOR CHAT", textMessage);
        try {
            asyncListenerManager.callOnStateChanged("CHAT_READY");
        } catch (IOException e) {
            e.printStackTrace();
        }
        isServerRegister = true;
    }

    private void handleOnMessageAckNeeded(WebSocket websocket, ClientMessage clientMessage) throws IOException {
        handleOnMessage(clientMessage);

        Message messageSenderAckNeeded = new Message();
        messageSenderAckNeeded.setMessageId(clientMessage.getSenderMessageId());

        JsonAdapter<Message> jsonSenderAckNeededAdapter = moshi.adapter(Message.class);
        String jsonSenderAckNeeded = jsonSenderAckNeededAdapter.toJson(messageSenderAckNeeded);
        String jsonSenderAckNeededWrapper = getMessageWrapper(moshi, jsonSenderAckNeeded, AsyncMessageType.MessageType.ACK);
        sendData(websocket, jsonSenderAckNeededWrapper);
    }

    private void deviceRegister(WebSocket websocket) {
        PeerInfo peerInfo = new PeerInfo();
        if (getPeerId() != null) {
            peerInfo.setRefresh(true);
        } else {
            peerInfo.setRenew(true);
        }
        peerInfo.setAppId(getAppId());
        peerInfo.setDeviceId(getDeviceId());

        JsonAdapter<PeerInfo> jsonPeerMessageAdapter = moshi.adapter(PeerInfo.class);
        String peerMessageJson = jsonPeerMessageAdapter.toJson(peerInfo);
        String jsonPeerInfoWrapper = getMessageWrapper(moshi, peerMessageJson, AsyncMessageType.MessageType.DEVICE_REGISTER);
        sendData(websocket, jsonPeerInfoWrapper);
    }

    @NonNull
    private String getMessageWrapper(Moshi moshi, String json, int messageType) {
        messageWrapperVo = new MessageWrapperVo();
        messageWrapperVo.setContent(json);
        messageWrapperVo.setType(messageType);
        JsonAdapter<MessageWrapperVo> jsonMessageWrapperVoAdapter = moshi.adapter(MessageWrapperVo.class);
        return jsonMessageWrapperVoAdapter.toJson(messageWrapperVo);
    }

    /**
     * If peerIdExistence we set {@param refresh = true} to the
     * Async else we set {@param renew = true}  to the Async to
     * get the new PeerId
     */
    private void reConnect() throws IOException, WebSocketException {
        WebSocketFactory webSocketFactory = new WebSocketFactory();
        webSocketFactory.setVerifyHostname(false);
        try {
            webSocketReconnect = webSocketFactory
                    .createSocket(getServerAddress())
                    .addListener(this);
            webSocketReconnect.connect();
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Logger.e("Async: reConnect", e.toString());
        }
    }

    /**
     * Remove the peerId and send ping again but this time
     * peerId that was set in the server was removed
     */

    private void removePeerId(String peerId, String nul) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(peerId, nul);
        editor.apply();
    }

    private void ping() {
        message = getMessageWrapper(moshi, "", AsyncMessageType.MessageType.PING);
        sendData(webSocket, message);
        if (BuildConfig.DEBUG) Logger.i("SEND_ASYNC_PING");
        ScheduleCloseSocket();
    }

    private void ScheduleCloseSocket() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (lastSendMessageTime - lastReceiveMessageTime >= SOCKET_CLOSE_TIMEOUT) {
                closeSocket();
            }
        }, SOCKET_CLOSE_TIMEOUT);
    }

    /**
     * After a delay Time it calls the method in the Run
     */
    private void scheduleCloseSocket() {
        long currentTime = new Date().getTime();
        Handler socketCloseHandler = new Handler();
        socketCloseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentTime - lastReceiveMessageTime > 10000) {
                    closeSocket();
                }
            }
        }, 10000);
    }

    /**
     * When its send message the lastSendMessageTime gets updated.
     * if the {@param currentTime} - {@param lastSendMessageTime} was bigger than 10 second
     * it means we need to send ping to keep socket alive.
     * we don't need to set ping interval because its send ping automatically by itself
     * with the {@param type}type that not 0.
     * We set {@param type = 0} with empty content.
     */
    private void sendPing() {
        lastSendMessageTime = new Date().getTime();
        long currentTime = new Date().getTime();
        if (currentTime - lastReceiveMessageTime >= 70000) {
            ping();
        }
    }

    private void stopSocket() {
        if (webSocket != null) {
            webSocket.disconnect();
            webSocket = null;
            pingHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * Checking if the peerId exist or not. if user logout Peer id is set to null
     */
    private boolean peerIdExistence() {
        boolean isPeerIdExistence;
        String peerId = sharedPrefs.getString(AsyncConstant.Constants.PEER_ID, null);
        setPeerId(peerId);
        isPeerIdExistence = peerId != null;
        return isPeerIdExistence;
    }

    /**
     * Save peerId in the SharedPreferences
     */
    private void savePeerId(String peerId) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(AsyncConstant.Constants.PEER_ID, peerId);
        editor.apply();
    }

    //Save deviceId in the SharedPreferences
    private static void saveDeviceId(String deviceId) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(AsyncConstant.Constants.DEVICE_ID, deviceId);
        editor.apply();
    }

    private void setServerName(String serverName) {
        this.serverName = serverName;
    }

    private String getServerName() {
        return serverName;
    }

    private String getDeviceId() {
        return sharedPrefs.getString(AsyncConstant.Constants.DEVICE_ID, null);
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getPeerId() {
        return sharedPrefs.getString(AsyncConstant.Constants.PEER_ID, null);
    }

    private void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    private String getAppId() {
        return appId;
    }

    private void setAppId(String appId) {
        this.appId = appId;
    }

    public String getState() {
        return state;
    }

    private void setState(String state) {
        this.state = state;
    }

    private void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private String getServerAddress() {
        return serverAddress;
    }

    private void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    private void setToken(String token) {
        this.token = token;
    }

    private String getToken() {
        return token;
    }

    /**
     * Get the manager that manages registered listeners.
     */
    AsyncListenerManager getListenerManager() {
        return asyncListenerManager;
    }

    private void setSsoHost(String ssoHost) {
        this.ssoHost = ssoHost;
    }

    private String getSsoHost() {
        return ssoHost;
    }
}

