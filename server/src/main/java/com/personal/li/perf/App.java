package com.personal.li.perf;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws InterruptedException {
        int port = args == null || args.length == 0 ? 8080 : Integer.parseInt(args[0]);
        new HttpServer().start(port, 100);
    }
}
