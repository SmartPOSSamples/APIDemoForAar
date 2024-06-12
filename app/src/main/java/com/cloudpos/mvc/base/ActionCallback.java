//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cloudpos.mvc.base;

import android.content.Context;
import android.os.Handler;

public abstract class ActionCallback {
    protected Context context;

    public ActionCallback() {
    }

    public ActionCallback(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.getContext();
    }

    public void callbackInHandler(final String methodName, final Object... args) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            public void run() {
                try {
                    (new BeanHelper(this)).invoke(methodName, args);
                } catch (Exception var2) {
                    var2.printStackTrace();
                }

            }
        });
    }

    public void sendResponse(int code) {
    }

    public void sendResponse(String msg) {
    }

    public void sendResponse(int code, String msg) {
    }
}

