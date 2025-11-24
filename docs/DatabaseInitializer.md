# Database Initialization Flow

This document describes the modernized database initialization used in the app.

## Overview

Initialization is now non-blocking and event-driven.

- Start via `DatabaseInitializer.execute()`
- Reports progress, completion, and failure through `TaskListener`
- Tracks status via `InitializationHealth`
- Configurable through `DatabaseInitConfig`

## Usage

```java
Context ctx = ...;
TaskListener listener = new TaskListener() {
    @Override public void onComplete() { /* route to next screen */ }
    @Override public void onProgress() { /* update loading UI */ }
    @Override public void onFailed() { /* show error */ }
};

DatabaseInitConfig config = DatabaseInitConfig.defaults();
DatabaseInitializer initializer = new DatabaseInitializer(ctx, listener, config);
initializer.execute();
```

## Configuration

- `maxRetries`: retry attempts per phase
- `timeoutMs`: overall timeout for initialization
- `parallelism`: worker threads for dataset initialization

Use `DatabaseInitConfig.defaults()` or construct a custom config.

## Health Checks

Use `initializer.getHealth()` to read status and message.

## Logging

Initialization logs under the tag `DB_INIT`.

## Backward Compatibility

Schemas remain unchanged. Inserts now use transactions and write-ahead logging for performance.