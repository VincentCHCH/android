package com.honeywell.hch.airtouch.plateform.easylink;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.honeywell.hch.airtouch.library.LibApplication;
import com.honeywell.hch.airtouch.library.util.LogUtil;
import com.honeywell.hch.airtouch.plateform.countly.CountlyUtil;
import com.honeywell.hch.airtouch.plateform.easylinkv3.api.EasyLink;
import com.honeywell.hch.airtouch.plateform.easylinkv3.api.EasyLinkEncrptUtil;
import com.honeywell.hch.airtouch.plateform.easylinkv3.helper.EasyLinkCallBack;
import com.honeywell.hch.airtouch.plateform.easylinkv3.helper.MyEasyLinkParams;
import com.honeywell.hch.airtouch.plateform.enrollinterface.IConnectAndDeviceManager;
import com.honeywell.hch.airtouch.plateform.eventbus.EventBusConstant;
import com.honeywell.hch.airtouch.plateform.eventbus.EventBusUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import io.fogcloud.sdk.mdns.api.MDNS;
import io.fogcloud.sdk.mdns.helper.SearchDeviceCallBack;

/**
 * Created by Vincent on 29/1/2018.
 */

public class EasyLinkConnectAndFindDeviceManager extends IConnectAndDeviceManager {
    private final String TAG = this.getClass().getName();
    private EasyLink mEasyLink;
    private MDNS mdns;
    private byte[] ssidKey;
    private Timer time = null;
    private TimerTask task = null;
    private final int TIMEOUT = 40000;
    //for test
    private final String MACID = "04786300CCD3";

    public EasyLinkConnectAndFindDeviceManager(Handler handler, String macId) {
        mDeviceMacWithcolon = macId;
//        mDeviceMacWithcolon = MACID;
        mActvitiyHandler = handler;
        mEasyLink = new EasyLink(LibApplication.getContext());
        mdns = new MDNS(LibApplication.getContext());
    }

    @Override
    public void startConnectingAndFinding(String ssidStr, String password, int mLocalIp) {
        //First step
        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "mDeviceMacWithcolon: " + mDeviceMacWithcolon);
        String randomString = EasyLinkEncrptUtil.generateString(16);
        String ssidstr = EasyLinkEncrptUtil.dataPadding(ssidStr);
        String pwdstr = EasyLinkEncrptUtil.dataPadding(password);
        final byte[] randomData = EasyLinkEncrptUtil.generateEncryptRandom(mDeviceMacWithcolon, randomString);
        ssidKey = EasyLinkEncrptUtil.encryptMacIdPwdKey(mDeviceMacWithcolon, randomString);
        final byte[] ssid = EasyLinkEncrptUtil.generateEncryptData(ssidKey, ssidstr);
        final byte[] pwd = EasyLinkEncrptUtil.generateEncryptData(ssidKey, pwdstr);

        final byte[] userInfo = EasyLinkEncrptUtil.initUserInfo(ssidStr, password, mDeviceMacWithcolon, randomString);
        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "randomData: " + EasyLinkEncrptUtil.bytesToHexString(randomData));
        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "ssid: " + EasyLinkEncrptUtil.bytesToHexString(ssid));
        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "pwd: " + EasyLinkEncrptUtil.bytesToHexString(pwd));

        MyEasyLinkParams elp = new MyEasyLinkParams();
        elp.ssid = ssid;
        elp.password = pwd;
        elp.userInfo = userInfo;
        elp.mIpAddress = mLocalIp;

        mEasyLink.startEasyLink(elp, new EasyLinkCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "success message: " + message);
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "success code: " + code);

            }

            @Override
            public void onFailure(int code, String message) {
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Failure message: " + message);
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Failure code: " + code);
            }
        });
        //Second step
        startMdns();

        //Third step
        startCountThread();
    }

    //stop mdns
    @Override
    protected void endAllThread() {
        stopMdns();
        stopTimer();
    }

    @Override
    protected void restAllThread() {

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {


            switch (msg.what) {
                case CONNECTING_TIMEOUT:
                case THREAD_ERROR:
                    endAllThread();

                    CountlyUtil.enrollEvent(mDeviceMacWithNocolon, CountlyUtil.EnrollEventType.SMARTLINK_TIMEOUT, "");
                    break;
                case PROCESS_END:
                    endAllThread();

                    EventBusUtil.post(EventBusConstant.END_AP_FIND_DEVICE, null);
                    break;
                case WIFI_CONNECTED_CHECK_END:
                    Bundle bundle = msg.getData();
                    boolean isConnecting = bundle.getBoolean(IS_CONNECT);
                    if (isConnecting) {
                        restAllThread();
                    }
                    mCheckWifiConnectThread = null;
                    break;

            }
            Message newMsg = Message.obtain();
            newMsg.what = msg.what;
            newMsg.obj = msg.obj;
            newMsg.setData(msg.getData());
            removeMessages(msg.what);
            mActvitiyHandler.sendMessage(newMsg);
        }
    };

    private void startCountThread() {
        time = new Timer();
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(CONNECTING_TIMEOUT);
            }
        }, TIMEOUT);

    }

    private void stopTimer() {
        if (time != null) {
            time.cancel();
        }
    }

    private void startMdns() {
        String serviceInfo = "_easylink._tcp.local.";
        mdns.startSearchDevices(serviceInfo, new SearchDeviceCallBack() {
            @Override
            public void onDevicesFind(int code, JSONArray jsonArray) {
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "onDevicesFind");
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "jsonArray: " + jsonArray.toString());
                if (!jsonArray.equals("") && !jsonArray.isNull(0)) {
                    LogUtil.log(LogUtil.LogLevel.INFO, TAG, jsonArray.toString());
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = jsonArray.getJSONObject(0);
                        LogUtil.log(LogUtil.LogLevel.INFO, TAG, jsonArray.toString());
                        String deviceType = jsonObject.getString("DeviceType");
                        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "deviceType: " + deviceType);
                        byte[] deviceTypeByte = deviceType.getBytes();
                        String hmacTag = jsonObject.getString("HMAC_TAG");
                        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "hmacTag: " + hmacTag);
                        byte[] hacTagByte = EasyLinkEncrptUtil.hexStringToByteArray(hmacTag);
                        byte[] hmacData = EasyLinkEncrptUtil.HMACSHA256(deviceTypeByte, ssidKey);
                        boolean isEqual = Arrays.equals(hacTagByte, hmacData);
                        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "isEqual: " + isEqual);
                        if (isEqual) {
                            mHandler.sendEmptyMessage(PROCESS_END);
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Fail----").append("\n").append("deviceType: ").append(deviceType).append("\n").append(" hmacTag: ").append(hmacTag);
                            Bundle bundle = new Bundle();
                            bundle.putString("data", sb.toString());
                            Message message = new Message();
                            message.what = THREAD_ERROR;
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        endAllThread();
                        mHandler.sendEmptyMessage(THREAD_ERROR);
                    }
                    endAllThread();
                }
            }

            @Override
            public void onFailure(int code, String message) {
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "onFailure");
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, message);
            }

            @Override
            public void onSuccess(int code, String message) {
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "onSuccess");
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, message);
            }
        });
    }

    private void stopMdns() {
        mdns.stopSearchDevices(new SearchDeviceCallBack() {
            public void onSuccess(int code, String message) {
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, message);
            }

            @Override
            public void onFailure(int code, String message) {
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, message);
            }
        });
        mdns = null;
        mEasyLink = null;
    }

}
