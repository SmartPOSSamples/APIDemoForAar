
package com.cloudpos.apidemo.action;

import com.cloudpos.DeviceException;
import com.cloudpos.advance.ext.POSTerminalAdvance;
import com.cloudpos.advance.ext.system.ISystemDevice;
import com.cloudpos.advance.ext.system.network.IEthernetDevice;
import com.cloudpos.advance.ext.system.network.INetworkDevice;
import com.cloudpos.advance.ext.system.network.IpBean;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

import java.util.Map;

public class IEthernetAction extends ActionModel {
    private IEthernetDevice iDevice = null;
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
                iDevice = iNetworkDevice.getEthernetManager();
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

    public void getStaticIp(Map<String, Object> param, ActionCallback callback) {
        try {
            IpBean ipBean = iDevice.getStaticIp();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " getStaticIp : " + ipBean);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isEnabledEthernet(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isEnabledEthernet();
            sendSuccessLog("isEnabledEthernet : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isStaticIp(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isStaticIp();
            sendSuccessLog("isStaticIp : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setEthernet(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.setEthernet(true);
            sendSuccessLog("setEthernet : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setStaticIp(Map<String, Object> param, ActionCallback callback) {
        try {
            IpBean ipBean = new IpBean();
            boolean b = iDevice.setStaticIp(ipBean);
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
