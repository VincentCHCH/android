package com.honeywell.hch.airtouch.plateform.database.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by nan.liu on 2/5/15.
 */
public class City implements Serializable, IDBModel {

    private int mId;
    private String mCode;
    private String mDistrictNameCN;
    private String mDistrictNameEN;
    private String mCityNameCN;
    private String mCityNameEN;
    private String mProvinceNameCN;
    private String mProvinceNameEN;
    private String mCountryName;
    private String mCountryCode;
    private String mCityLevel;

    public City(){

    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getDistrictNameCN() {
        return mDistrictNameCN;
    }

    public void setDistrictNameCN(String districtNameCN) {
        mDistrictNameCN = districtNameCN;
    }

    public String getDistrictNameEN() {
        return mDistrictNameEN;
    }

    public void setDistrictNameEN(String districtNameEN) {
        mDistrictNameEN = districtNameEN;
    }

    public String getCityNameCN() {
        return mCityNameCN;
    }

    public void setCityNameCN(String cityNameCN) {
        mCityNameCN = cityNameCN;
    }

    public String getCityNameEN() {
        return mCityNameEN;
    }

    public void setCityNameEN(String cityNameEN) {
        mCityNameEN = cityNameEN;
    }

    public String getProvinceNameCN() {
        return mProvinceNameCN;
    }

    public void setProvinceNameCN(String provinceNameCN) {
        mProvinceNameCN = provinceNameCN;
    }

    public String getProvinceNameEN() {
        return mProvinceNameEN;
    }

    public void setProvinceNameEN(String provinceNameEN) {
        mProvinceNameEN = provinceNameEN;
    }

    public String getCountryName() {
        return mCountryName;
    }

    public void setCountryName(String countryName) {
        mCountryName = countryName;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public String getCityLevel() {
        return mCityLevel;
    }

    public void setCityLevel(String cityLevel) {
        mCityLevel = cityLevel;
    }

    public City(HashMap<String, String> cityMap) {
        mId = Integer.parseInt(cityMap.get("id"));
        mCode = cityMap.get("Code");
        mDistrictNameCN = cityMap.get("DistrictName_CN");
        mDistrictNameEN = cityMap.get("DistrictName_EN");
        mCityNameCN = cityMap.get("CityName_CN");
        mCityNameEN = cityMap.get("CityName_EN");
        mProvinceNameCN = cityMap.get("ProvinceName_CN");
        mProvinceNameEN = cityMap.get("ProvinceName_EN");
        mCountryName = cityMap.get("CountryName");
        mCountryCode = cityMap.get("CountryCode");
        mCityLevel = cityMap.get("CityLevel");
    }

    public HashMap<String, Object> getHashMap() {
        HashMap<String, Object> cityMap = new HashMap<>();
        cityMap.put("id", mId);
        cityMap.put("Code", mCode);
        cityMap.put("DistrictName_CN", mDistrictNameCN);
        cityMap.put("DistrictName_EN", mDistrictNameEN);
        cityMap.put("CityName_CN", mCityNameCN);
        cityMap.put("CityName_EN", mCityNameEN);
        cityMap.put("ProvinceName_CN", mProvinceNameCN);
        cityMap.put("ProvinceName_EN", mProvinceNameEN);
        cityMap.put("CountryName", mCountryName);
        cityMap.put("CountryCode", mCountryCode);
        cityMap.put("CityLevel", mCityLevel);
        return cityMap;
    }
}
