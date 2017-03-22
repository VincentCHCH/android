package com.honeywell.hch.airtouch.plateform.devices.feature.filter;

import android.content.Context;

import com.honeywell.hch.airtouch.plateform.R;
import com.honeywell.hch.airtouch.plateform.appmanager.AppManager;
import com.honeywell.hch.airtouch.plateform.config.AppConfig;
import com.honeywell.hch.airtouch.plateform.config.HPlusConstants;

/**
 * Created by wuyuan on 4/19/16.
 */
public class Water75SFilterFeatureImpl implements IFilterFeature {

    private Context mContext = AppManager.getInstance().getApplication();

    @Override
    public int getFilterNumber() {
        return HPlusConstants.SMART_RO_FILTER_NUMBER;
    }


    @Override
    public String[] getFilterNames() {
        return new String[]{mContext.getString(R.string.composite_filter),
                mContext.getString(R.string.aqua_75_membrane_filter), mContext.getString(R.string.carbon_filter)};
    }

    @Override
    public String[] getFilterDescriptions() {
        return new String[]{mContext.getString(R.string.composite_filter_instruction),
                mContext.getString(R.string.membrane_filter_instruction), mContext.getString(R.string.carbon_filter_instruction)};
    }

    @Override
    public String[] getFilterPurchaseUrls() {
        return new String[]{AppConfig.shareInstance().getBasePurchaseUrl() +
                "model=RO75G-CompositeCartridge&product=YCZ-CT11-HRO-001&country=China&version=2",
                AppConfig.shareInstance().getBasePurchaseUrl() + "model=RO75G-ROCartridge&product=YCZ-CT11-HRO-001&country=China&version=2",
                AppConfig.shareInstance().getBasePurchaseUrl() + "model=RO75G-ActiveCarbonCartridge&product=YCZ-CT11-HRO-001&country=China&version=2"};
    }

    @Override
    public int[] getFilterImages() {
        return new int[]{R.drawable.aqua_filter, R.drawable.aqua_filter, R.drawable.aqua_filter,};
    }

}
