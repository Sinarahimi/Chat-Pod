package com.fanap.podasync;

import java.io.IOException;

public interface AsyncListener {

    void OnReceivedMessage(String textMessage) throws IOException;
}
