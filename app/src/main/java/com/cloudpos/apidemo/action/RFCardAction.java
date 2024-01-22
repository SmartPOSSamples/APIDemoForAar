
package com.cloudpos.apidemo.action;

import java.util.Map;

import com.cloudpos.DeviceException;
import com.cloudpos.OperationListener;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.TimeConstants;
import com.cloudpos.apidemo.common.Common;
import com.cloudpos.card.ATR;
import com.cloudpos.card.CPUCard;
import com.cloudpos.card.Card;
import com.cloudpos.card.MifareCard;
import com.cloudpos.card.MifareUltralightCard;
import com.cloudpos.card.MoneyValue;
import com.cloudpos.mvc.impl.ActionCallbackImpl;
import com.cloudpos.rfcardreader.RFCardReaderDevice;
import com.cloudpos.rfcardreader.RFCardReaderOperationResult;
import com.cloudpos.apidemo.util.StringUtility;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

public class RFCardAction extends ActionModel {
    private RFCardReaderDevice device = null;
    /*
*                      0x0000 CONTACTLESS_CARD_TYPE_B_CPU 0x0100
*                      CONTACTLESS_CARD_TYPE_A_CLASSIC_MINI 0x0001
*                      CONTACTLESS_CARD_TYPE_A_CLASSIC_1K 0x0002
*                      CONTACTLESS_CARD_TYPE_A_CLASSIC_4K 0x0003
*                      CONTACTLESS_CARD_TYPE_A_UL_64 0x0004
     */
    private static final int CPU_CARD = 0;
    private static final int MIFARE_CARD_S50 = 6;
    private static final int MIFARE_CARD_S70 = 3;
    private static final int MIFARE_ULTRALIGHT_CARD = 4;

