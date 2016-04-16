package com.personal.li.perf;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class AppTest {

    @Test
    public void test() throws Exception {
        System.out.println(Base64.encodeBase64URLSafeString("lishoubo".getBytes()));
        System.out.println(DigestUtils.md5Hex("lishoubo".getBytes()));
    }
}
