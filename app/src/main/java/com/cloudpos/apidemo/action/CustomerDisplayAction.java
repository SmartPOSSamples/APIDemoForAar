package com.cloudpos.apidemo.action;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.androidmvcmodel.MainActivity;
import com.cloudpos.androidmvcmodel.MainApplication;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.customerdisplay.CustomerDisplayDevice;
import com.cloudpos.mvc.base.ActionCallback;
import com.cloudpos.sdk.impl.DeviceName;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

public class CustomerDisplayAction extends ActionModel{

    private CustomerDisplayDevice device;

    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device = (CustomerDisplayDevice) POSTerminal.getInstance(mContext)
                    .getDevice(POSTerminal.DEVICE_NAME_CUSTOMER_DISPLAY);
        }
    }

    public void open(Map<String, Object> param, ActionCallback callback){
        if(device != null){
            try {
                boolean result = POSTerminal.getInstance(mContext).isDeviceExist(DeviceName.CUSTOMER_DISPLAY);
                if(result){
                    device.open();
                    sendSuccessLog(mContext.getString(R.string.operation_succeed));
                }else{
                    sendFailedLog("Device is not exist");
                }

            } catch (DeviceException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close(Map<String, Object> param, ActionCallback callback){
        if(device != null){
            try {
                boolean result = POSTerminal.getInstance(mContext).isDeviceExist(DeviceName.CUSTOMER_DISPLAY);
                if(result){
                    device.close();
                    sendSuccessLog(mContext.getString(R.string.operation_succeed));
                }else{
                    sendFailedLog("Device is not exist");
                }
            } catch (DeviceException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean displayImageOnCustomerScreenByBitmap(Map<String, Object> param, ActionCallback callback){
        if(device != null){
            try {
                boolean result = POSTerminal.getInstance(mContext).isDeviceExist(DeviceName.CUSTOMER_DISPLAY);
                if(result){
                    if(device.displayImageOnCustomerScreen(BitmapFactory.decodeResource(MainApplication.getInstance().getResources(), R.drawable.img))){
                        sendSuccessLog(mContext.getString(R.string.operation_succeed));
                        return true;
                    }else{
                        sendFailedLog(mContext.getString(R.string.operation_failed));
                        return false;
                    }
                }else{
                    sendFailedLog("Device is not exist");
                }
            } catch (DeviceException e) {
                sendFailedLog(mContext.getString(R.string.operation_failed));
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public boolean displayImageOnCustomerScreenByImageData(Map<String, Object> param, ActionCallback callback){
        if(device != null){
            try {
                boolean result = POSTerminal.getInstance(mContext).isDeviceExist(DeviceName.CUSTOMER_DISPLAY);
                if(result){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    BitmapFactory.decodeResource(MainApplication.getInstance().getResources(), R.drawable.ic_launcher).compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();
                    if(device.displayImageOnCustomerScreen(imageData)){
                        sendSuccessLog(mContext.getString(R.string.operation_succeed));
                        return true;
                    }else{
                        sendFailedLog(mContext.getString(R.string.operation_failed));
                        return false;
                    }
                }else{
                    sendFailedLog("Device is not exist");
                }

            } catch (DeviceException e) {
                sendFailedLog(mContext.getString(R.string.operation_failed));
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}
