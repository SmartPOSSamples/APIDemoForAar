//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cloudpos.mvc.common;

import android.util.Log;

public class Logger {
    public static int level = 3;

    public Logger() {
    }

    public static void debug(String msg) {
        if (level <= 3) {
            Log.d(createTag(), msg);
        }

    }

    public static void debug(String msg, Throwable tr) {
        if (level <= 3) {
            Log.d(createTag(), msg, tr);
        }

    }

    public static void info(String msg) {
        if (level <= 4) {
            Log.i(createTag(), msg);
        }

    }

    public static void info(String msg, Throwable tr) {
        if (level <= 4) {
            Log.i(createTag(), msg, tr);
        }

    }

    public static void warn(String msg) {
        if (level <= 5) {
            Log.w(createTag(), msg);
        }

    }

    public static void warn(String msg, Throwable tr) {
        if (level <= 5) {
            Log.w(createTag(), msg, tr);
        }

    }

    public static void error(String msg) {
        if (level <= 6) {
            Log.e(createTag(), msg);
        }

    }

    public static void error(String msg, Throwable tr) {
        if (level <= 6) {
            Log.e(createTag(), msg, tr);
        }

    }

    private static String createTag() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        } else {
            StackTraceElement[] var4 = sts;
            int var3 = sts.length;

            for(int var2 = 0; var2 < var3; ++var2) {
                StackTraceElement st = var4[var2];
                if (!st.isNativeMethod() && !st.getClassName().equals(Thread.class.getName()) && !st.getClassName().equals(Logger.class.getName())) {
                    return st.getLineNumber() + ":" + st.getFileName();
                }
            }

            return "";
        }
    }
}

