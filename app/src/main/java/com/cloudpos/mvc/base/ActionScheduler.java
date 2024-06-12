//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cloudpos.mvc.base;

import com.cloudpos.mvc.common.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ActionScheduler extends Thread {
    private static ActionScheduler actionScheduler = new ActionScheduler();
    private LinkedBlockingQueue<ActionContext> mActionQueue = new LinkedBlockingQueue(20);
    private ExecutorService service = Executors.newFixedThreadPool(30);

    public ActionScheduler() {
    }

    public static ActionScheduler getInstance() {
        return actionScheduler;
    }

    public void run() {
        ActionContext mContext = null;

        while(true) {
            while(true) {
                try {
                    mContext = (ActionContext)this.mActionQueue.take();
                    this.service.submit(mContext);
                } catch (Exception var3) {
                }
            }
        }
    }

    public void setActionContext(ActionContext context) {
        if (context != null) {
            try {
                this.mActionQueue.put(context);
            } catch (InterruptedException var3) {
                var3.printStackTrace();
            }

        }
    }
}

