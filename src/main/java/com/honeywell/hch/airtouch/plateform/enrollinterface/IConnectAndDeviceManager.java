package com.honeywell.hch.airtouch.plateform.enrollinterface;

/**
 * Created by Vincent on 29/1/2018.
 */

public interface IConnectAndDeviceManager {

    void startConnectingAndFinding(final String ssid, final String password, final int mLocalIp);
}
