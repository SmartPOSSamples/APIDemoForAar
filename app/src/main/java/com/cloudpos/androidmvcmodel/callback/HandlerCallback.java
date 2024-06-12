
package com.cloudpos.androidmvcmodel.callback;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.cloudpos.androidmvcmodel.helper.LogHelper;

public class HandlerCallback implements Handler.Callback {
    
    public static final int LOG = 1;
    public static final int LOG_SUCCESS = 2;
    public static final int LOG_FAILED = 3;
    public static final int ALERT_SOUND = 4;

    private TextView txtResult;
    private Context context;

    public HandlerCallback(Context context, TextView textView) {
        this.context = context;
        this.txtResult = textView;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case LOG:
                LogHelper.infoAppendMsg((String) msg.obj, txtResult);
                break;
            case LOG_SUCCESS:
                LogHelper.infoAppendMsgForSuccess((String) msg.obj, txtResult);
                break;
            case LOG_FAILED:
                LogHelper.infoAppendMsgForFailed((String) msg.obj, txtResult);
                break;
            case ALERT_SOUND:
                showDialog(msg.obj.toString());
                break;
            default:
                LogHelper.infoAppendMsg((String) msg.obj, txtResult);
                break;
        }
        return true;
    }

    private void showDialog(final String testItem) {
        final String[] showMsgAndItems = testItem.split("/");
//        new AlertDialog.Builder(context)
//                .setTitle(showMsgAndItems[0])
//                .setPositiveButton(context.getString(R.string.sound_btn_success),
//                        new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                setSuccessfullResult(showMsgAndItems[1], showMsgAndItems[2]);
//                            }
//                        })
//                .setNegativeButton(context.getString(R.string.sound_btn_failed),
//                        new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                setFailedResult(showMsgAndItems[1], showMsgAndItems[2]);
//                            }
//                        }).show();
    }

    private void setSuccessfullResult(String mainItem, String subItem) {
//        DBHelper.getInstance().saveTestResult(mainItem, subItem, SqlConstants.RESULT_SUCCESS);
    }

    private void setFailedResult(String mainItem, String subItem) {
//        DBHelper.getInstance().saveTestResult(mainItem, subItem, SqlConstants.RESULT_FAILED);
    }

}
