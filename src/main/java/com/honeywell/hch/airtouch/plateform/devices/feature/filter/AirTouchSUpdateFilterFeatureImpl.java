package com.honeywell.hch.airtouch.plateform.devices.feature.filter;


import com.honeywell.hch.airtouch.plateform.config.AppConfig;

/**
 * Created by Qian Jin on 3/28/16.
 */
public class AirTouchSUpdateFilterFeatureImpl extends AirTouch450FilterFeatureImpl {

    @Override
    public String[] getFilterPurchaseUrls() {
        return new String[]{AppConfig.shareInstance().getBasePurchaseUrl() +
                "version=2&model=AT300U-prefilter&product=KJ300F-JAC2801W&country=China",
                AppConfig.shareInstance().getBasePurchaseUrl() + "version=2&model=AT300U-HiSivCompositefilter&product=KJ300F-JAC2801W&country=China"
        };
    }
}
