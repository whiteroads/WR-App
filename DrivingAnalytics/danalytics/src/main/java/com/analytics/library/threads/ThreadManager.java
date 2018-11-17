package com.analytics.library.threads;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager{
    private static ThreadManager threadManager;
    private int NUMBER_OF_CORES = 5;
    private BlockingQueue<Runnable> mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();
    private ExecutorService mDecodeThreadPool;
    private ThreadManager(){
        if(mDecodeThreadPool ==null){
            mDecodeThreadPool = new ThreadPoolExecutor(
                    NUMBER_OF_CORES,       // Initial pool size
                    NUMBER_OF_CORES,       // Max pool size
                    0,
                    TimeUnit.SECONDS,
                    mDecodeWorkQueue);
        }
    }
    public static ThreadManager getInstance(){
        if(threadManager == null){
            threadManager =  new ThreadManager();
        }
        return threadManager;
    }
    public void addToQue(Runnable runnable){
        mDecodeThreadPool.execute(runnable);
    }
}
