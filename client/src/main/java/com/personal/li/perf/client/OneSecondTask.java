package com.personal.li.perf.client;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by lishoubo on 16/4/16.
 */
public abstract class OneSecondTask {
    private Executor executor;
    private int qps;

    public void start(int qps) {
        this.qps = qps;
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

                if (count >= qps) {
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
            executor.execute(this);
        }
    }
}
