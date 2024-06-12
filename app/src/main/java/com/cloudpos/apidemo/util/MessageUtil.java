package com.cloudpos.apidemo.util;

import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.openssl.PEMReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;

public class MessageUtil {



	public static PKCS10CertificationRequest readPEMCertificateRequest(byte[] bytes) {
        // See:
        // http://www.java2s.com/Open-Source/Java-Document/Security/Bouncy-Castle/org/bouncycastle/jce/PKCS10CertificationRequest.java.htm
        // readObject() can read the certificate request; And endMarker used before is not the only validate endMarker;
        PEMReader reader = new PEMReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
        PKCS10CertificationRequest csr = null;
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Object obj = reader.readObject();
            csr = (PKCS10CertificationRequest) obj;
            reader.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }

        return csr;
    }

	/**
     * 消息加密
     * */
    public static byte[] encryptByKey(byte[] data, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
        	e.printStackTrace();
//            logger.error("encryptByKey failed", e);
            return null;
        }
    }

    /**
     * 消息解密
     * */
    public static byte[] decryptByKey(byte[] data, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
        	e.printStackTrace();
//            log.error("decryptByKey failed", e);
            return null;
        }
    }

}
