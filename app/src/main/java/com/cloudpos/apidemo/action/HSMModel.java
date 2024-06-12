package com.cloudpos.apidemo.action;

import android.content.Context;
import android.util.Log;

import com.cloudpos.AlgorithmConstants;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.TerminalSpec;
import com.cloudpos.hsm.HSMDevice;
import com.cloudpos.apidemo.util.ByteConvertStringUtil;
import com.cloudpos.apidemo.util.CAController;
import com.cloudpos.apidemo.util.MessageUtil;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;

import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.openssl.PEMReader;

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

import static com.android.common.utils.special.Eth0Controller.APP_TAG;


public class HSMModel extends ActionModel {

    private HSMDevice device = null;
    public static final String ALIAS_PRIVATE_KEY = "hsm_pri";
    public static final String ALIAS_COMM_KEY = "testcase_comm_true";
    private byte[] CSR_buffer;
    String message = null;
    public byte[] certificate = null;
    private byte[] encryptBuffer;

    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device = (HSMDevice) POSTerminal.getInstance(mContext)
                    .getDevice("cloudpos.device.hsm");
        }
    }


    public void open(Map<String, Object> param, ActionCallback callback) {

        try {
            device.open();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
        }

    }
    //algorithm: - indicate the key type:SM4, DES,3DES or AES.
    public void isKeyExist(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean isSuccess = device.isKeyExist(5,AlgorithmConstants.ALG_3DES);
            if (isSuccess == true) {
                sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
            } else {
                sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
            }
        }catch (DeviceException e){
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }
    public void updateKey(Map<String, Object> param, ActionCallback callback){
        byte[] key = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        try {
            int result = device.updateKey(5,AlgorithmConstants.ALG_3DES,key);
            if(result >= 0){
                sendSuccessLog2(mContext.getString(R.string.hsm_succeed));
            }else {
                sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
            }

        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void keyEncryptAndDecry_v1(Map<String, Object> param, ActionCallback callback){
        byte[] byteData = new byte[]{
                (byte)0xab, (byte)0xab, 0x45, 0x45, 0x45, 0x45, 0x01, 0x01,
                (byte)0xab, (byte)0xab, 0x45, 0x45, 0x45, 0x45, 0x02, 0x02,
        };
        byte[] iv = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        try {
            sendSuccessLog2("byteData=\n"+ByteConvertStringUtil.bytesToHexString(byteData));
            byte[] encryData = device.keyEncrypt_v1(5,AlgorithmConstants.ALG_3DES,0,byteData,iv);
            if(encryData != null){
              sendSuccessLog2("keyEncrypt_v1=\n"+ByteConvertStringUtil.bytesToHexString(encryData));
              byte[] decryData = device.keyDecrypt_v1(5,AlgorithmConstants.ALG_3DES,0,encryData,iv);
              if(decryData != null){
                  sendSuccessLog2("keyDecrypt_v1=\n"+ByteConvertStringUtil.bytesToHexString(decryData));
              }else {
                  sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
              }
          }else {
              sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
          }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }
    public void keyEncryptAndDecry(Map<String, Object> param, ActionCallback callback){
        byte[] byteData = new byte[]{
                (byte)0xab, (byte)0xab, 0x45, 0x45, 0x45, 0x45, 0x01, 0x01,
                (byte)0xab, (byte)0xab, 0x45, 0x45, 0x45, 0x45, 0x02, 0x02,
        };
        byte[] iv = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        try {
            int result = device.keyEncrypt(5,AlgorithmConstants.ALG_3DES,0,byteData,iv);
            if(result >= 0){
                sendSuccessLog2("keyEncrypt："+mContext.getString(R.string.hsm_succeed)+ByteConvertStringUtil.bytesToHexString(byteData));
                int decryResult = device.keyDecrypt(5,AlgorithmConstants.ALG_3DES,0,byteData,iv);
                if(decryResult >= 0){
                    sendSuccessLog2("keyDecrypt："+mContext.getString(R.string.hsm_succeed)+ByteConvertStringUtil.bytesToHexString(byteData));
                }else {
                    sendFailedOnlyLog("keyDecrypt："+mContext.getString(R.string.hsm_falied));
                }
            }else {
                sendFailedOnlyLog("keyEncrypt："+mContext.getString(R.string.hsm_falied));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void isTampered(Map<String, Object> param, ActionCallback callback) {

        try {
            boolean isSuccess = device.isTampered();
            if (isSuccess == true) {

                sendSuccessLog2(mContext.getString(R.string.isTampered_succeed));

            } else {
                sendFailedOnlyLog(mContext.getString(R.string.isTampered_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.hsm_falied));
        }
    }

    public void generateKeyPair(Map<String, Object> param, ActionCallback callback) {

        try {
            device.generateKeyPair(ALIAS_PRIVATE_KEY, AlgorithmConstants.ALG_RSA, 2048);
            sendSuccessLog2(mContext.getString(R.string.generateKeyPair_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.generateKeyPair_failed));
        }
    }

    public void injectPublicKeyCertificate(Map<String, Object> param, ActionCallback callback) {

        try {
            byte[] bufCert = generatePublicKeyCertificate(mContext);

            boolean issuccess1 = device.injectPublicKeyCertificate(ALIAS_PRIVATE_KEY, ALIAS_PRIVATE_KEY, bufCert, device.CERT_FORMAT_PEM);
            if (issuccess1 == true) {
                sendSuccessLog2(mContext.getString(R.string.injectPublicKeyCertificate_succeed));
            } else {
                sendFailedOnlyLog(mContext.getString(R.string.injectPublicKeyCertificate_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.injectPublicKeyCertificate_failed));
        }
    }

    public void injectRootCertificate(Map<String, Object> param, ActionCallback callback) {

        try {
            InputStream in = mContext.getAssets().open("testcase_comm_true.crt");
            int length = in.available();
            byte[] bufCert = new byte[length];
            in.read(bufCert);
            Log.e(APP_TAG, "注入证书：文件名 :" + "testcase_comm_true.crt" + "  注入别名：" + "testcase_comm_true");
//            byte[] bufcert = generatePublicKeyCertificate(mContext);
            boolean issuccess2 = device.injectRootCertificate(device.CERT_TYPE_COMM_ROOT, ALIAS_COMM_KEY, bufCert, device.CERT_FORMAT_PEM);
            if (issuccess2 == true) {
                sendSuccessLog2(mContext.getString(R.string.injectRootCertificate_succeed));
            } else {
                sendFailedOnlyLog(mContext.getString(R.string.injectRootCertificate_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.injectRootCertificate_failed));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteCertificate(Map<String, Object> param, ActionCallback callback) {

        try {
            boolean isSuccess3 = device.deleteCertificate(HSMDevice.CERT_TYPE_PUBLIC_KEY, ALIAS_PRIVATE_KEY);
            boolean isSuccess4 = device.deleteCertificate(HSMDevice.CERT_TYPE_COMM_ROOT, ALIAS_COMM_KEY);
            if (isSuccess3 == true && isSuccess4 == true) {
                sendSuccessLog2(mContext.getString(R.string.deleteCertificate_succeed));
            } else {
                sendFailedOnlyLog(mContext.getString(R.string.deleteCertificate_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.deleteCertificate_failed));
        }
    }

    public void decrypt(Map<String, Object> param, ActionCallback callback) {
        sendSuccessLog2(mContext.getString(R.string.decrypt_data));
        try {
            CSR_buffer = device.generateCSR(ALIAS_PRIVATE_KEY, new X500Principal("CN=T1,OU=T2,O=T3,C=T4"));
            byte[] buffer = CSR_buffer;
            PKCS10CertificationRequest csr = MessageUtil.readPEMCertificateRequest(CSR_buffer);
            PublicKey publicKey = null;
            try {
                publicKey = csr.getPublicKey();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
            byte[] arryEncrypt = MessageUtil.encryptByKey("123456".getBytes(), publicKey);
            Log.e("TAG", arryEncrypt.length + "\n" + "*------*" + ByteConvertStringUtil.bytesToHexString(arryEncrypt));
            byte[] plain = device.decrypt(AlgorithmConstants.ALG_RSA, ALIAS_PRIVATE_KEY, arryEncrypt);
//            String string = ByteConvertStringUtil.bytesToHexString(plain);
            String string = new String(plain);
            sendSuccessLog2(mContext.getString(R.string.decrypt_succeed));
            sendSuccessLog2(string);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.decrypt_failed));
        }
    }

    public void deleteKeyPair(Map<String, Object> param, ActionCallback callback) {

        try {
            boolean issuccess4 = device.deleteKeyPair(ALIAS_PRIVATE_KEY);

            if (issuccess4 == true) {

                sendSuccessLog2(mContext.getString(R.string.deleteKeyPair_succeed));

            } else {
                sendFailedOnlyLog(mContext.getString(R.string.deleteKeyPair_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.deleteKeyPair_failed));
        }
    }


    public void encrypt(Map<String, Object> param, ActionCallback callback) {
        try {
            encryptBuffer = device.encrypt(AlgorithmConstants.ALG_RSA, ALIAS_PRIVATE_KEY, "123456".getBytes());
            String string = ByteConvertStringUtil.bytesToHexString(encryptBuffer);
            sendSuccessLog2(mContext.getString(R.string.encrypt_succeed) + "\n" + string);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.encrypt_failed));
        }
    }

    public void getEncryptedUniqueCode(Map<String, Object> param, ActionCallback callback) {

        TerminalSpec spec = POSTerminal.getInstance(mContext).getTerminalSpec();
        String uniqueCode = spec.getUniqueCode();
        try {
            String code = device.getEncryptedUniqueCode(uniqueCode, "123456");
            sendSuccessLog2(mContext.getString(R.string.getEncryptedUniqueCode_succeed));
            sendSuccessLog2(code);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.getEncryptedUniqueCode_failed));
        }

    }

    public void queryCertificates(Map<String, Object> param, ActionCallback callback) {

        try {
            final String[] alias = device.queryCertificates(HSMDevice.CERT_TYPE_PUBLIC_KEY);

            for (int i = 0; i < alias.length; i++) {

            }
            sendSuccessLog2(mContext.getString(R.string.queryCertificates_succeed) + "\n" + Arrays.toString(alias));
        } catch (DeviceException e) {
            sendFailedOnlyLog(mContext.getString(R.string.queryCertificates_failed));

        }

    }


    public void generateRandom(Map<String, Object> param, ActionCallback callback) {

        try {
            byte[] buff = device.generateRandom(2);
//            String string = new String(buff);
//            Log.d("TAG",string);
            sendSuccessLog2(mContext.getString(R.string.generateRandom_succeed));
            sendSuccessLog2(ByteConvertStringUtil.bytesToHexString(buff));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.generateRandom_failed));
        }
    }

    public void getFreeSpace(Map<String, Object> param, ActionCallback callback) {

        try {
            long freeSpace = device.getFreeSpace();
            sendSuccessLog2(mContext.getString(R.string.getFreeSpace_succeed) + freeSpace);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.getFreeSpace_failed));
        }
    }

    public void generateCSR(Map<String, Object> param, ActionCallback callback) {

        try {
            CSR_buffer = device.generateCSR(ALIAS_PRIVATE_KEY, new X500Principal("CN=T1,OU=T2,O=T3,C=T4"));
            String string = new String(CSR_buffer);
            sendSuccessLog2(mContext.getString(R.string.generateCSR_succeed) + "\n" + string);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.generateCSR_failed));
        }

    }

    public void getCertificate(Map<String, Object> param, ActionCallback callback) {

        try {
            certificate = device.getCertificate(HSMDevice.CERT_TYPE_PUBLIC_KEY, ALIAS_PRIVATE_KEY, HSMDevice.CERT_FORMAT_PEM);
            Log.e("TAG", certificate + "");
            if (certificate != null) {
                String strings = new String(certificate);
                sendSuccessLog2(mContext.getString(R.string.getCertificate_succeed) + "\n" + strings);
            } else {
                sendFailedOnlyLog(mContext.getString(R.string.getCertificate_failed));
            }

        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedOnlyLog(mContext.getString(R.string.getCertificate_failed));
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

    public void resetStatus(Map<String, Object> param, ActionCallback callback) {
        try {
            boolean bool = device.resetSensorStatus();
            if (bool) {
                sendSuccessLog(mContext.getString(R.string.operation_succeed));
            } else {
                sendFailedLog(mContext.getString(R.string.operation_failed));
            }
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

    public byte[] generatePublicKeyCertificate(Context mContext) {
        try {
            CSR_buffer = device.generateCSR(ALIAS_PRIVATE_KEY, new X500Principal("CN=T1,OU=T2,O=T3,C=T4"));
        } catch (DeviceException e) {
            e.printStackTrace();
        }
        byte[] publicCerts = null;
        byte[] cSRresult = new byte[2048];
        cSRresult = CSR_buffer;
        PEMReader reader = new PEMReader(new InputStreamReader(new ByteArrayInputStream(cSRresult)));
        try {
            Object obj = reader.readObject();
            if (obj instanceof PKCS10CertificationRequest) {
                PKCS10CertificationRequest csr = (PKCS10CertificationRequest) obj;
                publicCerts = CAController.getInstance().getPublicCert(mContext, csr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return publicCerts;
    }
//    public int injectServerCert(String fileName, String alias, Context host, int certType){
//        int result = -1;
//        try {
//            InputStream in = host.getAssets().open(fileName+".crt");
//            int length = in.available();
//            byte[] bufCert = new byte[length];
//            in.read(bufCert);
//            Log.e(APP_TAG, "inject cert：fileName :"+ fileName + "  alias：" + alias);
//            result = injectServerCert(alias, bufCert,certType);
//            Log.e(APP_TAG, "result = " + result);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }


}
