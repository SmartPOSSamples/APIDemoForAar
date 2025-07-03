package com.cloudpos.apidemo.action;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.cashdrawer.CashDrawerDevice;
import com.cloudpos.mvc.base.ActionCallback;

import java.util.Map;

public class CashBoxAction extends ActionModel {

    private CashDrawerDevice device;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if(device == null){
            device =
                    (CashDrawerDevice) POSTerminal.getInstance(mContext).getDevice("com.cloudpos.device.cashdrawer");
        }
    }
    public void open(Map<String, Object> param, ActionCallback callback) {
        try {
            device.open();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }
    public void openCashBox(Map<String, Object> param, ActionCallback callback) {
        try {
            device.openCashBox();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }
    public void queryStatus(Map<String, Object> param, ActionCallback callback) {
        try {
            int status = device.queryStatus();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + "status = " + status);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }
    public void kickOut(Map<String, Object> param, ActionCallback callback) {
        try {
            device.kickOut();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void close(Map<String, Object> param, ActionCallback callback) {
        try {
            device.close();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }
}
