
package com.cloudpos.apidemo.action;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.mvc.common.Logger;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.cloudpos.sdk.printer.impl.PrinterCommand;
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
            device.sendESCCommand(PrinterCommand.getCmdEscDN(10));
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


    /**
     * 创建二维码
     *
     * @param content   content
     * @param widthPix  widthPix
     * @param heightPix heightPix
     * @return 二维码
     */
    private Bitmap createQRCode(String content, int widthPix, int heightPix) {
        try {
            if (content == null || "".equals(content)) {
                return null;
            }
            // 配置参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix,
                    heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
            //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
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
        return bitmap;
    }

    public void printBitmap(Map<String, Object> param, ActionCallback callback) {
        Bitmap bitmapwrf = null;
        try {
//            String ss = "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XrnqKlWqRDzgooMzem9vvvmmy60071WFwr9sANHnCrHZ3XYh3uSqdI82VPohE1rjPpS++MOJVLpH/dXyXqG8t2ETb+N46KGH2qBBg1wrfW20VKrHT18IapGvDbhOEdNphfqCueGGG9zG3k8XxNUPMZXu0UYzp9fVaU+Rprug6HQrAAAAAMVfpH2noqD9KDV40tnT/oZZftpXUqtunRmtYRRQad9JAeG5557r9r8UTIWW8FCopX4KqGvUqGFr1qxx+2FXXHGFNW3a1A2j8E3ToNbs3n6XWhvrDO2TTz7ZNRAbPny4C549arQVrkyIxqP9OK9kq5/GefPNN7uw7quvvnJBufb1dBBg6tSp7ixytbRXoKiAUiGhGpFpukXjVgtfTaP2Lb15oLPNTz31VHvppZds7ty5bh9T71Ohm87w9geNnnDzyqP3efvtt9uBBx5oJ5xwguuncDKnErWvvvqqa9HvUWjr7ctGsxz81DL4rbfecq2FNa0KKnVAQuEkiofitN57ImU4eh/RrOOhIm1PNL7//e9/bn0fNmyYmw96j48++qg7MKby0HqdZs2aue2NGq9qmnSAQeu2n6pHaN5FU17aj7LRyIk+px7/beyef/1JhHUp5qBfPyi0YfVOdYz29C2/SBtb9deG9swzz3RHKkNp49awYUP3BaEvA7Xo99OBB20k9cNKG3tthL/77ju3odSRUz9thPVevKA/p9dVbcRI0w0AAAAAxYXCPgVUCqsjtQhVK/P777/fLrroIteyVTu/aoGr0h9qjX766ae71uX+wE/7kWpgpbO01YBLgbcCKQXSCqdvvPFGF7jpemwqj3HllVe6shOis7UfeughN97evXtnhvQK+jT+SCIFc+KNQxTu6QxxvQ+F9ppONSzTdKufQrMnn3zSTbOCNo1L41awpvDeG+7vv/+2e++91wWgKjWrUE/9FfZp+tWALNxBnd0F/aKGahq35onmq6ZP++MXX3yxm2eajzpLXcPts88+me833PuPdjn4KWTVAQu1FFaIqbPkdV/75ygeitN674mU4US7jofa3fZE43zkkUfs+uuvd2czKGNS2eirr77atfJXsKqzEXTdR6273jQp7Nf80ji9UliaP3q+5qW2TSqZpefr9dWpfJEyOT8dcNH80fzW9qZPnz7uYIG2a4AX7Ouv/7Zf6P2SSNsCP+++/vpvx7OYUnptOLTR0EZXRzhzE/LvjloHaGOpU470peF1Knejo5M6wqvHFfJrw6Vp8dMGUqeOeXTRGB0x9vcTPU9Hbz3RvC4AAAAAlAQK7M4++2zX6lSNoRQqqRWvQidRGPDDDz+44Tp37py506v9KbW8/eWXX1zAFEqtXdXyXKUuFKaJ/uq++utxBd46K1utULU/5tHt6667zoVbKsEaC+07Kmy75JJLXKdA0U/lcRRc633ovX377beuZe9RRx2V+d5UtkRho8I87SN6FMz5W8wqVNM80fBdunTJ7K/pVzCpkDA3FK4rNFQ5Hs0ntRZWaOoF8pqPuh6dypXkJJrlEEqlexTq668awnn3UXwUt/U+kljX8Wjp4J6qTCg3W7x4sSsZdO6552YpD60DfyrJlRM9Xwf2NF8V8ouer4MdWv/CbUfUX9sf/dWZA7Vr1ybkh6PPvDqtQ16nnFOlo7xOB5f890tqFzofNJ/8882bl/Esphb9OpqqjVSk05iipY2S6pdpg+21oNdGUadK6gjuzz//7Org64CCTk/yToHSkV69tjZ6qhunumz6AtIGWa3yNZx+8OgLQ0dlvaOgOgCglhMaXkdpdSqY+ukLSUdNdaQ6mteNdDQYAAAAAOKFgqa8lFr16LkqZ6EA7/3333f7TypLof02tUBVEBV65rSCaO0cq8SGWrX6W/aqJerrr7/ugigvXBPtSOt11DJe5WnUYle3FSZGEkuLftW/Vgtk7XOKGnmpCzcOr2W9SmMoOPfzhtd70cGBSK17I/V/9tln3V/tm4bKqUW/xumdra6/0ZSo1WuHmxb1y2k5hJuG3CrJJWk1f7WOqHGh5ndBima9j6ZEWHFY7z3hMpxY1vFQkdZtUcaki+rqjASV6dH6rooTKlHkp3VX+dPuWvTrNXTGgZ67u/LS+YWy0fmrMNf7nOgz4++03n0ze6x9PuMLm71ijqVvz7ieKLIrk1zGWtdtZce0PdoOa93brYtatv4uHsUU9GtDr1YJeW3Jr41WaB0yHUnVhkofOtUR1IZPR0P1Wjr1MPQCuPPnz3enVXlHWnXEcsiQIe6LyP/jSIG9TgkbN26cG7cOLKjkjzbuatXvbaCjed1wXxIAAAAAEC/yo9RqOGpsdeutt7qATa3GFUxp/OGCQ10cU9dU005xaOD39ddfu1aqXuju17hxYxcmKmhTy1pdey2SWIL+SMHc7oL+cKViveFVM7xXr14Rxx2pf26Dfu2r6rkKUv/73//aF198EVWJWr12uGmJZjn4LyiK3FMLULVy1/qjsy/CXf8gP7Dehxcuw4llHQ8Vad0WheXKntQoVfNN9fV1O3RdUjal3CqnoD+a8tKIT4W13udEwb73V9Ok75Jnxj9nH0z/yPVH9E5o388uPHiI275p++qF/PEY9se09deHIz++MLRB1obN33k/djTTjjnmGPcD5ZVXXnFXDPeOCvupLM8DDzzggnkdHdaFiHRal1rf+38Y6TQntd7QhYNee+01N06dKqUjzn7RvK6m+/HHHyfkBwAAABB38qvU6ttvv+2CJzWA8mi/SmGcQjIFXAou9Lj2kfzlTxX2qZ67/obSPpyerzDL/xw12lIrYdHr6L5qwSuU8HgNs7755hsXWBQU772pVbxaRvvpbG/tR+p9FCZNy6RJk1yrZ+23RluiNpJolgPyh9ZBrYtaJ7V8tI7mN9b72BTEOq4SRLpGhsoaqeKEWnKLyvf4qeqEzjDKCeWlE1thrPfR8of8Y2Z+TcifS5pvmn+aj5qf3kGUeJQ/h3kLgI6K6MiXV8MtEm38Qk9X9OhorI6g6kwE/SBSaweNVxtXnRGgjbue7xft6wIAAABAPFHZDrXozWvjLJ3Z/Ntvv9no0aNdfW4Fewra1KJcLXZFtaK1T6WyqF6pkGnTprmzrtVqN9yFPHUxTtXC1zAKBfWcVatWudbqCq0VjGg/TBeR1D6c9/rafxszZoxrjKWGWHp/6rSPp2nQfp+CxPyi96Z5qQZgmj6V4dD0aDoPOuggV5++oOh96P2o0zSoUdw999zjLo7qtWDWfNx3333tmWeecdcf0HLRBVHVclnP89M8UnkUjUuBk+Z5NMsB+UufV62bWg75jfU+dnlZxzVdWpf0+iodrZb8OrtGTjrpJDd9mt6ePXu6xqa6roH3HM03HUzw0/B6v//++68bp4bV83VgT9OjCyBrnml+q2GqDhSo/j7iX0Gu9znRZ8b7q2Ba67HK9SD3NP80H/1Bv/c3niTFUrpHG7BoarrFC81wtfjXl4MugqIabNqAv/fee+4LTKdk6agxAAAAACQ6BVX5UWpV+1EKkxQcKyQWlYk4//zz3XXPPGpVrvBOQZSoxMTAgQNdKK3wSiGcv4SHKNB655137OOPP3Yt4zScAjGV0FBJENHrq2X6008/nfn6qlU9dOhQF3CrcZaGUQkMlcpQa1qV8ggteSO7K7WhUC1SGRC9Ny9I10693tuAAQPce/MahUUad6T+Cu1kd6V7Jk+eHOyTcQFe7cPqGnQKWv0lAqItUasg8oknnnD9VELEKxcSzXJA/tLnSGdm7L///sE++YP1Pvt679F6GKn8cjTreCit2xqfR+9D8+jII490FxL2P0+BvQ7U6eLGev86CNK/f393toACe2+aND+8ShcaTgcNNO/UP5qy1ohvBbXe50Tritfps6jP1smvDLD0HduCQyBWZUqVtlHnvO3WZa3r2iZ5XTwp1kG/aOOoLwJdlEhfFt7GUfX8GzZsGBwKAAAAABJbQeyvKSCQSMGXKNhTKzeV34h2h1f7aQqr9RyFZZF4ZUQincVd0LyAJJb3Vpg0H1VqJLfTF+1yQP4oiHWU9T5vCnodV9Cq96wSSLlZx/R8HdTQ83e3PBC/iiJL9UJ+HWhQg2etrye+emrwUeTW+4PecQfRdQaT8mVtM+Ltt0Hclu7JL9qQqia/Wk/oCOmoUaNc7X1CfgAAAADYPQVLOYVL2un1yqRGS/tpKpmaU/CloK+oQn7Re4/1vRUmzb+8TF+0ywElS0la7wt6Hdd4Nf7crmN6PuWlkVsK+0UH2ZB33nz05ms8KvZBPwAAAAAAAACUNF7LfuSd5mM8h/xC0A8AAAAAAAAAxUy8B9OJhqAfAAAAAAAAAFBoCiKUrlK+ijWsXrLLocdz2E/QDwAAAAAAAADIRsH+7ScMtwk3fOu60Vd+YlNvn2wvnvuc9e14XHAoxAOCfgAAAAAAAABAFmcecLoL9vt1Ot6SAv8mLZjsuqVrl1qXFp3tjhNvs4cHPuha+qPoEfQDAAAAAAAAADIp5L+mz1W2cctGu+WD4Xbg3T1t8EtDXHf0Q8fZ4JeH2Oxlc6x3m0PtxfOeDT4rfx3T4Wgb3v9mu+uk2+3aPlfb/i27Bh+JLPQ57Rq1Cz5S/BH0AwAAAAAAAAAclevxQv7zXhpiH035JPjILpPmT3ahv1r4t67f2ob2uiD4SN4du+8x9unlH9ndJ91pJ+zX347b91g744CB9tygp+2toW+4swlCRXrO6/95xf4X6Do0aR8csviKi6B/S1qabdiYypWgAQAAAAAAAKAIDe2dEdqPGP2AzV42290OZ8OWDXbz+8PdAQGF6vnhlK4n2/XHXmuNazSyxWuX2FsT37E7P7nHxvz5taWmpVqbBnvbHScMz9K63/+c+asW2FNjn7Eb37vF3v11lK3dvNaF/CNOuccO2uvA4DOKpyIL+levWWtPvzLSTjr3Iut/9oV2yuCL7djTz7f/DhtuP02aUiSh/5hvJ9j5V1xv02dG/gCXRBtTU234/Y+5efP9T78E+yLUosVL7PIb77BX334/2AcAAAAAAABILL33PtSF9+Fa8odasnaJjZ31ravTH66lfSya1WrmSgZVLlfZJs77xU596jS757P7XGB/9dvD7OYPhltKaoo1qNbADRf6nPFzJtiZz51tz377vH069TN3gGDwSxfYnOV/Wf1q9e3U/U92zymuCj3oV4D/3Y+/2JArb7DPvhprLZs1tXMHnmxXXHieHXJAV1u2YoXddv9jdtfDT9mmzZuDzyocer1/lyyzrVvTg30gcxcsst+m/uHmjZbd1nTmTzjbtm23JctX2PoNG4N9AAAAAAAAgMShwL5y+co2azct+UPNXpoxbNfmXdzf3FK9/wbV6ltK6hr7349vWGrapuAjGcbOHGe97jvCOt7axS594wrXz3vOyg0r7X8/ZX/OvJXz7IvpX9rW7em2T6N9inWr/uRhw4YND97O0eLFi61x48bBe7nzy5RpNuKJ56xendp2783X2il9+9g+e7eyPVs0s4O6dbFjj+hlq1LWuEA5Ze0623+/fa1Uqfw5HqEgf0vaVitbpowlJSUF++4y++95Nun36XbYwT2sYf26wb7hpadvcy3dS5cuHfX06SCHShRt37HdygSmIZT3+M7AP403Gjm9p3DSAsNbYNBoplvT9OmYcTZ/0T9Wp3YtW7ZipR3QpZNVq5rz1bS3bd/u3k9yqWRLTo7utWJ9/yr7tGnzFitbNvr3nxvRTNuadevt6+9/sGaNG1m3wOcWAAAAKEz5sb/mp9/AS5Yttw8+/8pef/dD+2PWHNsc+P1dv06dwP5M+N/E2kf6+vsf7ZvxP9qkqdNdo6E6tWpa5UoVg0OYbd++w2b9Pdc99u/SZRE7/davWaN68FkZ06PnfP71t/bjpN/c+LV/ov2UCuXLBYcKb01g33Lqn7Myx12pYoXAc8oHH82g8f81b4F99MXXNvG3qTZl+gxL37bN6tapFdifSQ4OFd7f8xfanMBzNe5VKSluuvWcjambbNqMWbZo8VL3mPZZqlapHHwWSpr8Xkclv8epvOCnwPr15vuf2KdfjXXrWOXKlax6taph97mjXW9C18FwnV6rapUqbv/eo1xB43z348/tlTdHue2QXlPbodKld79eemJdtzW8tn0fB4Yf+f7Hbj9/7foN1qB+3Ry3NR5vPr73yRf2+qiPA9uIBS63qV+3Tpb5GGnbgeKjINb7aO3YsSPwnbvd0tPTbdT06KtP1K5c27WQX7J2qX0cRYt+aVGnufVqc6ir168ut/rv19f2abyPzVk2xx77+gnXTxfT7dZyf2tVv1WWrmXdlrZhy0Y7vF1v95y/lv9tT37ztHtOqDLJZezAPQ+wquWr2LyV8+33RVODj0TvlPYnBbZPZd06qkxV63JBZpG5kZSSkhJ1jZyJEydat27dgvdip5bON9/7sPt7783XuLA/HP04vO2Bx+3PWX/ZTVdcZD32zzjt46PRY+yN9z52BwhaNmvi+vnpcZUDuuuGq6zzvvu4fvpS+OKb7+y1dz7IbGmtH1Znntzfjj7sELehVUB77W332j+BH19+Gs7/WtrY/zz5d1eaZcGif10/Ldh2e+9l5w082fbea48sC9ibntuGXW4rV63OMg36Qhl6zhnWvXNHdwaBvjw+DAzvQvgAHfjQ421b75ntQ7N58xZ777Mv3ZeON75y5craUb0OtgH9j7Navh/D8xb+Y9fdMcIO7t7VDjukhz310uu2IjAtkeZhKA17/Z0PuAMfPXvsbw89/ZKdc9pJdmq/Y4JDZKcDJi+88Y5bftqoiJ4/+IxTrUfX/bK9n6XLV9iLb7xrP0+a4paX6D3ovXjLyE9nFIwd/5ObZytWrnb9SgdWsu5dOtm5A0+yRg3qu36ex194zf24HnHrdVYj8OMklB4f//OvWeaJ12/4tZfZxMAyz2nZaPjPxoxzt/2GnnO69etzRPAeAAAAUHDyur/mp9/l2p95eeQo2xHYD6pdq4bbb1kb2Hdq3LC+3XTlxda8SaPg0BlmzPnb7nroSVem1U/7KhcFfj8fGdhf0e9nBfg6g/vXKdOCQ4SnRmCXnH+2u60GTk+//IaN+e4H99tf4f7mLVvc9Ci0v/Q/57gzxEP3NTyTp/5hN979YPCeZdlnFO1jPPvqm2F/07dv09quv+zCLAcdQvn3B5o0apC57+Htj3n7bewflGz5uY568nOci5cut3sefdqFz1qvFLory9A24LQTjrPTT+rr1j9PLOtN6DoYTmgGo/X7gadecA0yNT01qlcLrPdptjpljbXes6XdHNgOadu0O7Gu29r2jXzvY3vrg0+tVGB7om2NxqHX1DQMu+QC1yB1dxb8s9jufOgJVxVBB0gqVazoQnxlCmrgeuXQ86xihQpu2EjbDhQfBbHe50T5pbpt27YFvru3Wmpqqg0YeUbw0ehMvX2yK8nT56Hjg31279o+V7sa/YNfHuIu0ptbw/vf7C6kq7I9Q14ZmqVfKLXcv/vTe61Li/2yPSeU6vnfdeLtVqNSDXt5/Kv25NjwBwR25+3T37BKlSq5sF+NgPWbI9LvjqJSqC361ZpfrUEUEu+uxbNm2JGHHmRnnNzPGtavb6VKZcw0Bciqn394zwPdBj5UaIt8fahfe/t9e/nNUS6cPff0k+3Abp1t+YpVrhWIQuiO+7Rxw20JfFnovoJttVjv0rG9C+614dfGXBv7F994x5577S3XeuWEY4+y/scc4TbakwOvOfqb71wr971aNs9cyN70rFqd4q47cNyRvd1zNO2z/ppr3//8qzVt3NAdVf7+p19t4InHu2EqBz40v/8xw7WC0fga1q/nxic6KHFn4MfzN9//aB3a7e3m0aEHdndnGIyb8HPg9aYFfrC2tyqVK2UOr6PPen96zobAyt2gbh03H6JplT/1j5luXp3at4917dghsAynui/bHvvvF/asBB0dv+W+R9yX70nHHW0nHneUew8zA+/3y7Hj3XS13qNF5jyau2ChO5Dw75Kl1ufwnoHPxrGBZdLW5v/zb+BH/AT3471j+7aZZx/oB/4DT75g73z0ufvCHdD/WDu6d08rG1gmE36e5J7TomnjLGH/L4FpWr5ylR0R+EyFO/quxxf9uyTL50r91Fpo8u9/uCP5p/Q9xu1o6PGpf860MYF52rJ5U2scfJ3UTZtcq4MlgR9GjQNfzr0O6u4+PzpbRUfsAQAAgIKWn60Gf5v2pwug9m3XxgVPZ5zU1/2+137VV9+OD+wTrLNunTtmtjxVkH3/k8/bug0b7Or/nm/DLr3Azjqlvx24f2ebFvj9rFBf4Zj2QfScrp06uH2Fk4/vk63be8+Wbr9C+yBNGjV04//g8zGZ+5J3Xn+lnXT80W7YQw7YPzCtf7hriWkfLtx+ouisgr5HH25V3L7WzGxncash0f/e/dB6BsZ3+3VX2AWDBrrpU6t/7U8FdmCsU2C/JNIOffs2reyYww+1BYsWu30Yb9+jSpXKgf2VQ9w+0YSJkwL7VBn7mSiZ4rlFvw7APf78qzZ77jy76qLBLtBWfqH1Rg0dx/3ws3Vo0zpLg81Y1htvHQxd39X1O+pw+2fJUrd90H63sgblNArblWOoseFNV11sJxxzpJ147JHWJrAOfTnue0sJbIf276QqEJGDtljXbWU4z7wy0uUQ9906zOU0JwWG1/brh4mTXZajBpteUB9K8/HR515xVRE0H6++6Hzr1+dwt/3ctGmzyyxqBrZT3nYg0rYDxUcituiXri26WOv6rV3rfLXsz8nNfW9wJX+eGfecu0Bvbh3c6mB3sd3N6VvsnV9HuX4rN6y23xZOcWV71C1cvdDaNmwTGGazffXHGGtaq2m254Tas+6e1rttLyuVVMpGT//SZgVLDcUiEVr0509NnCj9MXO2VahQ3gXU0Yqm5Esk+rGp1thqca+W2b0POsB1tw27zNq3be3Cd4XW2kArMD+4e0YdKYXtFw463V07QF9G8tOvv9mHgR+X+wd+kD5z/x12ZmB4tU7XcE+NuN2aNWnkWuzPD7b099MZCg/efkOW59x3yzArF/hw3PPoM+5o+eN33+o2/HpcrVZuuPwi91wdQNDRY9EX3YeBH7c62KEvp9uC70nTfdOV/7Wbr77Elq5Yaa+8NSrzOR61JDnx2KPszWcfsUfuutmaBn8w745Oqf3x1ynuaHLb1nu5gxr6cps9d7479TSUjlY/+eL/XPit96P3qy9Ava9H7rjJ/WD/X2Ae/RV4v6Ll89jzr7nbIwJfoJovev99Dutpj955s/vR/vEX39gvgWUoev+6rx/I+sHxxL3D3Re93v9VgS/Ph+640cqXKxd4/++5o+15pTMnGtWvZw8Hpl2v4y2724ddYWVKl7YPPvvSHXgQLQcdDCgf+EJWuK/h1OmgBQAAAJBItB/w7Q8TrXLFinbB2QMzzxjWzqxCtYO7dbUZs/+2latSXH/Rvp5KY+iAgM4mVqtfDa+WuUPPPcOVwdTveFF/NQDSfkZoV7VyZZs09Y/A/kqDwH5cKze8GhGpAZLGpX0anSHgUcOp888c4ErkaJoiUWMtjT9cgKbf9GpkpTMU/nPWAKtbu5brr/1EBYJqCPZDYNr97zeUhlVIGVrSSPNB/f2li4B4pFbov/85044/6jDrdWD3zPBKn92BgfVaodavv+86CyfW9cZbB8N1azdscAG6XtcL0JXVKLPReE449sjMM/01XTqopwMEupagyv1EEus0KnPQa2q//rzTT8lSLaHVHi1cVqTcQ9fmi0RnRajR5xE9D8oyHzX9ykYa1Ktjf8z6yzXWlEjbDqCoeSV7ru1zlQvwd2dorwusQfUG9vHvn7qzAPJCJXsU4Kvmvnfh3D8X/+kurOt1jWs0srKly9qK9Stt0oJJmc+pX62endT5BPecUF1bdLaq5QPbm01rXYmf4qrQgn4d1dTGsHpgA1azWvhWFgVly5atmSVhRC3mFZLff+t1roVFTjTtX307wX0BqfyM98Xj0RHtQQNOdK3mx074yX05+PXs0S1bq+4WTZu4H8k6jeb4wJdF6OlmOqLcZd/2Nm/hIlu7dr3rt3J1SuDH8WTr0LZ14EvjwMwvDI+G149u1ZvTl49fu9Z7uSPDoc/ZHbVknzL9Txfye3Xk1AJl+7bt9uMv2U/D+fHX39wZEaedcHy296NT4fTFqlbzKcEQ3tsZ0JflHs2buX4e/XjXPFXAviHwo11HIfVFr/mrVkQDA6/hP2VQ9MWrFj46qKGj8Pnh6MN6ZvtRrtfp1L6dLQz8ENrdj30AAAAgEanRkOppN2/SOPC7PqPhk0cNserVrW1pW7e6zqPa29UC+1bhGrq0bNbUmjdt7MIvrxxmJNoHmTRlmmv0o/BLtM+kM5PVWt8f8nvUX7/Zt2/PCM5ipd/0+m2vs7lr1cy6H6NwTiVCl69cHdifXR7sCxQ/69dvcI3d1Go/NDdQjqODc6mbMhq6SX6tN8pPdEZOtapVrVOHdsG+GdUJlAFo/z+0nK+0abWHrQtM8+KlWbMPv9xMowL+Fx6+x1UKCFWxQtbreoSjRpXPPniXnX/mqdnmY5myZaK+HiFQ1D6a8omNm/Wta9X/yMAHI4b9Cvkv7DXE3X567LPub168/cu7NnXRNKtUrpKd1eNM67X3ocFHMlx82EV24F4HugvrfjvrO1e+R+H/tH+mW+Vyle3MHmdYlxYZJeA9p3c/zfrv18+15v9+9gR34KC4KtQW/VKqVHLUF0zJK9V363P4oe6UKdXgVz1HfRGId9Q0NCwOZ/36ja60jE639I4Ah9KXj0rJ6EvE/4NX9mjeNHhrF/1AVukXCf3xLPrSUQt6fZFu3JRxtWhdnDhl7Vpr3LBB4DUCP74DX3r+bv3Gja5eplrK64e5n/pH86XkN2P2X66+pn5ke0eXvR/pOpXXm5eiVj8qf6ODIWp9E06bVnu6Vu76MpWFgeFFX9DhNGpQzx0c0LUH1Hpg+arV7mCHDlp4P/pD7RP4Atdy18Vs8krjCXdRZs0LPbZdp0AFOgAAAKA4Uav3u268ynWhLeAVyqUE9hGSA7/P1YnCe5XK1AU7q4b5na59G5WqWLxsuW3asiXYNzuN+8tvx7vbamnr0b5bi2ZNXGMm/z6IRw2I1OhK+ym5sWbdOlsX2IcK99tf6gQbMWlfDyiudPb+F2+/HLb+vHIJrWP+/CS/1hut1+PG/2QHdO2UWVEhGirvk1w62V3zL5JYp1HBvA5oaH8/NKTXWUO//DbNbYuaNc56fRI/78wFVRvw0/ZNGYsaR6pSBC34kQhufn+4zV42xwXno6/8xG4/Ybirw9+343F2TZ+rXD8v5JdHTn8gx9b/0Rgx+kGbs/wva1KjsT142gj77IqP7d3/vmVjrh5t5x9ynpUvU86+/OMre3pcxoEFhf33ff6A/bXib2tZp4U9eeZj9sEl77rn6LnXHH2VVSpbyX74+0d77OvH3XOKq0IL+rWRU0i+YeNGF0oXBm2Y+x51mN181cWuftKDT71gA/5zqZ132TD7Yuz32crbRKKgXV9sOgIcrgWJJAV+5KounE4b06mlfl7dylDJu6kjF05q4ItFR9h15fuBQy4L26lsTThe7aho6SyGCb9MdrXuW+/ZItjX3JeefnQr1F/wz64yRenb0l09zkg/7sNZlbLGtbzRBX6i4b3/SF/S4r1HfXnqPeRVLPMMAAAAKO7U8GbajNmudGmd2hmhnC7UqTIU1atWDdvyVvtQ2pfauDHVtRqOROP+6dcp1qVTh8Bv/l3XKVMjKdXI1t9bRzzqrqelfcpVq9fYux9/bi+9OcqOOeLQzFI/sdJFhnUGcc0aWVv8erS/ov2WJcsiB4pAcaYL6Sro3tdXhjm/1huVylFjSTUw9O9/q3SYrpeoi3yH27dfuOhflw+EXvzbL6/TqDLL43+e5C7m+58rb3DXERhy1mnuQEA0NM9UbUDXZbzp7odc6WaV9NF7BRKBau2f+tRAV3c/KfCvX6fj3UV37zjxNjvzgNOtYfWGrlyPLtirAwJq/f/iec/mOeyft3KeXfL65fb5tNGWmpZqjQKv06reXla7cm1bum6pPfHNU3bz+7cGh86g51z8v8vss6mf29ZtW61F7RbuOXru6tTV9sL4l+zi1y9zBwWKs0Jt0a/TONXaXF8SuZWuK0ZHCOg3BTbyofRFoWBadfXffv4xu/aSIVajWjV77PlX7Z5Hno4qDPa+YFT3PdKppjsDXx47dux0JW4K6sIplQJfQLrGwcl9+9ibzz26265D2+ivgxCOavDPDHyh6oIduvDtM6+OzOzmzJ3vlsHYCT+7lvxSpnQZqxb4kszpx7tfbf3YD3zxrd8Q3fDe+9/dDwWvbJIOKvmPoOt03/TAl3woTX9Opw8DAAAAyPjt/OHoMa4FrS6Y6ZU01T7VitWrXV3r3V1jLaczYqdM+9NWrlptvQ/qnm08Kodx69WXurMIbrnvEdfI6cyLrrQX33jXjjz0YDv3tJOjOls7HK9FcE5nQPvLwQIlhS7E+1Fgvde1D/3XW8yP9UZn6Khsjy76rTr6fjo4eEDX/Vx+9NHorzNzIO3zT/1zprtGovjLCYXK6zTqmol3PfykuxC48oWLzjvT2rbeM/hoztQAccQTz9nTL79hU2fMCmyrDnLXMYnUgBSIV2o5f+DdPW3wy0Nc6K9OtxXwK3BXXf7BLw3J17B/+frldsN7N9vB9/Syjrd2sX1v6Wydhnd1r/nS+FeCQ2Xlf85RDx5rl75xRWC6D7HD7z/anvzm6eBQxVuhBv1tW+3hLqCkjaVO7YxEF6+95rZ77ZiBg238z78G+6olR2l3xDZcXXT96NRFT/y0sdYXh3fBVJV70UVTH7jteuvf53D7c/Zf2Z4TTtWqlV25nHmL/nGnfoWjI736Uao6bqGnaOWXerVruVPZli5b4Q48eBeu8TpduEpfZPMWLHIt7PNi8u/TXQivLyadQaALEXudjrjLHzPn2Oo1GfX29UNcF8LSaXGRauTpwIEOFPwcfH6zwPAyZ274MjtaNs//7237ctx4dxTee/9/L1joLuYVjk7b1cEk7wr2oh/8awOfA12RP5R2Snb3WQQAAACQYdLU6fbZmHF22CE9XADncWVHq1YN3ssd7QN+Pf5HF6KpZGooteK//KY7XNh32ZBz7PmH7raH7rjRXaNrbOB5N93zkCsplBuRWvsCJZ2yFFUNUIv7waefYrreoSc/1ptpM2a5iwAfdvAB2c4GUsZwat9jbL8O7ezlN0fZKYMvtsGXX29nXnSVXX/nA9b74B6uAsHuDvDldRoHn3GKa0ip7Y1KGt354BP27GtvRn3QTxmJnjvymYfthssudGcnXHzdcJu7YGFwCCCxTJo/2YX+6nTbf+Fdtf73h/2PnP5g8JGiodD/u9nfF/sW/KEKNejXEdlBp53k6szfEdhAqvxLKAX5apUxfcZsO7h7F+vWuWPwEbNWe7Zwp1V98c13LoT20xHdnydnBMie5StWuY2oTu/UD0e/Urv5MtgestFWcH9I967u1FC1bg9tAa6g+NW333ctWA49sHuwb/5TzX6dnaCgXQdLvNbrHv3wvv7O++3zr7/N0wVeFKLrNXTdAZ0FoTp9od3Qc053gf7sv+cHn2XWI/BjX63033jvYzev/PQDQf2//u4HqxkYRlRPXz/i1Tog9ItOX5w6aq6uSmCZq/SQ9/5nzpnr3n/ol6vONNDy0cGk/drvugiYTi/UMvv6uwlZzgbR/JswcZI74JOfdFAidNkAAAAAiUxB+4jHn7XGDerboFNPzBKuKZDTvlC4MqbinUWrhle6cG440/6cZX/O+st6BfanvDMFPGq8pda7uubXo3fdbH0O62lNGjWwtoH7unDmtZdcYHPmzXflMXLzO9xr7avrsoWjfQidWR7pWmRAcaR9+Ieefsl+mTLNzjr1BGu9Z8vgIxnyut5o/DpwqJb8kcpuKQMYfu1ldtcNV7lGm2rwqGzmsbtvsROPPdKVT95dad+8TqO2RWpUqe3NhYMG2vlnDrDPx3wb2FbNCQ6xe9pOartXs0Z167F/Z7vjuitcOeSR732SJZsAigsv7J+0YLKN+Lxog/6SqlCDftFFR668cLBreX7RtbfYzfc+7Fpsq+7ZEy++ZoMuucaFuAqNLzl/UJajugpwD+95oE2e9oddcfOdLgT+8dff7PEXXrN7H3vG/ej0Uxmdgw/o6g4a3HrfozZ2wk+Zw3/42Vfuoq664KtHF2/S6z3/+tv21Muvu6PGqhMpvQ4+wPoefZgL0S++fnjma6uFut7Hv0uXuSPc4a7Mnl9UhmhA/2PdPFSNuNsfeNzNN02HShHdEbiv8jlnnNwvbG3MaM1buMidntexfduItef267CP1Qp8Wek0O9XjFM1LtajRhWwuueE2e+/TL+znyb+75XnZjXe4C/jqB8JeLZq54TVunbamVvXX3nafvTTyXTe8ltOwwH2dSaB5vn/wYI///WvYq265O/Oz8+BTL9iVN9/lvqT15eu/or7eh57z6Vfj7Jrh92Y+566Hn7I3Rn3kLlScHzQ/VDJI8+Shp190n43f/5gRfBQAAABIPArOdUbufY8/6/avbrn6EqsdvIClp1zZsq7BT6RymQq01qxdF7GGvx7/ZvxPbn+vu+8ivJ5Fi5e4RmJdO7bPdhBA9tm7lbVq2cJmzP7bwpVzzYnCRO2bRHquSpOqQRot/1FSqGHlA0++4LIG1aTXtQ9Dr1+X1/Xmr3kL3Dp7/JG9XRgeicLyzvvu487kUcB/waCBrlGirtOhg4C7Wy9jnUZt71QhwKsK4af3375ta3dQU3lJJMo3NF3hWv1rO6kDltqm7a7kEJDIdrXsnx3sg8JU6EG/No49e+xvzz10tx0b2KDrquMPP/OSq3v2+dffWZ1atezWay51F9BV630/beDVYuOic890G04v7P7192l2xYXnZWtNr5YlGv7iwWe5i6aMePw5N/xX48a74F5fFP4yOzqKPOTs02xTYIP78RffuIDaKxGj11aAfMUF57rT1rzX1nAtmze1e2+6xo7sdXC2L7/8ph+2wy65wE7p28ed5qb55r2nA7t1tgdvv8H9QM4tfbEpBJdu++0b8f3Uq1vblcfR8lPLHdGwqtV53y3DXOitsjvDRzxqjz73iptnN15xUbYfCDr9TV/WewV+mI/65As3vJaTlpeWs5afv7WQ3v/1lw21s07p7w4WeZ+dcRN+djsFD99xozu1z8+bZzoIoQsIe8/RdF932YXWvk3r4JB5ox8nqtmnMk/aUVGZI+/q/QAAAECi0b7Bx19+Y3c+9GRgH6Opa41ar07t4KO76Pd929Z72fKVq+3v+dnLcq5aneJ+h+sCvuFqZat0h87Q1gUqwwV+OuM6mlIZ2ufQ2bWxUmMdHcTQNIQGfJoH02bOdmGhv5EYUFypYsGNdz/oGlhe9p9B1q/P4WFzgbysNzrLR9f8qxMYR6eQ/XePxvHC62+7qgUK9UOp1K8a+nkXBQ8n1mnUAQ6VAVND0XBBvF5P3e4oTxnwn0vdhcVD6bqOXkNJACgISSkpKVGf2zhx4kTr1q1b8F7+0RFPbewU7EcKlkNpo6yNsML8cK06QnnD6weiLq5bpkzuS9uIviTStqbny7hyS+9JByL0Vxeq9Qfi8UDLVafulitbJqplpM/Axk2b3PuI5rPgLVP9mI/2/XvzrGxgmgrqWgoAAABAUcjP/TWV2tGZzjqjWfWzh557xm5/0/+7ZJndcNcDrizFbddentnqX+PR2dJqmHPH9Ve4i276KfB77rU3XUnNe2++1pXICLVi5Wobdsd9pnrcN1w+NNt0zPprrgvnDuzWxS49f5DbR1QrXR1c2KNFsyxnEXw0eow9/cpIVwpErYRF+wiqQ/7ux6Nda2F/4yQ1rtKBDjUOGnbpBZnjUhi6JbCvoxrc3rDa/9FZw2pQNOLW61zJD48uKKrwVCVQ+/U5ItgXJU1BZCr5OU6Vz9LZO/osX3fphdka0fnlZr3xzFv4j113xwjXWFKVESLt++tsf5UP+s9ZA7KMX6WCbx3xiNve6Cwjb5sQul7GOo0a/sWR79r7n36ZbXhlSTrwoMaetw+73Lp0bO/6h77mP4uXuvemswn820JRyeJb7n3ENRZVQ0gvk9jdtgOJr6Cy1N3RZ1mdzrTbunWrpaam2oCRZwQfRW69ffobVqlSJStbtqwrma513ttGxIvkYcOGDQ/eztHixYutceP8L02jmaPTPWOZORpWzykT5tTPcLzhK5Qv53745ZVeN7/GlVvuPZUr674cVMM+3mi5ah5Fu4w0LzV8tJ8Fb5nG8v69eZaXaxgAAAAA8Si/9tfUqOnOB5+0736caDWrV3MlTqfPnOOuCebv1m/caC2aZpxNrMAtOTnZvv7+R/t0zFibNWeuTfztd1fOUuHh8UcfZkf37pntd7uu+6WynF06dbDDDznQ1dwOVbFiBduavs2dca3ra6mednLg9/zmwHSqtMgDT73gQroLzxnoSnjq4MGTL79uT770unt+x312HVyY/fc8m/T7dDvs4B6Ztb21j9CkYQNXdvOrbyfYuAk/ubMM3nz/Exv18WgXIl46ZJDVqZXRcnjFqtV2w10P2nuffWH7Bsbt9VcQOP7nX11jpCMOPcjt23h0NvLY8T+58kM6MxolU0FkKvk1zh9+mWy3P/i4K13Tod3etiGwfoeu89Nnznalb1UrP9b1xqPw8f3PvnSle84741RXziYSnUE0f+E/LlyfOftv+2v+Anc9wef+95atXrPWhp5zhjtTSMKtl7FOo4Zv2qihG17bMl0HcGfg35y5C+zZV0a6eaTKByced5TLFMK9ps4QKF2mtHu+DlQoxNc2YfQ337qqB5u3bHHlkHRgwLO7bQcSX0FlqdFQw1idFZeenm6jpr8f7IvcOqX9SS7k1+8d/Z7RNkNdPImLoB8AAAAAkDf5tb+ms3J/mDjZNcTR2cvLVqyyJctWZOvUYtUL0bWj23qPFrZH86audb/CfbXarVmjmg0+41QXjJUJaWyjwE/hvVq7Dz7jFGtQL/xFNd2492rprkf27Q8/2ydfjrX3PvnC3vv0Sxf0N6hf1665+D+B18+4WKj2udXKf+acuXZkz4PcNHnCBf2igwkqBaqDBH/PX2h/zJxjG1NTrXvnTm7czZrsmq8aRoGfpv+Yww91wZ4Q9CMn8Rz0z/5rns1f9K/7POuzH26dX7NmnXXt1CGzxFYs641H4fjLI0dZm8B6cNyRvVxgFokaDXbbr6ML1Sf9Ps2mTJ/hgv9WgW3NtYHxd2i7d3DIyOtlrNOo4XXhX43nh4mTAtucie46JSoNphLKQ84emNkSP9xretvCTvu0tVmB7Y0OmGocs+fOt732aO7KCvunWwj6i7eizFL12STozz9e0K9tUrwG/XFRugcAAAAAkDclYX9N5T4XLV5sK1evcQcimjdp7FoEh+5oK9zYujXdnc1bEBTMBV6EM4URk3gv3ZPoimK9ZFuAnBRl6R6F/GlpaZTuySde6Z5y5VThJTkug/74q/cCAAAAAEAYOsNgj+bNrHvnjta1Y4fM8hyh1K+gQn7R9cEI9oD4UhTrJdsCxDsvjC5dis9pXmj+efMynhH0AwAAAAAAAEAx44XTLWu0CPZBbmj+EfQDAAAAAAAAAAqVF0yrnnzvFocG+yI3NP/itS6/H0E/AAAAAAAAABQTXiCtTvXkD2pxoPVq2jP4KGKh+ab556/Lry4eEfQDAAAAAAAAQDHghdD6q1bouo5E2bJlbUC7U6xvo+OsUbmGlpyU7IZBeJo/mk+aX5pvmn+aj16rfonHsD8pJSVlZ/B2jriaOwAAAADEJ/bXgPhWEOso6z0Q34pqHd25MyPu3bFjh23bts3S0tIsNTXV1q9fb6tWrbKNGze6/hrOGxa7zoRQqF+5cmWrXbu2Va1a1SpVqmTlypXLDPu9YeMNQT8AAAAAFAPsrwHxjaAfKHmKch31Qnwv7N+6datt2bLFdQr+1Z+QPzsF+ArzFeyXL1/edaEt+uMx5BeCfgAAAAAoBthfA+IbQT9Q8hT1OuoP+7dv3+4Cf/1V54X8hP27eAG+/qomvzoF/Pob7yG/EPQDAAAAQDHA/hoQ3wj6gZInHtZRL8hX2O+14g8N+Qn7s4b83l91CvjjuVyPX0wX49Wb0QcCAAAAAAAAABDfvHBaYbVap/u7MmXKuE6laUp6582L0HmUKCG/xBT06yIE69atC94DAAAAAAAAAMQzhdReUK2/Xit1usidf355t+NdTEF/vXr1bPHixbTqBwAAAAAAAIAE4oXWdNF3iSSmoL9WrVpWtWpVmzFjhq1Zs4bAHwAAAADihHZG2UcD4pPWzYIIjFjvgfhVUOs9EElMF+P1rF692pYvX24bN27kYg0AAAAAkEf5caE+Nchq0KCB1ahRI9gHQLxQY8mlS5da27Ztg33yB+s9EL8Kar0HIslV0A8AAAAAiC9qkOUFCqotCyA+qFWvF8irUkJ+Yr0H4lNBrvdAJAT9AAAAAFBMLFq0yNavX2+NGjWyatWqEfwBRUhB37p169y1DlUGuWnTpsFH8hfrPRA/Cmu9B8Ih6AcAAACAYoRSq0B8UG3uypUrW7169Qq8RS/rPRAfCnO9B0IR9AMAAAAAAAAAkMA4nwsAAAAAAAAAgARG0A8AAAAAAAAAQAIj6AcAAAAAAAAAIIER9AMAAAAAAAAAkMAI+gEAAAAAAAAASGAE/QAAAAAAAAAAJDCCfgAAAAAAAAAAElhSSkrKzuDtQrVz505buHChTZw40TZv3mzt2rWzDh06WLly5YJDRGft2rV288032+DBg61jx47BvvlH07lhwwarUKGClSlTJtg3u+3bt9tff/1l1apVswYNGgT75p/PPvvMfv/9d7vqqqusfPnywb4AAAAAAAAAgERWo0aN4K3cK5IW/QrF33nnHbvhhhts8eLFlpaWZk8//bTdcssttnr16uBQ8WHdunV244032p9//hnsE156erq999579ttvvwX7AAAAAAAAAABQ8Iok6F+xYoV99913dvnll9uVV15pF1xwgd17772u5fz3338fHCqDWtSr1b5asy9YsMAdJIhGtM/TcEuXLrVff/3VHXTwD6fbmqYdO3ZYamqqG9+WLVuCj8Yu1mnScBpe9yPZtGmT/fHHHzZr1ix3sEHDrl+/3t32y+m1/c/TgZc5c+ZkGwcAAACAkmPjxo2Z+yQAij/lAsoLlC9EyisAAPGrSEr36IvjgQcecGVoWrRoEeybnVrTP/XUUzZt2jSrVauWC9uTkpLcAQKV+RH96Awt3RPN80Rh+oMPPmjLli1zp0esWbPG6tSpY1dccYU1bdrUTefw4cNdAO7R6xx77LHBe7voAIDGpWkI93i007Ro0SJ7+OGH7d9//3XTogMN3bt3d7dVGsgr3aMv3VGjRrmucuXKVrZsWdfvrLPOcv1inR/efDz44IPtq6++cqWK7rjjDqtevbp7HAAAAEDJoTKr2h9Q+VLtH7Vq1Sr4SGLQvpH2eypVqmTJycnBvrun/aZ58+bZXnvt5faxPBqX9sVKlSpljRs3do2imjRp4vat/PRcNcRq3bq1zZ8/3w2/xx57uH0vj/a75s6da3vuuae7n9Pr6TH/873HtP/XsmXLYN+sFNbqNbZt2+aer/ev19U0aTlqnoSjAzt6bxpvuP1AjUOv7af9VM2L0Hkcy7CID/ps/e9//7Mvv/zSDj30UDvvvPN2W744Hmn9k4oVK7q/0dC2TtsKrbf+z6b6aX1o2LChy2AKc9ugktB+aoQ5c+ZMtw5FKhWtaZk9e7abVq3Dud1WhFJupnzKT++1fv36WaZfYhkWQHYJW7qnatWqbmPzwQcf2KpVq4J9s9KG7IUXXnAbHv194okn7KWXXrITTzzRlflR6/twon2ewvtHHnnEbURffPFFe/zxx91fbXiffPJJ93jz5s3dMI0aNXJlhd5///2wIX5OYpkmvbY23poWhfMaThtyzSs/nRHx8ccfu+Bfwzz77LN26623un7auHpinY/jx4+36667zh577DFCfgAAAKCEUiOhevXq2f33359wIb/8888/rgSr/kZLIZf2+T766CO3/+TRONRQbevWre6achMmTLBXXnnF7Wt5UlJS3L6WGpEpHNV+rhqCLVmyJDhERhCnRlnffPONCyKjeb3QcEyvqZKxDz30UMR96ZUrV7rHNZw3jWrEpgM3atQVjl5fAe+dd97phg1H/bWfOGnSJJs+fbqbDxpegfDXX3+d5T3EMizig/IILa///ve/rupCooX8ogMV6mKhigZaX3QWg9+3335rb7zxhmsEWdjbhlA66PD888/bM888k3kwI5QC+3vuucfGjBnj7ud2WxFK5an12lOmTHHrstZfleG+8MIL3Rlf/vkRy7AACkaRBP01a9a0Sy65xJYvX+5WeH2RaKOmDaBHAfTff/9tJ598cmaLA/3I6d27tzs6qo1EONE+TxtBteA//vjjMy8ArL9nnnmmnXbaaa5cT36JdZoGDRrkDoaIvgg0jZ06dXL3RWcP/PTTT3bUUUdZt27dMn/86cjxueeem+UobKzz8eijj3YHP7xxAgAAAChZFCQpqNW+iG6Hli9VKKXHtS+hVqih4Y0CI4WG6q99PC84Du2vUEiNlLzn668CqdD+oTQehXIqYarp03Bqje5Npx7Xfe3T6a+mMVKI5aeWuscdd5zb11IAJnqvCqvatGmT2eK3T58+ruWuWuZ69BzNr/3339/d19nVdevWtZ9//jnzfSgU/OWXX1zjMQ0bzetFopAw0j7xDz/84ELFcPSYlkEoXStv3LhxwXuRqTHYwIEDXWCvMz0U6ulMcQWiOlDgF8uwKFr63Ck7UEUBZQHeeuqnfvrM6XMfbn3S+qf1Tc/T589rgBjaX+u31nNv/PqrYUP7h9K6HqlssW7rNRS4q9M6771mTtSyfr/99nOff80H0fZp7Nixdswxx7hsprC3DZHovevsgFDetOiAQKjcbiv8mjVrZmeffbZbl6+//nrXePSkk05yDWNDxx3LsADyX/KwYcOGB28XKp2OcMQRR7gNp07l0Ub1rbfecqcU6bQe/RjUl7+OAn7xxRc2evRo1+nopL6AdLCgS5cu7ktDz9WGOZbn6UtEG0FNQ+nSpYNTZe5MA7Wg1+lOEjr+SHSqkzbgGia0xUss06Sg/8gjj8yycdcXhjbO+vHVo0cPN02ffPKJO51O4b6fvpT1haHx5WY+9uzZc7fvEwAAAEDxpmBGoZEaZn366acusNU+jkKpqVOn2k033eT2JxS6aR9uxowZtu+++7qWr6J9D50JoLBMrV31PO1zqP/dd9/tSmXobOM///zTtSTV2QPt2rWzN998015++WV3X/3VEEqhmNcwS+NR+Kbyqrq2m4bTmc/ad9Jf7dNoOvU6GkZBn/Zx1Aq3bdu27szpkSNHumBL+53hGjfVrl3bPV+hogI8BZDvvPOOnX766Zn7SSqroevOqUxG586d3euoFbEaTe29995uGE2TAkKdca1h1EpX+3CaR9oH9hpnRfN6ft5+p/ZhNQ1du3Z1+7AeBZ9aJps3b3bvV/uPGlbvWa219Z61vxu6H/njjz+6/UhROddwr61xqKWuGo15+8ve+BSE6r0qzNR7jWVYFD2VENa6qaBfny9dw1CfHS075SbPPfecq4KgdV7roFq3az1QKWZvPVLu8O6777r1Wi3Ytay1Pqq/1g99/vTZVtir4fT5VRUFjVfru/IQ/VUJLZUY9tYRhdgaXtM3efJkN21al7Rd0usoT1K+pBLI+hwrA9E6r/ekz5hKyeggk9bNcJ83ldHRuvLhhx+6wF4NIhWAazxqBOptfwp72+Dn5TV6ntZxPU/T7dG0qPGstpEK2rW9ze22IpTK/2h99j+uZa5tqPd5UQNUva9YhgWQnfc7Ki+KpEW/n77ktcG57bbb3MZcP9i8o6hVqlRxLdF1NNDr1NpdF/BVQB9JtM/Tkd78bLm/O3mdJm3YQ4Xrp425N/88uZ2PAAAAAEoele3Q2cMKkxSMe+VLFeIoTFMZUIVXKjWqEqIKpvTXv3+ihkoK+hXoa3wetYZVeKiDCXq+SpcqaFKZHQVsCuTUX60/NQ4dcPDogIKeN2DAAHv11VfdcJoeBYcK4Dw6OKByFCrBqr8KJdVPr6OgUkGk9pvC0bT169fPlTRV62KV0tBBAi+kE71ftdxVmKcwUC1xFQZ6LXY92r/VNCg01HtRQKmztf0hVzSvF45eSyVH9F78FKZqnP4zwj0KRg877DBXvkdhpUeBn+azwsjchgw60KP9WL3PnMQyLAqHAncdHFOQrLLFCt/1eVG2oINvaqGuUF3rudbBIUOGuG1AaAttfR4Vur/++utZyh7roF379u3dczWOe++91x00VA504IEHuvVe/fXaCoMV+nuiKVusz7y2IdpuqdM6r/vqr8Bf0+m1xA9H4biujaj1QI0vtY5ovdT88BT2tiEcHThTxQaN26NwX2WGFKZ71/7wy822IhoK8DXPNF/VKHV3YhkWQN4USdCv042uueaaLKczaYOmo59e0K16kNro6JQmHQjwOh051alKoWG2J9rn6cizNqjeF4NHp0zpSym0f17EMk06+qkWLn764aUvQY9K8OiLSF9+oafMaeOtLx1PbucjAAAAAHi8MEkt7xWkea14dYawWrSqXIW/pIQCMoViCgv9NLyCMC/Q0n6KzlTWfbUk91qzKgQ75JBDXLivAwh6fYViCokVSHvDaXwqX+oP5CJRIzMFjCojs7tATa161eL9tddecwGczrj2Xs+jC3QquFIZms8//9y9J43fT6+hsFNBpcal8apMSKhoXi+U5uvhhx/uxu3tEyq8V8O5cNPiUein+elfVrrIp/YZdRZ7bum9apq1P52TWIZF0dIyUkiudUzrqmjdVzivz5LCbn8mofVWn/nQlvM6kKD12dtuaD3QgUSvwoDXXzmFDsp5BxD0WVXwH03Z4kj0ulpPd3fwTOPVQTBtx3QAQ+G8wvhQhb1tCKXX1nqqbaG2iaLgXGfP+LerfrndVkRDZwhoXNGE97EMCyD3iiTo14ZPpxkpUFcorbBZQbY2lNpoaeOkU5/05aGjtQqvtRHzTnvScDptKpxon6criutHqi56qy8vDadwX8/TKarecJpObbh1UEJ13sK1ovfT4xpOnQJ6vbdYpkk/XHWBFbU0USsTnWKlafR/eWr+aIOsoF9fHnodPa6NvcryeLX4JbfzEQAAAAA8ahGq1pgK4kLDJLXE1z6IWs56dFZxaMgvajHu318RhVcq8xA6Xn9Y6L2+GkeFDqcWxF7pjPyg8etgglq86v2GC+AUDOoAhcqC6L0qnAxHpUv0/nTAQvtw4YLJaF4vHL2m9mEVTorCe+2PahyR6ICIAlOVU9E+pDoFgAcddFC+lNGJZRz58XooWMo1dBBIAb6fPsdqfKjPv86U8ehggIYPpXXUXzZG60/Tpk3d9sBfgkbj9UrliD7PagyprMQL+T1q1KgDBflFOZUaRSoAVz3+cAF4YW8bQmncyneUBXlnxOigiOa75mckudlWREvbbv+y3Z1YhgWQO0US9OvHhVpRKATXxXhPOeUUd0qlysuoRrxoA6Z6aNrg6crhunjHOeec40L4q6++2n1RhBPt87SB1murnptK2Gi4Sy+91G3c1d/78agNc9++fV29Nl1MREesd0fBu4ZTp/HpIEKs06SNrU5hO/XUU9207bPPPtlK7OgUMZ0VoaPbGpcucqRaeUOHDs3ygzq38xEAAAAAQin4i8Qf0BWUcA2vdEa4zgzPTwo21SiqR48eEQM4HeBQ2KmWzZFCa+3jqSWxutCw1C+a1wul11dAr6Be80Wth3VWRE5nNygoVEioAzMqMaLGd3lpzS9qpauDMbt7j55YhkXR07oVbr0Thbb+oL6ghHv9cGWL80LZiRqD6toCamEfSWFvG0Lp7AiV6FFZIK/sls5G2F2AntttRU6UK+l1VeM/J7EMCyD3iiToF7W4UA04XVRFdR9VY9F/Cqboti5Gotbnr7zyirs4k57jb62hUFv14/xHSaN5nqglyUUXXeQuQKLh9Ff3/S1MtLFXHTRNn+q8+evM+emotWrAaRiv0zi1EZbcTJNO4dLwCvl1RNmrMSeaLr1ntdTXmRHqbr75Zlf+KLTeZG7nIwAAAACI9kPUYlQtQnWGsJ/qb6tVr1rbFxTv9XXms8ItP9WrDi1/WhJon1AlQjT/dRFSnQ2u1vo5UdCmM8kV+qkc0wEHHJCnltE6K+Czzz5zwebuWhVLLMOi6KnVvPjLLovCYrUkV+AceoZOftK49VmJpmxxSaF8R2cMTJw40a332jbm1DI/t9uK3dHFdXURYR0kzGn7EcuwAPKmyIJ+jzY4XngdiYbRkUavlX20on2eNpQaTn8LWizTpFPFNHw4Y8aMsQceeMD9yNYRYm8e6iipnuN9Ifvldj4CAAAAgBpAaX9DjYcUsKmlr1qVqvGRWouqDEVB0uvrQpm6wK+CKjVwUvCvi4WGnk2g/Sm1Glf5DJ2FoJBQrX/VQEoX2lRJ00Sg6dT0arrDtV5W4y2VD9GFUlVTXC13c6L9QrXqHzdunAtr1eo4HB1Auf/++7PU1NbZEwrtvHK1mv+33367u1DzWWedlWXfPpZhEZ/0+VLVBV0EV+u61nmt+7qv8N9/vY6CoOxC1QnClS1W1YVw6722Udo+KCvR+hPucxzvNK2aZv9Fd/104V299+eeey7qslvRbCsibW80zzXvtR5rG6yDDLpwsqhqhD9Li2VYAPmvyIN+5I5K96g1jer5qzyQNqL64n3sscfcDzXvQjkAAAAAkB9UXkJBrYI+lRxVidAnn3zS+vfvb2eccUaBBzh6/euvv96VndHrq9SpDjqcf/752UptqCSrzozW9Kmsqlr/6qwDhc26HXoWdLzSdGp6Nd3+WugezXNdaFMhnv9ipznRmedq0atWvv4zvf1U2kcHAnRtBI/qfF922WVunmq+P//889apUycbMWJEthb6sQyL+KTPk9ZzlVnWuqTbWvcUpGtboNb2BS1S2eLLL7882zUHdXFchcyaxkcffdQd7Av3OY53mlZNs/+6J34K9nWQRWdRRVt2K5ptRaTtzbRp09w81bo8ZMgQV8FCVSduvfXWbOWgYxkWQP5LSklJSYymDMhGF6VROR4F/DraqusJDBgwwP2gpdU+AAAAgIKiVpsK0VRaoyBb9Eai0iFqMR5NS1YAeafW3qmpqa4leVHlDZs2bXLXBOBsEADFUX4cDCPoBwAAAAAAAACgiORH0E/pHgAAAAAAAAAAEhhBPwAAAAAAAAAACYygHwAAAAAAAACABEbQDwAAAAAAAABAAiPoBwAAAAAAAAAggRH0AwAAAAAAAACQwAj6AQAAAAAAAABIYAT9AAAAAAAAAAAkMIJ+AAAAAAAAAAASGEE/AAAAAAAAAAAJjKAfAAAAAAAAAIAERtAPAAAAAAAAAEACI+gHAAAAAAAAACCBEfQDAAAAAAAAAJDACPoBAAAAAAAAAEhgBP0AAAAAAAAAACQwgn4AAAAAAAAAABIYQT8AAAAAAAAAAAmMoB8AAAAAAAAAgARG0A8AAAAAAAAAQAIj6AcAAAAAAAAAIIER9AMAAAAAAAAAkMAI+gEAAAAAAAAASGAE/QAAAAAAAAAAJDCCfgAAAAAAAAAAEhhBPwAAAAAAAAAACYygHwAAAAAAAACABEbQDwAAAAAAAABAAiPoBwAAAAAAAAAggRH0AwAAAAAAAACQwAj6AQAAAAAAAABIYAT9AAAAAAAAAAAkMIJ+AAAAAAAAAAASGEE/AAAAAAAAAAAJjKAfAAAAAAAAAIAERtAPAAAAAAAAAEACI+gHAAAAAAAAACCBEfQDAAAAAAAAAJDACPoBAAAAAAAAACgCO3fuDN7KG4J+AAAAAAAAAACKQFpaWvBW3hD0AwAAAAAAAABQBFJTU4O38oagHwAAAAAAAACAQrZ69WrbvHlz8F7eEPQDAAAAAAAAAFAIduzYYevWrbMFCxZYSkqKlS5dOvhI3iS9++67+VPtHyWWPphSrVo19xcAihNt49i+AYgWv4tQUvD9iHjBZxGJht8KyC0+O4krKSnJ/dUyrFy5susqVKhglSpVsooVK7rbe+yxhxsmL5JGjx69M7+u7IuSae3ate5v9erV3V8AKE60jWP7BiBa/C5CScH3I+IFn0UkGn4rILf47CQmL+SX9evXZwb95cuXt3LlyrmQX7ebNm0aHCr3kiZMmJAl5Sf0R6zWrFnj/taoUcP9BYDiRNs4tm8AosXvIpQUfD8iXvBZRKLhtwJyi89O4vCH+x710zJUyF+lShUrW7asC/oV8pcpU8bq168fHDL3kqZMmUKyjzxRLSmpWbOm+wsAxYm2cWzfAESL30UoKfh+RLzgs4hEw28F5BafncSmoF/LUOV6qlat6oJ+1eb3/tauXTs4ZO4lzZ49m6AfebJq1Sr3Nz8+kAAQb7SNY/sGIFr8LkJJwfcj4gWfRSQafisgt/jsJD4v6FeLfrXiV8CvLjk5OV9KMiUtWLCAoB95woYGQHHGziOAWPC7CCUF34+IF3wWkWj4rYDc4rOT2NSiX8vQK92jcN8L+UuVKuX65VXS4sWLCfqRJytXrnR/69Sp4/4CQHGibRzbNwDR4ncRSgq+HxEv+Cwi0fBbAbnFZyex+YN+ter3WvMr5Bf1z6ukZcuWEfQjTziiCKA40zaO7RuAaPG7CCUF34+IF3wWkWj4rYDc4rOT+FavXu0CfXVeS379lYoVK7q/eZG0cuVKgn7kCUcUARRntBIDEAt+F6Gk4PsR8YLPIhINvxWQW3x2Ep+Wob90j4J+r0V/hQoV3N+8SEpNTd0ZYOp27Nhh27Ztc11aWppt2rTJ6tatGxw0Q40aNYK3AAAAAAAAAABAUcs4ZAAAAAAAAAAAABISQT8AAAAAAAAAAAmMoB8AAAAAAAAAgARG0A8AAAAAAAAAQAIj6AcAAAAAAAAAIIER9AMAAAAAAAAAkMAI+gEAAAAAAAAASGAE/QAAAAAAAAAAJDCCfgAAAAAAAAAAEhhBPwAAAAAAAAAACYygHwAAAAAAAACABEbQDwAAAAAAAABAAiPoBwAAAAAAAAAggRH0AwAAAAAAAACQwAj6AQAAAAAAAABIYAT9AAAAAAAAAAAkMIJ+AAAAAAAAAAASGEE/AAAAAAAAAAAJjKAfAAAAAAAAAIAERtAPAAAAAAAAAEACI+gHAAAAAAAAACCBEfQDAAAAAAAAAJDACPoBAAAAAAAAAEhgBP0AAAAAAAAAACQwgn4AAAAAAAAAABIYQT8AAAAAAAAAAAmMoB8AAAAAAAAAgARG0A8AAAAAAAAAQAIj6AcAAAAAAAAAIIER9AMAAAAAAAAAkMAI+gEAAAAAAAAASGAE/QAAAAAAAAAAJDCCfgAAAAAAAAAAEhhBPwAAAAAAAAAACYygHwAAAAAAAACABEbQDwAAAAAAAABAAiPoBwAAAAAAAAAggRH0AwAAAAAAAACQwAj6AQAAAAAAAABIYAT9AAAAAAAAAAAkMIJ+AAAAAAAAAAASGEE/AAAAAAAAAAAJjKAfAAAAAAAAAIAERtAPAAAAAAAAAEACI+gHAAAAAAAAACCBEfQDAAAAAAAAAJDACPoBAAAAAAAAAEhgBP0AAAAAAAAAACQwgn4AAAAAAAAAABIYQT8AAAAAAAAAAAmMoB8AAAAAAAAAgARG0A8AAAAAAAAAQAIj6AcAAAAAAAAAIIER9AMAAAAAAAAAkMAI+gEAAAAAAAAASGAE/QAAAAAAAAAAJDCCfgAAAAAAAAAAEhhBPwAAAAAAAAAACYygHwAAAAAAAACABEbQDwAAAAAAAABAAosY9CclJQVvAQAAAAAAAACAeEWLfgAAAAAAAAAAEthug35a9QMAAAAAAAAAEN9o0Q8AAAAAAAAAQAJzQX9oy33dpzU/AAAAAAAAAADxL7NFvxfs+wN+wn4AAAAAAAAAAOJbltI9oSG/up07dwb7AAAAAAAAAACAeFMqtNW+F/B70tLSgrcAAAAAAAAAAEC8CXsxXi/sV5eamhrsCwAAAAAAAAAA4k2WGv1e590vVaqUbd682VavXu36AQAAAAAAAACA+OKCfi/cF3/gn5ycbKVLl7aUlBRbsGCBrVu3LjgUAAAAAAAAAACIB9kuxqtOLfnVKegvW7asVaxY0dLT023hwoXBIQEAAAAAAAAAQDzIUrrH42/NX6ZMGRf2ly9f3ipVqhQcAgAAAAAAAAAAxINsLfq91vzqFPQr5K9QoYJr1a+/AAAAAAAAAAAgfpQKbcnv/fWH/WrVX65cOVr0AwAAAAAAAAAQZ7JdjFfhvhf0+8v3KOhXBwAAAAAAAAAA4keW0j0eBf3hwn51AAAAAAAAAAAgfkS8GK8X9OuvF/arAwAAAAAAAAAA8SPbxXi9wN8f9vsDfwAAAAAAAAAAED8ilu7xhAb+AAAAAAAAAAAgXpj9H1TRpoB+BD1yAAAAAElFTkSuQmCC";
//            bitmap = base64ToBitmap(ss);
            Format format = new Format();
            format.setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_CENTER);
            bitmapwrf = BitmapFactory.decodeStream(mContext.getResources().getAssets()
                    .open("64.png"));
            sendSuccessLog(mContext.getString(R.string.operation_succeed)
                    + "\ngetWidth  = " + bitmapwrf.getWidth() + ", getHeight = " + bitmapwrf.getHeight());
//            Bitmap bitmap = BitmapEdgeHelper.removeBottomWhiteEdge(bitmapwrf);
//            saveBitmap(bitmap,"dasd");
//
//            Bitmap bitmap2 = BitmapEdgeHelper.removeBottomWhiteEdge(bitmapwrf,100);
//            saveBitmap(bitmap2,"dasd30");
            device.printBitmap(format, bitmapwrf);
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
        opts.inJustDecodeBounds = true;//设置只是解密（减少占用图片内存）只是修改图片属性
        //修复图片参数（给的是图片地址，修改图片参数先要调用可以修改参数的方法decodeResource（）第三参数就是修改参数的对象）
        //执行 BitmapFactory.decodeResource（）方法 设置的opts属性才生效
        try {
            BitmapFactory.decodeStream(mContext.getResources().getAssets()
                    .open(id), null, opts);
            //先获取加载图片的宽高
            int outWidth = opts.outWidth;
            int outHeight = opts.outHeight;
            //设置缩放图片的系数(int类型)
            opts.inSampleSize = getSampleSize(outWidth, outHeight, w, h);
            //注意前面设置只获取图片信息，这里要设置回获取图片
            opts.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(mContext.getResources().getAssets()
                    .open(id), null, opts);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //计算图片实际宽高 是 传入宽高值的 最小整数倍数
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
