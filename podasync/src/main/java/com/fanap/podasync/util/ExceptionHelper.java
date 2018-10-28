package com.fanap.podasync.util;

import android.util.Log;

import com.orhanobut.logger.Logger;

public class ExceptionHelper extends Exception {

    private final boolean log;

    public ExceptionHelper(boolean log) {
        this.log = log;
    }

    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }

    @Override
    public String getMessage() {
        return super.getMessage();

    }


}
