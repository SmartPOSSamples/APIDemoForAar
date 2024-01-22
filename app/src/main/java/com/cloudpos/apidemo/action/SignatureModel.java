package com.cloudpos.apidemo.action;

import android.graphics.Bitmap;

import com.cloudpos.DeviceException;
import com.cloudpos.OperationListener;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.TimeConstants;
import com.cloudpos.signature.SignatureDevice;
import com.cloudpos.signature.SignatureOperationResult;
import com.cloudpos.apidemo.util.StringUtility;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

import java.util.Map;

public class SignatureModel extends ActionModel {


    private SignatureDevice device = null;


    @Override

    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device = (SignatureDevice) POSTerminal.getInstance(mContext).getDevice("cloudpos.device.signature");

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

    public void listenSignature(Map<String, Object> param, ActionCallback callback) {
        try {
            final OperationListener listener = new OperationListener() {
                @Override
                public void handleResult(OperationResult operationResult) {
                    if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                        sendSuccessLog2(mContext.getString(R.string.sign_succeed));
                        SignatureOperationResult signatureOperationResult = (SignatureOperationResult) operationResult;


                        int leg = signatureOperationResult.getSignatureLength();

                        byte[] Data = signatureOperationResult.getSignatureCompressData();

                        Bitmap bitmap = signatureOperationResult.getSignature();


//                        String str = StringUtility.ByteArrayToString(Data, Data.length);
//                        String string = String.format("leg = %d , Data = %s",leg, str);

                        sendSuccessLog2(String.format("leg = %d , Data = %s", leg,
                                StringUtility.ByteArrayToString(Data, Data.length)));


                    } else {
                        sendFailedOnlyLog(mContext.getString(R.string.sign_falied));
                    }
                }
            };

            device.listenSignature("sign", listener, TimeConstants.FOREVER);
            sendSuccessLog("");


        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }


    }

    public void waitSignature(Map<String, Object> param, ActionCallback callback) {

        try {
            sendSuccessLog("");

            SignatureOperationResult signatureOperationResult = device.waitSignature("sign", TimeConstants.FOREVER);

            if (signatureOperationResult.getResultCode() == SignatureOperationResult.SUCCESS) {

                sendSuccessLog2(mContext.getString(R.string.sign_succeed));

                int leg = signatureOperationResult.getSignatureLength();

                byte[] Data = signatureOperationResult.getSignatureCompressData();

                Bitmap bitmap = signatureOperationResult.getSignature();

                sendSuccessLog2(String.format("leg = %d , Data = %s", leg,
                        StringUtility.ByteArrayToString(Data, Data.length)));

            } else {
                sendFailedOnlyLog(mContext.getString(R.string.sign_falied));
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




