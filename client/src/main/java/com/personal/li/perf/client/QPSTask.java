package com.personal.li.perf.client;

/**
 * Created by lishoubo on 16/4/16.
 */
public class QPSTask extends OneSecondTask {
    private Sender sender;

    public QPSTask(Sender sender) {
        this.sender = sender;
    }

    @Override
    protected void doJob() {
        sender.send();
    }
}
