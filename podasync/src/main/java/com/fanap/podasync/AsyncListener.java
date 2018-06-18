package com.fanap.podasync;

import java.io.IOException;

public interface AsyncListener {

    void onReceivedMessage(String textMessage) throws IOException;

    void onStateChanged(String textMessage) throws IOException;

    void onConnected(String textMessage) throws IOException;

    void onDisconnected(String textMessage) throws IOException;

    void onError(String textMessage) throws IOException;
}
