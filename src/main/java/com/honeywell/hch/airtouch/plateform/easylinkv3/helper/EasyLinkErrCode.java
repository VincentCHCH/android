package com.honeywell.hch.airtouch.plateform.easylinkv3.helper;

public class EasyLinkErrCode{

	public static int START_CODE = 0;
	public static int STOP_CODE = 4000;
	public static int INVALID_CODE = 4001;
	public static int CONTEXT_CODE = 4002;
	public static int BUSY_CODE = 4003;
	public static int CLOSED_CODE = 4004;
	public static int CALLBACK_CODE = 4005;
	public static int EXCEPTION_CODE = 4006;

	public static String SUCCESS = toJsonM("success");
	public static String INVALID = toJsonM("invalid param");
	public static String BUSY = toJsonM("easylink busy");
	public static String CLOSED = toJsonM("easylink closed");
	public static String CONTEXT = toJsonM("invalid context");

	private static String toJsonM(String message){
		return "{\"message\":\""+ message +"\"}";
	}
}
