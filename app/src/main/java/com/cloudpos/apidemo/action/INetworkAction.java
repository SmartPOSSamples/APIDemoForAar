
package com.cloudpos.apidemo.action;

import com.alibaba.fastjson.JSONObject;
import com.cloudpos.DeviceException;
import com.cloudpos.advance.ext.POSTerminalAdvance;
import com.cloudpos.advance.ext.system.ISystemDevice;
import com.cloudpos.advance.ext.system.network.INetworkDevice;
import com.cloudpos.advance.ext.system.network.NetworkType;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

import java.util.Map;

public class INetworkAction extends ActionModel {
    private INetworkDevice iDevice = null;
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
                iDevice = iSystemDevice.getNetworkManager();
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

    public void getPreferredNetworkType(Map<String, Object> param, ActionCallback callback) {
        try {
            int phoneId = 1;
            int type = iDevice.getPreferredNetworkType(phoneId);
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + "getPreferredNetworkType : " + type);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getSupportedNetworkTypes(Map<String, Object> param, ActionCallback callback) {
        try {
            NetworkType[] types = iDevice.getSupportedNetworkTypes();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + "getSupportedNetworkTypes : " + JSONObject.toJSONString(types));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setMobileDataEnabled(Map<String, Object> param, ActionCallback callback) {
        try {
            int slot = 1;
            boolean b = iDevice.setMobileDataEnabled(slot,true);
            if (b) {
                sendSuccessLog("isAdminLoggedIn : " + mContext.getString(R.string.operation_succeed));
            }else{
                sendSuccessLog("isAdminLoggedIn : " + mContext.getString(R.string.operation_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setMobileDataRoamingEnabled(Map<String, Object> param, ActionCallback callback) {
        try {
            int slot = 1;
            //roaming - 1 if it should be enabled, 0 if it should be disabled.
            int roaming = 1;
            boolean b = iDevice.setMobileDataRoamingEnabled(slot, roaming);
            if (b) {
                sendSuccessLog("setMobileDataRoamingEnabled : " + mContext.getString(R.string.operation_succeed));
            }else{
                sendSuccessLog("setMobileDataRoamingEnabled : " + mContext.getString(R.string.operation_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setPreferredNetworkType(Map<String, Object> param, ActionCallback callback) {
        try {
            //subId - the id of the subscription to set the preferred network type for.
            //networkType - the preferred network type, defined in RILConstants.java.
            int subId = 1;
            int networkType = 1;
            boolean b = iDevice.setPreferredNetworkType(subId, networkType);
            if (b) {
                sendSuccessLog("setPreferredNetworkType : " + mContext.getString(R.string.operation_succeed));
            }else{
                sendSuccessLog("setPreferredNetworkType : " + mContext.getString(R.string.operation_failed));
            }
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
