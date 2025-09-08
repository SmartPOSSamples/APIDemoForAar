
package com.cloudpos.androidmvcmodel;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.mtp.MtpConstants;
import android.util.Log;

import com.cloudpos.androidmvcmodel.helper.XmlPullParserHelper;
import com.cloudpos.androidmvcmodel.entity.MainItem;
import com.cloudpos.androidmvcmodel.helper.LanguageHelper;
import com.cloudpos.androidmvcmodel.helper.TerminalHelper;
import com.cloudpos.mvc.base.ActionManager;

public class MainApplication extends Application {

    private Context context;
    public static List<MainItem> testItems = new ArrayList<MainItem>();

    private static MainApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        initParameter();
        ActionManager.initActionContainer(new com.cloudpos.mvc.impl.ActionContainerImpl(context));
        instance = this;
    }

    private void initParameter() {
        context = this;
        testItems = XmlPullParserHelper.getTestItems(context, TerminalHelper.getTerminalType());
        for (MainItem mainItem : testItems) {
            Log.e("DEBUG", "" + mainItem.getDisplayName(LanguageHelper.getLanguageType(context)));
        }
    }

    public static MainApplication getInstance(){
//        if(instance == null){
//            instance = new MainApplication();
//        }
        return instance;
    }
}
