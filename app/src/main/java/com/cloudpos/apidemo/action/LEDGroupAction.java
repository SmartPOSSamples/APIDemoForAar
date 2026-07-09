
package com.cloudpos.apidemo.action;

import java.util.Map;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.led.LEDDevice;
import com.cloudpos.led.LEDGroupDevice;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

public class LEDGroupAction extends ActionModel {

    private LEDGroupDevice device = null;

    private int logicalID = LEDDevice.ID_RED;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device = (LEDGroupDevice) POSTerminal.getInstance(mContext)
                    .getDevice(POSTerminal.DEVICE_NAME_LED_GROUP);
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

    public void turnOn(Map<String, Object> param, ActionCallback callback) {
        try {
            device.turnOn(logicalID);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void turnOff(Map<String, Object> param, ActionCallback callback) {
        try {
            device.turnOff(logicalID);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void startBlink(Map<String, Object> param, ActionCallback callback) {
        try {
            device.startBlink(logicalID, 100, 100, 10);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void cancelBlink(Map<String, Object> param, ActionCallback callback) {
        try {
            device.cancelBlink(logicalID);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getStatus(Map<String, Object> param, ActionCallback callback) {
        try {
            int status = device.getStatus(logicalID);
            String statusText = (status == LEDGroupDevice.STATUS_ON) ? "ON" : "OFF";
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Status: " + statusText);
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
