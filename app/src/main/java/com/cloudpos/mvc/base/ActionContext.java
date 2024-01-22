//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cloudpos.mvc.base;

import android.content.Context;

import com.cloudpos.androidmvcmodel.common.Constants;
import com.cloudpos.mvc.common.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ActionContext implements Runnable {
    private AbstractAction action;
    private Context context;
    private Map<String, Object> param;
    private ActionCallback callback;
    private Object result = null;
    private String actionUrl;
    private String methodName;
    private ReentrantLock resultLock = new ReentrantLock();
    private Condition resultCondition;
    private boolean hasReturn;

    public ActionContext() {
        this.resultCondition = this.resultLock.newCondition();
        this.hasReturn = false;
    }

    public void run() {
        if (this.action == null) {
            Logger.error("Not found action! Please initional ActionContain and register your Action Class");
        } else {
            if (this.callback == null) {
                Logger.warn("No call back");
            }

            try {
                this.resultLock.lock();
                this.action.setContext(this.context);
                this.action.doBefore(this.param, this.callback);
                this.invoke();
                this.action.doAfter(this.param, this.callback);
            } catch (Exception var6) {
                String errorMsg = "Invoke method error: " + this.action.getClass().getName() + "#" + this.methodName;
                if (var6.getCause() == null) {
                    Logger.error(errorMsg, var6);
                } else if (var6.getCause() instanceof UnknownHostException) {
                    Logger.error(errorMsg);
                    Logger.error(getStackTraceString(var6.getCause()));
                } else {
                    Logger.error(errorMsg, var6.getCause());
                }
                this.callback.sendResponse(Constants.HANDLER_LOG_FAILED, var6.getCause().toString());
            } finally {
                this.hasReturn = true;
                this.resultCondition.signalAll();
                this.resultLock.unlock();
            }

        }
    }

    public Object getResult() {
        this.resultLock.lock();

        try {
            if (!this.hasReturn) {
                this.resultCondition.await();
            }
        } catch (InterruptedException var5) {
            var5.printStackTrace();
        } finally {
            this.resultLock.unlock();
        }

        return this.result;
    }

    private void invoke() throws Exception {
        this.parseActionUrl();
        Class<?> callbackParam = ActionCallback.class;
        if (this.callback != null) {
            callbackParam = this.callback.getClass().getSuperclass();
        }

        BeanHelper helper = new BeanHelper(this.action);
        Method method = helper.getMethod(this.methodName, new Class[]{Map.class, callbackParam});
        this.result = method.invoke(this.action, this.param, this.callback);
    }

    public static String parseActionId(String actionUrl) {
        int index = actionUrl.indexOf("/");
        return index == -1 ? actionUrl : actionUrl.substring(0, index);
    }

    private void parseActionUrl() {
        int index = this.actionUrl.indexOf("/");
        if (index == -1) {
            this.methodName = "execute";
        } else {
            this.methodName = this.actionUrl.substring(index + 1);
        }

    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        } else {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            tr.printStackTrace(pw);
            return sw.toString();
        }
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public void setCallback(ActionCallback callback) {
        this.callback = callback;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setAction(AbstractAction action) {
        this.action = action;
    }
}

