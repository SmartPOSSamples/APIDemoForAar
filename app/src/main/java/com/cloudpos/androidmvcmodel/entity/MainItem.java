
package com.cloudpos.androidmvcmodel.entity;

import java.util.ArrayList;
import java.util.List;

public class MainItem extends TestItem {
    private boolean isActivity;
    private List<SubItem> subItems = new ArrayList<SubItem>();
    private String packageName;
    private boolean isUnique;

    public boolean isActivity() {
        return isActivity;
    }

    public void setActivity(boolean isActivity) {
        this.isActivity = isActivity;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public SubItem getSubItem(int position) {
        return subItems.get(position);
    }

    public List<SubItem> getSubItems() {
        return subItems;
    }

    public List<SubItem> getTestSubItems() {
        List<SubItem> testSubItems = new ArrayList<SubItem>();
        for (SubItem subItem : subItems) {
            if (subItem.isNeedTest()) {
                testSubItems.add(subItem);
            }
        }
        return testSubItems;
    }

    public void addSubItem(SubItem subItem) {
        this.subItems.add(subItem);
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

}
