
package com.cloudpos.apidemo.action;

import java.util.Map;

import com.cloudpos.DeviceException;
import com.cloudpos.OperationListener;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.TimeConstants;
import com.cloudpos.card.ATR;
import com.cloudpos.card.CPUCard;
import com.cloudpos.card.Card;
import com.cloudpos.card.SLE4442Card;
import com.cloudpos.smartcardreader.SmartCardReaderDevice;
import com.cloudpos.smartcardreader.SmartCardReaderOperationResult;
import com.cloudpos.apidemo.common.Common;
import com.cloudpos.apidemo.util.StringUtility;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

/*
 *   IC Card
 * */
public class SmartCardAction1 extends ActionModel {

    private SmartCardReaderDevice device = null;
    private Card icCard;
    int area = SLE4442Card.MEMORY_CARD_AREA_MAIN;
    int address = 0;
    int length = 10;
    int logicalID = SmartCardReaderDevice.ID_SMARTCARD;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device = (SmartCardReaderDevice) POSTerminal.getInstance(mContext)
                    .getDevice("cloudpos.device.smartcardreader",logicalID);
        }
    }

    public void open(Map<String, Object> param, ActionCallback callback) {
        try {
//            device.open(logicalID);
            device.open(logicalID,0);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void listenForCardPresent(Map<String, Object> param, ActionCallback callback) {
        try {
            OperationListener listener = new OperationListener() {

                @Override
                public void handleResult(OperationResult arg0) {
                    if (arg0.getResultCode() == OperationResult.SUCCESS) {
                        sendSuccessLog2(mContext.getString(R.string.find_card_succeed));
                        icCard = ((SmartCardReaderOperationResult) arg0).getCard();
                    } else {
                        sendFailedOnlyLog(mContext.getString(R.string.find_card_failed));
                    }
                }
            };
            device.listenForCardPresent(listener, TimeConstants.FOREVER);
            sendSuccessLog("");
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void waitForCardPresent(Map<String, Object> param, ActionCallback callback) {
        try {
            sendSuccessLog("");
            OperationResult operationResult = device.waitForCardPresent(TimeConstants.FOREVER);
            if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                sendSuccessLog2(mContext.getString(R.string.find_card_succeed));
                icCard = ((SmartCardReaderOperationResult) operationResult).getCard();
            } else {
                sendFailedOnlyLog(mContext.getString(R.string.find_card_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void listenForCardAbsent(Map<String, Object> param, ActionCallback callback) {
        try {
            OperationListener listener = new OperationListener() {

                @Override
                public void handleResult(OperationResult arg0) {
                    if (arg0.getResultCode() == OperationResult.SUCCESS) {
                        sendSuccessLog2(mContext.getString(R.string.absent_card_succeed));
                        icCard = null;
                    } else {
                        sendFailedOnlyLog(mContext.getString(R.string.absent_card_failed));
                    }
                }
            };
            device.listenForCardAbsent(listener, TimeConstants.FOREVER);
            sendSuccessLog("");
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void waitForCardAbsent(Map<String, Object> param, ActionCallback callback) {
        try {
            sendSuccessLog("");
            OperationResult operationResult = device.waitForCardAbsent(TimeConstants.FOREVER);
            if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                sendSuccessLog2(mContext.getString(R.string.absent_card_succeed));
                icCard = null;
            } else {
                sendFailedOnlyLog(mContext.getString(R.string.absent_card_failed));
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

    public void getID(Map<String, Object> param, ActionCallback callback) {
        try {
            byte[] cardID = icCard.getID();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Card ID = "
                    + StringUtility.byteArray2String(cardID));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getProtocol(Map<String, Object> param, ActionCallback callback) {
        try {
            int protocol = icCard.getProtocol();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Protocol = "
                    + protocol);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getCardStatus(Map<String, Object> param, ActionCallback callback) {
        try {
            int cardStatus = icCard.getCardStatus();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Card Status = "
                    + cardStatus);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void verify(Map<String, Object> param, ActionCallback callback) {
        byte[] key = new byte[] {
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF
        };
        try {

            boolean verifyResult = ((SLE4442Card) icCard).verify(key);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void read(Map<String, Object> param, ActionCallback callback) {
        try {
            byte[] result = ((SLE4442Card) icCard).read(area, address, length);
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " (" + area
                    + ", " + address + ") memory data: " + StringUtility.byteArray2String(result));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void write(Map<String, Object> param, ActionCallback callback) {
        byte[] arryData = Common.createMasterKey(10);
        try {
            ((SLE4442Card) icCard).write(area, address, arryData);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void connect(Map<String, Object> param, ActionCallback callback) {
        try {
            ATR atr = ((CPUCard) icCard).connect();
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
        try {
            byte[] apduResponse = ((CPUCard) icCard).transmit(arryAPDU);
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
            ((CPUCard) icCard).disconnect();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void close(Map<String, Object> param, ActionCallback callback) {
        try {
            icCard = null;
            device.close();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

}
