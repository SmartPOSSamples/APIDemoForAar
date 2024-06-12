
package com.cloudpos.androidmvcmodel.helper;

import android.util.Log;

public class SystemPropertyHelper {

    public static String get(String propertyName) {
        Object property = null;
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Log.i("systemProperties", systemProperties.toString());
            property = systemProperties.getMethod("get", new Class[] {
                    String.class, String.class
            }).invoke(systemProperties, new Object[] {
                    propertyName, "unknown"
            });
            Log.i("bootloaderVersion", property.getClass().toString());
        } catch (Exception e) {
            property = "";
            e.printStackTrace();
        }
        return property.toString();
    }
}
