package com.honeywell.hch.airtouch.plateform.database.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.hch.airtouch.plateform.config.AppConfig;
import com.honeywell.hch.airtouch.plateform.database.model.City;
import com.honeywell.hch.airtouch.plateform.database.model.PickerViewData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nan.liu on 2/3/15.
 */
public class CityChinaDBService extends DBService {

    //table info
    public static final String TABLE_NAME = "CNCityInfo";
    public static final String ID = "id";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String COUNTRY_NAME_CN = "countryCN";
    public static final String COUNTRY_NAME_EN = "countryEN";
    public static final String PROVINCE_NAME_CN = "provinceCN";
    public static final String PROVINCE_NAME_EN = "provinceEN";
    public static final String CITY_NAME_CN = "cityCN";
    public static final String CITY_NAME_EN = "cityEN";
    public static final String DISTRICT_NAME_CN = "districtCN";
    public static final String DISTRICT_NAME_EN = "districtEN";

    private String[] DBKey = {ID, COUNTRY_CODE, COUNTRY_NAME_CN, COUNTRY_NAME_EN, PROVINCE_NAME_CN, PROVINCE_NAME_EN,
    CITY_NAME_CN, CITY_NAME_EN, DISTRICT_NAME_CN, DISTRICT_NAME_EN};

    public CityChinaDBService(Context context) {
        super(context);
    }

    public  void insertAllCity(List<City> list) {
        List<HashMap<String, Object>> cityList = new ArrayList<>();
        for (City cityInfo : list) {
            cityList.add(cityInfo.getHashMap());
        }
        insertOrUpdate(TABLE_NAME, DBKey, cityList);
    }

    public  ArrayList<City> findAllCities() {
        ArrayList<HashMap<String, String>> cityDBList = findAll(TABLE_NAME, DBKey);
        ArrayList<City> cityList = new ArrayList<>();
        for (HashMap<String, String> cityMap : cityDBList) {
            cityList.add(new City(cityMap));
        }
        return cityList;
    }

    public PickerViewData getPickerViewData() {
        PickerViewData pickerViewData = new PickerViewData();
        ArrayList<String> pickerViewProvince = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getSqliteDatabase();
        if (sqLiteDatabase != null) {
            if ( AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH)) {
                Cursor cursor_p = sqLiteDatabase.rawQuery("SELECT DISTINCT " + PROVINCE_NAME_CN
                    + " FROM " + TABLE_NAME, null);
                while (cursor_p.moveToNext()) {
                    pickerViewProvince.add(cursor_p.getString(0));
                }
                cursor_p.close();

            } else {
                Cursor cursor_p = sqLiteDatabase.rawQuery("SELECT DISTINCT " + PROVINCE_NAME_EN
                        + " FROM " + TABLE_NAME, null);
                while (cursor_p.moveToNext()) {
                    pickerViewProvince.add(cursor_p.getString(0));
                }
                cursor_p.close();
            }
            pickerViewData.setProvinceList(pickerViewProvince);

            ArrayList<ArrayList<String>> pickerViewCities = new ArrayList<>();
            for (String province : pickerViewProvince) {
                ArrayList<String> cityNameList = new ArrayList<>();
                if ( AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH)) {
                    Cursor cursor_c = sqLiteDatabase.rawQuery("SELECT DISTINCT " + CITY_NAME_CN
                        + " FROM " + TABLE_NAME
                        + " WHERE " + PROVINCE_NAME_CN + " = '" + province + "'"
                        + " COLLATE NOCASE", null);
                    while (cursor_c.moveToNext()) {
                        cityNameList.add(cursor_c.getString(0));
                    }
                    cursor_c.close();

                } else {
                    Cursor cursor_c = sqLiteDatabase.rawQuery("SELECT DISTINCT " + CITY_NAME_EN
                            + " FROM " + TABLE_NAME
                            + " WHERE " + PROVINCE_NAME_EN + " = '" + province + "'"
                            + " COLLATE NOCASE", null);
                    while (cursor_c.moveToNext()) {
                        cityNameList.add(cursor_c.getString(0));
                    }
                    cursor_c.close();
                }
                pickerViewCities.add(cityNameList);
            }

            pickerViewData.setCityList(pickerViewCities);

            ArrayList<ArrayList<ArrayList<String>>> pickerViewDistricts = new ArrayList<>();

