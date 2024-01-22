package com.cloudpos.apidemo.util;

import java.util.Scanner;

/**
 *  @author john
 *  Convert byte[] to hex string
 * */
public class ByteConvertStringUtil {
	/**
	 *  将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 *  @param src byte[] data  
	 *  @param hex string  
	 *  
	 * */
	public static String bytesToHexString(byte[] src){
		StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}
	
	public static String byteToHexString(byte src){
		StringBuilder stringBuilder = new StringBuilder("");
		int v = src & 0xFF;
		String hv = Integer.toHexString(v);  
	    return hv;  
	}
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("input:");
//		String input = scan.next();
		Byte input = scan.nextByte();
		System.out.println(":"+byteToHexString(input));
	}

}
