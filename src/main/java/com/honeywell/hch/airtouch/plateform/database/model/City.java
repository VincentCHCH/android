package com.honeywell.hch.airtouch.plateform.database.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouch.library.http.model.IRequestParams;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by nan.liu on 2/5/15.
 */
public class City implements IRequestParams, Serializable, IDBModel {

    @SerializedName("id")
    private String mId;

    @SerializedName("countryCode")
    private String mCountryCode;

    @SerializedName("countryCN")
    private String mCountryCN;

    @SerializedName("countryEN")
    private String mCountryEN;

    @SerializedName("provinceCN")
    private String mProvinceCN;

    @SerializedName("provinceEN")
    private String mProvinceEN;

    @SerializedName("cityCN")
    private String mCityCN;

    @SerializedName("cityEN")
    private String mCityEN;

    @SerializedName("districtCN")
    private String mDistrictCN;

    @SerializedName("districtEN")
    private String mDistrictEN;

    public City(){

    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public String getCountryCN() {
        return mCountryCN;
    }

    public void setCountryCN(String countryCN) {
        mCountryCN = countryCN;
    }

    public String getCountryEN() {
        return mCountryEN;
    }

    public void setCountryEN(String countryEN) {
        mCountryEN = countryEN;
    }

    public String getProvinceCN() {
        return mProvinceCN;
    }

    public void setProvinceCN(String provinceCN) {
        mProvinceCN = provinceCN;
    }

    public String getProvinceEN() {
        return mProvinceEN;
    }

    public void setProvinceEN(String provinceEN) {
        mProvinceEN = provinceEN;
    }

    public String getCityCN() {
        return mCityCN;
    }

    public void setCityCN(String cityCN) {
        mCityCN = cityCN;
    }

    public String getCityEN() {
        return mCityEN;
    }

    public void setCityEN(String cityEN) {
        mCityEN = cityEN;
    }

    public String getDistrictCN() {
        return mDistrictCN;
    }

    public void setDistrictCN(String districtCN) {
        mDistrictCN = districtCN;
    }

    public String getDistrictEN() {
        return mDistrictEN;
    }

    public void setDistrictEN(String districtEN) {
        mDistrictEN = districtEN;
    }

    public City(HashMap<String, String> cityMap) {
        mId = cityMap.get("id");
        mCountryCode = cityMap.get("countryCode");
        mCountryCN = cityMap.get("countryCN");
        mCountryEN = cityMap.get("countryEN");
        mProvinceCN = cityMap.get("provinceCN");
        mProvinceEN = cityMap.get("provinceEN");
        mCityCN = cityMap.get("cityCN");
        mCityEN = cityMap.get("cityEN");
        mDistrictCN = cityMap.get("districtCN");
        mDistrictEN = cityMap.get("districtEN");
    }

    public HashMap<String, Object> getHashMap() {
        HashMap<String, Object> cityMap = new HashMap<>();
        cityMap.put("id", mId);
        cityMap.put("countryCode", mCountryCode);
        cityMap.put("countryCN", mCountryCN);
        cityMap.put("countryEN", mCountryEN);
        cityMap.put("provinceCN", mProvinceCN);
        cityMap.put("provinceEN", mProvinceEN);
        cityMap.put("cityCN", mCityCN);
        cityMap.put("cityEN", mCityEN);
        cityMap.put("districtCN", mDistrictCN);
        cityMap.put("districtEN", mDistrictEN);
        return cityMap;
    }

    @Override
    public String getRequest(Gson gson) {
        return gson.toJson(this);
    }

    @Override
    public String getPrintableRequest(Gson gson) {
        return getRequest(gson);
    }
}
