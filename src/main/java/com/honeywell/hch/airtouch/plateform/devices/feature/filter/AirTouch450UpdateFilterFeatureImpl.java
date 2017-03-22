package com.honeywell.hch.airtouch.plateform.devices.feature.filter;


import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;

import com.honeywell.hch.airtouch.plateform.R;
import com.honeywell.hch.airtouch.plateform.appmanager.AppManager;
import com.honeywell.hch.airtouch.plateform.config.AppConfig;
import com.honeywell.hch.airtouch.plateform.config.HPlusConstants;

/**
 * Created by Qian Jin on 3/28/16.
 */
public class AirTouch450UpdateFilterFeatureImpl extends AirTouch450FilterFeatureImpl {

    @Override
    public String[] getFilterPurchaseUrls() {
        return new String[]{AppConfig.shareInstance().getBasePurchaseUrl() +
                "version=2&model=AT450U-prefilter&product=KJ450F-JAC2522S&country=China",
                AppConfig.shareInstance().getBasePurchaseUrl() +
                        "version=2&model=AT450U-HiSivCompositefilter&product=KJ450F-JAC2522S&country=China"};
    }
}
