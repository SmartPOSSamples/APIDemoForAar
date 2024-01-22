
package com.cloudpos.mvc.impl;

import android.content.Context;
import android.os.Handler;

import com.cloudpos.androidmvcmodel.common.Constants;
import com.cloudpos.mvc.base.ActionCallback;

public class ActionCallbackImpl extends ActionCallback {

    private Handler handler;
    private Context context;

    public ActionCallbackImpl(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void sendResponse(int code) {
        handler.obtainMessage(code).sendToTarget();
    }
    
    @Override
    public void sendResponse(int code, String msg) {
        handler.obtainMessage(code, "\t\t" + msg + "\n").sendToTarget();
    }

    @Override
    public void sendResponse(String msg) {
        sendResponse(Constants.HANDLER_LOG, msg);
    }

//    public void sendLog(String log) {
//        handler.obtainMessage(Constants.HANDLER_LOG, "\t\t" + log + "\n").sendToTarget();
//    }
//
//    public void sendSuccessLog(String successLog) {
//        handler.obtainMessage(Constants.HANDLER_LOG_SUCCESS, "\t\t" + successLog + "\n")
//                .sendToTarget();
//    }
//
//    public void sendFailedLog(String failedLog) {
//        handler.obtainMessage(Constants.HANDLER_LOG_FAILED, "\t\t" + failedLog + "\n")
//                .sendToTarget();
//    }

}
