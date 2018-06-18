package com.fanap.podasync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class AsyncListenerManager {
    private final List<AsyncListener> mListeners = new ArrayList<>();
    private boolean mSyncNeeded = true;
    private List<AsyncListener> mCopiedListeners;


    public AsyncListenerManager() {
    }

    public void addListener(AsyncListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (mListeners) {
            mListeners.add(listener);
            mSyncNeeded = true;
        }
    }

    public void addListeners(List<AsyncListener> listeners) {
        if (listeners == null) {
            return;
        }

        synchronized (mListeners) {
            for (AsyncListener listener : listeners) {
                if (listener == null) {
                    continue;
                }

                mListeners.add(listener);
                mSyncNeeded = true;
            }
        }
    }

    public void removeListener(AsyncListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (mListeners) {
            if (mListeners.remove(listener)) {
                mSyncNeeded = true;
            }
        }
    }


    public void removeListeners(List<AsyncListener> listeners) {
        if (listeners == null) {
            return;
        }

        synchronized (mListeners) {
            for (AsyncListener listener : listeners) {
                if (listener == null) {
                    continue;
                }

                if (mListeners.remove(listener)) {
                    mSyncNeeded = true;
                }
            }
        }
    }


    public void clearListeners() {
        synchronized (mListeners) {
            if (mListeners.size() == 0) {
                return;
            }

            mListeners.clear();
            mSyncNeeded = true;
        }
    }

    private List<AsyncListener> getSynchronizedListeners() {
        synchronized (mListeners) {
            if (!mSyncNeeded) {
                return mCopiedListeners;
            }

            // Copy mListeners to copiedListeners.
            List<AsyncListener> copiedListeners = new ArrayList<>(mListeners.size());

            for (AsyncListener listener : mListeners) {
                copiedListeners.add(listener);
            }

            // Synchronize.
            mCopiedListeners = copiedListeners;
            mSyncNeeded = false;

            return copiedListeners;
        }
    }


    public void callOnTextMessage(String message) throws IOException {
        for (AsyncListener listener : getSynchronizedListeners()) {
            listener.onReceivedMessage(message);
        }
    }

    public void callOnStateChanged(String message) throws IOException {
        for (AsyncListener listener : getSynchronizedListeners()) {
            listener.onStateChanged(message);
        }
    }

    public void callOnConnected(String message) throws IOException {
        for (AsyncListener listener : getSynchronizedListeners()) {
            listener.onConnected(message);
        }
    }

    public void callOnDisconnected(String message) throws IOException {
        for (AsyncListener listener : getSynchronizedListeners()) {
            listener.onDisconnected(message);
        }
    }

    public void callOnError(String message) throws IOException {
        for (AsyncListener listener : getSynchronizedListeners()) {
            listener.onError(message);
        }
    }
}
