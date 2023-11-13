package com.fortitude.shamsulkarim.ieltsfordory.data.initializer;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskExecutor {

    private final ExecutorService executor;
    private Future<String> taskFuture;
    private final Task task;
    public TaskExecutor(Task task) {
        this.task = task;
        executor = Executors.newSingleThreadExecutor();

    }

    public void executeTask() {
        // Submit a task and get a Future object
        taskFuture = executor.submit(() -> {
            // Simulate a time-consuming task

            //Todo: initialize databases here
            task.execute();


            return "Task completed!";
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




}
