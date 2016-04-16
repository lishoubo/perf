package com.personal.li.perf.client;

import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lishoubo on 16/4/16.
 */
public class MidGenerator {
    private final static byte[] IP_BYTES = Utils.ipToBytes(Utils.getLocalIp());//4 bytes
    private final static byte[] PID = Utils.shortToBytes(getPid());//2 bytes
    private volatile static AtomicInteger SEQ = new AtomicInteger(1);
    private static ThreadLocal<ByteBuffer> BUFFER = new ThreadLocal<ByteBuffer>();

    public static String nextMid() {
        ByteBuffer buffer = BUFFER.get();
        if (buffer == null) {
            buffer = ByteBuffer.allocate(16);
            BUFFER.set(buffer);
        } else {
            buffer.flip();
        }

        for (; ; ) {
            int current = SEQ.get();
            int next = (current > Short.MAX_VALUE) ? 1 : current + 1;
            if (SEQ.compareAndSet(current, next)) {
                buffer.putShort((short) current);
                break;
            }
        }
        buffer.putLong(System.currentTimeMillis());//8
        buffer.put(IP_BYTES);//4
        buffer.put(PID);//2
        return Utils.toHexString(buffer.array());
    }

    private static short getPid() {
        // unsign short 0 to 65535
        int pid = Integer.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        if (pid < 0) {
            pid = 0;
        }
        if (pid > 65535) {
            String strPid = Integer.toString(pid);
            strPid = strPid.substring(strPid.length() - 4, strPid.length());
            pid = Integer.parseInt(strPid);
        }
        return (short) pid;
    }
}
