package com.fanap.podchat.util;

import java.util.ArrayList;

public class ThreadCallbacks {
    private int threadIdl;
    private ArrayList<Callback> callbacks;

    public int getThreadIdl() {
        return threadIdl;
    }

    public void setThreadIdl(int threadIdl) {
        this.threadIdl = threadIdl;
    }

    public ArrayList<Callback> getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(ArrayList<Callback> callbacks) {
        this.callbacks = callbacks;
    }
}
