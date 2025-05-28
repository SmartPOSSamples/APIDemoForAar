
package com.cloudpos.androidmvcmodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.utils.PackageUtils;
import com.cloudpos.androidmvcmodel.adapter.ListViewAdapter;
import com.cloudpos.androidmvcmodel.common.Constants;
import com.cloudpos.androidmvcmodel.entity.MainItem;
import com.cloudpos.androidmvcmodel.entity.SubItem;
import com.cloudpos.androidmvcmodel.entity.TestItem;
import com.cloudpos.androidmvcmodel.helper.LanguageHelper;
import com.cloudpos.androidmvcmodel.helper.LogHelper;
import com.cloudpos.mvc.impl.ActionCallbackImpl;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;
import com.cloudpos.mvc.base.ActionManager;

public class MainActivity extends Activity implements OnItemClickListener {

    private static final String TAG = "DEBUG";
    private static final int MENU_CLEAN_LOG = Menu.FIRST;
    private static final int MENU_UNINSTALL = Menu.FIRST + 1;
    private static final int MENU_GROUP_ID = 0;

    TextView txtLog;
    TextView txtIntroduction;
    ListView lvwTestItems;
    Context context;
    ListViewAdapter adapter;
    private boolean isMain = true;
    private int clickedPosition = 0;
    private int scrollPosition = 0;
    private MainItem clickedMainItem;
    private Handler handler;
    private ActionCallback actionCallback;
    private Map<String, Object> testParameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initParameter();
        initView();
        initUI();
    }

    private void initParameter() {
        this.context = MainActivity.this;
        adapter = new ListViewAdapter(context);
        handler = new Handler(handlerCallback);
        actionCallback = new ActionCallbackImpl(context, handler);
        testParameters = new HashMap<String, Object>();
    }

    private void initView() {
        txtLog = (TextView) findViewById(R.id.txt_log);
        txtIntroduction = (TextView) findViewById(R.id.txt_introduction);
        lvwTestItems = (ListView) findViewById(R.id.lvw_test_items);
    }

    private void initUI() {
        txtLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        lvwTestItems.setAdapter(adapter);
        lvwTestItems.setOnItemClickListener(this);
        lvwTestItems.setOnScrollListener(onTestItemsScrollListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu cleanLogMenu = menu.addSubMenu(MENU_GROUP_ID, MENU_CLEAN_LOG, Menu.NONE,
                R.string.clean_log);
        cleanLogMenu.setIcon(android.R.drawable.ic_menu_revert);
        SubMenu uninstallMenu = menu.addSubMenu(MENU_GROUP_ID, MENU_UNINSTALL, Menu.NONE,
                R.string.uninstall_app);
        uninstallMenu.setIcon(android.R.drawable.ic_menu_delete);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case MENU_CLEAN_LOG:
                txtLog.setText("");
                break;
            case MENU_UNINSTALL:
                PackageUtils.uninstall(context, context.getPackageName());
                break;

            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }
    private int currentMainIndex = ListViewAdapter.INDEX_NONE;
    private int currentSubIndex = ListViewAdapter.INDEX_NONE;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter.isAtMainLevel()) {
            currentMainIndex = position;
            currentSubIndex = ListViewAdapter.INDEX_NONE;
            adapter.enterSubList(position);
            displayIntroduction();
            actionCallback.sendResponse(context.getString(R.string.welcome_to)
                    + "\t" + MainApplication.testItems
                    .get(currentMainIndex).getDisplayName(LanguageHelper.getLanguageType(context)));
            lvwTestItems.setSelection(0);

        } else if (adapter.isAtSubLevel()) {
            currentSubIndex = position;

            SubItem subItem = MainApplication.testItems
                    .get(currentMainIndex)
                    .getSubItem(currentSubIndex);

            if (subItem.hasChildren()) {
                adapter.enterItemList(currentSubIndex);
                actionCallback.sendResponse(context.getString(R.string.welcome_to)
                        + "\t" + subItem.getDisplayName(LanguageHelper.getLanguageType(context)));
                lvwTestItems.setSelection(0);
            } else {

                testParameters.clear();
                testParameters.put(Constants.MAIN_ITEM,
                        MainApplication.testItems.get(currentMainIndex).getCommand());
                testParameters.put(Constants.SUB_ITEM, subItem.getCommand());

                ActionManager.doSubmit(MainApplication.testItems.get(currentMainIndex).getCommand()
                                + "/" + subItem.getCommand(),
                        context,
                        testParameters,
                        actionCallback
                );
            }
        } else if (adapter.isAtItemLevel()) {
            TestItem item = MainApplication.testItems.get(currentMainIndex)
                    .getSubItem(currentSubIndex)
                    .getItems()
                    .get(position);

            testParameters.clear();
            testParameters.put(Constants.MAIN_ITEM,
                    MainApplication.testItems.get(currentMainIndex).getCommand());
            testParameters.put(Constants.SUB_ITEM,
                    MainApplication.testItems.get(currentMainIndex)
                            .getSubItem(currentSubIndex).getCommand());
            testParameters.put(Constants.ITEM, item.getCommand());

            ActionManager.doSubmit(
                    MainApplication.testItems.get(currentMainIndex)
                            .getSubItem(currentSubIndex).getCommand()
                            + "/" + item.getCommand(),
                    context,
                    testParameters,
                    actionCallback
            );
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (adapter.isAtItemLevel()) {
                currentSubIndex = ListViewAdapter.INDEX_NONE;
                adapter.enterSubList(currentMainIndex);
            } else if (adapter.isAtSubLevel()) {
                adapter.resetToMain();
                currentMainIndex = ListViewAdapter.INDEX_NONE;
                currentSubIndex = ListViewAdapter.INDEX_NONE;
            } else {
                System.exit(0);
            }
            displayIntroduction();
            actionCallback.sendResponse(context.getString(R.string.test_end));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void setListViewSelection() {
        lvwTestItems.setAdapter(adapter);
        lvwTestItems.setSelection(scrollPosition);
    }

    private void performMainItemClick() {
        isMain = false;
        clickedMainItem = MainApplication.testItems.get(clickedPosition);
        actionCallback.sendResponse(context.getString(R.string.welcome_to)
                + "\t" + clickedMainItem.getDisplayName(LanguageHelper.getLanguageType(context)));
        displayIntroduction();
        if (clickedMainItem.isActivity()) {
            // if the test item is a activity, jump by package-name property.
            ComponentName cn = new ComponentName(context,
                    clickedMainItem.getPackageName());
            Intent intent = new Intent();
            intent.setComponent(cn);
            intent.putExtra(Constants.MAIN_ITEM,
                    clickedMainItem.getCommand());
            startActivityForResult(intent, clickedPosition);
        } else {
            // otherwise jump to SubItem page and automatically execute autoTest
            // item if exsies
            adapter.refreshView(clickedPosition);
            lvwTestItems.setSelection(0);
            lvwTestItems.setAdapter(adapter);
            // setLayoutIntroductionIfExists();
        }
    }

    private void performSubItemClick() {
        boolean hasChildren = clickedMainItem.getSubItem(clickedPosition).hasChildren();
        if(hasChildren){
            showSubItems(clickedMainItem.getSubItem(clickedPosition).getItems());
            return;
        }
        testParameters.clear();
        testParameters.put(Constants.MAIN_ITEM, clickedMainItem.getCommand());
        String subItemCommand = clickedMainItem.getSubItem(clickedPosition).getCommand();
        testParameters.put(Constants.SUB_ITEM, subItemCommand);
        Log.e(TAG, "itemPressed : " + clickedMainItem.getCommand() + "/" + subItemCommand);
        ActionManager.doSubmit(clickedMainItem.getCommand() + "/" + subItemCommand,
                context, testParameters, actionCallback);
    }

    private void showSubItems(List<SubItem> subItemList) {
        this.isMain = false;

        adapter.refreshView(0);
        lvwTestItems.setAdapter(adapter);
        lvwTestItems.setSelection(0);
    }



    private void displayIntroduction() {
        if (txtIntroduction != null) {
            if (isMain) {
                txtIntroduction.setVisibility(View.GONE);
            } else {
                txtIntroduction.setVisibility(View.VISIBLE);
                txtIntroduction.setText(context.getString(R.string.welcome_to) + "\n"
                        + clickedMainItem.getDisplayName(LanguageHelper
                                .getLanguageType(context)));
            }
        }
    }

    private OnScrollListener onTestItemsScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // position which records the position of the visible top line
            if (isMain) {
                scrollPosition = lvwTestItems.getFirstVisiblePosition();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                int totalItemCount) {
        }
    };

    private Handler.Callback handlerCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.HANDLER_LOG:
                    LogHelper.infoAppendMsg((String) msg.obj, txtLog);
                    break;
                case Constants.HANDLER_LOG_SUCCESS:
                    LogHelper.infoAppendMsgForSuccess((String) msg.obj, txtLog);
                    break;
                case Constants.HANDLER_LOG_FAILED:
                    LogHelper.infoAppendMsgForFailed((String) msg.obj, txtLog);
                    break;

                default:
                    break;
            }
            return true;
        }
    };

}
