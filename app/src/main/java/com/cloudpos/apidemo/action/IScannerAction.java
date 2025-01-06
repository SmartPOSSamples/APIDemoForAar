
package com.cloudpos.apidemo.action;

import com.alibaba.fastjson.JSONObject;
import com.cloudpos.DeviceException;
import com.cloudpos.advance.ext.POSTerminalAdvance;
import com.cloudpos.advance.ext.scanner.IScanCallBack;
import com.cloudpos.advance.ext.scanner.IScannerDevice;
import com.cloudpos.advance.ext.scanner.ScanParameter;
import com.cloudpos.advance.ext.scanner.ScanResult;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

import java.util.Map;

public class IScannerAction extends ActionModel {
    private IScannerDevice iDevice = null;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (iDevice == null) {
            iDevice = POSTerminalAdvance.getInstance().getScannerDevice();
        }
    }

    public void open(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.open(this.mContext);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getScanType(Map<String, Object> param, ActionCallback callback) {
        try {
            int cameraId = 1;
            IScannerDevice.ScanType scanType = iDevice.getScanType(cameraId);
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " getScanType : " + JSONObject.toJSONString(scanType));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void scanBarcode(Map<String, Object> param, ActionCallback callback) {
        try {
            ScanParameter parameter = new ScanParameter();
            ScanResult result = iDevice.scanBarcode(parameter);
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " scanBarcode result: " + result.getText());
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void startScan(Map<String, Object> param, ActionCallback callback) {
        try {
            ScanParameter parameter = new ScanParameter();
            IScanCallBack callBack = new IScanCallBack() {
                @Override
                public void foundBarcode(ScanResult result) {
                    sendSuccessLog(mContext.getString(R.string.operation_succeed) + " scanBarcode result: " + result.getText());
                }
            };
            iDevice.startScan(parameter, callBack);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void stopScan(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.stopScan();
            if (b) {
                sendSuccessLog("stopScan : " + mContext.getString(R.string.operation_succeed));
            } else {
                sendSuccessLog("stopScan : " + mContext.getString(R.string.operation_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void close(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.close();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }
}
