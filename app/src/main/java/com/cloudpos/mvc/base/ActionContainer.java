//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cloudpos.mvc.base;

import com.cloudpos.mvc.common.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class ActionContainer {
    protected Map<String, Object> actions = new HashMap();

    public ActionContainer() {
    }

    public abstract void initActions();

    public Map<String, Object> getActions() {
        return this.actions;
    }

    private Object searchInstance(Class<? extends AbstractAction> clazz) throws Exception {
        Iterator var3 = this.actions.entrySet().iterator();

        Object value;
        do {
            if (!var3.hasNext()) {
                return clazz.newInstance();
            }

            Entry<String, Object> entry = (Entry)var3.next();
            value = entry.getValue();
        } while(value == null || !value.getClass().equals(clazz));

        return value;
    }

    protected boolean addAction(String actionId, Class<? extends AbstractAction> clazz, boolean singleton) {
        if (singleton) {
            try {
                this.actions.put(actionId, this.searchInstance(clazz));
            } catch (Exception var5) {
                Logger.error("build singleton instance occur an error:", var5);
                return false;
            }
        } else {
            this.actions.put(actionId, clazz);
        }

        return true;
    }

    protected boolean addAction(String actionId, Class<? extends AbstractAction> clazz) {
        return this.addAction(actionId, clazz, false);
    }

    protected boolean addAction(Class<? extends AbstractAction> clazz, boolean singleton) {
        return this.addAction(clazz.getName(), clazz, singleton);
    }

    protected boolean addAction(Class<? extends AbstractAction> clazz) {
        return this.addAction(clazz, false);
    }
}

