
package com.cloudpos.mvc.impl;

import android.content.Context;
import android.util.Log;

import com.cloudpos.androidmvcmodel.MainApplication;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.androidmvcmodel.entity.MainItem;
import com.cloudpos.mvc.base.ActionContainer;

public class ActionContainerImpl extends ActionContainer {
    private static final String TAG = "ActionContainerImpl";
    private Context context;

    public ActionContainerImpl(Context context) {
        this.context = context;
    }

    @Override
    public void initActions() {
        for (MainItem mainItem : MainApplication.testItems) {
            try {
                String classPath = getClassPath(mainItem);
                Class clazz = Class.forName(classPath);
                addAction(mainItem.getCommand(), clazz, true);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Can't find this action");
            }
        }
    }

    private String getClassPath(MainItem mainItem) {
        String classPath = null;
        if (mainItem.isUnique()) {
            classPath = mainItem.getPackageName();
        } else {
            classPath = context.getResources().getString(R.string.action_package_name)
                    + mainItem.getCommand();
        }
        return classPath;
    }

}
