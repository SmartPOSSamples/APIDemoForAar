package com.cloudpos.apidemo.action;

import java.util.Map;

import com.cloudpos.DeviceException;
import com.cloudpos.OperationListener;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.TimeConstants;
import com.cloudpos.fingerprint.Fingerprint;
import com.cloudpos.fingerprint.FingerprintDevice;
import com.cloudpos.fingerprint.FingerprintOperationResult;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

public class FingerPrintAction extends ActionModel {

    private FingerprintDevice device = null;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);

        if (device == null) {
            device = (FingerprintDevice) POSTerminal.getInstance(mContext).getDevice("cloudpos.device.fingerprint");
        }
    }

    public void open(Map<String, Object> param, ActionCallback callback) {
        boolean result = POSTerminal.getInstance(mContext).isDeviceExist("cloudpos.device.fingerprint");
        try {
            if(result){
                device.open(FingerprintDevice.FINGERPRINT);
                sendSuccessLog(mContext.getString(R.string.operation_succeed));
            }else{
                sendSuccessLog(mContext.getString(R.string.operation_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void listenForFingerprint(Map<String, Object> param, ActionCallback callback) {
        sendSuccessLog("");
        try {
            OperationListener listener = new OperationListener() {

                @Override
                public void handleResult(OperationResult arg0) {
                    if (arg0.getResultCode() == OperationResult.SUCCESS) {
                        sendSuccessLog2(mContext.getString(R.string.scan_fingerprint_success));
                    } else {
                        sendFailedOnlyLog(mContext.getString(R.string.scan_fingerprint_fail));
                    }
                }
            };
            device.listenForFingerprint(listener, TimeConstants.FOREVER);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void waitForFingerprint(Map<String, Object> param, ActionCallback callback) {
        sendSuccessLog("");
        try {
            FingerprintOperationResult operationResult = device.waitForFingerprint(TimeConstants.FOREVER);
            if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                sendSuccessLog2(mContext.getString(R.string.scan_fingerprint_success));
            } else {
                sendFailedOnlyLog(mContext.getString(R.string.scan_fingerprint_fail));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void match(Map<String, Object> param, ActionCallback callback) {
        try {
            sendNormalLog(mContext.getString(R.string.scan_fingerprint_first));
            Fingerprint fingerprint1 = getFingerprint();
            sendNormalLog(mContext.getString(R.string.scan_fingerprint_second));
            Fingerprint fingerprint2 = getFingerprint();
            if (fingerprint1 != null && fingerprint2 != null) {
                int match = device.match(fingerprint1, fingerprint2);
                sendSuccessLog(mContext.getString(R.string.match_fingerprint_result) + match);
            } else {
                sendFailedLog(mContext.getString(R.string.match_fingerprint_fail));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }


    public void cancelRequest(Map<String, Object> param, ActionCallback callback) {
        try {
            device.cancelRequest();
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

    private Fingerprint getFingerprint() {
        boolean result = POSTerminal.getInstance(mContext).isDeviceExist("cloudpos.device.fingerprint");
        if(result){
            Fingerprint fingerprint = null;
            try {
                FingerprintOperationResult operationResult = device.waitForFingerprint(TimeConstants.FOREVER);
                if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                    fingerprint = operationResult.getFingerprint(0, 0);
                    sendSuccessLog2(mContext.getString(R.string.scan_fingerprint_success));
                } else {
                    sendFailedOnlyLog(mContext.getString(R.string.scan_fingerprint_fail));
                }
            } catch (DeviceException e) {
                e.printStackTrace();
                sendFailedOnlyLog(mContext.getString(R.string.operation_failed));
            }
            return fingerprint;
        }
        return null;
    }
}
