
package com.cloudpos.apidemo.action;

import com.cloudpos.DeviceException;
import com.cloudpos.advance.ext.POSTerminalAdvance;
import com.cloudpos.advance.ext.system.ISystemDevice;
import com.cloudpos.advance.ext.system.app.IUpdateAppDevice;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

import java.util.Map;

public class IUpdateAppAction extends ActionModel {
    private IUpdateAppDevice iDevice = null;
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
                iDevice = iSystemDevice.getUpdateAppManager();
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

    public void installAndLaunchApkFile(Map<String, Object> param, ActionCallback callback) {
        try {
            String apkFilePath = "";
            int type = 1;
            String packageName = "";
            String classNameOrAction = "";
            boolean b = iDevice.installAndLaunchApkFile(apkFilePath, type, packageName, classNameOrAction);
            if (b) {
                sendSuccessLog("installAndLaunchApkFile : " + mContext.getString(R.string.operation_succeed));
            } else {
                sendSuccessLog("installAndLaunchApkFile : " + mContext.getString(R.string.operation_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void installApkFile(Map<String, Object> param, ActionCallback callback) {
        try {
            String apkFilePath = "";
            int result = iDevice.installApkFile(apkFilePath);
            if (result == 1) {
                sendSuccessLog("forceModifyUserPwd : " + mContext.getString(R.string.operation_succeed));
            } else {
                sendSuccessLog("forceModifyUserPwd : " + mContext.getString(R.string.operation_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void uninstall(Map<String, Object> param, ActionCallback callback) {
        try {
            String packageName = "";
            boolean b = iDevice.uninstall(packageName);
            if (b) {
                sendSuccessLog("uninstall : " + mContext.getString(R.string.operation_succeed));
            } else {
                sendSuccessLog("uninstall : " + mContext.getString(R.string.operation_failed));
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
