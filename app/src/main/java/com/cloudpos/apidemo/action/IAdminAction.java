
package com.cloudpos.apidemo.action;

import com.cloudpos.DeviceException;
import com.cloudpos.advance.ext.POSTerminalAdvance;
import com.cloudpos.advance.ext.system.ISystemDevice;
import com.cloudpos.advance.ext.system.admin.IAdminDevice;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

import java.util.Map;

public class IAdminAction extends ActionModel {
    private IAdminDevice iDevice = null;
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
                iDevice = iSystemDevice.getAdminManager();
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

    public void forceModifyAdminPwd(Map<String, Object> param, ActionCallback callback) {
        try {
            String newPassword = "12345";
            boolean b = iDevice.forceModifyAdminPwd(newPassword);
            sendSuccessLog("forceModifyAdminPwd : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void forceModifyUserPwd(Map<String, Object> param, ActionCallback callback) {
        try {
            String newPassword = "12345";
            boolean b = iDevice.forceModifyUserPwd(newPassword);
            sendSuccessLog("forceModifyUserPwd : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isAdminLoggedIn(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isAdminLoggedIn();
            sendSuccessLog("isAdminLoggedIn : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isAdminPwd(Map<String, Object> param, ActionCallback callback) {
        try {
            String password = "12345";
            boolean b = iDevice.isAdminPwd(password);
            sendSuccessLog("isAdminPwd : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isUserLoggedIn(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isUserLoggedIn();
            sendSuccessLog("isUserLoggedIn : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isUserLoginMode(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isUserLoginMode();
            sendSuccessLog("isUserLoginMode : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setUserLoginMode(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.setUserLoginMode(true);
            sendSuccessLog("setUserLoginMode : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void verifyUserPwd(Map<String, Object> param, ActionCallback callback) {
        try {
            String password = "12345";
            boolean b = iDevice.verifyUserPwd(password);
            sendSuccessLog("verifyUserPwd : " + b);
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
