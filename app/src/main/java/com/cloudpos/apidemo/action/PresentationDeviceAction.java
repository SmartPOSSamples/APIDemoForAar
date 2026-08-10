package com.cloudpos.apidemo.action;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;
import com.cloudpos.presentation.PresentationDevice;

import java.util.Map;

public class PresentationDeviceAction extends ActionModel{

    private PresentationDevice device;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device =
                    (PresentationDevice) POSTerminal.getInstance(mContext).getDevice("com.cloudpos.device.presentation");
        }
    }

    public void open(Map<String, Object> param, ActionCallback callback){
        if(device != null){
            try {
                device.open();
                sendSuccessLog(mContext.getString(R.string.operation_succeed));

            } catch (DeviceException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close(Map<String, Object> param, ActionCallback callback){
        if(device != null){
            try {
                device.close();
                sendSuccessLog(mContext.getString(R.string.operation_succeed));
            } catch (DeviceException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean setScreenOn(Map<String, Object> param, ActionCallback callback){
        if(device != null){
            try {
                device.setScreenOn(true);
                sendSuccessLog(mContext.getString(R.string.operation_succeed));
                return true;

            } catch (DeviceException e) {
                sendFailedLog(mContext.getString(R.string.operation_failed));
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public boolean setScreenOff(Map<String, Object> param, ActionCallback callback){
        if(device != null){
            try {
                device.setScreenOn(false);
                sendSuccessLog(mContext.getString(R.string.operation_succeed));
                return true;

            } catch (DeviceException e) {
                sendFailedLog(mContext.getString(R.string.operation_failed));
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public boolean isScreenOn(Map<String, Object> param, ActionCallback callback){
        if(device != null){
            try {
                if(device.isScreenOn()){
                    sendSuccessLog("Secondary Display is enable");
                    return true;
                }else{
                    sendFailedLog("Secondary Display is disable");
                    return false;
                }
            } catch (DeviceException e) {
                sendFailedLog(mContext.getString(R.string.operation_failed));
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}
