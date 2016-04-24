package com.personal.li.perf.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by lishoubo on 16/4/16.
 */
public abstract class OneSecondTask {
    private Executor executor;
    private int limit;
    private static Logger task = LoggerFactory.getLogger("task");

    public void start(int limit) {
        this.limit = limit;
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new MyRunnable());
    }

    protected abstract void doJob();


    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            long begin = System.currentTimeMillis(), wait;
            int count = 0;


            while (true) {
                doJob();

                long now = System.currentTimeMillis();
                wait = 1000 - (now - begin);
                count++;

                if (count >= limit) {
                    break;
                }
                if (wait <= 0) {
                    break;
                }
            }
            if (wait > 0) {
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            task.info("[task] limit:{}, execute:{}, wait:{}", limit, count, wait);
            executor.execute(this);
        }
    }
}
