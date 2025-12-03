
package com.cloudpos.apidemo.action;

import android.util.Log;

import java.util.Map;

import com.cloudpos.DeviceException;
import com.cloudpos.OperationListener;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.TimeConstants;
import com.cloudpos.androidmvcmodel.common.Constants;
import com.cloudpos.card.ATR;
import com.cloudpos.card.CPUCard;
import com.cloudpos.card.Card;
import com.cloudpos.card.SLE4442Card;
import com.cloudpos.smartcardreader.SmartCardReaderDevice;
import com.cloudpos.smartcardreader.SmartCardReaderOperationResult;
import com.cloudpos.apidemo.util.StringUtility;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

/*
 *   PSAM Card
 * */
public class SmartCardAction2 extends ActionModel {

    private SmartCardReaderDevice device1 =null;
    private SmartCardReaderDevice device2 =null;
    private SmartCardReaderDevice device3 =null;
    private SmartCardReaderDevice device4 =null;
    private SmartCardReaderDevice currentDevice =null;
    private Card psamCard;
    int area = SLE4442Card.MEMORY_CARD_AREA_MAIN;
    int address = 0;
    int length = 10;
    int logicalID = 2;
    int samCardID = 1;

    boolean isOpen;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device1 == null) {
            device1 = (SmartCardReaderDevice) POSTerminal.getInstance(mContext)
                    .getDevice("cloudpos.device.smartcardreader",1);
        }
        if (device2 == null) {
            device2 = (SmartCardReaderDevice) POSTerminal.getInstance(mContext)
                    .getDevice("cloudpos.device.smartcardreader",2);
        }
        if (device3 == null) {
            device3 = (SmartCardReaderDevice) POSTerminal.getInstance(mContext)
                    .getDevice("cloudpos.device.smartcardreader",3);
        }
        if (device4 == null) {
            device4 = (SmartCardReaderDevice) POSTerminal.getInstance(mContext)
                    .getDevice("cloudpos.device.smartcardreader",4);
        }
    }

    public void open(Map<String, Object> param, ActionCallback callback) {
        if(isOpen){
            sendFailedLog(mContext.getString(R.string.operation_failed));
            return;
        }
        if(param.get(Constants.LOGICID) != null) {
            try {
                samCardID = (int) param.get(Constants.LOGICID);
                Log.d("SmartCardAction2", "smartCardId = " + samCardID);
                switch (samCardID){
                    case 1:
                        currentDevice = device1;
                        break;
                    case 2:
                        currentDevice = device2;
                        break;
                    case 3:
                        currentDevice = device3;
                        break;
                    case 4:
                        currentDevice = device4;
                        break;
                    default:
                        break;
                }
                currentDevice.open(samCardID);
                isOpen = true;
                sendSuccessLog(mContext.getString(R.string.operation_succeed));
            } catch (DeviceException e) {
//                if(e.getCode() == DeviceException.ARGUMENT_EXCEPTION || e.getCode() == DeviceException.GENERAL_EXCEPTION)
//                    device = null;
                e.printStackTrace();
                sendFailedLog(mContext.getString(R.string.operation_failed));
            }
        }else{
            mCallback.sendResponse(Constants.HANDLER_OPEN_PSAMCARD_PORT, null);
        }
    }

    public void listenForCardPresent(Map<String, Object> param, ActionCallback callback) {
        try {
            OperationListener listener = new OperationListener() {

                @Override
                public void handleResult(OperationResult arg0) {
                    if (arg0.getResultCode() == OperationResult.SUCCESS) {
                        sendSuccessLog2(mContext.getString(R.string.find_card_succeed));
                        psamCard = ((SmartCardReaderOperationResult) arg0).getCard();
                    } else {
                        sendFailedOnlyLog(mContext.getString(R.string.find_card_failed));
                    }
                }
            };
            currentDevice.listenForCardPresent(listener, TimeConstants.FOREVER);
            sendSuccessLog("");
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void waitForCardPresent(Map<String, Object> param, ActionCallback callback) {
        try {
            sendSuccessLog("");
            OperationResult operationResult = currentDevice.waitForCardPresent(TimeConstants.FOREVER);
            if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                sendSuccessLog2(mContext.getString(R.string.find_card_succeed));
                psamCard = ((SmartCardReaderOperationResult) operationResult).getCard();
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
            currentDevice.cancelRequest();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getID(Map<String, Object> param, ActionCallback callback) {
        try {
            byte[] cardID = psamCard.getID();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Card ID = "
                    + StringUtility.byteArray2String(cardID));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getProtocol(Map<String, Object> param, ActionCallback callback) {
        try {
            int protocol = psamCard.getProtocol();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Protocol = "
                    + protocol);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getCardStatus(Map<String, Object> param, ActionCallback callback) {
        try {
            int cardStatus = psamCard.getCardStatus();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Card Status = "
                    + cardStatus);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void connect(Map<String, Object> param, ActionCallback callback) {
        try {
            ATR atr = ((CPUCard) psamCard).connect();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " ATR: "
                    + StringUtility.byteArray2String(atr.getBytes()));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }

    }

    public void transmit(Map<String, Object> param, ActionCallback callback) {
        byte[] arryAPDU = new byte[] {
                0x00, (byte) 0x84, 0x00, 0x00, 0x08
        };
        byte[] arryAPDU1 = new byte[] {
                0x00, (byte) 0x84, 0x00, 0x00, 0x08
        };
        try {
            byte[] apduResponse = ((CPUCard) psamCard).transmit(arryAPDU);
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " APDUResponse: "
                    + StringUtility.byteArray2String(apduResponse));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void disconnect(Map<String, Object> param, ActionCallback callback) {
        try {
            sendNormalLog(mContext.getString(R.string.rfcard_remove_card));
            ((CPUCard) psamCard).disconnect();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void close(Map<String, Object> param, ActionCallback callback) {
        try {
            psamCard = null;
            currentDevice.close();
            isOpen = false;
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

}
