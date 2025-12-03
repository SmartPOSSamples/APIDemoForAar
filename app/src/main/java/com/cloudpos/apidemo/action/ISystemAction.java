
package com.cloudpos.apidemo.action;

import com.cloudpos.DeviceException;
import com.cloudpos.advance.ext.POSTerminalAdvance;
import com.cloudpos.advance.ext.sdk.system.constants.SystemAdvanceConstants;
import com.cloudpos.advance.ext.system.ISystemDevice;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

import java.util.Map;

public class ISystemAction extends ActionModel {
    private ISystemDevice iDevice = null;


    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (iDevice == null) {
            iDevice = POSTerminalAdvance.getInstance().getSystemDevice();
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

    public void getMtpStatus(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.getMtpStatus();
            sendSuccessLog("getMtpStatus : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getRebootTimeByEveryDay(Map<String, Object> param, ActionCallback callback) {
        try {
            String time = iDevice.getRebootTimeByEveryDay();
            sendSuccessLog("getRebootTimeByEveryDay : " + time);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getShowTouchesState(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.getShowTouchesState();
            sendSuccessLog("getShowTouchesState : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getStatusbarSettingsButtonVisibility(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.getStatusbarSettingsButtonVisibility();
            sendSuccessLog("getStatusbarSettingsButtonVisibility : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getTouchScreenWakeupValue(Map<String, Object> param, ActionCallback callback) {
        try {
            String value = iDevice.getTouchScreenWakeupValue();
            sendSuccessLog("getTouchScreenWakeupValue : " + value);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isEnableAutoTime(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isEnableAutoTime();
            sendSuccessLog("isEnableAutoTime : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isEnableAutoTimeGUI(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isEnableAutoTimeGUI();
            sendSuccessLog("isEnableAutoTimeGUI : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isEnableAutoTimezone(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isEnableAutoTimezone();
            sendSuccessLog("isEnableAutoTimezone : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isEnableAutoTimezoneGUI(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isEnableAutoTimezoneGUI();
            sendSuccessLog("isEnableAutoTimezoneGUI : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isEnableBluetooth(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isEnableBluetooth();
            sendSuccessLog("isEnableBluetooth : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isEnableWifi(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isEnableWifi();
            sendSuccessLog("isEnableWifi : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void isPowerKeyBlocked(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.isPowerKeyBlocked();
            sendSuccessLog("isPowerKeyBlocked : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void reboot(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.reboot();
            sendSuccessLog("reboot : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setAutoTime(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.setAutoTime(true);
            sendSuccessLog("setAutoTime : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setAutoTimeGUI(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.setAutoTimeGUI(true);
            sendSuccessLog("setAutoTimeGUI : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setAutoTimezone(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.setAutoTimezone(true);
            sendSuccessLog("setAutoTimezone : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setAutoTimezoneGUI(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.setAutoTimezoneGUI(true);
            sendSuccessLog("setAutoTimezoneGUI : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setBluetooth(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.setBluetooth(false);
            sendSuccessLog("setBluetooth : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setCustomAttribute(Map<String, Object> param, ActionCallback callback) {
        try {
//          key: - property's key, length less than 16. for example: persist.wp.usr.${key} ${value}.
//          value: - property's value, length less than 32.
            String key = "";
            String value = "";
            boolean b = iDevice.setCustomAttribute(key, value);
            sendSuccessLog("setCustomAttribute : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setDeviceOwner(Map<String, Object> param, ActionCallback callback) {
        try {
//            pkg: - package name.
//            deviceCls: - policyReceiver's class name.
            String pkg = "";
            String devicecls = "";
            boolean b = iDevice.setDeviceOwner(pkg, devicecls);
            sendSuccessLog("setDeviceOwner : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setLanguage(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.setLanguage("", "", "");
            sendSuccessLog("setLanguage : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setMtp(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.setMtp(true);
            sendSuccessLog("setMtp : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setPasswordLock(Map<String, Object> param, ActionCallback callback) {
        try {
            String password = "123";
            iDevice.setPasswordLock(true, password);
            sendSuccessLog("setPasswordLock : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setPatternLock(Map<String, Object> param, ActionCallback callback) {
        try {
//            pattern - an array of integers representing the pattern, with each element being a number between 1 and 9.
            String pattern = "";
            iDevice.setPatternLock(true, pattern);
            sendSuccessLog("setPatternLock : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setPinLock(Map<String, Object> param, ActionCallback callback) {
        try {
//            pin - the PIN to set, must be at least 4 digits (numeric only).
            String pin = "";
            iDevice.setPinLock(true, pin);
            sendSuccessLog("setPinLock : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setPowerKeyBlocked(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.setPowerKeyBlocked(false);
            sendSuccessLog("setPowerKeyBlocked : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setRebootTimeByEveryDay(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.setRebootTimeByEveryDay(8, 10, 0);
            sendSuccessLog("setRebootTimeByEveryDay : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setRestrictBackground(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.setRestrictBackground(true);
            sendSuccessLog("setRestrictBackground : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setScreenOffTimeout(Map<String, Object> param, ActionCallback callback) {
        try {
            int timeout = 3000;
            boolean b = iDevice.setScreenOffTimeout(timeout);
            sendSuccessLog("setScreenOffTimeout : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setShowTouches(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.setShowTouches(true);
            sendSuccessLog("setShowTouches : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setStatusbarSettingsButtonVisibility(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.setStatusbarSettingsButtonVisibility(true);
            sendSuccessLog("setStatusbarSettingsButtonVisibility : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setTouchScreenWakeupValue(Map<String, Object> param, ActionCallback callback) {
        try {
//            touch:Add - wake on touch；none: Only the power button wakes up
            String touch = "none";
            boolean b = iDevice.setTouchScreenWakeupValue(touch);
            sendSuccessLog("setTouchScreenWakeupValue : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setUnknownSources(Map<String, Object> param, ActionCallback callback) {
        try {
            String packageName = "";
            boolean b = iDevice.setUnknownSources(packageName,true);
            sendSuccessLog("setUnknownSources : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void enableWifi(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.setWifi(true);
            sendSuccessLog("setWifi : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void disableWifi(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.setWifi(false);
            sendSuccessLog("setWifi : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void shutdown(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean b = iDevice.shutdown(false,"Pos shutdown！",true);
            sendSuccessLog("shutdown : " + b);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void enableAirplaneMode(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.setAirplaneMode(true);
            sendSuccessLog("enableAirplaneMode : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }
    public void setStatusBarUnLocked(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.setStatusBarLocked(false);
            sendSuccessLog("setStatusBarUnLocked : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }
    public void setStatusBarLocked(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.setStatusBarLocked(true);
            sendSuccessLog("setStatusBarLocked : " + mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void close(Map<String, Object> param, ActionCallback callback) {
        try {
            if (iDevice.isOpened()) {
                iDevice.close();
            }
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }


    public void enableNavigationButton(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.disableNavigationBarButton(SystemAdvanceConstants.BTN_ID_HOME, true);
            iDevice.disableNavigationBarButton(SystemAdvanceConstants.BTN_ID_BACK, true);
            iDevice.disableNavigationBarButton(SystemAdvanceConstants.BTN_ID_RESENT, true);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void disableNavigationButton(Map<String, Object> param, ActionCallback callback) {
        try {
            iDevice.disableNavigationBarButton(SystemAdvanceConstants.BTN_ID_HOME, false);
            iDevice.disableNavigationBarButton(SystemAdvanceConstants.BTN_ID_BACK, false);
            iDevice.disableNavigationBarButton(SystemAdvanceConstants.BTN_ID_RESENT, false);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }
}
