package com.honeywell.hch.airtouch.plateform.database.model;

import java.util.ArrayList;

/**
 * Created by h234385 on 31/10/2017.
 */

public class PickerViewData {
    private ArrayList<String> mProvinceList;
    private ArrayList<ArrayList<String>> mCityList;
    private ArrayList<ArrayList<ArrayList<String>>> mDistrictList;

    public ArrayList<String> getProvinceList() {
        return mProvinceList;
    }

    public void setProvinceList(ArrayList<String> provinceList) {
        mProvinceList = provinceList;
    }

    public ArrayList<ArrayList<String>> getCityList() {
        return mCityList;
    }

    public void setCityList(ArrayList<ArrayList<String>> cityList) {
        mCityList = cityList;
    }

    public ArrayList<ArrayList<ArrayList<String>>> getDistrictList() {
        return mDistrictList;
    }

    public void setDistrictList(ArrayList<ArrayList<ArrayList<String>>> districtList) {
        mDistrictList = districtList;
    }
}
