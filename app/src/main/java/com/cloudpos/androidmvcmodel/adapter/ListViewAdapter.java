
package com.cloudpos.androidmvcmodel.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.cloudpos.androidmvcmodel.MainApplication;
import com.cloudpos.androidmvcmodel.OnItemEventListener;
import com.cloudpos.androidmvcmodel.entity.SubItem;
import com.cloudpos.androidmvcmodel.entity.TestItem;
import com.cloudpos.androidmvcmodel.entity.TypeConstant;
import com.cloudpos.apidemo.util.PreferenceHelper;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.androidmvcmodel.entity.MainItem;
import com.cloudpos.androidmvcmodel.helper.LanguageHelper;

import java.util.HashMap;

public class ListViewAdapter extends BaseAdapter {

    private static final String TAG = "ListViewAdapter";
    private final OnItemEventListener mOnItemEventListener;
    private LayoutInflater inflater;
    // private DBHelper dbHelper;
    private int mainItemIndex = INDEX_NONE;
    public static final int INDEX_NONE = -1;
    private Context context;
    private int subItemIndex = INDEX_NONE;
    private HashMap<String, ArrayAdapter> mAdapterMap = new HashMap<>();

    public ListViewAdapter(Context context, OnItemEventListener onItemEventListener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mOnItemEventListener = onItemEventListener;
        // dbHelper = DBHelper.getInstance();
    }

    @Override
    public int getCount() {
        if (mainItemIndex == INDEX_NONE) {
            return MainApplication.testItems.size();
        } else if (subItemIndex == INDEX_NONE) {
            return MainApplication.testItems.get(mainItemIndex).getSubItems().size();
        } else {
            return MainApplication.testItems.get(mainItemIndex)
                    .getSubItem(subItemIndex)
                    .getItems().size();
        }
    }


    @Override
    public Object getItem(int position) {
        if (mainItemIndex == INDEX_NONE) {
            return MainApplication.testItems.get(position);
        } else if (subItemIndex == INDEX_NONE) {
            return MainApplication.testItems.get(mainItemIndex).getSubItem(position);
        } else {
            return MainApplication.testItems.get(mainItemIndex)
                    .getSubItem(subItemIndex)
                    .getItems().get(position);
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        TextView txt_signature = (TextView) convertView.findViewById(R.id.txt_signature);
//        setDisplayedSignature(position, txtSignature);
        return setDisplayedButton(convertView, position);
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

    private View setDisplayedButton(View convertView, int position) {
        if (mainItemIndex == INDEX_NONE) {
            MainItem mainItem = MainApplication.testItems.get(position);
            convertView = setConvertView(convertView, position, mainItem);
        } else if (subItemIndex == INDEX_NONE) {
            SubItem subItem = MainApplication.testItems.get(mainItemIndex).getSubItem(position);
            convertView = setConvertView(convertView, position, subItem);
        } else {
            TestItem item = MainApplication.testItems
                    .get(mainItemIndex)
                    .getSubItem(subItemIndex)
                    .getItems()
                    .get(position);
            convertView = setConvertView(convertView, position, item);
        }
        return convertView;
    }

    private View setConvertView(View convertView, int rootPosition, TestItem item){
        ViewHloder viewHloder = new ViewHloder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_test, null);
            viewHloder.txtButton = (TextView) convertView.findViewById(R.id.txt_button);
            viewHloder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            viewHloder.spinner = (Spinner) convertView.findViewById(R.id.myspinner);
            viewHloder.aSwitch = (Switch) convertView.findViewById(R.id.sw_btn);
            viewHloder.llSpinner = (LinearLayout) convertView.findViewById(R.id.ll_spinner);
            viewHloder.rlItemTest = (RelativeLayout) convertView.findViewById(R.id.rl_item_test);
            convertView.setTag(viewHloder);
        }
        viewHloder = (ViewHloder) convertView.getTag();
        switch (item.getType()){
            case TypeConstant.TYPE_CONSTANT_BUTTON:
                viewHloder.llSpinner.setVisibility(View.GONE);
                viewHloder.aSwitch.setVisibility(View.GONE);
                viewHloder.txtButton.setVisibility(View.VISIBLE);
                viewHloder.txtButton.setText(item.getDisplayName(LanguageHelper.getLanguageType(context)));
                viewHloder.rlItemTest.setBackgroundResource(R.drawable.btn_selector);
                break;
            case TypeConstant.TYPE_CONSTANT_SPINNER:
                viewHloder.rlItemTest.setBackgroundResource(R.color.transparent);
                viewHloder.llSpinner.setVisibility(View.VISIBLE);
                viewHloder.aSwitch.setVisibility(View.GONE);
                viewHloder.txtButton.setVisibility(View.GONE);
                viewHloder.txtName.setText(item.getDisplayName(LanguageHelper.getLanguageType(context)));
                ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, item.getSpinner());
                viewHloder.spinner.setAdapter(adapter);
                mAdapterMap.put(item.getDisplayName(LanguageHelper.getLanguageType(context)), adapter);
                viewHloder.spinner.setSelection(PreferenceHelper.getInstance(context).getIntValue(item.getCommand()));
//                mOnItemEventListener.onSpinnerSelected(rootPosition, PreferenceHelper.getInstance(context).getIntValue(item.getCommand()), item.getSpinner());
                viewHloder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mOnItemEventListener.onSpinnerSelected(rootPosition, position, item.getSpinner());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            case TypeConstant.TYPE_CONSTANT_SWITCH:
                viewHloder.rlItemTest.setBackgroundResource(R.color.transparent);
                viewHloder.llSpinner.setVisibility(View.GONE);
                viewHloder.aSwitch.setVisibility(View.VISIBLE);
                viewHloder.txtButton.setVisibility(View.GONE);
                viewHloder = (ViewHloder) convertView.getTag();
                viewHloder.aSwitch.setText(item.getDisplayName(LanguageHelper.getLanguageType(context)));
                viewHloder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mOnItemEventListener.onSwitch(rootPosition, isChecked);
                    }
                });
                break;
            default:
                viewHloder.llSpinner.setVisibility(View.GONE);
                viewHloder.aSwitch.setVisibility(View.GONE);
                viewHloder.txtButton.setVisibility(View.VISIBLE);
                viewHloder.txtButton.setText(item.getDisplayName(LanguageHelper.getLanguageType(context)));
                break;
        }
        return convertView;
    }

    private class ViewHloder{
        Switch aSwitch;
        Spinner spinner;
        TextView txtName;
        TextView txtButton;
        LinearLayout llSpinner;
        RelativeLayout rlItemTest;
        int index = 0;
        boolean isChecked = false;
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

    public boolean isAtMainLevel() {
        return mainItemIndex == INDEX_NONE;
    }

    public boolean isAtSubLevel() {
        return mainItemIndex != INDEX_NONE && subItemIndex == INDEX_NONE;
    }

    public boolean isAtItemLevel() {
        return mainItemIndex != INDEX_NONE && subItemIndex != INDEX_NONE;
    }

    public void enterSubList(int mainIndex) {
        this.mainItemIndex = mainIndex;
        this.subItemIndex = INDEX_NONE;
        notifyDataSetChanged();
    }

    public void enterItemList(int subIndex) {
        this.subItemIndex = subIndex;
        notifyDataSetChanged();
    }

    public void resetToMain() {
        this.mainItemIndex = INDEX_NONE;
        this.subItemIndex = INDEX_NONE;
        notifyDataSetChanged();
    }
        public int getMainIndex() {
        return mainItemIndex;
    }

    public int getSubIndex() {
        return subItemIndex;
    }

}
