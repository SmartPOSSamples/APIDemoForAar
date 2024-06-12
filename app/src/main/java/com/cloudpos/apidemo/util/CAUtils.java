package com.cloudpos.apidemo.util;

import android.content.Context;
import android.util.Log;

import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PasswordFinder;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

public class CAUtils {
	public static final boolean IS_DEBUD = true;
	private static final String TAG ="CAUtils";
	private static final String DEFAULT_PEM_PWD = "cloudpos";
	private static final String PEM_FILE_NAME = "wizarpos.pem";
	private static final String CA_FILE_NAME = "wizarpos.cert";
	private static final String PUB_FILE_NAME = "wizarpos.pub";
	public static final int DEFAULT_VALIDATE = 10;
	public static final int DEFAULT_KEYSIZE = 2048;
	public static final String  DEBUG_CERT_PATH = "sdcard/tmp/";
//	keysize
	public KeyPair getTerminalKeyPair(Context context ){
		boolean exist = PreferenceHelper.getInstance(context).terminalCertExist();
		if (!exist) {
			createTerminalKeyPair(context);
		}

		KeyPair keyPair = null;
		try {
			FileInputStream in = context.openFileInput(PEM_FILE_NAME);
			PEMReader reader = new PEMReader(new InputStreamReader(in), new PasswordFinder() {
				@Override
				public char[] getPassword() {
					return DEFAULT_PEM_PWD.toCharArray();
				}
			});
			Object obj = reader.readObject();
			Log.d(TAG, "KeyPair = " + obj);
			if(obj instanceof KeyPair){
				keyPair = (KeyPair) obj;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{

		}
		return keyPair ;
	}

//	产生根证书
	private boolean createTerminalKeyPair(Context context){
		String cn = "testing_java";
		String dnStr = "CN=" + cn + ",OU=AndroidSoft,O=Wizarpos,C=CN,ST=Shanghai,L=Shanghai";
		String snStr = android.os.Build.SERIAL;
		X500Principal dn = new X500Principal(dnStr);
		boolean isSuccess = false;
		try {
			isSuccess = CAUtils.getInstance().createRootCert(dn, snStr, CAUtils.DEFAULT_VALIDATE, context);
		} catch (Exception e) {
			isSuccess = false;
			e.printStackTrace();
		}
		if(isSuccess){
			PreferenceHelper.getInstance(context).setValue(PreferenceHelper.KEY_TERMINAL_IS_EXIST, true);
		}
		return isSuccess;
	}

	public X509Certificate getTerminalX509Certificate(Context context ){
		X509Certificate cert = null;
		try {
			FileInputStream in = context.openFileInput(CA_FILE_NAME);
			Log.d(TAG, "in = " + in.available());

			CertificateFactory factory = CertificateFactory.getInstance("X509");
	        cert = (X509Certificate) factory.generateCertificate(in);
//
//			PEMReader reader = new PEMReader(new InputStreamReader(in));
//			Object obj = reader.readObject();
//
			Log.d(TAG, "X509Certificate = " + cert);
//			if(obj instanceof X509Certificate){
//				cert = (X509Certificate) obj;
//			}
//			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{

		}
		return cert ;
	}
	private CAUtils() {
		// TODO Auto-generated constructor stub
	}
	private static CAUtils instance;
	public static CAUtils getInstance(){
		if(instance == null){
			instance = new CAUtils();
		}
		return instance ;
	}
	public String generateCSR(PublicKey pub, PrivateKey pri, String subject)throws Exception {
		X509Name dn = new X509Name(subject);
		PKCS10CertificationRequest p10 = new PKCS10CertificationRequest("SHA1WithRSA", dn, pub , new DERSet(), pri);
		StringWriter sw = new StringWriter();
		PEMWriter pemw = new PEMWriter(sw);
		pemw.writeObject(p10);
		pemw.close();
		return sw.toString();
	}

	public boolean createRootCert(X500Principal dn, String snStr, int validate, Context context)throws Exception {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(DEFAULT_KEYSIZE);
		KeyPair kp = keyGen.generateKeyPair();
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		certGen.setSerialNumber(new BigInteger("1234567890123456"));
		certGen.setNotBefore(new Date(System.currentTimeMillis()));
		certGen.setNotAfter(new Date(System.currentTimeMillis() + validate * 24 * 60 * 60 * 1000L));
		certGen.setSubjectDN(dn);
		certGen.setPublicKey(kp.getPublic());
		certGen.setIssuerDN(dn);
		certGen.setSignatureAlgorithm("SHA1WithRSA");
		X509Certificate certificate = certGen.generate(kp.getPrivate());

		saveX509CertificateInFiles(certificate, CA_FILE_NAME , context);
		savePEMInFiles(kp.getPrivate(), DEFAULT_PEM_PWD, PEM_FILE_NAME ,context);


		if(IS_DEBUD){
			File file = new File(DEBUG_CERT_PATH);
			if(!file.exists()){
				file.mkdir();
			}
			saveX509Certificate(certificate,  DEBUG_CERT_PATH + CA_FILE_NAME);
			savePEM(kp.getPrivate(), DEFAULT_PEM_PWD, DEBUG_CERT_PATH + PEM_FILE_NAME);
		}
//		saveX509CertificateInFiles(certificate,   CA_FILE_NAME , context);
//		savePEMInFiles(kp.getPrivate(), DEFAULT_PEM_PWD,  PEM_FILE_NAME, context);
		return true;
//		return kp;
//		return certificate.getEncoded();
//		return certificate;
	}

	private void saveX509Certificate(X509Certificate certificate,String caCertPath) throws Exception {
		FileOutputStream stream = new FileOutputStream(caCertPath);
		stream.write(certificate.getEncoded());
		stream.close();
	}

	private void saveX509CertificateInFiles(X509Certificate certificate, String fileName,Context context) throws Exception {
		FileOutputStream stream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
		stream.write(certificate.getEncoded());
		stream.close();
	}

	private void savePEM(PrivateKey key, String pemPassword, String pemPath) throws Exception {
		PEMWriter writer = new PEMWriter(new FileWriter(pemPath));
		writer.writeObject(key, "DES-EDE3-CBC", pemPassword.toCharArray(),new SecureRandom());
		writer.close();
	}
	public byte[] x509ConvertPem( Context context , X509Certificate cert) throws Exception{
		byte [] pem = null;
		FileOutputStream stream = context.openFileOutput(PUB_FILE_NAME, Context.MODE_PRIVATE);
		PEMWriter writer = new PEMWriter(new OutputStreamWriter(stream));
		writer.writeObject(cert);
//		writer.writeObject(cert, "DES-EDE3-CBC", null, new SecureRandom());
		writer.close();

		PEMReader reader = new PEMReader(new InputStreamReader(context.openFileInput(PUB_FILE_NAME)));
		String certStr = "";
		while(reader.ready()){
			certStr = certStr +"\n"+ reader.readLine();
		}
		reader.close();
		pem = certStr.getBytes();
		return pem;
	}
	private void savePEMInFiles(PrivateKey key, String pemPassword, String pemName , Context context) throws Exception {
		FileOutputStream stream = context.openFileOutput(pemName, Context.MODE_PRIVATE);
		PEMWriter writer = new PEMWriter(new OutputStreamWriter(stream));
		writer.writeObject(key, "DES-EDE3-CBC", pemPassword.toCharArray(),new SecureRandom());
		writer.close();
	}
}
