//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cloudpos.mvc.base;

import android.content.Context;

import com.cloudpos.mvc.base.AbstractAction;
import com.cloudpos.mvc.base.ActionCallback;
import com.cloudpos.mvc.base.ActionContainer;
import com.cloudpos.mvc.base.ActionContext;
import com.cloudpos.mvc.base.ActionScheduler;
import com.cloudpos.mvc.common.Logger;
import com.cloudpos.mvc.common.MVCException;
import java.util.HashMap;
import java.util.Map;

public final class ActionManager {
    private static ActionManager actionManager = new ActionManager();
    private ActionScheduler actionScheduler = ActionScheduler.getInstance();
    protected Map<String, Object> mActionContainer = new HashMap();
    private boolean isStart = false;

    public ActionManager() {
    }

    private static ActionManager getInstance() {
        if (!actionManager.isStart) {
            actionManager.start();
            actionManager.isStart = true;
        }

        return actionManager;
    }

    private void start() {
        this.actionScheduler.start();
    }

    public static void initActionContainer(ActionContainer actions) {
        actions.initActions();
        getInstance().mActionContainer.putAll(actions.getActions());
    }

    public static void doSubmit(String actionUrl, Map<String, Object> param, ActionCallback callback) {
        doSubmit((String)actionUrl, (Context)null, param, callback);
    }

    public static void doSubmit(Class<? extends AbstractAction> clazz, String methodName, Map<String, Object> param, ActionCallback callback) {
        doSubmit(clazz.getName() + "/" + methodName, param, callback);
    }

    public static void doSubmit(String actionUrl, Context context, Map<String, Object> param, ActionCallback callback) {
        getInstance().newActionContext(actionUrl, context, param, callback);
    }

    public static void doSubmit(Class<? extends AbstractAction> clazz, String methodName, Context context, Map<String, Object> param, ActionCallback callback) {
        doSubmit(clazz.getName() + "/" + methodName, context, param, callback);
    }

    public static <T> T doSubmitForResult(String actionUrl, Map<String, Object> param, ActionCallback callback) {
        return doSubmitForResult((String)actionUrl, (Context)null, param, callback);
    }

    public static <T> T doSubmitForResult(Class<? extends AbstractAction> clazz, String methodName, Map<String, Object> param, ActionCallback callback) {
        return doSubmitForResult(clazz.getName() + "/" + methodName, param, callback);
    }

    public static <T> T doSubmitForResult(String actionUrl, Context context, Map<String, Object> param, ActionCallback callback) {
        ActionContext acontext = getInstance().newActionContext(actionUrl, context, param, callback);
        return (T) acontext.getResult();
    }

    public static <T> T doSubmitForResult(Class<? extends AbstractAction> clazz, String methodName, Context context, Map<String, Object> param, ActionCallback callback) {
        return doSubmitForResult(clazz.getName() + "/" + methodName, context, param, callback);
    }

    private ActionContext newActionContext(String actionUrl, Context context, Map<String, Object> param, ActionCallback callback) {
        ActionContext acontext = new ActionContext();
        acontext.setActionUrl(actionUrl);
        acontext.setParam(param);
        acontext.setCallback(callback);
        if (acontext != null) {
            acontext.setContext(context);
        }

        this.setAction(actionUrl, acontext);
        this.actionScheduler.setActionContext(acontext);
        return acontext;
    }

    private void setAction(String actionUrl, ActionContext context) {
        String actionId = ActionContext.parseActionId(actionUrl);
        Object obj = this.mActionContainer.get(actionId);
        if (obj == null) {
            throw new MVCException("Not found actionId in ActionContainer. The actionId is [" + actionId + "].");
        } else {
            if (Class.class.isInstance(obj)) {
                try {
                    context.setAction((AbstractAction)((Class)Class.class.cast(obj)).newInstance());
                } catch (Exception var6) {
                    Logger.error("build instance error:", var6);
                }
            } else {
                context.setAction((AbstractAction)obj);
            }

        }
    }
}

