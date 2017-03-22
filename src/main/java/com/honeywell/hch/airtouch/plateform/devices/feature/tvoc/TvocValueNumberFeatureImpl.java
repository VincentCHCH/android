package com.honeywell.hch.airtouch.plateform.devices.feature.tvoc;

import com.honeywell.hch.airtouch.plateform.R;
import com.honeywell.hch.airtouch.plateform.appmanager.AppManager;
import com.honeywell.hch.airtouch.plateform.config.HPlusConstants;
import com.honeywell.hch.airtouch.plateform.devices.airtouch.model.AirtouchRunStatus;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by h127856 on 16/10/25.
 * tvoc的值为数字的
 */
public class TvocValueNumberFeatureImpl extends TvocFeatureBaseImpl{

    public TvocValueNumberFeatureImpl(AirtouchRunStatus airtouchRunStatus){
        super(airtouchRunStatus);
    }

    @Override
    protected String getDetailFeatureValue(float tvocValueF) {
        if (tvocValueF >= 0 && tvocValueF < HPlusConstants.ERROR_SENSOR) {
            if (tvocValueF > 990) {
                return HPlusConstants.MAX_TVOC_VALUE;
            }
            DecimalFormat decimalFormat = new DecimalFormat(HPlusConstants.FLOATFORMAT);//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String value = decimalFormat.format(tvocValueF / 1000);//format 返回的是字符串
            return value;
        } else {
            return HPlusConstants.DATA_LOADING_STATUS;
        }
    }

    @Override
    protected int getDetailFeatureLevel(double tvocValueF) {
        BigDecimal bg = null;
        if (tvocValueF > 0) {
            bg = new BigDecimal(tvocValueF / 1000);
        }
        if (tvocValueF > 0 && tvocValueF < HPlusConstants.PM25_MAX_VALUE) {
            tvocValueF = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        if (tvocValueF >= 0 && tvocValueF < HPlusConstants.TVOC_LOW_LIMIT_FOR_PREMIUM) {
            return HPlusConstants.TVOC_GOOD_LEVEL;
        } else if (tvocValueF < HPlusConstants.TVOC_HIGH_LIMIT_FOR_PREMIUM) {
            return HPlusConstants.TVOC_MID_LEVEL;
        } else if (tvocValueF != HPlusConstants.ERROR_MAX_VALUE && tvocValueF != HPlusConstants.ERROR_SENSOR) {
            return HPlusConstants.TVOC_BAD_LEVEL;
        } else if (tvocValueF == HPlusConstants.ERROR_MAX_VALUE) {
            return HPlusConstants.TVOC_ERROR_LEVEL;
        } else if (tvocValueF == HPlusConstants.ERROR_SENSOR) {
            return HPlusConstants.TVOC_SENSOR_ERROR_LEVEL;
        }
        return HPlusConstants.TVOC_ERROR_LEVEL;
    }

    @Override
    protected int getDetailFeatureColor(float tvocValueF) {
        if (tvocValueF > 0) {
            tvocValueF = tvocValueF / 1000;
        }
        if (tvocValueF > 0 && tvocValueF <= HPlusConstants.TVOC_LOW_LIMIT_FOR_PREMIUM) {
            return AppManager.getInstance().getApplication().getResources().getColor(R.color.pm_25_good);
        } else if (tvocValueF <= HPlusConstants.TVOC_HIGH_LIMIT_FOR_PREMIUM) {
            return AppManager.getInstance().getApplication().getResources().getColor(R.color.pm_25_bad);
        } else {
            return AppManager.getInstance().getApplication().getResources().getColor(R.color.pm_25_worst);
        }
    }
}
