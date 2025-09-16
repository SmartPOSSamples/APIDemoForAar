
package com.cloudpos.apidemo.action;

import com.cloudpos.DeviceException;
import com.cloudpos.advance.ext.POSTerminalAdvance;
import com.cloudpos.advance.ext.system.ISystemDevice;
import com.cloudpos.advance.ext.system.network.INetworkDevice;
import com.cloudpos.advance.ext.system.network.IWifiDevice;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;
import com.cloudpos.sdk.util.ByteConvertStringUtil;
import com.cloudpos.sdk.util.SystemUtil;

import java.util.Map;

public class IWifiAction extends ActionModel {
    private IWifiDevice iDevice = null;
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
                iDevice = iNetworkDevice.getWifiManager();
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

    public void removeWifiSSID(Map<String, Object> param, ActionCallback callback) {
        try {
            //ssid - : wifi ssid name
            String ssid = "";
            boolean b = iDevice.removeWifiSSID(ssid);
            if (b) {
                sendSuccessLog("removeWifiSSID : " + mContext.getString(R.string.operation_succeed));
            } else {
                sendSuccessLog("removeWifiSSID : " + mContext.getString(R.string.operation_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getWifiMac(Map<String, Object> param, ActionCallback callback) {
        try {
            byte[] bytes = iDevice.getWifiMac();
            if (bytes != null && bytes.length > 0) {
                sendSuccessLog(ByteConvertStringUtil.bytesToMac(bytes));
            } else {
                sendFailedLog( mContext.getString(R.string.operation_failed));
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
