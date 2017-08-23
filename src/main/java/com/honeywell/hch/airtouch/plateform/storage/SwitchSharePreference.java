package com.honeywell.hch.airtouch.plateform.storage;

import com.honeywell.hch.airtouch.library.util.SharePreferenceUtil;
import com.honeywell.hch.airtouch.plateform.config.AppConfig;

/**
 * Created by Vincent on 9/11/16.
 */
public class SwitchSharePreference {
    private final static String DEVELOP_ENV_KEY = "develop_env_key";
    private final static String DEVELOP_INFO_SHAREPREFERENCE = "develop_info_sharepreference";
    private final static String SELF_HTTP_KEY = "self_http_key";
    private final static String SELF_WEBSOCKET_KEY = "self_websocket_key";
    public static void saveDevelopEnv(int id) {
        AppConfig.isChangeEnv = true;
        SharePreferenceUtil.setPrefInt(DEVELOP_INFO_SHAREPREFERENCE,
                DEVELOP_ENV_KEY, id);
    }

    public static int getDevelopEnv() {
        return SharePreferenceUtil.getPrefInt(DEVELOP_INFO_SHAREPREFERENCE, DEVELOP_ENV_KEY, AppConfig.QA_ENV);
    }

    public static void saveSelfHttpUrl(String url) {
        AppConfig.isChangeEnv = true;
        SharePreferenceUtil.setPrefString(DEVELOP_INFO_SHAREPREFERENCE,
                SELF_HTTP_KEY, url);
    }

    public static void saveWebsocketUrl(String url) {
        AppConfig.isChangeEnv = true;
        SharePreferenceUtil.setPrefString(DEVELOP_INFO_SHAREPREFERENCE,
                SELF_WEBSOCKET_KEY, url);
    }

    public static String gethttpUrl() {
        return SharePreferenceUtil.getPrefString(DEVELOP_INFO_SHAREPREFERENCE, SELF_HTTP_KEY, "");
    }

    public static String getWebsocketUrl() {
        return SharePreferenceUtil.getPrefString(DEVELOP_INFO_SHAREPREFERENCE, SELF_WEBSOCKET_KEY, "");
    }
}
