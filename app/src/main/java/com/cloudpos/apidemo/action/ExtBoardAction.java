package com.cloudpos.apidemo.action;

import android.content.Context;
import android.util.Log;

import com.cloudpos.AlgorithmConstants;
import com.cloudpos.DeviceException;
import com.cloudpos.OperationListener;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.TerminalSpec;
import com.cloudpos.apidemo.util.ByteConvertStringUtil;
import com.cloudpos.apidemo.util.CAController;
import com.cloudpos.apidemo.util.KeyGenerator;
import com.cloudpos.apidemo.util.MessageUtil;
import com.cloudpos.apidemo.util.SM4Utils;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.extboard.ExtBoardDevice;
import com.cloudpos.extboard.ExtBoardOperationResult;
import com.cloudpos.hsm.HSMDevice;
import com.cloudpos.mvc.base.ActionCallback;

import org.bouncycastle.jce.PKCS10CertificationRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;

import javax.security.auth.x500.X500Principal;


public class ExtBoardAction extends ActionModel {

    private ExtBoardDevice device = null;
    private byte[] CSR_buffer;
    String message = null;
    public byte[] certificate = null;
    private byte[] encryptBuffer;

    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device = (ExtBoardDevice) POSTerminal.getInstance(mContext)
                    .getDevice("cloudpos.device.extboard");
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

    public void getBoardVersion(Map<String, Object> param, ActionCallback callback) {
        try {
            int version = device.getBoardVersion();
            sendSuccessLog2("BoardVersion = " + version);
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void listenForRead(Map<String, Object> param, ActionCallback callback) {
        try {
            device.listenForRead(100, new OperationListener() {
                @Override
                public void handleResult(OperationResult operationResult) {
                    sendSuccessLog2("resultCode = " + operationResult.getResultCode());
                }
            }, 30);

        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void readDIN(Map<String, Object> param, ActionCallback callback) {
        try {
            int din = device.readDIN(0);
            sendSuccessLog2("Din = " + din);
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void setLowPulseVoltage(Map<String, Object> param, ActionCallback callback) {
        try {
            device.setPulseVoltage(0);
            sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void setHighPulseVoltage(Map<String, Object> param, ActionCallback callback) {
        try {
            device.setPulseVoltage(1);
            sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void triggerRelay(Map<String, Object> param, ActionCallback callback) {
        try {
            device.triggerRelay(0, 1000, 1000, 5);
            sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void triggerPulse(Map<String, Object> param, ActionCallback callback) {
        try {
            device.triggerPulse(0, 0, 1000, 1000, 10);
            sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied) + " " + e.getMessage());
        }
    }

    public void waitForRead(Map<String, Object> param, ActionCallback callback) {
        try {
            ExtBoardOperationResult result = device.waitForRead(100, 30);
            sendSuccessLog2("resultCode = " + result.getResultCode());
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied) + " " + e.getMessage());
        }
    }

    public void write(Map<String, Object> param, ActionCallback callback) {
        try {
            byte[] data = {1,2,3,4,5,6,7,8};
            device.write(data, data.length);
            sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }
}
