package com.honeywell.hch.airtouch.plateform.easylink;

import android.os.Handler;

import com.honeywell.hch.airtouch.plateform.enrollinterface.IConnectAndDeviceManager;

/**
 * Created by Vincent on 29/1/2018.
 */

public class EasyLinkConnectAndFindDeviceManager implements IConnectAndDeviceManager {
    private Handler mActvitiyHandler;

    public EasyLinkConnectAndFindDeviceManager(Handler handler) {

        mActvitiyHandler = handler;
    }


    @Override
    public void startConnectingAndFinding(String ssid, String password, int mLocalIp) {

    }

}
