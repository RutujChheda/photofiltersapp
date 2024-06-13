package com.rutuj.photofiltersapp.util;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceManager {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private ExecutorServiceManager() {
        // Private constructor to prevent instantiation
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static void shutdown() {
        executorService.shutdown();
    }
}
