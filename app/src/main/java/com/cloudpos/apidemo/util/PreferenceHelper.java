
package com.cloudpos.apidemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.cloudpos.apidemoforunionpaycloudpossdk.R;

public class PreferenceHelper {

    private static PreferenceHelper instance;
    public static final String KEY_TERMINAL_IS_EXIST = "terminal_java_is_exist";

    public static PreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceHelper(context);
        }
        return instance;
    }

    private SharedPreferences preferences;

    private Context context;

    // 当前事件的处理方式
    public static final String CURRENT_NAME = "current_item_name";
    // ssl双向测试
    public static final String KEY_SSL_IP = "ssl_url";

    private PreferenceHelper(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // AOTU

    public boolean isAOTUTest() {
        boolean isAuto = preferences.getBoolean(getString(R.string.auto_test_pref), true);
        return isAuto;
    }

    public boolean isAutoOnly() {
        boolean isAuto = preferences.getBoolean(getString(R.string.auto_only_pref), false);
        return isAuto;
    }

    public boolean isManuOnly() {
        boolean isManu = preferences.getBoolean(getString(R.string.manu_only_pref), false);
        return isManu;
    }

    // public String getDefaultServerAdress4TestcaseSSL(){
    // String serverIp =
    // preferences.getString(getString(R.string.auto_test_pref),
    // "192.168.200.8");
    // return serverIp;
    // }

    public String getDBDefaultCopyPath() {
        String path = preferences.getString(getString(R.string.copy_db_pref),
                getString(R.string.defalut_copy_db_path));
        return path;
    }

    private String getString(int id) {
        return context.getResources().getString(id);
    }
    
    public void putBooleanValue(String key, boolean value){
        Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    
    public boolean getBooleanValue(String key){
        return preferences.getBoolean(key, false);
    }
    
    public String getStringValue(String key){
        return preferences.getString(key, "");
    }

	public boolean terminalCertExist (){
		boolean exist = preferences.getBoolean(KEY_TERMINAL_IS_EXIST, false);
		return exist ;
	}
	
	public void setValue(String key , boolean value){
		Editor edit = preferences.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}
}
