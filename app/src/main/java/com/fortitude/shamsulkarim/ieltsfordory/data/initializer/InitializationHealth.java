package com.fortitude.shamsulkarim.ieltsfordory.data.initializer;

import java.util.concurrent.atomic.AtomicReference;

public class InitializationHealth {

    public enum Status { NOT_STARTED, RUNNING, COMPLETED, FAILED }

    private final AtomicReference<Status> status = new AtomicReference<>(Status.NOT_STARTED);
    private volatile String message = "";

    public Status getStatus() {
        return status.get();
    }

    public void setRunning(String msg) {
        status.set(Status.RUNNING);
        message = msg;
    }

    public void setCompleted(String msg) {
        status.set(Status.COMPLETED);
        message = msg;
    }

    public void setFailed(String msg) {
        status.set(Status.FAILED);
        message = msg;
    }

    public String getMessage() {
        return message;
    }
}