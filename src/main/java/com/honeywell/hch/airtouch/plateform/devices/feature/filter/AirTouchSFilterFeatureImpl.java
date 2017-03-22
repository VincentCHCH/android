package com.honeywell.hch.airtouch.plateform.devices.feature.filter;


import android.content.Context;

import com.honeywell.hch.airtouch.plateform.R;
import com.honeywell.hch.airtouch.plateform.appmanager.AppManager;
import com.honeywell.hch.airtouch.plateform.config.AppConfig;
import com.honeywell.hch.airtouch.plateform.config.HPlusConstants;

/**
 * Created by Qian Jin on 3/28/16.
 */
public class AirTouchSFilterFeatureImpl implements IFilterFeature {

    protected Context mContext = AppManager.getInstance().getApplication();


    @Override
    public int getFilterNumber() {
        return HPlusConstants.AIR_TOUCH_S_FILTER_NUMBER;
    }


    @Override
    public CharSequence[] getFilterNames() {
        return new CharSequence[]{mContext.getString(R.string.pre_filter),
                mContext.getString(R.string.pm25_filter),
                mContext.getString(R.string.hisiv_filter)};
    }

    @Override
    public CharSequence[] getFilterDescriptions() {
        return new CharSequence[]{mContext.getString(R.string.pre_filter_instruction),
                mContext.getString(R.string.pm25_filter_instruction),
                mContext.getString(R.string.hisiv_filter_instruction)};
    }

    @Override
    public String[] getFilterPurchaseUrls() {
        return new String[]{AppConfig.shareInstance().getBasePurchaseUrl() +
                "version=2&model=prefilter&product=PAC35M2101S&country=China",
                AppConfig.shareInstance().getBasePurchaseUrl() + "version=2&model=hepafilter&product=PAC35M2101S&country=China",
                AppConfig.shareInstance().getBasePurchaseUrl() + "version=2&model=chemicalfilter&product=PAC35M2101S&country=China"};
    }


    @Override
    public int[] getFilterImages() {
        return new int[]{R.drawable.ic_pre_filter, R.drawable.ic_hepa_filter, R.drawable.ic_hisiv_filter};
    }
}
