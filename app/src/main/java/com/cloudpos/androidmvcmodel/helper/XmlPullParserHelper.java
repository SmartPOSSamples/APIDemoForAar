
package com.cloudpos.androidmvcmodel.helper;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

import com.cloudpos.androidmvcmodel.MainApplication;
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
        String type = xmlPullParser.getAttributeValue(null, "type");
        mainItem.setDisplayNameCN(nameCN);
        mainItem.setDisplayNameEN(nameEN);
        mainItem.setCommand(command);
        try {
            mainItem.setType(Integer.parseInt(type));
        }catch (NumberFormatException e){
            mainItem.setType(0);
        }
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
        String type = xmlPullParser.getAttributeValue(null, "type");
        String spinner = xmlPullParser.getAttributeValue(null, "spinner");
        subItem.setDisplayNameCN(nameCN);
        subItem.setDisplayNameEN(nameEN);
        subItem.setCommand(command);
        if(type != null){
            try {
                subItem.setType(Integer.parseInt(type));
            }catch (NumberFormatException e){
                subItem.setType(0);
            }
        }

        if(spinner != null)
         subItem.setSpinner(spinner.split(","));
        if (needTest != null && needTest.equals("true")) {
            subItem.setNeedTest(true);
        } else {
            subItem.setNeedTest(false);
        }
        return subItem;
    }

    public static List<MainItem> getTestItems(Context context, int terminalType) {
        List<MainItem> testItems = new ArrayList<>();
        try {
            XmlPullParser parser = getXmlPullParser(context, terminalType);
            int eventType = parser.getEventType();
            MainItem currentMainItem = null;
            List<SubItem> currentSubItems = null;
            List<SubItem> currentItemStack = new ArrayList<>(); // 用栈维护当前的 SubItem 嵌套层级

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                boolean isEquals = Constants.SUB_ITEM.equals(tagName) || Constants.ITEM.equals(tagName);
                if (eventType == XmlPullParser.START_TAG) {
                    if (Constants.MAIN_ITEM.equals(tagName)) {
                        currentMainItem = parseToMainItem(parser);
                        currentSubItems = new ArrayList<>();
                    } else if (isEquals) {
                        SubItem subItem = parseToSubItem(parser);

                        // 如果当前在嵌套结构中，加入其父 SubItem 的 items
                        if (!currentItemStack.isEmpty()) {
                            currentItemStack.get(currentItemStack.size() - 1).addItem(subItem);
                        } else if (currentSubItems != null) {
                            currentSubItems.add(subItem);
                        }

                        currentItemStack.add(subItem); // 入栈
                    }

                } else if (eventType == XmlPullParser.END_TAG) {
                    if (Constants.MAIN_ITEM.equals(tagName)) {
                        if (currentMainItem != null && currentSubItems != null) {
                            for (SubItem item : currentSubItems) {
                                currentMainItem.addSubItem(item);
                            }
                            testItems.add(currentMainItem);
                        }
                        currentMainItem = null;
                        currentSubItems = null;
                    } else if (isEquals) {
                        // 出栈
                        if (!currentItemStack.isEmpty()) {
                            currentItemStack.remove(currentItemStack.size() - 1);
                        }
                    }
                }

                eventType = parser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return testItems;
    }


}
