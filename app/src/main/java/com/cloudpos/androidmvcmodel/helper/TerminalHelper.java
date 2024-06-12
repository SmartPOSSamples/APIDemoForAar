
package com.cloudpos.androidmvcmodel.helper;

import android.util.Log;

public class TerminalHelper {

    public static final int TERMINAL_TYPE_WIZARPOS_1 = 0;
    public static final int TERMINAL_TYPE_WIZARHAND_Q1 = 1;
    public static final int TERMINAL_TYPE_WIZARHAND_M0 = 2;
    public static final int TERMINAL_TYPE_WIZARPAD_1 = 3;
    public static final int TERMINAL_TYPE_NONE = -1;
    private static int terminalType = TERMINAL_TYPE_NONE;
    private static String productModel = null;

    /**
     * 获取设备类型<br/>
     * {@link #TERMINAL_TYPE_WIZARPOS_1}<br/>
     * {@link #TERMINAL_TYPE_WIZARHAND_Q1}<br/>
     * {@link #TERMINAL_TYPE_WIZARHAND_M0}<br/>
     * {@link #TERMINAL_TYPE_WIZARPAD_1}<br/>
     */
    public static int getTerminalType() {
        if (terminalType == TERMINAL_TYPE_NONE) {
            terminalType = TERMINAL_TYPE_WIZARPOS_1;
            productModel = getProductModel();
            Log.d("model", productModel);
            // 找到对应的设备类型WIZARPOS,WIZARPAD,...
            // 判断是否为手持
            if (productModel.equals("WIZARHAND_Q1") || productModel.equals("MSM8610")
                    || productModel.equals("WIZARHAND_Q0")) {
                terminalType = TERMINAL_TYPE_WIZARHAND_Q1;
            } else if (productModel.equals("FARS72_W_KK") || productModel.equals("WIZARHAND_M0")) {
                terminalType = TERMINAL_TYPE_WIZARHAND_M0;
            } else if (productModel.equals("WIZARPOS1") || productModel.equals("WIZARPOS_1")) {
                terminalType = TERMINAL_TYPE_WIZARPOS_1;
            } else if (productModel.equals("WIZARPAD1") || productModel.equals("WIZARPAD_1")) {
                terminalType = TERMINAL_TYPE_WIZARPAD_1;
            }
        }
        return terminalType;
    }

    /**
     * 获取设备的model<br/>
     * 通过读取 ro.product.model 属性 获得
     */
    public static String getProductModel() {
        if (productModel == null) {
            productModel = SystemPropertyHelper.get("ro.product.model").trim();
            productModel = productModel.replace(" ", "_");
            productModel = productModel.toUpperCase();
        }
        return productModel;
    }
    
}
