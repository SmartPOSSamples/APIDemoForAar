
package com.cloudpos.androidmvcmodel.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudpos.androidmvcmodel.MainApplication;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.androidmvcmodel.entity.MainItem;
import com.cloudpos.androidmvcmodel.helper.LanguageHelper;

public class ListViewAdapter extends BaseAdapter {

    private static final String TAG = "ListViewAdapter";
    private LayoutInflater inflater;
    // private DBHelper dbHelper;
    private int mainItemIndex = INDEX_NONE;
    public static final int INDEX_NONE = -1;
    private Context context;

    public ListViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        // dbHelper = DBHelper.getInstance();
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mainItemIndex <= INDEX_NONE) {
            count = MainApplication.testItems.size();
        } else {
            count = MainApplication.testItems.get(mainItemIndex).getSubItems().size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        Object item;
        if (mainItemIndex <= INDEX_NONE) {
            item = MainApplication.testItems.get(position);
        } else {
            item = MainApplication.testItems.get(mainItemIndex).getSubItem(position);
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_test, null);
        }
        TextView txtButton = (TextView) convertView.findViewById(R.id.txt_button);
//        TextView txt_signature = (TextView) convertView.findViewById(R.id.txt_signature);
//        setDisplayedSignature(position, txtSignature);
        setDisplayedButton(position, txtButton);
        return convertView;
    }

    /**
     * refresh listview when switching from MainItem page and SubItem page
     */
    public void refreshView(int index) {
        mainItemIndex = index;
        Log.e(TAG, "mainItemIndex = " + mainItemIndex);
        notifyDataSetChanged();
    }

    /**
     * refresh specified item when changed
     */
    public void refreshChangedItemView(int position, View convertView, ViewGroup parent) {
        getView(position, convertView, parent);
    }

    // private void setDisplayedSignature(int position, TextView txtSignature) {
    // String testResult = getTestResult(position);
    // Log.e(TAG, "setDisplayedSignature testResult = " + testResult);
    // if (testResult.equals(SqlConstants.RESULT_SUCCESS)) {
    // txtSignature.setText("√");
    // txtSignature.setTextColor(Color.rgb(0, 0, 0));
    // } else if (testResult.equals(SqlConstants.RESULT_FAILED)) {
    // txtSignature.setText("X");
    // txtSignature.setTextColor(Color.rgb(255, 0, 0));
    // } else if (testResult.equals(SqlConstants.RESULT_EXCEPTION)) {
    // txtSignature.setText("O");
    // txtSignature.setTextColor(Color.rgb(255, 255, 255));
    // } else {
    // txtSignature.setText("");
    // txtSignature.setTextColor(Color.rgb(0, 0, 0));
    // }
    // }

    private void setDisplayedButton(int position, TextView txtButton) {
        MainItem mainItem = getMainItem(position);
        // txtButton.setText(mainItem.getDisplayName(LanguageHelper.getLanguageType(context)));
        if (mainItemIndex <= INDEX_NONE) {
            txtButton.setText(mainItem.getDisplayName(LanguageHelper.getLanguageType(context)));
        } else {
            txtButton.setText(mainItem.getSubItem(position).getDisplayName(
                    LanguageHelper.getLanguageType(context)));
//            txtButton.setTag(mainItem.getSubItem(position).getDisplayName(LanguageHelper.getLanguageType(context)));
        }
    }

    /**
     * get test result from sqlite database<br/>
     */
    // private String getTestResult(int position) {
    // MainItem mainItem = getMainItem(position);
    // // String testResult = dbHelper.queryTestResultByMainItem(mainItem);
    // return testResult;
    // }

    private MainItem getMainItem(int position) {
        MainItem mainItem = null;
        if (mainItemIndex <= INDEX_NONE) {
            mainItem = MainApplication.testItems.get(position);
        } else {
            mainItem = MainApplication.testItems.get(mainItemIndex);
        }
        return mainItem;
    }

}
