package com.honeywell.hch.airtouch.plateform.appmanager;

import android.app.Application;


/**
 * Initial mHPlusApplication
 */
public class AppManager {

    private static String INDIA_COUNTRY_CODE = "IN";

    private static String TAG = AppManager.class.getSimpleName();

    private static AppManager mAppManager =null;

    private Application mHPlusApplication = null;


    public Application getApplication() {
        return mHPlusApplication;
    }

    public void setHPlusApplication(Application application) {
        mHPlusApplication = application;
    }


    private AppManager() {
    }

    public synchronized static AppManager getInstance() {
        if (mAppManager == null) {
            mAppManager = new AppManager();
        }
        return mAppManager;
    }


}
