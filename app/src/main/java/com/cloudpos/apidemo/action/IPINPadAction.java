
package com.cloudpos.apidemo.action;

import com.cloudpos.DeviceException;
import com.cloudpos.advance.ext.POSTerminalAdvance;
import com.cloudpos.advance.ext.pinpad.IPINPadDevice;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

import java.util.Map;

public class IPINPadAction extends ActionModel {
    private IPINPadDevice iDevice = null;


    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (iDevice == null) {
            iDevice = POSTerminalAdvance.getInstance().getPINPadDevice();
        }
    }

    public void open(Map<String, Object> param, ActionCallback callback) {
        try {
            if (!iDevice.isOpened()) {
                iDevice.open(this.mContext);
            }
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void resetMasterKey(Map<String, Object> param, ActionCallback callback) {
        try {
            int slot = 1;
            boolean b = iDevice.resetMasterKey(slot);
            if (b) {
                sendSuccessLog("resetMasterKey : " + mContext.getString(R.string.operation_succeed));
            } else {
                sendSuccessLog("resetMasterKey : " + mContext.getString(R.string.operation_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void resetTransferKey(Map<String, Object> param, ActionCallback callback) {
        try {
            int slot = 1;
            boolean b = iDevice.resetTransferKey(slot);
            if (b) {
                sendSuccessLog("resetTransferKey : " + mContext.getString(R.string.operation_succeed));
            } else {
                sendSuccessLog("resetTransferKey : " + mContext.getString(R.string.operation_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void close(Map<String, Object> param, ActionCallback callback) {
        try {
            if (iDevice.isOpened()) {
                iDevice.close();
            }
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }
}
