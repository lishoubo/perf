package com.personal.li.perf.client;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by lishoubo on 16/4/16.
 */
public class Utils {

    private static String LOCAL_IP = null;

    public static String getLocalIp() {
        if (LOCAL_IP != null) {
            return LOCAL_IP;
        }
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ipAddresses = ni.getInetAddresses();
                while (ipAddresses.hasMoreElements()) {
                    InetAddress address = ipAddresses.nextElement();

                    if (address.isSiteLocalAddress()
                            && !address.isLoopbackAddress() // 127.开头的都是loopback地址
                            && !address.getHostAddress().contains(":")) {
                        LOCAL_IP = address.getHostAddress();
                        return LOCAL_IP;
                    }
                }
            }
            LOCAL_IP = "127.0.0.1";
            return LOCAL_IP;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static byte[] shortToBytes(short i) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((i >>> 8) & 0xFF);
        bytes[1] = (byte) (i & 0xFF);
        return bytes;
    }

    public static byte[] intToBytes(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((i >>> 24) & 0xFF);
        bytes[0] = (byte) ((i >>> 16) & 0xFF);
        bytes[0] = (byte) ((i >>> 8) & 0xFF);
        bytes[1] = (byte) (i & 0xFF);
        return bytes;
    }

    public static byte[] ipToBytes(String ip) {
        byte[] ret = new byte[4];
        try {
            String[] ipArr = ip.split("\\.");
            ret[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);
            ret[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);
            ret[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);
            ret[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);
            return ret;
        } catch (Exception e) {
            throw new IllegalArgumentException(ip + " is invalid IP");
        }

    }

    public static String intToIp(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(((i >>> 24) & 0xFF)).append(".");
        sb.append(((i >>> 16) & 0xFF)).append(".");
        sb.append(((i >>> 8) & 0xFF)).append(".");
        sb.append((i & 0xFF));
        return sb.toString();
    }


    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String toHexString(byte[] bytes) {
        return new String(encodeHex(bytes, DIGITS));
    }

    public static byte[] toHexBytes(String str) {
        return decodeHex(str.toCharArray());
    }

    private static byte[] decodeHex(final char[] data) {
        final int len = data.length;
        if ((len & 0x01) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }
        final byte[] out = new byte[len >> 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

    private static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    private static int toDigit(final char ch, final int index) {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

}
