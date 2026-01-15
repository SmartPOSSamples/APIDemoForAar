package com.cloudpos.apidemo.action;

import com.cloudpos.DeviceException;
import com.cloudpos.advance.ext.POSTerminalAdvance;
import com.cloudpos.advance.ext.system.ISystemDevice;
import com.cloudpos.advance.ext.system.admin.IAdminDevice;
import com.cloudpos.advance.ext.system.login.ILoginDevice;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

import java.util.Map;

public class ILoginAction extends ActionModel{

    private ILoginDevice iDevice = null;
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
                iDevice = iSystemDevice.getLoginManager();
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

    public void isAdminLogin(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isAdminLogin();
            sendSuccessLog("isAdminLogin : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isUserLogin(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isUserLogin();
            sendSuccessLog("isUserLogin : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void loginAdmin(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.loginAdmin("99999999");
            sendSuccessLog("loginAdmin : " + (b?"success":"failed"));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void loginUser(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.loginUser("99999999");
            sendSuccessLog("loginUser : " + (b?"success":"failed"));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void logoutAdmin(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.logoutAdmin();
            sendSuccessLog("logoutAdmin : " + (b?"success":"failed"));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void logoutUser(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.logoutUser();
            sendSuccessLog("logoutUser : " + (b?"success":"failed"));
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
