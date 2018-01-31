package com.honeywell.hch.airtouch.plateform.easylinkv3.helper;

/**
 * Many javas will use this function Project：MiCOSDK
 * Author：Sin Creat time
 * 2016-01-20
 *
 * @version 1.0
 */
public class ComHelper {

	/**
	 * Check argument, whether it is null or blank
	 *
	 * @param param the paraments you want to check
	 * @return true or false
	 */
	public static boolean checkPara(String... param) {
		if (null == param || param.equals("")) {
			return false;
		} else if (param.length > 0) {
			for (String str : param) {
				if (null == str || str.equals("")) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * check para is number and can send to devices
	 */
	public static boolean isInteger(String value) {
		try {
			int isno = Integer.parseInt(value);
			if(isno > 999 && isno <10000)
				return true;
			else
				return  false;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * EasyLink.
	 * Check if we will call back to the developer.
	 * @param code error code
	 * @param message string message
	 * @param easylinkcb callback
     */
	public void successCBEasyLink(int code, String message, EasyLinkCallBack easylinkcb) {
		if (null == easylinkcb)
			return;
		easylinkcb.onSuccess(code, message);
	}
	public void failureCBEasyLink(int code, String message, EasyLinkCallBack easylinkcb) {
		if (null == easylinkcb)
			return;
		easylinkcb.onFailure(code, message);
	}
}
