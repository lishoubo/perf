package com.personal.li.server;

/**
 * Hello world!
 */
public class Server {
    public static void main(String[] args) throws InterruptedException {
        int port = args == null || args.length == 0 ? 8080 : Integer.parseInt(args[0]);
        new HttpServer().start(port, 100);
    }
}
