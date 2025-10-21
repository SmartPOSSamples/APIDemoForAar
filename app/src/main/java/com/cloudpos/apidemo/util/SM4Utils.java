package com.cloudpos.apidemo.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class SM4Utils {

    private static final String ALGORITHM = "SM4";
    private static final String TRANSFORMATION_ECB = ALGORITHM + "/ECB/PKCS5Padding";
    private static final String TRANSFORMATION_CBC = ALGORITHM + "/CBC/PKCS5Padding";
    private static final String TRANSFORMATION_ECB_NoPADDING = ALGORITHM + "/ECB/NoPadding";
    private static final String TRANSFORMATION_CBC_NoPADDING = ALGORITHM + "/CBC/NoPadding";


    private static final String ALGORITHM_HMAC_SM4 = "HmacSHA256";
    private static final String PROVIDER_NAME = "BC";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 签名
     */
    public static byte[] sign(byte[] key, byte[] data) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM_HMAC_SM4, PROVIDER_NAME);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM_HMAC_SM4);
        mac.init(secretKeySpec);
        return mac.doFinal(data);
    }

    /**
     * 验证签名
     */
    public static boolean verify(byte[] key, byte[] data, byte[] signature) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM_HMAC_SM4, PROVIDER_NAME);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM_HMAC_SM4);
        mac.init(secretKeySpec);
        byte[] computedSignature = mac.doFinal(data);
        return Arrays.equals(computedSignature, signature);
    }

    /**
     * /CBC/PKCS7Padding 加密
     */
    public static byte[] encryptByCbcPkcs7Padding(byte[] key, byte[] data) throws Exception{
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_CBC , "BC");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        // CBC模式需要一个初始化向量
        byte[] iv = new byte[16]; // 随机生成或指定
        Arrays.fill(iv, (byte) 0x00); // 示例中填充为0
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(data);
    }

    /**
     * /CBC/PKCS7Padding 解密
     */
    public static byte[] decryptByCbcPkcs7Padding(byte[] key, byte[] data) throws Exception{
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_CBC , "BC");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        byte[] iv = new byte[16]; // 随机生成或指定
        Arrays.fill(iv, (byte) 0x00); // 示例中填充为0
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(data);
    }

    /**
     * /ECB/PKCS7Padding 加密
     */
    public static byte[] encryptByEcbPkcs7Padding(byte[] key, byte[] data) throws Exception{
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_ECB , "BC");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * /ECB/PKCS7Padding 解密
     */
    public static byte[] decryptByEcbPkcs7Padding(byte[] key, byte[] data) throws Exception{
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_ECB , "BC");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * /ECB/NoPadding 加密
     */
    public static byte[] encryptByEcbPkcs7PaddingNoPadding(byte[] key, byte[] data) throws Exception{
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_ECB_NoPADDING , "BC");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * /ECB/NoPadding 解密
     */
    public static byte[] decryptByEcbPkcs7PaddingNoPadding(byte[] key, byte[] data) throws Exception{
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_ECB_NoPADDING , "BC");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * /CBC/NoPadding 加密
     */
    public static byte[] encryptByCbcPkcs7PaddingNoPadding(byte[] key, byte[] data) throws Exception{
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_CBC_NoPADDING , "BC");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        byte[] iv = new byte[16];
        Arrays.fill(iv, (byte) 0x00);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(data);
    }

    /**
     * /CBC/NoPadding 解密
     */
    public static byte[] decryptByCbcPkcs7PaddingNoPadding(byte[] key, byte[] data) throws Exception{
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_CBC_NoPADDING , "BC");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        byte[] iv = new byte[16];
        Arrays.fill(iv, (byte) 0x00);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(data);
    }


}


