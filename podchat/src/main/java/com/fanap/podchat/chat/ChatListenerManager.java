package com.fanap.podchat.chat;

import java.util.ArrayList;
import java.util.List;

public class ChatListenerManager {
    private final List<ChatListener> mListeners = new ArrayList<>();
    private boolean mSyncNeeded = true;
    private List<ChatListener> mCopiedListeners;

    public ChatListenerManager() {
    }

    public void addListener(ChatListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (mListeners) {
            mListeners.add(listener);
            mSyncNeeded = true;
        }
    }

    public void addListeners(List<ChatListener> listeners) {
        if (listeners == null) {
            return;
        }

        synchronized (mListeners) {
            for (ChatListener listener : listeners) {
                if (listener == null) {
                    continue;
                }

                mListeners.add(listener);
                mSyncNeeded = true;
            }
        }
    }

    public void removeListener(ChatListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (mListeners) {
            if (mListeners.remove(listener)) {
                mSyncNeeded = true;
            }
        }
    }


    public void removeListeners(List<ChatListener> listeners) {
        if (listeners == null) {
            return;
        }

        synchronized (mListeners) {
            for (ChatListener listener : listeners) {
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

    private List<ChatListener> getSynchronizedListeners() {
        synchronized (mListeners) {
            if (!mSyncNeeded) {
                return mCopiedListeners;
            }

            // Copy mListeners to copiedListeners.
            List<ChatListener> copiedListeners = new ArrayList<>(mListeners.size());

            for (ChatListener listener : mListeners) {
                copiedListeners.add(listener);
            }

            // Synchronize.
            mCopiedListeners = copiedListeners;
            mSyncNeeded = false;

            return copiedListeners;
        }
    }

    public void callOnGetThread(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            listener.onGetThread(content);
        }
    }

    public void callOnGetThreadHistory(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            listener.onGetHistory(content);
        }
    }

    public void callOnGetContacts(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            listener.onGetContacts(content);
        }
    }

    public void callOnSentMessage(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSent(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSeenMessage(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSeen(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnDeliveryMessage(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onDeliver(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnError(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            listener.onError(content);
        }
    }

    public void callOnGetThreadParticipant(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetThreadParticipant(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    private void callHandleCallbackError(ChatListener listener, Throwable cause) {
        try {
            listener.handleCallbackError(cause);
        } catch (Throwable t) {
        }
    }

    public void callOnEditedMessage(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onEditedMessage(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnAddContact(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onContactAdded(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnRemoveContact(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onRemoveContact(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnMuteThread(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onMuteThread(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUnmuteThread(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUnmuteThread(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUserInfo(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUserInfo(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnCreateThread(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCreateThread(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUpdateContact(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUpdateContact(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnRenameThread(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onRenameThread(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnGetfile(String url) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetFile(url);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnNewMessage(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onNewMessage(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }
    public void callOnGetImageFile(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetImageFile(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUploadImageFile(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUploadImageFile(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }
    public void callOnUploadFile(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUploadFile(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSyncContact(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSyncContact(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }
}
