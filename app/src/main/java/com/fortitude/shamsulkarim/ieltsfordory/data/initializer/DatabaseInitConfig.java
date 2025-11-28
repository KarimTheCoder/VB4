package com.fortitude.shamsulkarim.ieltsfordory.data.initializer;

public class DatabaseInitConfig {

    private final int maxRetries;
    private final long timeoutMs;
    private final int parallelism;

    public DatabaseInitConfig(int maxRetries, long timeoutMs, int parallelism) {
        this.maxRetries = maxRetries;
        this.timeoutMs = timeoutMs;
        this.parallelism = parallelism;
    }

    public static DatabaseInitConfig defaults() {
        return new DatabaseInitConfig(2, 60_000L, Runtime.getRuntime().availableProcessors());
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public int getParallelism() {
        return parallelism;
    }
}