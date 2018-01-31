package com.honeywell.hch.airtouch.plateform.easylinkv3.helper;

/**
 *
 * @author Sin
 *
 * on 2016-6-23
 */
public class EasyLinkParams {
	public String ssid;
	public String password;
	public boolean isSendIP;
	public int runSecond;
	public int sleeptime;
	public String extraData;
	public String rc4key;
	public boolean isSmallMTU;

	public EasyLinkParams(){
		this.ssid = "";
		this.password = "";
		this.isSendIP = false;
		this.isSmallMTU = false;
		this.extraData = "";
		this.rc4key = "";
		this.runSecond = 60000;
		this.sleeptime = 50;
	}
}
