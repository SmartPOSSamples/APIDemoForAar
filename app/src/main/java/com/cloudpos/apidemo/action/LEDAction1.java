
package com.cloudpos.apidemo.action;

import java.util.Map;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.led.LEDDevice;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

public class LEDAction1 extends ActionModel {

    private LEDDevice device = null;

    private int logicalID = LEDDevice.ID_BLUE;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device = (LEDDevice) POSTerminal.getInstance(mContext)
                    .getDevice("cloudpos.device.led",logicalID);
        }
    }

    public void open(Map<String, Object> param, ActionCallback callback) {
        try {
            device.open(logicalID);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getLogicalID(Map<String, Object> param, ActionCallback callback) {
        try {
            int logicalID = device.getLogicalID();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Logical ID = "
                    + logicalID);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void startBlink(Map<String, Object> param, ActionCallback callback) {
        try {
            device.startBlink(100, 100, 10);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
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

    public void cancelBlink(Map<String, Object> param, ActionCallback callback) {
        try {
            device.cancelBlink();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void blink(Map<String, Object> param, ActionCallback callback) {
        try {
            sendSuccessLog("");
            device.blink(100, 100, 100);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getStatus(Map<String, Object> param, ActionCallback callback) {
        try {
            int status = device.getStatus();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Status: "
                    + (status == LEDDevice.STATUS_ON ? "ON" : "OFF"));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void turnOn(Map<String, Object> param, ActionCallback callback) {
        try {
            device.turnOn();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void turnOff(Map<String, Object> param, ActionCallback callback) {
        try {
            device.turnOff();
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
