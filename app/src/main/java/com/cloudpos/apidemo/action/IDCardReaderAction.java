//
//package com.cloudpos.apidemo.action;
//
//import java.util.Map;
//
//import com.cloudpos.DeviceException;
//import com.cloudpos.OperationListener;
//import com.cloudpos.OperationResult;
//import com.cloudpos.POSTerminal;
//import com.cloudpos.TimeConstants;
//import com.cloudpos.idcardreader.IDCardReaderDevice;
//import com.cloudpos.apidemoforunionpaycloudpossdk.R;
//import com.cloudpos.mvc.base.ActionCallback;
//
//public class IDCardReaderAction extends ActionModel {
//
//    IDCardReaderDevice device = null;
//
//    @Override
//    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
//        super.doBefore(param, callback);
//
//        if (device == null) {
//            device = (IDCardReaderDevice) POSTerminal.getInstance(mContext)
//                    .getDevice("cloudpos.device.idcardreader");
//        }
//    }
//
//    public void open(Map<String, Object> param, ActionCallback callback) {
//        try {
//            device.open();
//            sendSuccessLog(mContext.getString(R.string.operation_succeed));
//        } catch (DeviceException e) {
//            e.printStackTrace();
//            sendFailedLog(mContext.getString(R.string.operation_failed));
//        }
//    }
//
//    public void listenForCardPresent(Map<String, Object> param, ActionCallback callback) {
//        try {
//            OperationListener listener = new OperationListener(){
//
//                @Override
//                public void handleResult(OperationResult arg0) {
//                    sendSuccessLog("");
//                }
//            };
//            device.listenForCardPresent(listener, TimeConstants.FOREVER);
//            sendSuccessLog(mContext.getString(R.string.operation_succeed));
//        } catch (DeviceException e) {
//            e.printStackTrace();
//            sendFailedLog(mContext.getString(R.string.operation_failed));
//        }
//    }
//
////    public void open(Map<String, Object> param, ActionCallback callback) {
////        try {
////            device.open();
////            sendSuccessLog(mContext.getString(R.string.operation_succeed));
////        } catch (DeviceException e) {
////            e.printStackTrace();
////            sendFailedLog(mContext.getString(R.string.operation_failed));
////        }
////    }
////
////    public void open(Map<String, Object> param, ActionCallback callback) {
////        try {
////            device.open();
////            sendSuccessLog(mContext.getString(R.string.operation_succeed));
////        } catch (DeviceException e) {
////            e.printStackTrace();
////            sendFailedLog(mContext.getString(R.string.operation_failed));
////        }
////    }
////
////    public void open(Map<String, Object> param, ActionCallback callback) {
////        try {
////            device.open();
////            sendSuccessLog(mContext.getString(R.string.operation_succeed));
////        } catch (DeviceException e) {
////            e.printStackTrace();
////            sendFailedLog(mContext.getString(R.string.operation_failed));
////        }
////    }
////
////    public void open(Map<String, Object> param, ActionCallback callback) {
////        try {
////            device.open();
////            sendSuccessLog(mContext.getString(R.string.operation_succeed));
////        } catch (DeviceException e) {
////            e.printStackTrace();
////            sendFailedLog(mContext.getString(R.string.operation_failed));
////        }
////    }
////
////    public void open(Map<String, Object> param, ActionCallback callback) {
////        try {
////            device.open();
////            sendSuccessLog(mContext.getString(R.string.operation_succeed));
////        } catch (DeviceException e) {
////            e.printStackTrace();
////            sendFailedLog(mContext.getString(R.string.operation_failed));
////        }
////    }
//
//    public void close(Map<String, Object> param, ActionCallback callback) {
//        try {
//            device.close();
//            sendSuccessLog(mContext.getString(R.string.operation_succeed));
//        } catch (DeviceException e) {
//            e.printStackTrace();
//            sendFailedLog(mContext.getString(R.string.operation_failed));
//        }
//    }
//}
