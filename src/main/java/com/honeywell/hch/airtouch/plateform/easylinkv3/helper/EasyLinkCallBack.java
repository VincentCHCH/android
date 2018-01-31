package com.honeywell.hch.airtouch.plateform.easylinkv3.helper;

public interface EasyLinkCallBack {
	public void onSuccess(int code, String message);
	public void onFailure(int code, String message);
}
