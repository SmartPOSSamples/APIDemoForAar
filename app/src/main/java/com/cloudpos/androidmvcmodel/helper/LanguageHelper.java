
package com.cloudpos.androidmvcmodel.helper;

import java.util.Locale;

import android.content.Context;

public class LanguageHelper {

    public static final int LANGUAGE_TYPE_OTHER = 0;
    public static final int LANGUAGE_TYPE_CN = 1;
    private static final int LANGUAGE_TYPE_NONE = -1;
    private static int languageType = LANGUAGE_TYPE_NONE;

    public static int getLanguageType(Context context) {
        if (languageType == LANGUAGE_TYPE_NONE) {
            languageType = LANGUAGE_TYPE_OTHER;
            Locale locale = context.getResources().getConfiguration().locale;
            String language = locale.getLanguage();
            if (language.endsWith("zh")) {
                languageType = LANGUAGE_TYPE_CN;
            }
        }
        return languageType;
    }
}
