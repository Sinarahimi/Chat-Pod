package com.fanap.podchat.util;

import java.util.ArrayList;

public class ThreadCallbacks {
    private int threadIdl;
    private ArrayList<Callbacks> callbacks;

    public int getThreadIdl() {
        return threadIdl;
    }

    public void setThreadIdl(int threadIdl) {
        this.threadIdl = threadIdl;
    }

    public ArrayList<Callbacks> getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(ArrayList<Callbacks> callbacks) {
        this.callbacks = callbacks;
    }
}
