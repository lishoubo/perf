package com.personal.li.perf.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lishoubo on 16/4/16.
 */
public class Monitor extends OneSecondTask {
    private static Logger logger = LoggerFactory.getLogger(Monitor.class);
    private static Logger access = LoggerFactory.getLogger("access");

    private AtomicInteger qps;
    private AtomicInteger tps;
    private AtomicLong time;
    private AtomicInteger total;
    private AtomicInteger error;

    public Monitor() {
        qps = new AtomicInteger(0);
        tps = new AtomicInteger(0);
        total = new AtomicInteger(0);
        error = new AtomicInteger(0);
        time = new AtomicLong();
    }

    public void recordQps() {
        qps.incrementAndGet();
    }

    public void recordTps(long duration) {
        tps.incrementAndGet();
        total.incrementAndGet();
        time.addAndGet(duration);
    }

    public void recordError() {
        error.incrementAndGet();
        total.incrementAndGet();
    }

    public void send(String mid) {

    }

    @Override
    protected void doJob() {
        printDetail();
    }

    private void printDetail() {
        int t = this.tps.get();
        access.info("[monitor] total:{}, error:{}, qps:{}, tps:{}, rt:{}",
                total.get(), error.get(), qps.get(), t, time.get() / (t == 0 ? 1 : t));
        qps.set(0);
        tps.set(0);
        time.set(0);
    }
}
