
package com.cloudpos.apidemo.util;

import android.text.TextUtils;

public class StringUtility
{
    static public boolean isEmpty(String strInput)
    {
        /*
         * if(strInput == null) return true; return strInput.length() == 0 ?
         * true : false;
         */
        return TextUtils.isEmpty(strInput);

    }

    static protected boolean CheckByte(byte byteIn)
    {
        // '0' - '9'
        if (byteIn <= 0x39 && byteIn >= 0x30)
            return true;
        // 'A' - 'F'
        if (byteIn <= 0x46 && byteIn >= 0x41)
            return true;
        // 'a' - 'f'
        if (byteIn <= 0x66 && byteIn >= 0x61)
            return true;
        return false;
    }

    static protected boolean CheckString(String strInput)
    {
        strInput = strInput.trim();
        if (strInput.length() != 2)
            return false;
        byte[] byteArry = strInput.getBytes();
        for (int i = 0; i < 2; i++)
        {
            if (!CheckByte(byteArry[i]))
                return false;
        }
        return true;
    }

    static protected byte StringToByte(String strInput)
    {
        byte[] byteArry = strInput.getBytes();
        for (int i = 0; i < 2; i++)
        {

            if (byteArry[i] <= 0x39 && byteArry[i] >= 0x30)
            {
                byteArry[i] -= 0x30;
            }
            else if (byteArry[i] <= 0x46 && byteArry[i] >= 0x41)
            {
                byteArry[i] -= 0x37;
            }
            else if (byteArry[i] <= 0x66 && byteArry[i] >= 0x61)
            {
                byteArry[i] -= 0x57;
            }
        }
        // Log.i("APP", String.format("byteArry[0] = 0x%X\n", byteArry[0]));
        // Log.i("APP", String.format("byteArry[1] = 0x%X\n", byteArry[1]));
        return (byte) ((byteArry[0] << 4) | (byteArry[1] & 0x0F));
    }

    static public int StringToByteArray(String strInput, byte[] arryByte)
    {
        strInput = strInput.trim();// 清除空白
        String[] arryString = strInput.split(" ");
        if (arryByte.length < arryString.length)
            return -1;
        for (int i = 0; i < arryString.length; i++)
        {
            if (!CheckString(arryString[i]))
                return -1;
            arryByte[i] = StringToByte(arryString[i]);
            // Log.i("APP", String.format("%02X", arryByte[i]));
        }

        return arryString.length;
    }

    static public String ByteArrayToString(byte[] arryByte, int nDataLength)
    {
        String strOut = new String();
        for (int i = 0; i < nDataLength; i++)
            strOut += String.format("%02X ", arryByte[i]);
        return strOut;
    }
    
    public static String byteArray2String(byte[] arryByte){
        String strOut = new String();
        for (int i = 0; i < arryByte.length; i++)
            strOut += String.format("%02X ", arryByte[i]);
        return strOut;
    }

    static public String ByteArrayToString(byte[] arryByte, int offset, int nDataLength)
    {
        String strOut = "";
        try {
            for(int i = offset; i < nDataLength; i++){
                strOut += String.format("%02X ", arryByte[i]);
            }
        } catch (Exception e) {
            strOut = "";
        }
        return strOut;
    }

    static public String[] spiltStrings(String str, String reg) {
        String[] arrayStr = str.split(reg);
        return arrayStr;
    }
}
