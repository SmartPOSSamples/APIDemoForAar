package com.cloudpos.apidemo.util;

import java.util.Arrays;
import java.util.Random;

public class MIFARECryptoOperations {

    public static class AuthenticationResult {
        public boolean success;
        public byte[] sessionKey; // 会话密钥（模拟）
        public byte[] challenge;  // 挑战值

        public AuthenticationResult(boolean success, byte[] sessionKey, byte[] challenge) {
            this.success = success;
            this.sessionKey = sessionKey;
            this.challenge = challenge;
        }
    }

    public static byte[] simulateEncrypt(byte[] data, byte[] sessionKey) {
        if (data == null || sessionKey == null) {
            throw new IllegalArgumentException("Data and session key cannot be null");
        }

        byte[] encrypted = new byte[data.length];
        byte[] keystream = generateKeystream(data.length, sessionKey);

        for (int i = 0; i < data.length; i++) {
            encrypted[i] = (byte) (data[i] ^ keystream[i % keystream.length]);
        }

        System.out.println("Encryption completed:");
        System.out.println("Original: " + bytesToHex(data));
        System.out.println("Encrypted: " + bytesToHex(encrypted));

        return encrypted;
    }

    public static byte[] simulateDecrypt(byte[] encryptedData, byte[] sessionKey) {
        // 流密码中，加密和解密是相同的操作
        return simulateEncrypt(encryptedData, sessionKey);
    }

    private static byte[] generateKeystream(int length, byte[] key) {
        byte[] keystream = new byte[length];
        Random random = new Random(Arrays.hashCode(key));
        random.nextBytes(keystream);
        return keystream;
    }

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) return "null";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}