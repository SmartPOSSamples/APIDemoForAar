package com.cloudpos.apidemo.action;

import com.cloudpos.DeviceException;
import com.cloudpos.OperationListener;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.mvc.common.Logger;
import com.cloudpos.mvc.impl.ActionCallbackImpl;
import com.cloudpos.sdk.common.SystemProperties;
import com.cloudpos.serialport.SerialPortDevice;
import com.cloudpos.serialport.SerialPortOperationResult;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

import java.util.Map;

public class SerialPortAction extends ActionModel {

    private SerialPortDevice device = null;

    private int timeout = 5000;
    private int baudrate = 38400;
    private String testString = "cloudpos";

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device = (SerialPortDevice) POSTerminal.getInstance(mContext).getDevice("cloudpos.device.serialport");
        }
    }


    public void open(Map<String, Object> param, ActionCallback callback) {
        try {
            /**
             *     int ID_USB_SLAVE_SERIAL = 0;
             *     int ID_USB_HOST_SERIAL = 1;
             *     int ID_SERIAL_EXT = 2;
             */
            device.open(SerialPortDevice.ID_USB_SLAVE_SERIAL);
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

    public void waitForRead(Map<String, Object> param, ActionCallbackImpl callback) {
        try {
            final byte[] arryData = new byte[testString.length()];
            SerialPortOperationResult serialPortOperationResult = device.waitForRead(arryData.length, timeout);
            if (serialPortOperationResult.getData() != null) {
                sendSuccessLog("Result = " + new String(serialPortOperationResult.getData()));
                sendSuccessLog(mContext.getString(R.string.port_waitforread_succeed));
            } else {
                sendFailedLog(mContext.getString(R.string.port_waitforread_failed));
            }

        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void listenForRead(Map<String, Object> param, ActionCallbackImpl callback) {
        final byte[] arryData = new byte[testString.length()];
        try {

            OperationListener listener = new OperationListener() {
                @Override
                public void handleResult(OperationResult arg0) {
                    Logger.debug("arg0 getResultCode = " + arg0.getResultCode() + "");
                    if (arg0.getResultCode() == OperationResult.SUCCESS) {
                        byte[] data = ((SerialPortOperationResult) arg0).getData();
                        sendSuccessLog2(mContext.getString(R.string.port_listenforread_succeed));
                        sendSuccessLog("\nResult = " + new String(data));
                    } else if (arg0.getResultCode() == OperationResult.ERR_TIMEOUT) {
                        byte[] data = ((SerialPortOperationResult) arg0).getData();
                        sendSuccessLog2(mContext.getString(R.string.port_listenforread_succeed));
                        sendSuccessLog("\nResult = " + new String(data));
                    } else {
                        sendFailedOnlyLog(mContext.getString(R.string.port_listenforread_failed));
                    }
                }
            };
            device.write(arryData, 0, arryData.length);
            device.listenForRead(arryData.length, listener, timeout);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void write(Map<String, Object> param, ActionCallbackImpl callback) {
        try {
            final byte[] arryData = testString.getBytes();
            final int length = 5;
            final int offset = 2;
            device.write(arryData, 0, length);
            sendSuccessLog2(mContext.getString(R.string.port_write_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    private enum Mode {SLAVE, HOST}

    private String getModelName(Mode mode) {
        //    	"USB_SLAVE_SERIAL" : slave mode,(USB)
//    	"USB_HOST_SERIAL" : host mode(OTG)
        String deviceName;
        String model = SystemProperties.getSystemPropertie("ro.wp.product.model").trim().replace(" ", "_");
        if (mode.equals(Mode.SLAVE)) {
            deviceName = "USB_SLAVE_SERIAL";
            if (model.equalsIgnoreCase("W1") || model.equalsIgnoreCase("W1V2")) {
                deviceName = "DB9";
            } else if (model.equalsIgnoreCase("Q1")) {
                deviceName = "WIZARHANDQ1";
            }
        } else {
            deviceName = "USB_SERIAL";
            if (model.equalsIgnoreCase("W1") || model.equalsIgnoreCase("W1V2")) {
                deviceName = "GS0_Q1";
            }
        }
        return deviceName;
    }
}
