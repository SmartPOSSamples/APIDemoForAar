
package com.cloudpos.androidmvcmodel.helper;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

import com.cloudpos.androidmvcmodel.common.Constants;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.androidmvcmodel.entity.MainItem;
import com.cloudpos.androidmvcmodel.entity.SubItem;

public class XmlPullParserHelper {
    public static XmlPullParser getXmlPullParser(Context context, int terminalType)
            throws XmlPullParserException {
        XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
        switch (terminalType) {
            case TerminalHelper.TERMINAL_TYPE_WIZARHAND_Q1:
                xmlPullParser = context.getResources().getXml(R.xml.wizarhand_q1);
                break;
            case TerminalHelper.TERMINAL_TYPE_WIZARPAD_1:
                xmlPullParser = context.getResources().getXml(R.xml.wizarpad_1);
                break;
            case TerminalHelper.TERMINAL_TYPE_WIZARHAND_M0:
                xmlPullParser = context.getResources().getXml(R.xml.wizarhand_m0);
                break;
            default:
                xmlPullParser = context.getResources().getXml(R.xml.wizarpos_1);
                break;
        }
        return xmlPullParser;
    }

    public static MainItem parseToMainItem(XmlPullParser xmlPullParser) {
        MainItem mainItem = new MainItem();
        String nameCN = xmlPullParser.getAttributeValue(null, "name_CN");
        String nameEN = xmlPullParser.getAttributeValue(null, "name_EN");
        String command = xmlPullParser.getAttributeValue(null, "command");
        String isActivity = xmlPullParser.getAttributeValue(null, "isActivity");
        mainItem.setDisplayNameCN(nameCN);
        mainItem.setDisplayNameEN(nameEN);
        mainItem.setCommand(command);
        if (isActivity != null && isActivity.equals("true")) {
            mainItem.setActivity(true);
        } else {
            mainItem.setActivity(false);
        }
        return mainItem;
    }

    public static SubItem parseToSubItem(XmlPullParser xmlPullParser) {
        SubItem subItem = new SubItem();
        String nameCN = xmlPullParser.getAttributeValue(null, "name_CN");
        String nameEN = xmlPullParser.getAttributeValue(null, "name_EN");
        String command = xmlPullParser.getAttributeValue(null, "command");
        String needTest = xmlPullParser.getAttributeValue(null, "needTest");
        subItem.setDisplayNameCN(nameCN);
        subItem.setDisplayNameEN(nameEN);
        subItem.setCommand(command);
        if (needTest != null && needTest.equals("true")) {
            subItem.setNeedTest(true);
        } else {
            subItem.setNeedTest(false);
        }
        return subItem;
    }

    public static List<MainItem> getTestItems(Context context, int terminalType) {
        Log.d("DEBUG", "getTestItems");
        List<MainItem> testItems = new ArrayList<MainItem>();
        try {
            XmlPullParser xmlPullParser = XmlPullParserHelper.getXmlPullParser(context,
                    terminalType);
            int mEventType = xmlPullParser.getEventType();
            MainItem mainItem = null;
            String tagName = null;
            while (mEventType != XmlPullParser.END_DOCUMENT) {
                if (mEventType == XmlPullParser.START_TAG) {
                    tagName = xmlPullParser.getName();
                    if (tagName.equals(Constants.MAIN_ITEM)) {
                        mainItem = XmlPullParserHelper.parseToMainItem(xmlPullParser);
//                        Log.d("DEBUG", "" + mainItem.getDisplayName(1));
                    } else if (tagName.equals(Constants.SUB_ITEM)) {
                        SubItem subItem = parseToSubItem(xmlPullParser);
                        mainItem.addSubItem(subItem);
                    }
                } else if (mEventType == XmlPullParser.END_TAG) {
                    tagName = xmlPullParser.getName();
                    if (tagName.equals(Constants.MAIN_ITEM)) {
                        testItems.add(mainItem);
                    }
                }
                mEventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testItems;
    }

}
