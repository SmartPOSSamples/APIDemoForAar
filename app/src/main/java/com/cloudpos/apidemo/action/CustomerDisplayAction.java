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
                device.open();
                sendSuccessLog(mContext.getString(R.string.operation_succeed));
            } catch (DeviceException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close(Map<String, Object> param, ActionCallback callback){
        if(device != null){
            try {
                device.close();
                sendSuccessLog(mContext.getString(R.string.operation_succeed));
            } catch (DeviceException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean displayImageOnCustomerScreenByBitmap(Map<String, Object> param, ActionCallback callback){
        if(device != null){
            try {
                if(device.displayImageOnCustomerScreen(BitmapFactory.decodeResource(MainApplication.getInstance().getResources(), R.drawable.img))){
                    sendSuccessLog(mContext.getString(R.string.operation_succeed));
                    return true;
                }else{
                    sendFailedLog(mContext.getString(R.string.operation_failed));
                    return false;
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
            } catch (DeviceException e) {
                sendFailedLog(mContext.getString(R.string.operation_failed));
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}
