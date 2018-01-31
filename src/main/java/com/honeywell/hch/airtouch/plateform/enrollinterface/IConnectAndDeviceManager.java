package com.honeywell.hch.airtouch.plateform.enrollinterface;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.honeywell.hch.airtouch.library.util.LogUtil;
import com.honeywell.hch.airtouch.plateform.countly.CountlyUtil;
import com.honeywell.hch.airtouch.plateform.eventbus.EventBusConstant;
import com.honeywell.hch.airtouch.plateform.eventbus.EventBusUtil;

/**
 * Created by Vincent on 29/1/2018.
 */

public abstract class IConnectAndDeviceManager {
    protected volatile String mDeviceMacWithcolon; //这个mac是扫二维码后得到的

    protected volatile String mDeviceMacWithNocolon;

    protected Handler mActvitiyHandler;

    public static final int CONNECTING_TIMEOUT = 2000;
    public static final int PROCESS_END = 2001;
    public static final int WIFI_CONNECTED_CHECK_END = 2002;

    public static final int THREAD_ERROR = 2003;
    public static final String IS_CONNECT = "isconnecting";
    protected Thread mCheckWifiConnectThread = null;

    public abstract void startConnectingAndFinding(final String ssid, final String password, final int mLocalIp);

    protected abstract void endAllThread();

    protected abstract void restAllThread();

    protected Handler mHandler = new Handler() {
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

}