            for(int j = 0 ; j < pickerViewCities.size() ; j++) {
                String province = pickerViewProvince.get(j);
                ArrayList<String> cities = pickerViewCities.get(j);
                ArrayList<ArrayList<String>> cityDistictList = new ArrayList<>();
                for (String city : cities) {
                    ArrayList<String> districtList = new ArrayList<>();
                    if ( AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH)) {

                        Cursor cursor_d = sqLiteDatabase.rawQuery("SELECT " + DISTRICT_NAME_CN
                                + " FROM " + TABLE_NAME
                                + " WHERE " + PROVINCE_NAME_CN + " = '" + province + "'"
                                + " AND " + CITY_NAME_CN + " = '" + city + "'"
                                + " COLLATE NOCASE", null);

                        while (cursor_d.moveToNext()) {
                            districtList.add(cursor_d.getString(0));
                        }
                        cursor_d.close();

                    } else {
                        Cursor cursor_d = sqLiteDatabase.rawQuery("SELECT " + DISTRICT_NAME_EN
                                + " FROM " + TABLE_NAME
                                + " WHERE " + PROVINCE_NAME_EN + " = '" + province + "'"
                                + " AND " + CITY_NAME_EN + " = '" + city + "'"
                                + " COLLATE NOCASE", null);
                        while (cursor_d.moveToNext()) {
                            districtList.add(cursor_d.getString(0));
                        }
                        cursor_d.close();
                    }
                    cityDistictList.add(districtList);
                }
                pickerViewDistricts.add(cityDistictList);
                pickerViewData.setDistrictList(pickerViewDistricts);
            }
            sqLiteDatabase.close();
        }

        return pickerViewData;
    }

//    public  ArrayList<City> getCitiesByKey(String key) {
//        ArrayList<City> cityList = new ArrayList<>();
//        SQLiteDatabase sqLiteDatabase = getSqliteDatabase();
//        if (sqLiteDatabase != null) {
//            Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
//                    + " FROM " + TABLE_NAME
//                    + " WHERE " + NAME_ZH + " LIKE '" + "%" + key + "%'"
//                    + " OR " + NAME_EN + " LIKE '" + "%" + key + "%'"
//                    + " COLLATE NOCASE", null);
//            while (cursor.moveToNext()) {
//                HashMap<String, String> cityMap = new HashMap<>();
//                for (int i = 0; i < DBKey.length; i++) {
//                    cityMap.put(DBKey[i], cursor.getString(i));
//                }
//                cityList.add(new City(cityMap));
//            }
//            cursor.close();
//            sqLiteDatabase.close();
//        }
//
//        return cityList;
//    }
//
    public  City getCityByName(String provinceName, String cityName, String districtName) {
        SQLiteDatabase sqLiteDatabase = getSqliteDatabase();
        City city = new City();

        if (sqLiteDatabase != null) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
                    + " FROM " + TABLE_NAME
                    + " WHERE " + "(" + PROVINCE_NAME_CN + " = '" + provinceName + "'"
                    + " OR " + PROVINCE_NAME_EN + " = '" + provinceName + "'" + ")"
                    + " AND " + "(" + CITY_NAME_CN + " = '" + cityName + "'"
                    + " OR " + CITY_NAME_EN + " = '" + cityName + "'" + ")"
                    + " AND " + "(" + DISTRICT_NAME_CN + " = '" + districtName + "'"
                    + " OR " + DISTRICT_NAME_EN + " = '" + districtName + "'" + ")"
                    + " COLLATE NOCASE", null);

            if (cursor.getCount() != 0) {
                cursor.moveToNext();
                HashMap<String, String> cityMap = new HashMap<>();
                for (int i = 0; i < DBKey.length; i++) {
                    cityMap.put(DBKey[i], cursor.getString(i));
                }
                city = new City(cityMap);
            }
            cursor.close();
            sqLiteDatabase.close();
        }


        return city;
    }

    public City getCityByCode(String cityCode) {
        City city = new City();
        SQLiteDatabase sqLiteDatabase = getSqliteDatabase();
        if (sqLiteDatabase != null) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
                    + " FROM " + TABLE_NAME
                    + " WHERE " + ID + " = '" + cityCode + "'", null);

            if (cursor.getCount() != 0) {
                cursor.moveToNext();
                HashMap<String, String> cityMap = new HashMap<>();
                for (int i = 0; i < DBKey.length; i++) {
                    cityMap.put(DBKey[i], cursor.getString(i));
                }
                city = new City(cityMap);
            }

            cursor.close();
            sqLiteDatabase.close();
        }


        return city;
    }
}
