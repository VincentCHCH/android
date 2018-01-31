package com.honeywell.hch.airtouch.plateform.easylinkv3.helper;

/**
 * Created by Vincent on 29/1/2018.
 */
public class MyEasyLinkParams {
    public byte[] ssid;
    public byte[] password;
    public boolean isSendIP;
    public int runSecond;
    public int sleeptime;
    public byte[] extraData;
    public String rc4key;
    public boolean isSmallMTU;
    public byte[] userInfo;
    public int mIpAddress;

    public MyEasyLinkParams() {
        this.isSendIP = true;
        this.isSmallMTU = false;
        this.rc4key = "";
        this.runSecond = 60000;
        this.sleeptime = 50;

    }
}
