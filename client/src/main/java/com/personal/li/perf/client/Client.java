package com.personal.li.perf.client;

import com.personal.li.perf.client.http.HttpSyncSender;
import com.personal.li.perf.client.model.Request;

/**
 * Hello world!
 */
public class Client {
    public static void main(String[] args) {
        String url = args[0], port = args[1], threads = args[2];

        Monitor monitor = new Monitor();
        monitor.start(1);

        Request request = new Request();
        request.setUrl("http://" + url + ":" + port);
        request.setMethod("POST");
        request.setBody("lishoubo".getBytes());

        for (int i = 0; i < Integer.parseInt(threads); i++) {
            new QPSTask(new HttpSyncSender(monitor, request)).start(1000);
        }
    }
}
