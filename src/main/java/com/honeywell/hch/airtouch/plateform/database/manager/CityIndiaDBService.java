package com.honeywell.hch.airtouch.plateform.database.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.hch.airtouch.library.util.LogUtil;
import com.honeywell.hch.airtouch.plateform.config.AppConfig;
import com.honeywell.hch.airtouch.plateform.database.model.City;
import com.honeywell.hch.airtouch.plateform.database.model.PickerViewData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Qian Jin on 11/25/15.
 */
public class CityIndiaDBService extends DBService {

    //table info
    public static final String TABLE_NAME = "INCityInfo";
    public static final String ID = "id";
    public static final String CODE = "Code";
    public static final String CITY_NAME_CN = "CityName_CN";
    public static final String CITY_NAME_EN = "CityName_EN";
    public static final String PROVINCE_NAME_CN = "ProvinceName_CN";
    public static final String PROVINCE_NAME_EN = "ProvinceName_EN";
    public static final String COUNTRY_NAME = "CountryName";
    public static final String COUNTRY_CODE = "CountryCode";
    public static final String CITY_LEVEL = "CityLevel";

    private String[] DBKey = {ID, CITY_NAME_CN, CITY_NAME_EN, CODE,
            PROVINCE_NAME_CN, PROVINCE_NAME_EN,
            COUNTRY_NAME, COUNTRY_CODE, CITY_LEVEL};

    public CityIndiaDBService(Context context) {
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
            if (AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH)) {
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
                if (AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH)) {
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
        }

        return pickerViewData;
    }


//    public  ArrayList<City> getCitiesByKey(String key) {
//        ArrayList<City> cityList = new ArrayList<>();
//        SQLiteDatabase sqLiteDatabase = getSqliteDatabase();
//        Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
//                + " FROM " + TABLE_NAME
//                + " WHERE " + NAME_ZH + " LIKE '" + "%" + key + "%'"
//                + " OR " + NAME_EN + " LIKE '" + "%" + key + "%'"
//                + " COLLATE NOCASE", null);
//        while (cursor.moveToNext()) {
//            HashMap<String, String> cityMap = new HashMap<>();
//            for (int i = 0; i < DBKey.length; i++) {
//                cityMap.put(DBKey[i], cursor.getString(i));
//            }
//            cityList.add(new City(cityMap));
//        }
//        cursor.close();
//        sqLiteDatabase.close();
//        return cityList;
//    }

//    public  City getCityByKey(String key) {
//        City city = new City();
//        SQLiteDatabase sqLiteDatabase = getSqliteDatabase();
//        Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
//                + " FROM " + TABLE_NAME
//                + " WHERE " + NAME_ZH + " LIKE '" + "%" + key + "%'"
//                + " OR " + NAME_EN + " LIKE '" + "%" + key + "%'"
//                + " COLLATE NOCASE", null);
//        while (cursor.moveToNext()) {
//            HashMap<String, String> cityMap = new HashMap<>();
//            for (int i = 0; i < DBKey.length; i++) {
//                cityMap.put(DBKey[i], cursor.getString(i));
//            }
//            city = new City(cityMap);
//        }
//        cursor.close();
//        sqLiteDatabase.close();
//        return city;
//    }

    public  City getCityByName(String provinceName, String cityName) {
        SQLiteDatabase sqLiteDatabase = getSqliteDatabase();
        City city = new City();

        if (sqLiteDatabase != null) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
                    + " FROM " + TABLE_NAME
                    + " WHERE " + "(" + PROVINCE_NAME_CN + " = '" + provinceName + "'"
                    + " OR " + PROVINCE_NAME_EN + " = '" + provinceName + "'" + ")"
                    + " AND " + "(" + CITY_NAME_CN + " = '" + cityName + "'"
                    + " OR " + CITY_NAME_EN + " = '" + cityName + "'" + ")"
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
                    + " WHERE " + CODE + " = '" + cityCode + "'", null);

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
