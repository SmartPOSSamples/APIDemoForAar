package com.cloudpos.apidemo.action;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.TerminalSpec;
import com.cloudpos.androidmvcmodel.common.Constants;
import com.cloudpos.antenna.AntennaDevice;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

import java.util.Map;

public class AntennaAction extends ActionModel{

    private AntennaDevice device;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if(device == null){
            device =
                    (AntennaDevice) POSTerminal.getInstance(mContext).getDevice("com.cloudpos.device.antenna");
        }
    }

    public void open(Map<String, Object> param, ActionCallback callback) {
        try {
            device.open();
            sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void close(Map<String, Object> param, ActionCallback callback) {
        try {
            device.close();
            sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void getAntennaMode(Map<String, Object> param, ActionCallback callback) {
        try {
            AntennaDevice.AntennaMode mode = device.getAntennaMode();
            switch (mode){
                case INTERNAL:
                    sendSuccessLog2(mContext.getString(R.string.current_antennamode) + "INTERNAL");
                    break;
                case EXTERNAL:
                    sendSuccessLog2(mContext.getString(R.string.current_antennamode) + "EXTERNAL");
                    break;
            }

        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void setAntennaMode2Internal(Map<String, Object> param, ActionCallback callback) {
        try {
            device.setAntennaMode(AntennaDevice.AntennaMode.INTERNAL);
            sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void setAntennaMode2External(Map<String, Object> param, ActionCallback callback) {
        try {
            device.setAntennaMode(AntennaDevice.AntennaMode.EXTERNAL);
            sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }
}
