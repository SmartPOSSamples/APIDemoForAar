
package com.cloudpos.apidemo.action;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.advance.ext.POSTerminalAdvance;
import com.cloudpos.advance.ext.system.ISystemDevice;
import com.cloudpos.advance.ext.system.battery.IBatteryDevice;
import com.cloudpos.androidmvcmodel.common.Constants;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;
import com.cloudpos.serialport.SerialPortDevice;

import java.util.Map;

public class IBatteryAction extends ActionModel {
    private IBatteryDevice iDevice = null;
    private ISystemDevice iSystemDevice = null;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (iSystemDevice == null) {
            iSystemDevice = POSTerminalAdvance.getInstance().getSystemDevice();
        }
        if (iDevice == null) {
            try {
                if (!iSystemDevice.isOpened()) {
                    iSystemDevice.open(this.mContext);
                }
                iDevice = iSystemDevice.getBatteryManager();
            } catch (DeviceException e) {
                throw new RuntimeException(e);
            }
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

    public void switchShowBatteryPercent(Map<String, Object> param, ActionCallback callback) {
        try {
            iSystemDevice.setShowBatteryPercent(!iSystemDevice.isEnabledShowBatteryPercent());
            sendSuccessLog(iSystemDevice.isEnabledShowBatteryPercent()?"open":"close");
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getBatteryLevel(Map<String, Object> param, ActionCallback callback) {
        try {
            int level = iDevice.getBatteryLevel();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " getBatteryLevel : " + level);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isAutoCounterMode(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isAutoCounterMode();
            sendSuccessLog(" isAutoCounterMode : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isCounterMode(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isCounterMode();
            sendSuccessLog(" isCounterMode : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setAutoCounterMode(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.setAutoCounterMode(true);
            sendSuccessLog("setAutoCounterMode : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setCounterMode(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.setCounterMode(true);
            sendSuccessLog("setCounterMode : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void close(Map<String, Object> param, ActionCallback callback) {
        try {
            if (iSystemDevice.isOpened()) {
                iSystemDevice.close();
            }
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
