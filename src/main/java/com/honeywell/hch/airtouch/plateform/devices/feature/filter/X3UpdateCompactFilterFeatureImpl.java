package com.honeywell.hch.airtouch.plateform.devices.feature.filter;


import android.content.Context;

import com.honeywell.hch.airtouch.plateform.R;
import com.honeywell.hch.airtouch.plateform.appmanager.AppManager;
import com.honeywell.hch.airtouch.plateform.config.AppConfig;
import com.honeywell.hch.airtouch.plateform.config.HPlusConstants;

/**
 * Created by Qian Jin on 3/28/16.
 */
public class X3UpdateCompactFilterFeatureImpl extends X3CompactFilterFeatureImpl {

    @Override
    public String[] getFilterPurchaseUrls() {
        return new String[]{AppConfig.shareInstance().getBasePurchaseUrl() +
                "version=2&model=AT620-prefilter&product=KJ620F-PAC2159R&country=China", AppConfig.shareInstance().getBasePurchaseUrl() +
                "version=2&model=AT620-polytechfilter&product=KJ620F-PAC2159R&country=China"};
    }

}
