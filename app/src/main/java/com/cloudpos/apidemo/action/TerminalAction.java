package com.cloudpos.apidemo.action;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.TerminalSpec;
import com.cloudpos.androidmvcmodel.common.Constants;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.cashdrawer.CashDrawerDevice;
import com.cloudpos.mvc.base.ActionCallback;
import com.cloudpos.serialport.SerialPortDevice;

import java.util.Map;

public class TerminalAction extends ActionModel{

    private TerminalSpec device;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if(device == null){
            device =
                    (TerminalSpec) POSTerminal.getInstance(mContext).getTerminalSpec();
        }
    }

    public void getIMEI(Map<String, Object> param, ActionCallback callback) {
        if(param.get(Constants.SLOT_ID) != null) {
            int slotID = (int) param.get(Constants.SLOT_ID);
            String imei = device.getIMEI(slotID);
            sendSuccessLog(imei);
        }else{
            mCallback.sendResponse(Constants.HANDLER_CHECK_IMEI_SLOT, null);
        }
    }

    public void getSIMCardSN(Map<String, Object> param, ActionCallback callback) {
        if(param.get(Constants.SLOT_ID) != null) {
            int slotID = (int) param.get(Constants.SLOT_ID);
            String cardSN = device.getSIMCardSN(slotID);
            sendSuccessLog(cardSN);
        }else{
            mCallback.sendResponse(Constants.HANDLER_CHECK_IMEI_SLOT, null);
        }
    }

    public void getSN(Map<String, Object> param, ActionCallback callback) {
        if(device == null){
            device =
                    (TerminalSpec) POSTerminal.getInstance(mContext).getTerminalSpec();
            device.getSerialNumber();
        }

    }
}
