package com.cloudpos.apidemo.util;

import android.content.Context;
import android.util.Log;

import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

public class CAController {

	private static final String TAG = "CAController";
	private static CAController instance;
    static {
        if(Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) != null){
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        }
        Security.addProvider(new BouncyCastleProvider());
    }

    private CAController() {
	}
	public static CAController getInstance(){
		if(instance == null){
			instance = new CAController();
		}
		return instance;
	}


	public void injectPubCert(){

	}

	public byte[] getPublicCert(Context context , PKCS10CertificationRequest csr){
		byte [] pubCertData = null;
		KeyPair keyPair = CAUtils.getInstance().getTerminalKeyPair(context);
		X509Certificate rootCert = CAUtils.getInstance().getTerminalX509Certificate(context);
		Date beginDate = new Date();
        Date endDate = new Date(beginDate.getTime() + 8 * 356 * 24 * 60 * 60 * 1000L);

        try {
        	X509Certificate cert = issueX509V3Certificate(keyPair, rootCert, csr, beginDate, endDate);
        	if(cert != null){
        		pubCertData = CAUtils.getInstance().x509ConvertPem(context, cert);
        		Log.e(TAG, "成功获得公钥证书 : " + new String (pubCertData));
        	}
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return pubCertData;
	}

	public String getCSRFromKeyPair(Context context, String cn){
		KeyPair keyPair = CAUtils.getInstance().getTerminalKeyPair(context);
		Log.d(TAG, "keyPair " + keyPair);
		if(keyPair != null){
			String dnStr = "CN=" + cn + ",OU=AndroidSoft,O=Wizarpos,C=CN,ST=Shanghai,L=Shanghai";
			String csr = null;
			try {
				csr = CAUtils.getInstance().generateCSR(keyPair.getPublic(), keyPair.getPrivate(), dnStr);
				return csr;
			} catch (Exception e) {
				e.printStackTrace();
				csr = null;
			}
		}
		return null;
	}
	/**
     * 用公钥加密

     * 每次加密的字节数，不能超过密钥的长度值减去11
     *
     * @param data
     *            需加密数据的byte数据
     * @param pubKey
     *            公钥
     * @return 加密后的byte型数据
     */
    public byte[] encryptData(byte[] data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            // 编码前设定编码方式及密钥
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 传入编码数据并返回编码结果
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用私钥解密
     * @param encryptedData
     *            经过encryptedData()加密返回的byte数据
     * @param privateKey
     *            私钥
     * @return
     */
    public  byte[] decryptData(byte[] encryptedData, PrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encryptedData);
        } catch (Exception e){
            return null;
        }
    }

    /**
     * Issues certificate for partners, such as application providers.
     *
     * @param issuerName
     * @param subjectName
     * @param beginDate
     * @param endDate
     * @param privateKey
     * @param publicKey
     * @return
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws Exception
     */
    private X509Certificate issueX509V3Certificate(KeyPair kp, X509Certificate rootCert, PKCS10CertificationRequest csr, Date beginDate, Date endDate) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {

        PrivateKey privateKey = kp.getPrivate();
        PublicKey publicKey = csr.getPublicKey();
        X509Name subjectName = csr.getCertificationRequestInfo().getSubject();
        X509Certificate cert = null;
        X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
        v3CertGen.setSerialNumber(BigInteger.valueOf(new Date().getTime()));
        v3CertGen.setIssuerDN(rootCert.getIssuerX500Principal());
        String[] subjArray = subjectName.toString().replace("E=", "EMAILADDRESS=").split(",");
        String subjectNameString = "";
        for (int j = subjArray.length - 1; j > -1; j--) {
            if (j != 0) {
                subjectNameString += subjArray[j] + ",";
            } else {
                subjectNameString += subjArray[j];
            }
        }
        v3CertGen.setSubjectDN(new X500Principal(subjectNameString));
        v3CertGen.setNotBefore(beginDate);
        v3CertGen.setNotAfter(endDate);
        v3CertGen.setPublicKey(publicKey);
        v3CertGen.setSignatureAlgorithm(SIGNATURE_ALGORITHM_SHA256WITHRSA);
        try {
            cert = v3CertGen.generate(privateKey,   "BC");
        } catch (Exception e) {
            Log.e( TAG ,"Issue certificate error", e);
        }
        return cert;
    }

    public static final String SIGNATURE_ALGORITHM_SHA256WITHRSA = "SHA256withRSA";


}
