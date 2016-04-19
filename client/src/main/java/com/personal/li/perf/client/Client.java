package com.personal.li.perf.client;

import com.personal.li.perf.client.http.HttpSyncClientSender;
import com.personal.li.perf.client.http.HttpSyncSender;
import com.personal.li.perf.client.model.Request;

/**
 * Hello world!
 */
public class Client {

    public static void main(String[] args) {
        String url = args[0], threads = args[1], qps = args[2];

        Monitor monitor = new Monitor();
        monitor.start(1);

        Request request = new Request();
        request.setUrl(url);
        request.setMethod("POST");
        request.setBody("lishoubo".getBytes());

        for (int i = 0; i < Integer.parseInt(threads); i++) {
            new QPSTask(new HttpSyncClientSender(monitor, request)).start(
                    Integer.parseInt(qps)
            );
        }
    }
}
