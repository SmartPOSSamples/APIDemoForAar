package com.cloudpos.androidmvcmodel;

public interface OnItemEventListener {
    void onSpinnerSelected(int rootPositon, int selectedIndex, String[] spinners);

    void onSwitch(int rootPositon, boolean isChecked);

    void onInputText(int rootPositon, String s);
}