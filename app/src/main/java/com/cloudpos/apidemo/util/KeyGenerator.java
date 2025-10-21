package com.cloudpos.apidemo.util;

import java.security.SecureRandom;

public class KeyGenerator {

    /**
     * Generate a 128-bit (16-byte) random key.
     *
     * @return The generated 128-bit key byte array.
     */
    public static byte[] generate128BitKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[16]; // 16 bytes for 128 bits
        secureRandom.nextBytes(key);
        return key;
    }
}


