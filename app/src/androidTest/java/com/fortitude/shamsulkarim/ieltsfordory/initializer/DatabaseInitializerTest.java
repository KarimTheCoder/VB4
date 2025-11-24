package com.fortitude.shamsulkarim.ieltsfordory.initializer;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.fortitude.shamsulkarim.ieltsfordory.data.initializer.DatabaseInitConfig;
import com.fortitude.shamsulkarim.ieltsfordory.data.initializer.DatabaseInitializer;
import com.fortitude.shamsulkarim.ieltsfordory.data.initializer.InitializationHealth;
import com.fortitude.shamsulkarim.ieltsfordory.data.initializer.TaskListener;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class DatabaseInitializerTest {

    @Test
    public void initializationCompletes() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();

        CountDownLatch latch = new CountDownLatch(1);
        DatabaseInitializer initializer = new DatabaseInitializer(ctx, new TaskListener() {
            @Override public void onComplete() { latch.countDown(); }
            @Override public void onProgress() { }
            @Override public void onFailed() { Assert.fail("Initialization failed"); }
        }, DatabaseInitConfig.defaults());

        initializer.execute();
        boolean finished = latch.await(120, TimeUnit.SECONDS);
        Assert.assertTrue("Initialization did not complete", finished);
        Assert.assertEquals(InitializationHealth.Status.COMPLETED, initializer.getHealth().getStatus());
    }

    @Test
    public void healthReportsRunningThenComplete() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        CountDownLatch latch = new CountDownLatch(1);
        DatabaseInitializer initializer = new DatabaseInitializer(ctx, new TaskListener() {
            @Override public void onComplete() { latch.countDown(); }
            @Override public void onProgress() { }
            @Override public void onFailed() { Assert.fail("Initialization failed"); }
        }, DatabaseInitConfig.defaults());

        initializer.execute();
        InitializationHealth.Status status = initializer.getHealth().getStatus();
        Assert.assertTrue(status == InitializationHealth.Status.RUNNING || status == InitializationHealth.Status.COMPLETED);
        latch.await(120, TimeUnit.SECONDS);
        Assert.assertEquals(InitializationHealth.Status.COMPLETED, initializer.getHealth().getStatus());
    }

    @Test
    public void performanceUnderThreshold() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        long start = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(1);

        DatabaseInitializer initializer = new DatabaseInitializer(ctx, new TaskListener() {
            @Override public void onComplete() { latch.countDown(); }
            @Override public void onProgress() { }
            @Override public void onFailed() { Assert.fail("Initialization failed"); }
        }, DatabaseInitConfig.defaults());

        initializer.execute();
        latch.await(120, TimeUnit.SECONDS);
        long duration = System.currentTimeMillis() - start;
        Assert.assertTrue("Initialization too slow", duration < 120_000L);
    }

    @Test
    public void threadSafetyVerification() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        CountDownLatch latch = new CountDownLatch(1);
        DatabaseInitConfig config = new DatabaseInitConfig(1, 120_000L, Math.max(2, Runtime.getRuntime().availableProcessors()));
        DatabaseInitializer initializer = new DatabaseInitializer(ctx, new TaskListener() {
            @Override public void onComplete() { latch.countDown(); }
            @Override public void onProgress() { }
            @Override public void onFailed() { Assert.fail("Initialization failed"); }
        }, config);

        initializer.execute();
        boolean finished = latch.await(120, TimeUnit.SECONDS);
        Assert.assertTrue(finished);
    }
}