
package com.cloudpos.apidemo.common;


import java.util.Random;

public class Common {

    static byte[] testBytes = {0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39};


    public static byte[] createMasterKey(int length) {
        byte[] array = new byte[length];
        for (int i = 0; i < length; i++) {
//            array[i] = (byte) 0x38;
            array[i] = testBytes[new Random().nextInt(testBytes.length)];
        }
        return array;
    }


    public static byte[] createRandomBytes(int length) {
        byte[] array = new byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = (byte) new Random().nextInt(255);
        }
        return array;
    }


    public static int transferErrorCode(int errorCode) {
        int a = -errorCode;
        int b = a & 0x0000FF;
        return -b;
    }


    public static String getMethodName() {
        StackTraceElement[] eles = Thread.currentThread().getStackTrace();
//        for (StackTraceElement stackTraceElement : eles) {
//            Log.e("stackTraceElement", stackTraceElement.getMethodName());
//        }
        return eles[5].getMethodName();
    }

}
