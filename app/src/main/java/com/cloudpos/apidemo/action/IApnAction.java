
package com.cloudpos.apidemo.action;

import com.alibaba.fastjson.JSONObject;
import com.cloudpos.DeviceException;
import com.cloudpos.advance.ext.POSTerminalAdvance;
import com.cloudpos.advance.ext.system.ISystemDevice;
import com.cloudpos.advance.ext.system.network.IApnDevice;
import com.cloudpos.advance.ext.system.network.INetworkDevice;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

import java.util.List;
import java.util.Map;

public class IApnAction extends ActionModel {
    private IApnDevice iDevice = null;
    private INetworkDevice iNetworkDevice = null;
    private ISystemDevice iSystemDevice = null;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (iSystemDevice == null) {
            iSystemDevice = POSTerminalAdvance.getInstance().getSystemDevice();
        }
        if (iNetworkDevice == null) {
            try {
                if (!iSystemDevice.isOpened()){
                    iSystemDevice.open(mContext);
                }
                iNetworkDevice = iSystemDevice.getNetworkManager();
            } catch (DeviceException e) {
                throw new RuntimeException(e);
            }
        }
        if (iDevice == null) {
            try {
                if (!iNetworkDevice.isOpened()) {
                    iNetworkDevice.open(this.mContext);
                }
                iDevice = iNetworkDevice.getApnManager();
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

    public void add(Map<String, Object> param, ActionCallback callback) {
        try {
            String name = "";
            String apn = "";
            String result = iDevice.add(name, apn);
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " add : " + result);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void addByAllArgs(Map<String, Object> param, ActionCallback callback) {
        try {
            String apn = "IMS";
            String carrier = "Orange Botswana";
            String mcc = "460";
            String mnc = "01";
            String protocol = "IPv4/IPv6";
            String roaming_protocol = "IPv4/IPv6";
            String type = "ims";
            String result = iDevice.addByAllArgs(carrier, apn, mcc, mnc, null, null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null, protocol, roaming_protocol, type, null, null, null);
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " addByAllArgs : " + result);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void addByMCCAndMNC(Map<String, Object> param, ActionCallback callback) {
        try {
            String apn = "IMS";
            String carrier = "Orange Botswana";
            String mcc = "460";
            String mnc = "01";
            String result = iDevice.addByMCCAndMNC(carrier, apn, mcc, mnc);
            sendSuccessLog("addByMCCAndMNC : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void clear(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.clear();
            if (b) {
                sendSuccessLog("clear : " + mContext.getString(R.string.operation_succeed));
            } else {
                sendSuccessLog("clear : " + mContext.getString(R.string.operation_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void clearWithSlot(Map<String, Object> param, ActionCallback callback) {
        try {
            int slot = 1;
            boolean b = iDevice.clearWithSlot(slot);
            if (b) {
                sendSuccessLog("clearWithSlot : " + mContext.getString(R.string.operation_succeed));
            } else {
                sendSuccessLog("clearWithSlot : " + mContext.getString(R.string.operation_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getSelected(Map<String, Object> param, ActionCallback callback) {
        try {
            Map m = iDevice.getSelected();
            sendSuccessLog("getSelected : " + JSONObject.toJSONString(m));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void query(Map<String, Object> param, ActionCallback callback) {
        try {
            String name = "name", value = "BeMobile";
            List l = iDevice.query(name, value);
            sendSuccessLog("query : " + JSONObject.toJSONString(l));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void queryByName(Map<String, Object> param, ActionCallback callback) {
        try {
            String value = "BeMobile";
            List l = iDevice.queryByName(value);
            sendSuccessLog("queryByName : " + JSONObject.toJSONString(l));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setSelected(Map<String, Object> param, ActionCallback callback) {
        try {
            String name = "BeMobile";
            boolean b = iDevice.setSelected(name);
            if (b) {
                sendSuccessLog("setSelected : " + mContext.getString(R.string.operation_succeed));
            } else {
                sendSuccessLog("setSelected : " + mContext.getString(R.string.operation_failed));
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
            if (iNetworkDevice.isOpened()) {
                iNetworkDevice.close();
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
