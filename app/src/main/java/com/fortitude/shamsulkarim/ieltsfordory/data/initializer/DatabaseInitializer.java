package com.fortitude.shamsulkarim.ieltsfordory.data.initializer;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class DatabaseInitializer {

    private final TaskListener taskListener;
    private final TaskExecutor taskExecutor;
    private final DatabaseInitConfig config;
    private final InitializationHealth health = new InitializationHealth();


    public DatabaseInitializer(Context context, TaskListener taskListener) {
        this(context, taskListener, DatabaseInitConfig.defaults());
    }

    public DatabaseInitializer(Context context, TaskListener taskListener, DatabaseInitConfig config) {
        this.taskListener = taskListener;
        this.config = config;
        DatabaseTask dbTask = new DatabaseTask(context, config);
        taskExecutor = new TaskExecutor(dbTask);
    }

    public void execute(){
        health.setRunning("Initialization started");
        taskExecutor.executeTask();

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable poll = new Runnable() {
            @Override
            public void run() {
                if (taskExecutor.isTaskDone()) {
                    String result = taskExecutor.getTaskResult();
                    if (taskExecutor.hasFailed()) {
                        health.setFailed(result);
                        taskListener.onFailed();
                        Log.e("DB_INIT", "Initialization failed: " + result);
                    } else {
                        health.setCompleted(result);
                        taskListener.onComplete();
                        Log.d("DB_INIT", "Initialization completed: " + result);
                    }
                    taskExecutor.shutdownExecutor();
                } else {
                    taskListener.onProgress();
                    handler.postDelayed(this, 250);
                }
            }
        };
        handler.post(poll);
    }

    public InitializationHealth getHealth() {
        return health;
    }


}
