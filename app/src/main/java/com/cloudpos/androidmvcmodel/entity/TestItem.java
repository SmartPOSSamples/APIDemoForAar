
package com.cloudpos.androidmvcmodel.entity;

import com.cloudpos.androidmvcmodel.helper.LanguageHelper;

import java.util.ArrayList;
import java.util.List;


public class TestItem {
    private String command;
    private String displayNameCN;
    private String displayNameEN;

    private List<SubItem> items = new ArrayList<>();

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
    public List<SubItem> getItems() {
        return items;
    }

    public void setItems(List<SubItem> items) {
        this.items = items;
    }

    public void addItem(SubItem item) {
        this.items.add(item);
    }

    public boolean hasChildren() {
        return items != null && !items.isEmpty();
    }
    public String getDisplayName(int languageType) {
        if (languageType == LanguageHelper.LANGUAGE_TYPE_CN) {
            return displayNameCN;
        } else {
            return displayNameEN;
        }
    }

    public void setDisplayNameCN(String displayNameCN) {
        this.displayNameCN = displayNameCN;
    }

    public void setDisplayNameEN(String displayNameEN) {
        this.displayNameEN = displayNameEN;
    }

    @Override
    public String toString() {
        return String.format("command = %s, displayCN = %s, displyEN = %s", command, displayNameCN,
                displayNameEN);
    }
}
