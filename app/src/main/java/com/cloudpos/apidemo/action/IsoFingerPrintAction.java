package com.cloudpos.apidemo.action;

import android.os.RemoteException;
import android.util.Log;

import com.cloudpos.DeviceException;
import com.cloudpos.OperationListener;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.TimeConstants;
import com.cloudpos.androidmvcmodel.common.Constants;
import com.cloudpos.fingerprint.Fingerprint;
import com.cloudpos.fingerprint.FingerprintDevice;
import com.cloudpos.fingerprint.FingerprintNoneOperationResult;
import com.cloudpos.fingerprint.FingerprintOperationResult;
import com.cloudpos.fingerprint.FingerprintPressOperationResult;
import com.cloudpos.fingerprint.FingerprintRemoveOperationResult;
import com.cloudpos.fingerprint.FingerprintTimeoutOperationResult;
import com.cloudpos.sdk.util.SystemUtil;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * create by rf.w 19-2-28上午10:24
 */
public class IsoFingerPrintAction extends ActionModel {
    private FingerprintDevice device = null;

    private int userID = 9;
    private int timeout = 60 * 1000;
    private static int ISOFINGERPRINT_TYPE_DEFAULT = 0;
    private static int ISOFINGERPRINT_TYPE_ISO2005 = 1;
    private static int ISOFINGERPRINT_TYPE_ISO2015 = 2;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);

        if (device == null) {
            device = (FingerprintDevice) POSTerminal.getInstance(mContext).getDevice("cloudpos.device.fingerprint");
            if (SystemUtil.getProperty("wp.fingerprint.model").equalsIgnoreCase("aratek")) {
                ISOFINGERPRINT_TYPE_ISO2005 = 0;
            }
        }
    }

    public void open(Map<String, Object> param, ActionCallback callback) {
        try {
            device.open(FingerprintDevice.ISO_FINGERPRINT);
            //清除指纹数据
            device.delAllFingers();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }


    public void listenForFingerprint(Map<String, Object> param, ActionCallback callback) {
        sendSuccessLog("");
        try {
            OperationListener listener = new OperationListener() {

                @Override
                public void handleResult(OperationResult arg0) {
                    if (arg0.getResultCode() == OperationResult.SUCCESS) {
                        sendSuccessLog2(mContext.getString(R.string.scan_fingerprint_success));
                    } else {
                        sendFailedOnlyLog(mContext.getString(R.string.scan_fingerprint_fail));
                    }
                }
            };
            device.listenForFingerprint(listener, TimeConstants.FOREVER);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void waitForFingerprint(Map<String, Object> param, ActionCallback callback) {
        sendSuccessLog("");
        try {
            FingerprintOperationResult operationResult = device.waitForFingerprint(TimeConstants.FOREVER);
            if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                sendSuccessLog2(mContext.getString(R.string.scan_fingerprint_success));
            } else {
                sendFailedOnlyLog(mContext.getString(R.string.scan_fingerprint_fail));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void cancelRequest(Map<String, Object> param, ActionCallback callback) {
        try {
            device.cancelRequest();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void enroll(Map<String, Object> param, ActionCallback callback) {
        try {
            callback.sendResponse(mContext.getString(R.string.press_fingerprint));
            userID++;
            device.enroll(userID, timeout);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void listenForEnroll(Map<String, Object> param, final ActionCallback callback) {
        try {
            device.listenForEnroll(new OperationListener() {
                @Override
                public void handleResult(OperationResult operationResult) {
                    if (operationResult instanceof FingerprintPressOperationResult) {
                        callback.sendResponse(mContext.getString(R.string.press_fingerprint));
                    } else if (operationResult instanceof FingerprintRemoveOperationResult) {
                        callback.sendResponse(mContext.getString(R.string.remove_fingerprint));
                    } else if (operationResult instanceof FingerprintNoneOperationResult) {
                        callback.sendResponse(Constants.HANDLER_LOG_FAILED, mContext.getString(R.string.scan_fingerprint_fail) + ",retry");//重试
                    } else if (operationResult instanceof FingerprintTimeoutOperationResult) {
                        callback.sendResponse(Constants.HANDLER_LOG_FAILED, mContext.getString(R.string.enroll_timeout));
                    } else if (operationResult instanceof FingerprintOperationResult) {
                        Fingerprint fingerprint = ((FingerprintOperationResult) operationResult).getFingerprint(100, 100);
                        byte[] feature = fingerprint.getFeature();
                        if(feature != null){
                            callback.sendResponse(Constants.HANDLER_LOG_SUCCESS, "finger.length=" + feature.length);
                        } else {
                            callback.sendResponse(Constants.HANDLER_LOG_SUCCESS, "finger.length=null");
                        }
                    }
                }
            }, 10000);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void verifyAgainstUserId(Map<String, Object> param, ActionCallback callback) {
        callback.sendResponse(mContext.getString(R.string.press_fingerprint));
        try {
            device.verifyAgainstUserId(userID, timeout);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void verifyAll(Map<String, Object> param, ActionCallback callback) {
        try {
            callback.sendResponse(mContext.getString(R.string.press_fingerprint));
            device.verifyAll(timeout);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getId(Map<String, Object> param, ActionCallback callback) {
        try {
            device.getId();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    Fingerprint fingerprint1;

    public void getFingerprint(Map<String, Object> param, ActionCallback callback) {
        callback.sendResponse(mContext.getString(R.string.press_fingerprint));
        try {
            fingerprint1 = device.getFingerprint(ISOFINGERPRINT_TYPE_DEFAULT);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void verifyAgainstFingerprint(Map<String, Object> param, ActionCallback callback) {
        callback.sendResponse(mContext.getString(R.string.press_fingerprint));
        try {
            device.verifyAgainstFingerprint(fingerprint1, timeout);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void storeFeature(Map<String, Object> param, ActionCallback callback) {
        try {
            device.storeFeature(userID, fingerprint1);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void match(Map<String, Object> param, ActionCallback callback) {
        callback.sendResponse(mContext.getString(R.string.press_fingerprint));
        try {
            Fingerprint fingerprint2 = device.getFingerprint(ISOFINGERPRINT_TYPE_ISO2005);
            device.match(fingerprint1, fingerprint2);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void compare(Map<String, Object> param, ActionCallback callback) {
        try {
            if (fingerprint1 == null) {
                sendFailedLog(mContext.getString(R.string.call_get_fingerprint));
            } else {
                callback.sendResponse(mContext.getString(R.string.press_fingerprint));
                Fingerprint fingerprint2 = device.getFingerprint(ISOFINGERPRINT_TYPE_ISO2005);
                int result = device.compare(fingerprint1.getFeature(), fingerprint2.getFeature());
                if (result == 0) {
                    sendSuccessLog(mContext.getString(R.string.operation_succeed) + result);
                } else {
                    sendFailedLog(mContext.getString(R.string.operation_failed) + result);
                }
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void identify(Map<String, Object> param, ActionCallback callback) {
        try {
            List fpList = new ArrayList();
            callback.sendResponse(mContext.getString(R.string.press_fingerprint) + "1");
            fpList.add(device.getFingerprint(ISOFINGERPRINT_TYPE_ISO2005).getFeature());
            callback.sendResponse(mContext.getString(R.string.press_fingerprint) + "2");
            fpList.add(device.getFingerprint(ISOFINGERPRINT_TYPE_ISO2005).getFeature());
            callback.sendResponse(mContext.getString(R.string.press_fingerprint) + "3");
            fpList.add(device.getFingerprint(ISOFINGERPRINT_TYPE_ISO2005).getFeature());
            callback.sendResponse(mContext.getString(R.string.press_fingerprint) + "4");
            fpList.add(device.getFingerprint(ISOFINGERPRINT_TYPE_ISO2005).getFeature());
            callback.sendResponse(mContext.getString(R.string.waiting));
            int[] matchResultIndex = device.identify(fingerprint1.getFeature(), fpList, 3);
            Log.d("matchResultIndex", "matchResultIndex = " + matchResultIndex.length);
            if (matchResultIndex.length > 0) {
                for (int index : matchResultIndex) {
                    sendSuccessLog(mContext.getString(R.string.operation_succeed) + " , No." + index);
                }
            } else {
                sendFailedLog(mContext.getString(R.string.not_get_match));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void formatFingerConvert(Map<String, Object> param, ActionCallback callback) {
        try {
            if (fingerprint1 == null) {
                sendFailedLog(mContext.getString(R.string.call_get_fingerprint));
            } else {
                callback.sendResponse(mContext.getString(R.string.press_fingerprint));
                Fingerprint fingerprint2 = device.getFingerprint(ISOFINGERPRINT_TYPE_ISO2005);
                byte[] ss = device.convertFormat(fingerprint2.getFeature(), 0, 1);
                int result = device.compare(fingerprint1.getFeature(), fingerprint2.getFeature());
                if (result == 0) {
                    sendSuccessLog(mContext.getString(R.string.operation_succeed) + result);
                } else {
                    sendFailedLog(mContext.getString(R.string.operation_failed) + result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }


    public void convertFormat(Map<String, Object> param, ActionCallback callback) {
        try {
            Fingerprint fingerprint2 = new Fingerprint();
            byte[] arryFea = new byte[8192];
            fingerprint2.setFeature(arryFea);
            device.convertFormat(fingerprint1, ISOFINGERPRINT_TYPE_ISO2005, fingerprint2, ISOFINGERPRINT_TYPE_DEFAULT);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void listAllFingersStatus(Map<String, Object> param, ActionCallback callback) {
        try {
            device.listAllFingersStatus();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void delFinger(Map<String, Object> param, ActionCallback callback) {
        try {
            device.delFinger(userID);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void delAllFingers(Map<String, Object> param, ActionCallback callback) {
        try {
            device.delAllFingers();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
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
