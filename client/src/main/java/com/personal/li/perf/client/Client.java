package com.personal.li.perf.client;

import com.personal.li.perf.client.http.HttpSyncSender;
import com.personal.li.perf.client.model.Request;

/**
 * Hello world!
 */
public class Client {
    public static void main(String[] args) {
        Monitor monitor = new Monitor();
        monitor.start(1);

        Request request = new Request();
        request.setUrl("http://" + args[0] + ":" + args[1]);
        request.setMethod("POST");
        request.setBody("lishoubo".getBytes());

        new QPSTask(new HttpSyncSender(monitor, request)).start(1000);
    }
}
