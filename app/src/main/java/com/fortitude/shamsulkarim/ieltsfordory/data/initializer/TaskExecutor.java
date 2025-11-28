package com.fortitude.shamsulkarim.ieltsfordory.data.initializer;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskExecutor {

    private final ExecutorService executor;
    private Future<String> taskFuture;
    private volatile boolean failed = false;
    private final Task task;
    public TaskExecutor(Task task) {
        this.task = task;
        executor = Executors.newSingleThreadExecutor();

    }

    public void executeTask() {
        taskFuture = executor.submit(() -> {
            try {
                task.execute();
                return "Task completed!";
            } catch (Exception e) {
                failed = true;
                e.printStackTrace();
                return "Task failed!";
            }
        });
    }

    public boolean isTaskDone() {
        return taskFuture != null && taskFuture.isDone();
    }

    public String getTaskResult() {
        if (isTaskDone()) {
            try {
                return taskFuture.get(); // Get the result of the completed task
            } catch (Exception e) {
                e.printStackTrace();
                return "Task failed!";
            }
        } else {
            return "Task is not yet completed.";
        }
    }

    public void shutdownExecutor() {
        executor.shutdown();
    }

    public boolean hasFailed() {
        return failed;
    }




}
