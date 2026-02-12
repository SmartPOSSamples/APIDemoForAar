package com.cloudpos.apidemo.action;

import android.content.Context;
import android.os.Environment;

import com.cloudpos.DeviceException;
import com.cloudpos.OperationListener;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.advance.log.ILogDevice;
import com.cloudpos.advance.log.sdk.LogDeviceImpl;
import com.cloudpos.apidemoforunionpaycloudpossdk.R;
import com.cloudpos.extboard.ExtBoardDevice;
import com.cloudpos.extboard.ExtBoardOperationResult;
import com.cloudpos.mvc.base.ActionCallback;

import java.io.File;
import java.util.Map;


public class LogDeviceAction extends ActionModel {

    private ILogDevice device = null;

    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device = LogDeviceImpl.getInstance(this.mContext);
        }
    }

    private Thread th;
    public void saveLogcatToFile(Map<String, Object> param, ActionCallback callback){
        if(th == null || th.getState() == Thread.State.TERMINATED){
            th = new Thread(new Runnable() {
                @Override
                public void run() {

                    File myDir = new File("sdcard", "test");
                    if (!myDir.exists()) {
                        boolean isMkdirs = myDir.mkdirs();
                        sendSuccessLog("mkdirs result ("+isMkdirs+")");
                    }
                    boolean result = device.packageLog(ILogDevice.LEVEL_PRIMARY, "sdcard/test/test.zip");
                    if(result)
                        sendSuccessLog("packageLog result ("+result+")");
                    else
                        sendFailedLog("packageLog result ("+result+")");
                }
            });
            th.start();
        }
    }
}
