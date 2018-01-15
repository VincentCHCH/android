package com.honeywell.hch.airtouch.plateform.config;


import com.honeywell.hch.airtouch.library.util.StringUtil;
import com.honeywell.hch.airtouch.plateform.ap.WAPIRouter;
import com.honeywell.hch.airtouch.plateform.ap.model.WAPIDeviceResponse;
import com.honeywell.hch.airtouch.plateform.ap.model.WAPIKeyResponse;

import java.io.Serializable;

public class DIYInstallationState implements Serializable {

    private static final long serialVersionUID = -2083291746396398400L;

    private static volatile WAPIKeyResponse mWAPIKeyResponse;
    private static volatile WAPIDeviceResponse mWAPIDeviceResponse;
    private static volatile  WAPIRouter mWAPIRouter;
    private static volatile  String mDeviceName = "";
    private static volatile  String mHomeName;
    private static  volatile String mCityCode;
    private static int errorCode;
    private static volatile  String mHomeConnectedSsid;
    private static boolean isDeviceAlreadyEnrolled = false;
    private static boolean isUpdateWifi = false;
    private static String mDeviceType = "";
    private static volatile  String mSsid;
    private static int mLocationId;
    private static int mEnrollDeviceId;

    public static String getmHomeConnectedSsid() {
        return mHomeConnectedSsid;
    }

    public static void setmHomeConnectedSsid(String mHomeConnectedSsid) {
        DIYInstallationState.mHomeConnectedSsid = mHomeConnectedSsid;
    }

    public static String getCityCode() {
        return mCityCode;
    }

    public static void setCityCode(String cityCode) {
        DIYInstallationState.mCityCode = cityCode;
    }

    public static String getDeviceName() {
        if (StringUtil.isEmpty(mDeviceName)){
            mDeviceName = "Tuna";
        }
        return mDeviceName;
    }

    public static void setDeviceName(String deviceName) {
        DIYInstallationState.mDeviceName = deviceName;
    }

    public static String getHomeName() {
        return mHomeName;
    }

    public static void setHomeName(String mHomeName) {
        DIYInstallationState.mHomeName = mHomeName;
    }

    public static int getErrorCode() {
        return errorCode;
    }

    public static void setErrorCode(int errorCode) {
        DIYInstallationState.errorCode = errorCode;
    }

    public static WAPIKeyResponse getWAPIKeyResponse() {
        return mWAPIKeyResponse;
    }

    public static void setWAPIKeyResponse(WAPIKeyResponse wapiKeyResponse) {
        mWAPIKeyResponse = wapiKeyResponse;
    }

    public static WAPIDeviceResponse getWAPIDeviceResponse() {
        return mWAPIDeviceResponse;
    }

    public static void setWAPIDeviceResponse(WAPIDeviceResponse wapiDeviceResponse) {
        mWAPIDeviceResponse = wapiDeviceResponse;
    }

    public static WAPIRouter getWAPIRouter() {
        return mWAPIRouter;
    }

    public static void setWAPIRouter(WAPIRouter wapiRouter) {
        mWAPIRouter = wapiRouter;
    }

    public static boolean getIsDeviceAlreadyEnrolled() {
        return isDeviceAlreadyEnrolled;
    }

    public static void setIsDeviceAlreadyEnrolled(Boolean isDeviceAlreadyEnrolled) {
        DIYInstallationState.isDeviceAlreadyEnrolled = isDeviceAlreadyEnrolled;
    }

    public static String getDeviceType() {
        return mDeviceType;
    }

    public static void setDeviceType(String mDeviceType) {
        DIYInstallationState.mDeviceType = mDeviceType;
    }

    public static String getSsid() {
        return mSsid;
    }

    public static void setSsid(String mSsid) {
        DIYInstallationState.mSsid = mSsid;
    }

    public static int getLocationId() {
        return mLocationId;
    }

    public static void setLocationId(int locationId) {
        DIYInstallationState.mLocationId = locationId;
    }

    public static boolean isUpdateWifi() {
        return isUpdateWifi;
    }

    public static void setIsUpdateWifi(boolean isUpdateWifi) {
        DIYInstallationState.isUpdateWifi = isUpdateWifi;
    }

    public static int getEnrollDeviceId() {
        return mEnrollDeviceId;
    }

    public static void setEnrollDeviceId(int enrollDeviceId) {
        DIYInstallationState.mEnrollDeviceId = enrollDeviceId;
    }

    /**
     * 在每次进入enroll的开始界面，都进行reset操作，保证这个对象里的所有状态为初始状态
     */
    public static void reset() {
        mWAPIKeyResponse = null;
        mWAPIDeviceResponse = null;
        mWAPIRouter = null;
        mDeviceName = "";
        mHomeName = null;
        mCityCode = null;
        errorCode = 0;
        mHomeConnectedSsid = null;
        isDeviceAlreadyEnrolled = false;
        isUpdateWifi = false;
        mDeviceType = "";
        mSsid = null;
        mLocationId = 0;
        mEnrollDeviceId = 0;
    }
}
