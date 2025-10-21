package com.cloudpos.apidemo.action;

import com.cloudpos.DeviceException;
import com.cloudpos.OperationListener;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.advance.ext.POSTerminalAdvance;
import com.cloudpos.advance.ext.system.security.IUserCredentials;
import com.cloudpos.apidemo.util.ByteConvertStringUtil;
import com.cloudpos.apidemo.util.CertHelper;
import com.cloudpos.apidemo.util.PreferenceHelper;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.extboard.ExtBoardDevice;
import com.cloudpos.extboard.ExtBoardOperationResult;
import com.cloudpos.mvc.base.ActionCallback;
import com.cloudpos.sdk.util.ByteUtil;
import com.cloudpos.sdk.util.HexUtil;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateEncodingException;
import java.util.Map;


public class IUserCredentialsAction extends ActionModel {

    private IUserCredentials device = null;
    private byte[] CSR_buffer;
    String message = null;
    public byte[] certificate = null;
    private byte[] encryptBuffer;

    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device = POSTerminalAdvance.getInstance().getSystemDevice().getUserCredentialsManager();
        }
    }

    public void open(Map<String, Object> param, ActionCallback callback) {
        try {
            device.open(mContext);
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

    public void clearCredentials(Map<String, Object> param, ActionCallback callback) {
        try {
            device.clearCredentials();
            sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void deleteCaCert(Map<String, Object> param, ActionCallback callback) {
        try {
            String alias = PreferenceHelper.getInstance(mContext).getStringValue("cert_alias");
            device.deleteCaCert(alias);
            sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void existCaCert(Map<String, Object> param, ActionCallback callback) {
        try {
            String alias = PreferenceHelper.getInstance(mContext).getStringValue("cert_alias");
            boolean exist = device.existCaCert(alias);
            sendSuccessLog2("existCaCert = " + exist);
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void existWifiKeyPair(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean exist = device.existKeyPair("keypair_wifi");
            sendSuccessLog2("existWifiKeyPair = " + exist);
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void existVPNKeyPair(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean exist = device.existKeyPair("keypair_vpn");
            sendSuccessLog2("existVPNKeyPair = " + exist);
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void getCaCert(Map<String, Object> param, ActionCallback callback) {
        try {
            String alias = PreferenceHelper.getInstance(mContext).getStringValue("cert_alias");
            byte[] data = device.getCaCert(alias);
            sendSuccessLog2("getCaCert = " + ByteConvertStringUtil.bytesToHexString(data));
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void installCaCert(Map<String, Object> param, ActionCallback callback) {
        try {
            InputStream in = mContext.getAssets().open("testcase_comm_true.crt");
            int length = in.available();
            byte[] bufCert = new byte[length];
            in.read(bufCert);
            String alias = device.installCaCert(bufCert);
            if(alias != null){
                PreferenceHelper.getInstance(mContext).setStringValue("cert_alias", alias);
                sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
            }else{
                sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
            }
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void installWifiKeyPair(Map<String, Object> param, ActionCallback callback) {
        try {

            try {
                if(device.installKeyPair("keypair_wifi", 0, CertHelper.getInstance().getPriKey().getEncoded(), CertHelper.getInstance().getCert(CertHelper.pubKey.getBytes()).getEncoded()))
                    sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
                else
                    sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
            } catch (CertificateEncodingException e) {
                throw new RuntimeException(e);
            }
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void installVPNKeyPair(Map<String, Object> param, ActionCallback callback) {
        try {

            try {
                if(device.installKeyPair("keypair_vpn", 1, CertHelper.getInstance().getPriKey().getEncoded(), CertHelper.getInstance().getCert(CertHelper.pubKey.getBytes()).getEncoded()))
                    sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
                else
                    sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
            } catch (CertificateEncodingException e) {
                throw new RuntimeException(e);
            }
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void removeWifiKeyPair(Map<String, Object> param, ActionCallback callback) {
        try {
            if(device.removeKeyPair("keypair_wifi"))
                sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
            else
                sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void removeVPNKeyPair(Map<String, Object> param, ActionCallback callback) {
        try {
            if(device.removeKeyPair("keypair_vpn"))
                sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
            else
                sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }
}
