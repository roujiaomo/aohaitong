package com.aohaitong.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConnectThreadPoolManager {

    private static ConnectThreadPoolManager mInstance;

    public static ConnectThreadPoolManager getInstance() {
        if (mInstance == null) {
            synchronized (ConnectThreadPoolManager.class) {
                if (mInstance == null) {
                    mInstance = new ConnectThreadPoolManager();
                }
            }
        }
        return mInstance;
    }

    private final ThreadPoolExecutor executor;

    private ConnectThreadPoolManager() {
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        long keepAliveTime = 5;
        TimeUnit unit = TimeUnit.HOURS;
        executor = new ThreadPoolExecutor(
                corePoolSize, //当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
                corePoolSize, //5,先corePoolSize,然后new LinkedBlockingQueue<Runnable>(),然后maximumPoolSize,但是它的数量是包含了corePoolSize的
                keepAliveTime, //表示的是maximumPoolSize当中等待任务的存活时间
                unit,
                new LinkedBlockingQueue<>(100), //缓冲队列，用于存放等待任务，Linked的先进先出
                Executors.defaultThreadFactory(), //创建线程的工厂
                new ThreadPoolExecutor.AbortPolicy() //用来对超出maximumPoolSize的任务的处理策略
        );
    }

    public void execute(Runnable runnable) {
        if (runnable == null)
            return;
        executor.execute(runnable);
    }

    public void remove(Runnable runnable) {
        if (runnable == null)
            return;
        executor.remove(runnable);
    }

    public void cancelAllThread() {
        mInstance = null;
        executor.shutdown();
    }
}
