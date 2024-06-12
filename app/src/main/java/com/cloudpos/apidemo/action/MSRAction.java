
package com.cloudpos.apidemo.action;

import java.util.Map;

import com.cloudpos.DeviceException;
import com.cloudpos.OperationListener;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.TimeConstants;
import com.cloudpos.msr.MSRDevice;
import com.cloudpos.msr.MSROperationResult;
import com.cloudpos.msr.MSRTrackData;
import com.cloudpos.apidemo.util.StringUtility;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

public class MSRAction extends ActionModel {

//    private MSRDevice device = new MSRDeviceImpl();
    private MSRDevice device = null;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device = (MSRDevice) POSTerminal.getInstance(mContext)
                    .getDevice("cloudpos.device.msr");
        }
    }

    public void open(Map<String, Object> param, ActionCallback callback) {
        try {
            device.open();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void listenForSwipe(Map<String, Object> param, ActionCallback callback) {
        try {
            OperationListener listener = new OperationListener() {

                @Override
                public void handleResult(OperationResult arg0) {
                    if (arg0.getResultCode() == OperationResult.SUCCESS) {
                        sendSuccessLog2(mContext.getString(R.string.find_card_succeed));
                        MSRTrackData data = ((MSROperationResult) arg0).getMSRTrackData();
                        int trackError = 0;
                        byte[] trackData = null;
                        for (int trackNo = 0; trackNo < 3; trackNo++) {
                            trackError = data.getTrackError(trackNo);
                            if (trackError == MSRTrackData.NO_ERROR) {
                                trackData = data.getTrackData(trackNo);
                                sendSuccessLog2(String.format("trackNO = %d, trackData = %s", trackNo,
                                        StringUtility.ByteArrayToString(trackData, trackData.length)));
                            } else {
                                sendFailedOnlyLog(String.format("trackNO = %d, trackError = %s", trackNo,
                                        trackError));
                            }
                        }
                    } else {
                        sendFailedOnlyLog(mContext.getString(R.string.find_card_failed));
                    }
                }
            };
            device.listenForSwipe(listener, TimeConstants.FOREVER);
            sendSuccessLog("");
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void waitForSwipe(Map<String, Object> param, ActionCallback callback) {
        try {
            sendSuccessLog("");
            OperationResult operationResult = device.waitForSwipe(TimeConstants.FOREVER);
            if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                sendSuccessLog2(mContext.getString(R.string.find_card_succeed));
                MSRTrackData data = ((MSROperationResult) operationResult).getMSRTrackData();
                int trackError = 0;
                byte[] trackData = null;
                for (int trackNo = 0; trackNo < 3; trackNo++) {
                    trackError = data.getTrackError(trackNo);
                    if (trackError == MSRTrackData.NO_ERROR) {
                        trackData = data.getTrackData(trackNo);
                        sendSuccessLog2(String.format("trackNO = %d, trackData = %s", trackNo,
                                StringUtility.ByteArrayToString(trackData, trackData.length)));
                    } else {
                        sendFailedOnlyLog(String.format("trackNO = %d, trackError = %s", trackNo,
                                trackError));
                    }
                }
            } else {
                sendFailedOnlyLog(mContext.getString(R.string.find_card_failed));
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
