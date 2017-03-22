package com.honeywell.hch.airtouch.plateform.devices.feature.filter;


import android.content.Context;

import com.honeywell.hch.airtouch.plateform.R;
import com.honeywell.hch.airtouch.plateform.appmanager.AppManager;
import com.honeywell.hch.airtouch.plateform.config.AppConfig;
import com.honeywell.hch.airtouch.plateform.config.HPlusConstants;

/**
 * Created by Qian Jin on 3/28/16.
 */
public class X3CompactFilterFeatureImpl implements IFilterFeature {

    protected Context mContext = AppManager.getInstance().getApplication();

    @Override
    public int getFilterNumber() {
        return HPlusConstants.AIR_TOUCH_XCOMPACT_FILTER_NUMBER;
    }


    @Override
    public CharSequence[] getFilterNames() {
        return new CharSequence[]{mContext.getString(R.string.pre_filter), mContext.getString(R.string.airpremium_filter_title)};
    }

    @Override
    public CharSequence[] getFilterDescriptions() {
        return new CharSequence[]{mContext.getString(R.string.pre_filter_instruction), mContext.getString(R.string.airpremium_filter_des)};
    }

    @Override
    public String[] getFilterPurchaseUrls() {
        return new String[]{AppConfig.shareInstance().getBasePurchaseUrl() +
                "version=2&model=AT600-prefilter&product=KJ600F-PAC2158A&country=China", AppConfig.shareInstance().getBasePurchaseUrl() +
                "version=2&model=AT600-polytechfilter&product=KJ600F-PAC2158A&country=China"};
    }

    @Override
    public int[] getFilterImages() {
        return new int[]{R.drawable.ic_pre_filter, R.drawable.premium_polytesh};
    }

}
