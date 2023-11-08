package com.fortitude.shamsulkarim.ieltsfordory.data.initializer;

import android.content.Context;

public class DatabaseInitializer {

    private final TaskListener taskListener;
    private final TaskExecutor taskExecutor;


    public DatabaseInitializer(Context context, TaskListener taskListener) {
        this.taskListener = taskListener;
        DatabaseTask dbTask = new DatabaseTask(context);
        taskExecutor = new TaskExecutor(dbTask);



    }

    public void execute(){

        taskExecutor.executeTask();

        // Check if the task is done
        while (!taskExecutor.isTaskDone()) {
            System.out.println("Task is still in progress...");
            taskListener.onProgress();
            try {
                Thread.sleep(1000); // Check every second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        taskListener.onComplete();

        // Get the task result
        String result = taskExecutor.getTaskResult();
        System.out.println("Task result: " + result);

        // Shutdown the executor when done
        taskExecutor.shutdownExecutor();



    }


}