    Card rfCard;
    /*zh:需要根据实际情况调整索引.
     *en:The index needs to be adjusted according to the actual situation.
     * */
    // mifare card : 2-63,012;
    //ultralight card : 0,4-63
    int sectorIndex = 0;
    int blockIndex = 1;
    private int pinType_level3 = 2;
    int cardType = -1;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device = (RFCardReaderDevice) POSTerminal.getInstance(mContext)
                    .getDevice("cloudpos.device.rfcardreader");
        }
    }

    public void open(Map<String, Object> param, ActionCallback callback) {
        try {
            device.open();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void listenForCardPresent(Map<String, Object> param, ActionCallback callback) {


        try {
            OperationListener listener = new OperationListener() {

                @Override
                public void handleResult(OperationResult arg0) {
                    if (arg0.getResultCode() == OperationResult.SUCCESS) {
                        rfCard = ((RFCardReaderOperationResult) arg0).getCard();
                        try {
                            int[] cardTypeValue = device.getCardTypeValue();
                            cardType = cardTypeValue[0];
                            if (cardType == MIFARE_CARD_S50 || cardType == MIFARE_CARD_S70) {
                                sectorIndex = 3;
                                blockIndex = 0;//0,1,2
                            } else if (cardType == MIFARE_ULTRALIGHT_CARD) {
                                sectorIndex = 0;
                                blockIndex = 5;//4-63
                            }
                            sendSuccessLog2(mContext.getString(R.string.find_card_succeed) + ",cardType=" + cardType + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
                        } catch (DeviceException e) {
                            e.printStackTrace();
                        }
                    } else {
                        sendFailedOnlyLog(mContext.getString(R.string.find_card_failed));
                    }
                }
            };
            device.listenForCardPresent(listener, TimeConstants.FOREVER);
            sendSuccessLog("");
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void waitForCardPresent(Map<String, Object> param, ActionCallback callback) {
        try {
            sendSuccessLog("");
            OperationResult operationResult = device.waitForCardPresent(TimeConstants.FOREVER);
            if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                sendSuccessLog2(mContext.getString(R.string.find_card_succeed));
                rfCard = ((RFCardReaderOperationResult) operationResult).getCard();
            } else {
                sendFailedOnlyLog(mContext.getString(R.string.find_card_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void listenForCardAbsent(Map<String, Object> param, ActionCallback callback) {
        try {
            OperationListener listener = new OperationListener() {

                @Override
                public void handleResult(OperationResult arg0) {
                    if (arg0.getResultCode() == OperationResult.SUCCESS) {
                        sendSuccessLog2(mContext.getString(R.string.absent_card_succeed));
                        rfCard = null;
                    } else {
                        sendFailedOnlyLog(mContext.getString(R.string.absent_card_failed));
                    }
                }
            };
            device.listenForCardAbsent(listener, TimeConstants.FOREVER);
            sendSuccessLog("");
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void waitForCardAbsent(Map<String, Object> param, ActionCallback callback) {
        try {
            sendSuccessLog("");
            OperationResult operationResult = device.waitForCardAbsent(TimeConstants.FOREVER);
            if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                sendSuccessLog2(mContext.getString(R.string.absent_card_succeed));
                rfCard = null;
            } else {
                sendFailedOnlyLog(mContext.getString(R.string.absent_card_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void cancelRequest(Map<String, Object> param, ActionCallback callback) {
        try {
            device.cancelRequest();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void getMode(Map<String, Object> param, ActionCallback callback) {
        try {
            int mode = device.getMode();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Mode = " + mode);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void setSpeed(Map<String, Object> param, ActionCallback callback) {
        try {
            device.setSpeed(460800);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void getSpeed(Map<String, Object> param, ActionCallback callback) {
        try {
            int speed = device.getSpeed();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Speed = " + speed);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void getID(Map<String, Object> param, ActionCallback callback) {
        try {
            byte[] cardID = rfCard.getID();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Card ID = "
                    + StringUtility.byteArray2String(cardID));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void getProtocol(Map<String, Object> param, ActionCallback callback) {
        try {
            int protocol = rfCard.getProtocol();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Protocol = "
                    + protocol);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void getCardStatus(Map<String, Object> param, ActionCallback callback) {
        try {
            int cardStatus = rfCard.getCardStatus();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Card Status = "
                    + cardStatus);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void verifyKeyA(Map<String, Object> param, ActionCallback callback) {
        byte[] key = new byte[]{
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF
        };
        try {

            boolean verifyResult = ((MifareCard) rfCard).verifyKeyA(sectorIndex, key);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void verifyKeyB(Map<String, Object> param, ActionCallback callback) {
        byte[] key = new byte[]{
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF
        };
        try {

            boolean verifyResult = ((MifareCard) rfCard).verifyKeyB(sectorIndex, key);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void verify_level3(Map<String, Object> param, ActionCallback callback) {
        final byte[] arryKey = {
                (byte) 0x49, (byte) 0x45, (byte) 0x4D, (byte) 0x4B, (byte) 0x41, (byte) 0x45, (byte) 0x52, (byte) 0x42,
                (byte) 0x21, (byte) 0x4E, (byte) 0x41, (byte) 0x43, (byte) 0x55, (byte) 0x4F, (byte) 0x59, (byte) 0x46
        };
        try {
            boolean verifyLevel3Result = ((MifareUltralightCard) rfCard).verifyKey(arryKey);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void transmit_level3(Map<String, Object> param, ActionCallbackImpl callback) {
        final byte[] arryAPDU = new byte[]{
                (byte) 0x30, (byte) 0x00
        };
        try {
            byte[] result = null;
            if (rfCard instanceof CPUCard) {
                result = ((CPUCard) rfCard).transmit(arryAPDU, 0);
            } else if (rfCard instanceof MifareCard) {
                result = ((MifareCard) rfCard).transmit(arryAPDU, 0);
            } else if (rfCard instanceof MifareUltralightCard) {
                result = ((MifareUltralightCard) rfCard).transmit(arryAPDU, 0);
            } else {
                //result = (Real Card Type) rfCard.transmit(arryAPDU, 0);
            }
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " (" + sectorIndex
                    + ", " + blockIndex + ")transmit_level3: " + StringUtility.byteArray2String(result));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void sendControlCommand(Map<String, Object> param, ActionCallbackImpl callback) {
//        int cmdID = 0x80;
//        final byte[] command = new byte[]{
//                (byte) 0x01
//        };
//        try {
//            int result = device.sendControlCommand(cmdID,command);
//            sendSuccessLog(mContext.getString(R.string.operation_succeed) + result);
//        } catch (DeviceException e) {
//            e.printStackTrace();
//            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
//        }
    }

    public void readBlock(Map<String, Object> param, ActionCallback callback) {
        try {
            byte[] result = ((MifareCard) rfCard).readBlock(sectorIndex, blockIndex);
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " (" + sectorIndex
                    + ", " + blockIndex + ")Block data: " + StringUtility.byteArray2String(result));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }


    public void writeBlock(Map<String, Object> param, ActionCallback callback) {
        byte[] arryData = Common.createMasterKey(16);// 随机创造16个字节的数组
        try {
            ((MifareCard) rfCard).writeBlock(sectorIndex, blockIndex, arryData);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void readValue(Map<String, Object> param, ActionCallback callback) {
        try {
            MoneyValue value = ((MifareCard) rfCard).readValue(sectorIndex, blockIndex);
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " value = "
                    + value.getMoney() + " user data: "
                    + StringUtility.byteArray2String(value.getUserData()));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void writeValue(Map<String, Object> param, ActionCallback callback) {
        try {
            MoneyValue value = new MoneyValue(new byte[]{
                    (byte) 0x39
            }, 1024);
            ((MifareCard) rfCard).writeValue(sectorIndex, blockIndex, value);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void incrementValue(Map<String, Object> param, ActionCallback callback) {
        try {
            ((MifareCard) rfCard).increaseValue(sectorIndex, blockIndex, 10);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void decrementValue(Map<String, Object> param, ActionCallback callback) {
        try {
            ((MifareCard) rfCard).decreaseValue(sectorIndex, blockIndex, 10);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void read(Map<String, Object> param, ActionCallback callback) {
        try {
            byte[] result = ((MifareUltralightCard) rfCard).read(blockIndex);
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " (" + sectorIndex
                    + ", " + blockIndex + ")Block data: " + StringUtility.byteArray2String(result));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void write(Map<String, Object> param, ActionCallback callback) {
        byte[] arryData = Common.createMasterKey(4);// 随机创造4个字节的数组
        try {
            ((MifareUltralightCard) rfCard).write(blockIndex, arryData);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void connect(Map<String, Object> param, ActionCallback callback) {
        try {
            ATR atr = ((CPUCard) rfCard).connect();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " ATR: "
                    + StringUtility.byteArray2String(atr.getBytes()));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void transmit(Map<String, Object> param, ActionCallback callback) {
        final byte[] arryAPDU = new byte[]{
                (byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00,
                (byte) 0x0E, (byte) 0x32, (byte) 0x50, (byte) 0x41,
                (byte) 0x59, (byte) 0x2E, (byte) 0x53, (byte) 0x59,
                (byte) 0x53, (byte) 0x2E, (byte) 0x44, (byte) 0x44,
                (byte) 0x46, (byte) 0x30, (byte) 0x31
        };
        //byte[] FelicaArryAPDU1 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x06, (byte) 0x01, (byte) 0x0B, (byte) 0x00, (byte) 0x01, (byte) 0x80, (byte) 0x04};
        try {
            byte[] apduResponse = ((CPUCard) rfCard).transmit(arryAPDU);
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " APDUResponse: "
                    + StringUtility.byteArray2String(apduResponse));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void disconnect(Map<String, Object> param, ActionCallback callback) {
        try {
            sendNormalLog(mContext.getString(R.string.rfcard_remove_card));
            ((CPUCard) rfCard).disconnect();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }

    public void close(Map<String, Object> param, ActionCallback callback) {
        try {
            rfCard = null;
            device.close();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed) + ",sectorIndex=" + sectorIndex + ",blockIndex=" + blockIndex);
        }
    }
}
