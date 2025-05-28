
package com.cloudpos.mvc.impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.cloudpos.androidmvcmodel.MainApplication;
import com.cloudpos.androidmvcmodel.entity.SubItem;
import com.cloudpos.androidmvcmodel.entity.TestItem;
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

                if(TextUtils.isEmpty(classPath)){
                    for (SubItem subItem: mainItem.getSubItems()){
                        classPath = context.getResources().getString(R.string.action_package_name)
                                + subItem.getCommand();

                        Class clazz = Class.forName(classPath);
                        addAction(subItem.getCommand(), clazz, true);
                    }

                }else {
                    Class clazz = Class.forName(classPath);
                    addAction(mainItem.getCommand(), clazz, true);
                }


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
        } else if (mainItem.getSubItem(0).hasChildren()){
            classPath = null;
        } else {
            classPath = context.getResources().getString(R.string.action_package_name)
                    + mainItem.getCommand();
        }
        return classPath;
    }

}
