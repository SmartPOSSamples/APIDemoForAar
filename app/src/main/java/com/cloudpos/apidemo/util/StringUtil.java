package com.cloudpos.apidemo.util;


public class StringUtil {

	public static String getFormatString(byte[] bytes) {
		String value = "";
		for (byte b : bytes) {
			value += String.format("%02X ", b);
		}
		return value;
	}

	public static String getFormatString(byte[] bytes, int length) {
		String value = "";
		for (int i = 0; i < length; i++) {
			value += String.format("%02X ", bytes[i]);
		}
		return value;
	}

	public static boolean isExist(String[] ss, String str) {
		String[] t = ss;
		for (int i = 0; i < t.length; i++) {
			if (str.equals(t[i].toString())) {
				return true;
			}
		}
		return false;
	}
}
