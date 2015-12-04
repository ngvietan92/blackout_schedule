package com.ngvietan.blackoutschedule.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import com.activeandroid.ActiveAndroid;
import com.ngvietan.blackoutschedule.R;
import com.ngvietan.blackoutschedule.models.District;
import com.ngvietan.blackoutschedule.models.Province;

/**
 * Created by NgVietAn on 02/08/2015.
 */
public class CommonMethods {

    public static void initializeData() {
        ActiveAndroid.beginTransaction();
        try {
            // da nang
            Province daNangProvince = new Province(Province.ID_DANANG, "Đà Nẵng");
            daNangProvince.save();
            District daNangDistrict = new District(daNangProvince, "ĐL Hải Châu");
            daNangDistrict.save();
            daNangDistrict = new District(daNangProvince, "ĐL Thanh Khê");
            daNangDistrict.save();
            daNangDistrict = new District(daNangProvince, "ĐL Cẩm Lệ");
            daNangDistrict.save();
            daNangDistrict = new District(daNangProvince, "ĐL Sơn Trà");
            daNangDistrict.save();
            daNangDistrict = new District(daNangProvince, "ĐL Liên Chiểu");
            daNangDistrict.save();

            // quang nam
            Province quangNamProvince = new Province(Province.ID_QUANG_NAM, "Quảng Nam");
            quangNamProvince.save();

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
    }

    public static void saveProvinceIdSharedPref(Context context, Province province) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.PREF_PROVINCE, province.id);
        editor.commit();
    }

    public static String getProvinceIdSharedPref(Context context) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        return sharedPref.getString(Constants.PREF_PROVINCE, "");
    }

    public static void saveDistrictIdSharedPref(Context context, District district) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (district != null) {
            editor.putLong(Constants.PREF_DISTRICT, district.getId());
        } else {
            editor.putLong(Constants.PREF_DISTRICT, -1);
        }
        editor.commit();
    }

    public static long getDistrictIdSharedPref(Context context) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        return sharedPref.getLong(Constants.PREF_DISTRICT, -1);
    }

    public static void putStringSharedPref(Context context, String key, String value) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringSharedPref(Context context, String key) {
        return getSharedPreferences(context).getString(key, "");
    }

    public static void clearSharedPref(Context context) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.PREF_PROVINCE, "");
        editor.putLong(Constants.PREF_DISTRICT, -1);
        editor.commit();
    }

    /**
     * Check internet connection
     *
     * @return string which describe style connection, empty string if not have internet connection
     */
    public static String getConnectionStyle(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            if (mobileNetwork != null && mobileNetwork.isConnected()) {
                return Constants.SETTING_BOTH;
            } else {
                return Constants.SETTING_WIFI;
            }
        }

        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return Constants.SETTING_MOBILE;
        }

        return "";
    }

    public static AlertDialog getMessage(Context context, String title, String message,
            DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.global_ok), listener);
        return builder.create();
    }

    public static AlertDialog getMessage(Context context, String title, String message,
            String acceptButton, DialogInterface.OnClickListener acceptListener,
            String cancelButton, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(acceptButton, acceptListener);
        builder.setNegativeButton(cancelButton, cancelListener);
        return builder.create();
    }
}
