package com.ngvietan.blackoutschedule.common;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by NgVietAn on 03/08/2015.
 */
public class Constants {
    public static final String SHARED_PREF = "blackoutPref";

    public static final String PREF_FIRST_TIME = "prefFirstTime";

    public static final String PREF_PROVINCE = "prefProvince";

    public static final String PREF_DISTRICT = "prefDistrict";

    public static final String PREF_SETTING_UPDATE = "prefSettingUpdate";

    public static final SimpleDateFormat DATE_WEB_FORMAT = new SimpleDateFormat("dd/MM/yyyy",
            new Locale("vi", "VN"));

    public static final SimpleDateFormat DATE_WEB_FORMAT_2 = new SimpleDateFormat("dd/MM/yy",
            new Locale("vi", "VN"));

    public static final SimpleDateFormat DATE_DB_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    public static final String SETTING_WIFI = "wifi";

    public static final String SETTING_MOBILE = "mobile";

    public static final String SETTING_BOTH = "wifi_mobile";
}
