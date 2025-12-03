
package com.cloudpos.apidemo.action;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.mvc.common.Logger;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.mvc.base.ActionCallback;
import com.msprintsdk.PrintCmd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PrinterAction extends ActionModel {

    private PrinterDevice device = null;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device = (PrinterDevice) POSTerminal.getInstance(mContext)
                    .getDevice("cloudpos.device.printer");
        }
    }

    public void open(Map<String, Object> param, ActionCallback callback) {
        try {
            device.open();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void printText(Map<String, Object> param, ActionCallback callback) {
        try {
            device.printlnText(
                    "Demo receipts\n" +
                            "MERCHANT COPY\n" +
                            "\n" +
                            "MERCHANT NAME\n" +
                            "SHXXXXXXCo.,LTD.\n" +
                            "530310041315039\n" +
                            "TERMINAL NO\n" +
                            "50000045\n" +
                            "OPERATOR\n" +
                            "50000045\n" +
                            "\n" +
                            "CARD NO\n" +
                            "623020xxxxxx3994 I\n" +
                            "\n\n\n");
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void printTextForFormat(Map<String, Object> param, ActionCallback callback) {
        try {
            Format format = new Format();
            format.setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_LARGE);
            format.setParameter(Format.FORMAT_FONT_BOLD, Format.FORMAT_FONT_VAL_TRUE);
            device.printText(format, "This is printTextForFormat LARGE BOLD");
            device.printText("\n\n");
            device.resetFormat();
            format.setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_MEDIUM);
            format.setParameter(Format.FORMAT_FONT_BOLD, Format.FORMAT_FONT_VAL_TRUE);
            device.printText(format, "This is printTextForFormat MEDIUM BOLD");
            device.printText("\n\n");
            device.resetFormat();
            format.setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_CENTER);
            format.setParameter(Format.FORMAT_FONT_BOLD, Format.FORMAT_FONT_VAL_FALSE);
            device.printText(format, "This is printTextForFormat ALIGN_CENTER");
            device.printText("\n\n");
            device.resetFormat();
            device.printText(format, "This is resetFormat");
            device.printText("\n\n");
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }


    public void sendESCCommand(Map<String, Object> param, ActionCallback callback) {
        byte[] command = new byte[]{
                (byte) 0x1B, (byte) 0x45, (byte) 1    //font bold
//                (byte) 0x1B, (byte) 0x24, 10,1
        };
        try {
            device.sendESCCommand(new byte[]{27, 100, (byte)10});
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void saveBitmap(Bitmap bitmap, String name) {
        File f = new File(Environment.getExternalStorageDirectory() + "/Download/" + name + "-" + System.currentTimeMillis() + ".png");
        Log.d("printermodeldemo", "save img," + f.getPath());
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.d("printermodeldemo", "saved ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void queryStatus(Map<String, Object> param, ActionCallback callback) {
        try {
            int status = device.queryStatus();
            sendSuccessLog(mContext.getString(R.string.operation_succeed)
                    + " Status: "
                    + (status == PrinterDevice.STATUS_OUT_OF_PAPER ? "out of paper" : "paper exists"));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void cutPaper(Map<String, Object> param, ActionCallback callback) {
        try {
            device.cutPaper();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }


    private Bitmap createQRCode(String content, int widthPix, int heightPix) {
        try {
            if (content == null || "".equals(content)) {
                return null;
            }
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix,
                    heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];

            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     *base64 to bitmap
     * import android.util.Base64;
     * import android.graphics.Bitmap;
     * import android.graphics.BitmapFactory;
     */

    public static Bitmap base64ToBitmap(String base64String) {
        byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        bitmap = compressMatrix(bitmap);
        return bitmap;
    }

    private static Bitmap compressMatrix(Bitmap orgBitmap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        Matrix matrix = new Matrix();
        matrix.setScale(0.35f, 0.35f);  //缩放
        return Bitmap.createBitmap(orgBitmap, 0, 0, orgBitmap.getWidth(), orgBitmap.getHeight(), matrix, true);
    }

    public void printBitmap(Map<String, Object> param, ActionCallback callback) {
        Bitmap bitmapwrf = null;
        try {
            String ss = "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";
            bitmapwrf = base64ToBitmap(ss);
            Format format = new Format();
            format.setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_CENTER);
//            bitmapwrf = BitmapFactory.decodeStream(mContext.getResources().getAssets()
//                    .open("64.png"));
            sendSuccessLog(mContext.getString(R.string.operation_succeed)
                    + "\ngetWidth  = " + bitmapwrf.getWidth() + ", getHeight = " + bitmapwrf.getHeight());
//            Bitmap bitmap = BitmapEdgeHelper.removeBottomWhiteEdge(bitmapwrf);
//            saveBitmap(bitmap,"dasd");
//
//            Bitmap bitmap2 = BitmapEdgeHelper.removeBottomWhiteEdge(bitmapwrf,100);
//            saveBitmap(bitmap2,"dasd30");

            Bitmap bitmap = createQRCode("https://vernpos-onlineorder.vercel.app/store14?t_no=7&qr=eb8977fae3cbd78171af1236d19e77fa16cd9542796421b8342c668d547722", 300, 300);
            device.printBitmap(format, bitmap);
//            device.printBitmap(format, bitmap2);
        } catch (Exception e1) {
            e1.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }

//        byte[] imageDatabyte = {-1, -40, -1, -32, 0, 16, 74, 70, 73, 70, 0, 1, 2, 0, 0, 1, 0, 1, 0, 0, -1, -37, 0, 67, 0, 8, 6, 6, 7, 6, 5, 8, 7, 7, 7, 9, 9, 8, 10, 12, 20, 13, 12, 11, 11, 12, 25, 18, 19, 15, 20, 29, 26, 31, 30, 29, 26, 28, 28, 32, 36, 46, 39, 32, 34, 44, 35, 28, 28, 40, 55, 41, 44, 48, 49, 52, 52, 52, 31, 39, 57, 61, 56, 50, 60, 46, 51, 52, 50, -1, -37, 0, 67, 1, 9, 9, 9, 12, 11, 12, 24, 13, 13, 24, 50, 33, 28, 33, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, -1, -64, 0, 17, 8, 0, -56, 0, -96, 3, 1, 34, 0, 2, 17, 1, 3, 17, 1, -1, -60, 0, 31, 0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 16, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125, 1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, -127, -111, -95, 8, 35, 66, -79, -63, 21, 82, -47, -16, 36, 51, 98, 114, -126, 9, 10, 22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -60, 0, 31, 1, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 17, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119, 0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, -127, 8, 20, 66, -111, -95, -79, -63, 9, 35, 51, 82, -16, 21, 98, 114, -47, 10, 22, 36, 52, -31, 37, -15, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -126, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -30, -29, -28, -27, -26, -25, -24, -23, -22, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -38, 0, 12, 3, 1, 0, 2, 17, 3, 17, 0, 63, 0, -89, -58, 105, 113, -102, 66, 57, -91, -81, 43, 83, -88, 90, 63, 26, 90, 76, 12, 80, 49, 105, -64, -111, 77, -18, 40, -19, 64, 15, 20, -18, -44, -63, -33, -102, 112, 52, 5, -123, 30, -44, -93, -90, 13, 55, 35, 52, -66, -40, -95, -118, -61, -80, 51, 71, 97, -102, 51, -7, -47, -34, -106, -95, 97, -32, -46, -98, -97, 90, 104, -29, -16, -93, -75, 49, 88, 15, 94, -108, -46, 113, -33, -83, 56, -9, -90, -10, -30, -112, 1, -26, -114, -72, -17, 73, -37, -102, 81, -4, -5, 82, -36, 3, -116, 82, 16, 58, -9, -11, -23, -102, 95, 106, 66, 126, 83, 64, -118, -39, 25, -91, -17, -59, 55, -83, 0, -15, 84, -117, 31, -102, 94, 122, 83, 65, -91, -17, -118, 16, -61, 60, -46, -118, 76, -29, -38, -128, 113, 64, 15, -19, 78, -49, 111, -46, -101, 81, -53, 113, 20, 17, -105, -106, 69, -115, 71, 86, 102, -64, 20, 110, 4, -92, -5, -48, 27, -100, 10, -26, 47, 124, 101, 101, 25, 41, 108, -81, 49, -2, -10, 54, -113, -41, -102, -83, 109, -29, 53, 93, -34, 124, 71, -18, -4, -72, -11, -19, 90, 42, 82, -75, -20, 46, 100, 118, 121, 35, 6, -118, -29, -105, -58, -79, -103, 1, 123, 102, -57, 114, -83, 90, 86, -98, 45, -45, 110, 31, 99, 59, -62, 120, -63, -111, 120, 39, -45, -116, -46, 116, -28, -123, -52, -114, -121, 60, 81, -17, 76, 89, 81, -44, 50, 58, -78, -98, 65, 83, -111, 70, 106, 7, 97, -4, -26, -114, -44, -36, -28, -47, -11, -92, 33, 115, -57, 90, 76, -13, 72, 78, 105, 58, 10, 64, 41, 61, 105, 59, 30, -94, -114, 49, -17, 71, 110, 104, -47, 8, -81, 64, -12, -19, 72, 57, -92, 7, -15, -6, 85, -102, 18, 82, -9, -90, -10, -26, -116, -3, 105, 0, -76, 12, 119, 32, -127, -21, 73, -100, -10, -90, -53, 42, 65, 27, 73, 35, 109, 69, 27, -119, -12, 30, -76, -64, -83, -87, -22, -74, -6, 85, -81, -101, 59, 100, -97, -71, 24, 60, -79, -1, 0, 61, -21, -50, 117, 93, 102, -21, 84, -104, -68, -51, -124, 31, 117, 1, -31, 127, -49, -83, 26, -42, -90, -6, -99, -12, -109, 18, 68, 96, -31, 23, -48, 86, 91, 54, 78, 51, 93, -108, -87, -14, -85, -67, -52, 39, 59, -20, 72, -84, 51, -55, -19, 82, 39, -51, -110, 123, 10, -122, 52, 36, -16, 42, -12, 118, -28, 68, 73, -32, -102, -43, -78, 34, -101, 42, -18, 34, -109, 121, 39, -91, 72, -15, -112, 122, 31, -54, -94, 42, 67, 112, 15, -27, 64, 51, 99, 71, -41, -82, 52, -85, -128, 67, 23, -124, -3, -24, -55, -29, 30, -43, -24, -74, 23, -16, -22, 22, -53, 52, 14, 25, 79, -89, 111, 99, 94, 65, -56, 53, -73, -31, -19, 97, -76, -85, -48, -59, -119, -127, -8, -111, 127, -83, 101, 86, -110, -106, -85, 114, -95, 59, 104, -49, 79, -93, 52, -64, -5, -64, 96, 114, 15, 32, -114, -12, 87, 19, -71, -67, -121, -10, -93, -91, 39, 0, 123, -46, -44, -7, 18, 4, 115, 64, -12, -64, -4, -88, -19, 71, 108, -118, 108, 10, -125, -83, 40, -3, 104, -49, 90, 78, 51, -113, 74, 101, -113, -49, -75, 28, 127, -6, -88, -57, -25, 70, 0, -21, 77, -116, 58, 87, 59, -30, -37, -33, -77, -23, 98, 21, 63, 60, -51, -113, -64, 114, 127, -91, 116, 120, 3, 21, -62, -8, -34, 98, -38, -116, 48, -122, -49, -105, 22, 72, -12, 36, -97, -16, 21, -91, 53, 121, 34, 102, -19, 19, -108, 99, -109, -128, 42, -27, -67, -97, -104, 50, -44, -53, 56, 119, -79, 98, 51, 90, -47, 70, 84, -127, -38, -70, -27, 46, -120, -50, -100, 46, -18, -55, 32, -77, -115, 49, -14, -43, -77, 108, -116, -93, -113, -46, -110, 33, 86, 0, 59, 122, -42, 109, -99, 106, 41, 34, 15, -79, -57, -116, -19, -88, 39, -77, -116, -125, -123, 25, -6, 85, -4, 83, 95, -91, 43, -119, -59, 28, -19, -51, -96, 25, 42, 48, 106, -120, 5, 27, 6, -70, 57, -93, 12, 63, -6, -43, -115, 121, 6, -58, -56, -11, -83, 35, 46, -121, 29, 90, 105, 106, -113, 65, -16, -59, -31, -69, -47, -94, -55, -53, 66, 124, -78, 72, -12, -23, -6, 17, 91, 93, -21, -112, -16, 52, -60, -57, 115, 17, -24, 10, -80, -6, -13, -97, -27, 93, -122, 107, -114, -94, -76, -102, 52, -90, -17, 20, 20, -31, -38, -101, -33, 57, 24, -89, 3, -111, -59, 102, -12, 101, 48, -23, 65, 7, -74, 49, 70, 104, 60, -15, 65, 37, 76, -13, -38, -105, 28, 103, 35, -102, 111, 20, -93, -98, 106, -115, 7, 14, -108, -32, 121, -26, -102, 61, -23, 69, 12, 7, 28, -2, 21, -25, 30, 44, -53, 107, -77, 0, 73, -31, 64, -10, -7, 69, 122, 54, 43, -49, 117, -10, 75, -113, 17, -65, -106, 114, 3, 97, -66, -93, -125, -4, -85, 106, 63, 17, 53, 54, 43, 68, -111, -37, -58, 21, -120, 7, 21, 50, 92, -63, -3, -6, -85, 113, -124, 60, -116, -102, -84, 119, 52, -120, -71, 10, 88, -31, 75, 97, 87, -15, 39, -91, 116, 37, 113, 115, -72, -24, -115, -56, -18, 98, 56, -38, -64, -98, -43, 101, 100, -82, 98, 41, -37, 35, 35, -113, 81, 91, 118, -78, 25, 0, -2, -75, 50, -115, -118, -123, 89, 62, -123, -46, -2, -68, 10, 99, 93, 68, -65, 125, -44, 126, 53, 21, -45, 20, -113, 6, -79, 100, -108, 9, 51, -127, -63, -18, 105, 69, 92, -119, 98, 26, 118, -79, -80, -9, 16, 48, -30, 64, 106, -84, -15, -84, -79, 54, 58, -30, -85, -57, 42, 125, -46, -120, 72, -21, -118, -79, 18, 43, 124, -55, -111, -22, 42, -83, 98, 125, -89, 54, -115, 23, -68, 24, -52, -70, -124, -54, 59, -88, -32, -3, 107, -67, -21, -46, -72, -113, 9, 66, -85, 125, 114, -27, -62, -72, -7, 21, 113, -55, -25, 57, -3, 63, 90, -19, -108, -28, 103, 61, 125, -85, -98, -73, -60, 85, 53, 100, 5, 87, -106, -38, -71, 60, 19, -114, 77, 59, 62, -12, -45, 70, 120, -22, 43, 30, -91, 49, -44, 12, -19, -28, -26, -109, 35, 28, -46, 19, -57, 20, 8, -83, 78, 4, -45, 115, -49, 61, 104, 29, 106, -117, 31, 74, 41, -71, -26, -108, 116, -95, -95, -118, 122, -30, -68, -6, -19, 115, -82, -35, -109, -44, 72, -1, 0, -6, 22, 43, -48, 59, -41, 35, -82, -38, 24, 53, 67, 56, 95, -110, 101, -50, 125, -5, -1, 0, 67, -8, -42, -108, -102, 76, 77, 92, -51, 123, 127, 53, 9, -37, -109, 84, -26, -75, 50, 97, 93, 73, 3, -128, 43, 78, 39, -30, -90, 96, 8, -50, 5, 110, -115, 61, -102, -110, 50, -42, -44, 48, 95, -109, 110, -47, -127, 90, 54, 112, 109, 97, -23, 81, -69, 0, -40, -17, -23, 86, -83, -55, 39, -127, 77, -107, 24, 36, -20, 54, -18, 2, -22, 120, -50, 43, 38, 91, 29, -54, 84, -114, -89, 59, -79, -46, -74, -38, 80, -68, 53, 10, -118, -51, -110, 57, -88, 78, -58, 114, -89, 22, -52, -124, -79, 41, 17, -115, 84, 109, 98, 9, -9, -57, -42, -84, 36, 62, 82, 98, -75, 4, 75, -73, -38, -85, 92, 0, 7, 0, 83, 122, -109, -20, -93, 4, 51, 66, 14, -70, -62, 17, -9, 124, -58, -23, -12, -82, -28, 31, -108, 122, -30, -71, 79, 15, -37, -18, -71, -110, 114, 56, 67, -57, -44, -15, -4, -77, 93, 79, -13, -84, 42, -22, -55, 75, 65, -28, -109, -8, 117, -93, 52, -52, -15, 75, -98, 43, 32, 23, 60, 26, 81, -23, 77, 25, -95, -55, 39, 36, -11, -21, 64, -118, -64, -46, -2, 84, -33, -46, -105, 53, 69, -114, -49, 28, 83, -123, 70, 13, 56, 31, 90, 0, 127, -91, 97, -8, -126, -42, -30, -32, 68, -15, 38, 82, 37, 102, 115, -111, -57, 79, -58, -74, -14, 58, 103, -15, -88, -90, 80, -22, -56, 121, 12, 48, 126, -99, -23, -89, 109, 70, 112, -86, -40, 36, 85, -127, 46, 23, -102, -83, 112, -115, 5, -61, -93, 14, 85, -120, 52, -48, -7, 60, -12, -82, -83, -57, 25, 14, -106, 70, -114, 81, 38, -62, -36, 17, 78, -76, -44, -52, 103, -25, 66, 14, 122, 41, -90, -76, -56, 62, -5, 1, -8, -47, 28, -112, 22, -22, 51, -21, 64, -91, -27, 43, 22, 126, -40, -46, 72, 85, -83, -103, 65, -5, -83, -98, 65, -85, 81, -99, -72, -36, 114, 113, 85, 82, 88, -40, -115, -82, 51, -24, 106, 111, 51, 2, -95, -111, 27, 46, -73, 45, 22, -30, -87, 78, -39, 52, -26, -105, -27, 52, -106, -112, -3, -86, -10, 56, -117, -20, -36, 122, -6, 99, -102, 70, 50, -101, -108, -84, 111, 104, -47, -92, 118, -86, -23, -76, -4, -60, 49, 83, -112, 122, 114, 15, -27, 90, -29, -98, -43, 89, 98, -114, 32, 66, 70, -85, -71, -73, 49, 81, -115, -51, -22, 113, -34, -89, 29, 51, -40, -6, -42, 50, 119, 103, 66, 90, 14, -49, -75, 4, -30, -102, 127, 14, 125, -24, -50, -47, -98, -98, -107, 34, 100, -103, -25, 24, -26, -125, -127, -97, -16, -90, 116, 3, 63, -98, 113, 77, 102, -18, 7, 30, -44, -119, 34, 4, 122, -47, -6, 26, 64, 120, -96, -98, 125, 106, -117, 20, 30, 105, -32, -1, 0, -6, -86, 48, 121, -51, 25, -89, -44, 99, -53, 96, 117, -84, -51, 67, 86, -126, -62, 50, -14, 28, -5, 3, -42, -109, 80, -66, 72, 99, 98, 88, 42, -88, -7, -101, -46, -68, -6, -6, -15, -82, -18, -98, 67, -62, -25, 10, 61, 5, 107, 78, -97, 51, -44, -119, -53, -107, 23, -26, -43, 63, -76, 47, 102, -111, -43, 80, -79, -54, -127, -23, -2, 52, -69, -72, -84, 44, -112, 114, 14, 57, -30, -76, -83, -18, 60, -60, 27, -70, -9, -82, -103, 69, 37, -95, -108, 103, 125, 25, 101, 99, 64, -37, -118, -126, 79, 82, 121, -85, 11, -10, 108, 115, 10, 103, -42, -94, 64, 9, 28, -43, -107, -122, 34, 55, 48, 21, 13, -105, -49, 40, -83, 44, 30, 85, -65, -16, -57, 24, -56, -19, -42, -128, 21, 15, -54, -57, 30, -124, -26, -89, 16, -90, -36, -88, 3, 3, -75, 86, -108, -84, 103, -81, 53, 23, -71, -51, 58, -110, 123, -110, 25, 70, 57, 60, 86, 77, -27, -31, -110, 81, -27, -79, 1, 14, 65, 7, 28, -45, -18, -26, 102, -123, -74, -110, 5, 102, 41, -83, 97, 21, -72, -94, -17, -87, -42, -39, 120, -66, 72, -84, 66, 92, 68, 103, -72, 83, -128, -60, -29, 35, -44, -15, -55, -85, -48, 120, -58, -47, -25, 85, -106, 25, 35, -116, -127, -105, 4, 28, 30, -7, 30, -125, -41, -81, -75, 112, -64, -13, 75, -70, -121, 70, 15, -95, -78, -87, 35, -45, -96, -42, -12, -21, -128, 26, 59, -56, -70, -29, 12, 118, -110, 126, -121, 21, 123, 37, -80, 55, 100, 17, -38, -68, -116, -79, -87, -32, -68, -72, -74, -36, -48, 77, 36, 69, -121, 37, 24, -82, 127, 42, -55, -31, -41, 70, 87, -75, -18, -113, 87, 29, 61, 62, -126, -105, 39, 4, 14, 127, 26, -32, -84, -4, 91, -88, 64, -1, 0, -23, 5, 110, 35, -56, -56, 32, 41, 3, -40, -113, -21, -102, -21, -76, -3, 82, -41, 83, -124, -55, 111, 38, 89, 120, 101, 110, 25, 125, 50, 43, 25, -46, 113, -36, -91, 37, 34, 108, -15, -3, 40, -49, 76, -11, -90, -12, 25, -19, 84, -17, 47, -46, -44, 96, 124, -46, 17, -45, -45, -21, 82, -107, -51, 99, 23, 39, 100, 92, 121, 21, 6, 88, -127, -40, 123, -46, 58, 92, 72, 8, 76, 68, -72, -5, -51, -55, 63, -121, -8, -43, 77, 38, -29, -49, 46, -17, -121, -112, 12, -17, 56, -56, -10, 30, -99, -86, -27, -27, -48, -74, -75, 121, -100, 124, -79, -95, 114, 51, -116, -29, -74, 106, -7, 44, 84, -41, 35, -77, 56, -49, 19, -35, -89, -38, -66, -57, 1, 37, 35, -63, -112, -98, 119, 55, -1, 0, 91, -7, -25, -46, -71, -61, -125, -38, -91, -98, 71, -106, 102, -111, -50, -26, 98, 89, -113, -87, -88, 107, -74, 42, -56, -30, -108, -100, -99, -40, -58, 28, -102, 124, 47, -75, -7, -24, 105, 26, -104, 120, 52, -34, -88, -117, -22, 106, -57, 33, 7, -125, 86, 82, -23, -41, 28, 10, -52, -122, 66, 112, 77, 93, 81, 89, 52, 76, -20, -43, -53, 31, 104, -112, -116, 1, -118, -117, 99, 72, -3, -49, 52, -11, 66, -40, 29, -86, 87, 117, -123, 122, 115, -4, -22, 47, -87, -124, 19, -109, -44, -95, -88, -111, 28, 34, 49, -44, -100, -109, 89, -53, -48, 84, -9, -78, 25, 37, -26, -96, -19, 93, 17, 86, 71, 75, 86, -48, 119, 122, 76, -47, -98, 105, -76, -64, 113, 52, 103, -118, 105, -93, -75, 32, 36, -49, 21, 44, 55, 18, -37, -54, -78, 67, 35, 36, -117, -47, -108, -32, -118, -125, 52, -71, -30, -99, -124, 122, 61, -11, -32, -73, -113, 98, -1, 0, -84, 97, -63, -12, -82, 122, 71, 50, -56, 89, -104, 110, 111, 111, 90, -106, -18, -29, -49, -107, -97, -112, 9, -10, -23, 85, 55, 103, -111, -51, 114, -62, 41, 30, -11, 24, 40, -57, -52, -44, -47, 102, 11, 124, 23, -111, -69, -114, -67, -5, 14, 42, 111, 21, -36, -20, -45, -74, 12, 126, -15, -64, -28, -11, 29, 115, -6, 15, -50, -78, -83, -36, -84, -15, -107, -50, 67, 2, 61, -23, -98, 41, -69, -13, -25, -123, 113, -116, 41, 111, -49, -89, -14, -85, -75, -28, -114, 92, 102, -102, -100, -23, 57, 52, -108, -124, -13, 70, 107, 118, 121, -96, -35, 13, 33, 25, -91, -11, -92, 7, -116, 80, -47, 35, -31, -31, -74, -6, -42, -116, 68, -112, 5, 102, 41, -61, 3, -23, 91, 80, -89, -18, -124, -127, 126, 92, 2, 79, -90, 107, 57, -24, 37, 78, 83, -40, 12, -123, 22, -96, 59, -27, 124, -79, -6, 85, -125, 25, 126, 123, 83, -43, 85, 61, 9, -19, 80, -76, 58, 41, -48, -27, 49, 46, 7, -17, -104, 30, -58, -93, -23, -118, -106, -27, -125, 78, -25, 24, -26, -95, -18, 43, 101, -79, -108, -12, 108, 127, 122, 109, 45, 37, 50, 64, -47, -38, -125, 73, -38, -128, 23, 52, -71, -90, -26, -126, 105, -120, -22, 51, -13, 17, -55, -49, 74, 102, 8, 44, 6, 48, 57, -28, -46, -110, 73, -29, 62, -104, -4, 105, 62, -10, 112, 127, 12, 87, 50, 103, -48, -90, 55, -26, 83, -107, 39, 29, -120, 56, 53, -97, -86, 17, -25, -90, 27, 63, 39, 95, -60, -42, -122, -14, -89, 41, -58, 71, -89, 53, -99, -87, 38, 29, 72, 109, -61, -98, -43, 113, -36, -26, -59, -58, -12, -18, 103, -98, -76, -108, 54, 104, -83, 79, 36, 90, 67, -42, -118, 83, -46, -114, -126, 23, 21, -87, -89, -54, 12, 38, 60, -107, 101, -24, 65, -57, 31, -2, -70, -53, 21, 61, -77, -20, -103, 125, 9, -63, -87, 122, -94, -87, -69, 74, -26, -58, -42, 96, 51, 39, -2, 58, 51, 72, -64, 34, -109, -33, -71, 52, 6, -90, 77, -109, 25, 29, -49, 21, -119, -36, -37, -79, -117, 47, -33, 36, -102, 96, -22, 42, 123, -75, 9, 59, -86, -12, 24, -2, 85, 0, -22, 43, -95, 28, 50, -36, 83, 73, 65, 63, 54, 41, 40, -71, 34, -97, -83, 39, 106, 83, -46, -109, -75, 0, 37, 58, -101, 75, -98, 41, -79, 29, 34, 57, -50, 14, 65, -23, -8, 81, 38, -11, -35, -55, 43, -98, 64, -23, -57, 122, 8, 15, -56, -57, -65, -42, -100, -84, -85, 25, 92, -28, -100, 113, 92, -105, 61, -21, -111, 28, 50, -125, -98, -68, -43, 59, -17, -102, -36, 123, 26, -74, -5, 81, -127, -55, -38, 79, 35, -5, -66, -30, -85, -35, 0, 96, 126, 48, 8, -56, 53, 106, 70, 115, -107, -32, -47, -112, -35, 105, 5, 13, -42, -123, -83, -18, 120, -30, -46, -98, -108, -108, 82, -72, -125, -67, 56, 26, 74, 7, 20, 92, 73, -101, 81, 62, -24, -108, -9, 32, 82, 76, -28, 21, -64, 39, -98, -125, -87, -90, -64, -114, 116, -60, -99, 1, 10, -116, 81, -72, -4, 115, -6, -29, -14, -89, -39, 22, 118, -72, 102, 63, 117, 6, 48, 61, 77, 100, -12, -44, -19, -69, 112, 76, -55, -70, -1, 0, 94, -29, -98, -67, -22, 33, -44, 83, -26, 109, -45, 57, -9, -88, -3, -21, 100, -50, 54, 39, 87, 38, -106, -102, 58, -45, -88, -72, 92, 15, 74, 59, 82, 26, 95, -31, -94, -31, 113, 13, 29, -88, 52, 118, 52, -37, 17, -46, -110, 3, -100, 100, 125, 41, -20, 58, 29, -39, 29, 114, 5, 20, 87, 37, -113, 117, -94, 9, 121, 92, 103, -81, 60, -43, 6, 114, -124, -60, -1, 0, 116, -114, 40, -94, -86, 59, -100, -13, -47, 93, 25, -51, -42, -112, 117, -94, -118, -24, 91, 30, 99, 29, -21, 65, -94, -118, 75, 82, 46, 20, -90, -118, 41, -118, -25, 65, -94, -93, 93, 104, -9, -80, 109, -50, -62, 28, 96, 114, 115, -37, -1, 0, 29, 20, -70, 125, -69, -101, 123, -25, -5, -71, 64, 64, -56, 39, -41, -97, -62, -118, 43, 9, -18, -50, -104, 55, -54, -111, -49, 56, -7, -37, 62, -76, -34, -44, 81, 91, -93, 23, -72, -125, -83, 41, -94, -118, 4, 52, -11, -21, 78, -2, 26, 40, -91, -44, 4, 52, 118, -94, -118, -95, 31, -1, -39};
//        Bitmap bitmap = BitmapFactory.decodeByteArray(imageDatabyte, 0, imageDatabyte.length);
//        try {
//            Format format = new Format();
//            format.setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_CENTER);
//            device.printText("printBitmap end,this bitmap show align right,printBitmap also support other format param.\n\n\n");
//            Bitmap bitmap2 = PrinterFloydDitherInterface.floydSteinbergDithering(bitmap);
//            saveBitmap(bitmap2, "src-bmp");
//            Bitmap adjustBitmap = autoAdjustBitmap(bitmap);
//            device.printBitmap(format, bitmap);
//            device.printText("----------");
//            device.printBitmapAutoGrayscale(format, bitmap);
//            sendSuccessLog(mContext.getString(R.string.operation_succeed));
//        } catch (Exception e) {
//            e.printStackTrace();
//            sendFailedLog(mContext.getString(R.string.operation_failed));
//        }
    }

    public static Bitmap setBitmapSize(Context mContext, String id, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();//获取自定义参数对象
        opts.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(mContext.getResources().getAssets()
                    .open(id), null, opts);
            int outWidth = opts.outWidth;
            int outHeight = opts.outHeight;
            opts.inSampleSize = getSampleSize(outWidth, outHeight, w, h);
            opts.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(mContext.getResources().getAssets()
                    .open(id), null, opts);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int getSampleSize(int outWidth, int outHeight, int w, int h) {
        int sizer = 1;
        if (outWidth > w && outHeight > h) {
            sizer = 2;
            while (outWidth / sizer > w && outHeight / sizer > h) {
                sizer *= 2;
            }
        }

        sizer /= 2;
        return sizer;
    }


    private Bitmap autoAdjustBitmap(Bitmap bitmap, float contrast, float brightness) {
        Bitmap adjustBitmap = null;
        int darkBitmapDarkness = getDarkBitmapDarkness(bitmap);
        if (darkBitmapDarkness < 128) {
            if (darkBitmapDarkness <= 64) {
                adjustBitmap = changeBitmapContrastBrightness(bitmap, contrast, brightness);
            } else if (darkBitmapDarkness <= 80) {
                adjustBitmap = changeBitmapContrastBrightness(bitmap, contrast, brightness);
            } else if (darkBitmapDarkness <= 96) {
                adjustBitmap = changeBitmapContrastBrightness(bitmap, contrast, brightness);
            } else {
                adjustBitmap = changeBitmapContrastBrightness(bitmap, contrast, brightness);
            }
        } else {
            adjustBitmap = bitmap;
        }
//        Bitmap bitmap2 = PrinterFloydDitherInterface.floydSteinbergDithering(adjustBitmap);
        return adjustBitmap;
    }

    private Bitmap autoAdjustBitmap(Bitmap bitmap) {
        Bitmap adjustBitmap = null;
        int darkBitmapDarkness = getDarkBitmapDarkness(bitmap);
        if (darkBitmapDarkness < 128) {
            if (darkBitmapDarkness <= 64) {
                adjustBitmap = changeBitmapContrastBrightness(bitmap, 5f, 2f);
            } else if (darkBitmapDarkness <= 80) {
                adjustBitmap = changeBitmapContrastBrightness(bitmap, 4f, 2f);
            } else if (darkBitmapDarkness <= 96) {
                adjustBitmap = changeBitmapContrastBrightness(bitmap, 3f, 2f);
            } else {
                adjustBitmap = changeBitmapContrastBrightness(bitmap, 2f, 2f);
            }
        } else {
            adjustBitmap = bitmap;
        }
//        Bitmap bitmap2 = PrinterFloydDitherInterface.floydSteinbergDithering(adjustBitmap);
        return adjustBitmap;
    }


    public static String buf2StringCompact(byte[] buf) {
        int i, index;
        StringBuilder sBuf = new StringBuilder();
        sBuf.append("[");
        for (i = 0; i < buf.length; i++) {
            index = buf[i] < 0 ? buf[i] + 256 : buf[i];
            if (index < 16) {
                sBuf.append("0").append(Integer.toHexString(index));
            } else {
                sBuf.append(Integer.toHexString(index));
            }
            sBuf.append(" ");
        }
        String substring = sBuf.substring(0, sBuf.length() - 1);
        return substring + "]".toUpperCase();
    }

//    It feels like the serial port not writing the data out correctly.
//    I'm just doing the following. Open the port, write start session.

    private Bitmap adjustBrightness(Bitmap imgsrc) {
        int width = imgsrc.getWidth();
        int height = imgsrc.getHeight();
        int totalPixels = width * height;
        int[] intValues = new int[totalPixels];
//        imgsrc.getRGB(0, 0, width, height, intValues, 0, width);
        imgsrc.getPixels(intValues, 0, width, 0, 0, width, height);
        int pixelLuminanceLowerThanBound = 0;
        int pixelLuminanceHigherThanBound = 0;
        float luminanceSum = 0.f;
        for (int val : intValues) {
            int red = (val >> 16) & 0xFF;
            int green = ((val >> 8) & 0xFF);
            int blue = val & 0xFF;
            float luminance = (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255;
            if (luminance < 96) {
                pixelLuminanceLowerThanBound++;
            }
            if (luminance > 192) {
                pixelLuminanceHigherThanBound++;
            }
            luminanceSum += luminance;
        }
        float threshold = (totalPixels * 100);
        if (!(pixelLuminanceLowerThanBound > threshold) && !(pixelLuminanceHigherThanBound > threshold)) {
            return imgsrc;
        }

        float luminanceMean = luminanceSum / (totalPixels);
        float brightnessDiff = (float) (1) / 100 - luminanceMean;
        float expandFactor = (float) (Math.pow(1. + brightnessDiff, 2.5));
//        RescaleOp rescaleOp = new RescaleOp(expandFactor, 35, null);
//        rescaleOp.filter(imgsrc, imgsrc);
        imgsrc.setPixels(intValues, 0, width, 0, 0, width, height);
        return imgsrc;
    }


//    public static boolean isDarkBitamp(Bitmap bitmap){
//        boolean isDark = false;
//        try {
//            if (bitmap != null) {
//                isDark = getBitmapSamplings(bitmap);
//            }
//        }catch (Exception e){
//            Log.e("TAG","read wallpaper error");
//        }
//        return isDark;
//    }
//    public static boolean getBitmapSamplings(Bitmap bitmap){
//        Palette palette = Palette.from(bitmap)
//                .setRegion(0, 0, bitmap.getWidth(),bitmap.getHeight())
//                .clearFilters()
//                .generate();
//        return getBitmapPaletteDark(palette);
//    }
//    public static boolean getBitmapPaletteDark(Palette hotseatPalette) {
//        if (hotseatPalette != null && isSuperLight(hotseatPalette)) {
//            Log.d("TAG","updateHotseatPalette isSuperLight");
//            return false;
//        } else if (hotseatPalette != null && isSuperDark(hotseatPalette)) {
//            Log.d("TAG","updateHotseatPalette isSuperDark");
//            return true;
//        } else {
//            Log.d("TAG","updateHotseatPalette normal");
//            return true;
//        }
//    }
//    public static boolean isSuperLight(Palette p) {
//        return !isLegibleOnWallpaper(Color.WHITE, p.getSwatches());
//    }
//    public static boolean isSuperDark(Palette p) {
//        return !isLegibleOnWallpaper(Color.BLACK, p.getSwatches());
//    }

    public static int getDarkBitmapDarkness0(Bitmap bitmap) {
        double pixelsValue = -1;
        try {
            if (bitmap != null) {
                int x = bitmap.getWidth() / 2; //50
                int y = bitmap.getHeight() / 4; // 100
                for (int i = 0; i < y; i++) {
                    if (bitmap.isRecycled()) {
                        break;
                    }
                    pixelsValue += getDarkness(bitmap.getPixel(x, i));
                }
                pixelsValue /= y;
            }
        } catch (Exception e) {
            Log.e("TAG", "read wallpaper error");
        }
        return (int) pixelsValue;
    }

    public static int getDarkBitmapDarkness1(Bitmap bitmap) {
        double pixelsValue = -1;
        try {
            if (bitmap != null) {
                int w = bitmap.getWidth();
                int x = w / 2;
                int h = bitmap.getHeight();
                int y = h / 2;
                for (int i = 0; i < h; i += 2) {
                    if (bitmap.isRecycled()) {
                        break;
                    }
                    pixelsValue += getDarkness(bitmap.getPixel(x, i));
                }
                for (int i = 0; i < w; i += 2) {
                    if (bitmap.isRecycled()) {
                        break;
                    }
                    pixelsValue += getDarkness(bitmap.getPixel(i, y));
                }
                pixelsValue /= (x + y);
            }
        } catch (Exception e) {
            Log.e("TAG", "read bitmap error");
        }
        Logger.debug("pixelsValue avg =   " + pixelsValue);
        return (int) pixelsValue;
    }

    public static int getDarkBitmapDarkness(Bitmap bitmap) {
        double pixelsValue = -1;
        try {
            if (bitmap != null) {
                int w = bitmap.getWidth();
                int x1 = w / 3;
                int x2 = w / 3 * 2;
                int h = bitmap.getHeight();
                int y1 = h / 3;
                int y2 = h / 3 * 2;
                for (int i = 0; i < h; i += 2) {
                    if (bitmap.isRecycled()) {
                        break;
                    }
                    pixelsValue += getDarkness(bitmap.getPixel(x1, i));
                }
                for (int i = 0; i < w; i += 2) {
                    if (bitmap.isRecycled()) {
                        break;
                    }
                    pixelsValue += getDarkness(bitmap.getPixel(i, y1));
                }

                for (int i = 0; i < h; i += 2) {
                    if (bitmap.isRecycled()) {
                        break;
                    }
                    pixelsValue += getDarkness(bitmap.getPixel(x2, i));
                }
                for (int i = 0; i < w; i += 2) {
                    if (bitmap.isRecycled()) {
                        break;
                    }
                    pixelsValue += getDarkness(bitmap.getPixel(i, y2));
                }


                pixelsValue /= (x1 + y1 + x2 + y2);
            }
        } catch (Exception e) {
            Log.e("TAG", "read bitmap error");
        }
        Logger.debug("pixelsValue avg =   " + pixelsValue);
        return (int) pixelsValue;
    }

    private static double getDarkness(int pixelValue) {
        return Color.red(pixelValue) * 0.299 + Color.green(pixelValue) * 0.578 + Color.blue(pixelValue) * 0.114;
    }

    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        Logger.debug("changeBitmapContrastBrightness, contrast=" + contrast + ", brightness=" + brightness);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });
        Bitmap retBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(retBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
        return retBitmap;
    }

    public void printHtml(final Map<String, Object> param, final ActionCallback callback) {
        try {
            final String htmlContent = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "    <style type=\"text/css\">" +
                    "     * {" +
                    "        margin:0;" +
                    "        padding:0;" +
                    "     }" +
                    "    </style>" +
                    "</head>" +
                    "<body>" +
                    "Demo receipts<br />" +
                    "MERCHANT COPY<br />" +
                    "<hr/>" +
                    "MERCHANT NAME<br />" +
                    "SHXXXXXXCo.,LTD.<br />" +
                    "530310041315039<br />" +
                    "TERMINAL NO<br />" +
                    "50000045<br />" +
                    "OPERATOR<br />" +
                    "50000045<br />" +
                    "<hr />" +
                    "CARD NO<br />" +
                    "623020xxxxxx3994 I<br />" +
                    "ISSUER ACQUIRER<br />" +
                    "<br />" +
                    "TRANS TYPE<br />" +
                    "PAY SALE<br />" +
                    "PAY SALE<br />" +
                    "<hr/>" +
                    "DATE/TIME EXP DATE<br />" +
                    "2005/01/21 16:52:32 2099/12<br />" +
                    "REF NO BATCH NO<br />" +
                    "165232857468 000001<br />" +
                    "VOUCHER AUTH NO<br />" +
                    "000042<br />" +
                    "AMOUT:<br />" +
                    "RMB:0.01<br />" +
                    "<hr/>" +
                    "BEIZHU<br />" +
                    "SCN:01<br />" +
                    "UMPR NUM:4F682D56<br />" +
                    "TC:EF789E918A548668<br />" +
                    "TUR:008004E000<br />" +
                    "AID:A000000333010101<br />" +
                    "TSI:F800<br />" +
                    "ATC:0440<br />" +
                    "APPLAB:PBOC DEBIT<br />" +
                    "APPNAME:PBOC DEBIT<br />" +
                    "AIP:7C00<br />" +
                    "CUMR:020300<br />" +
                    "IAD:07010103602002010A01000000000005DD79CB<br />" +
                    "TermCap:EOE1C8<br />" +
                    "CARD HOLDER SIGNATURE<br />" +
                    "I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICE<br />" +
                    "I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICE<br />" +
                    "I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICE<br />" +
                    "<br />" +
                    "Demo receipts,do not sign!<br />" +
                    "<br />" +
                    "<br />" +
                    "</body>" +
                    "</html>";
            String htmlTestString = "<html>" +

                    "<body>" +

                    "<table width =100% border=0 cellspacing=0>\n" +

                    "<tr>" +
                    "<td colspan=\"3\" class=\"aligncenter1\"><b><font size =\"6\">ABCDPDCL<font></b></td>\n" +
                    "</tr>" +

                    "<tr>" +
                    "<td colspan=\"3\" class=\"aligncenter1\"><b><font size =\"6\">ELECTRICITY BILL<font></b></td>\n" +
                    "</tr>" +

                    "<tr>" +
                    "<td colspan=\"3\" class=\"aligncenter1\"><b><font size =\"6\">CUM NOTICE<font></b></td>\n" +
                    "</tr>" +

                    "</table>" +
                    "</body>" +
                    "</html>";
            try {
                //device.printHTML(mContext, htmlTestString);
                Bitmap bitmapReceipt ;
                Log.d("DPLog","printer before convertHTML2image calling");
                device.printHTML(htmlContent);
                Log.d("DPLog","printer after convertHTML2image calling");
                Log.d("DPLog","before printing");
//                device.printBitmap(bitmapReceipt);
                Log.d("DPLog","after printing");
                Log.d("DPLog","printer before closing");
                device.close();
                Log.d("DPLog","printer after closing");
                sendSuccessLog(mContext.getString(R.string.operation_succeed));
            } catch (Exception e) {
                sendFailedLog(mContext.getString(R.string.operation_failed));
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }


    public void printBarcode(Map<String, Object> param, ActionCallback callback) {

        try {
            Format format = new Format();
            format.setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_RIGHT);
            format.setParameter(Format.FORMAT_BARCODE_HRILOCATION, Format.FORMAT_BARCODE_HRILOCATION_UP);
            device.printBarcode(format, PrinterDevice.BARCODE_JAN13, "9787111640127");
            device.printlnText("\n\n\n");
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void printPDF(Map<String, Object> param, ActionCallback callback) {

        try {
            InputStream openinputs = mContext.getResources().getAssets().open("patym.pdf");
            device.printPDF(openinputs);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException | IOException e) {
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
}
