package com.magi.chlendar.utils;

import android.os.Handler;
import android.os.Message;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author zhangzhaowen @ Zhihu Inc.
 * @since 01-04-2017
 */

public class EnhancedHandler extends Handler {
    private final Thread _thread;

    public EnhancedHandler() {
        _thread = Thread.currentThread();
    }

    public void runInMainThread(Runnable runnable) {
        if (Thread.currentThread() != _thread)
            post(runnable);
        else
            try {
                runnable.run();
            } catch (Throwable e) {
                LogUtils.e(e, "Error occurred in handler run thread");
            }

    }

    public void runInHandlerThreadDelay(Runnable runnable) {
        post(runnable);
    }

    public <T> T callInHandlerThread(Callable<T> callable, T defaultValue) {
        T result = null;
        try {
            if (Thread.currentThread() != _thread)
                result = postCallable(callable).get();
            else
                result = callable.call();
        } catch (Throwable e) {
            LogUtils.e(e, "Error occurred in handler call thread");
        }

        return result == null ? defaultValue : result;
    }

    public <T> Future<T> postCallable(Callable<T> callable) {
        FutureTask<T> task = new FutureTask<T>(callable);
        post(task);
        return task;
    }

    @Override
    public void dispatchMessage(Message msg) {
        Runnable callback = msg.getCallback();
        if (callback != null) {
            try {
                callback.run();
            } catch (Throwable e) {
                LogUtils.e(e, "Error occurred in handler thread, dispatchMessage");
            }
        } else {
            handleMessage(msg);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
    }
}
