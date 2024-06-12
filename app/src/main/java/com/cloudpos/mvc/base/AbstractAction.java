//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cloudpos.mvc.base;

import android.content.Context;
import java.util.Map;

public abstract class AbstractAction {
    protected Context mContext;

    public AbstractAction() {
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
    }

    protected void doAfter(Map<String, Object> param, ActionCallback callback) {
    }
}

