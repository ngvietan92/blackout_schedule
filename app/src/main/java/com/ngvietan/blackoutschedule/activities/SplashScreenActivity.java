package com.ngvietan.blackoutschedule.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;

import com.ngvietan.blackoutschedule.R;
import com.ngvietan.blackoutschedule.common.CommonMethods;
import com.ngvietan.blackoutschedule.common.CommonVariables;
import com.ngvietan.blackoutschedule.common.Constants;
import com.ngvietan.blackoutschedule.data.DistrictDao;
import com.ngvietan.blackoutschedule.data.ProvinceDao;
import com.ngvietan.blackoutschedule.dialogs.ProvinceDialog;
import com.ngvietan.blackoutschedule.models.Province;
import com.ngvietan.blackoutschedule.utils.Utils;

/**
 * Splash Screen
 * Created by NgVietAn on 02/08/2015.
 */
public class SplashScreenActivity extends BaseActivity {

    private Handler handler;
    private Runnable runnable;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initData() {
        super.initData();

        // get data from shared preferences
        SharedPreferences sharedPref = CommonMethods.getSharedPreferences(this);
        // first time
        boolean firstTime = sharedPref.getBoolean(Constants.PREF_FIRST_TIME, true);
        // province id
        String provinceId = CommonMethods.getProvinceIdSharedPref(this);
        // district id
        long districtId = CommonMethods.getDistrictIdSharedPref(this);

        // mode update
        CommonVariables.modeUpdate = CommonMethods.getStringSharedPref(this,
                Constants.PREF_SETTING_UPDATE);

        // first time open app
        if (firstTime) {
            // init data
            CommonMethods.initializeData();

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(Constants.PREF_FIRST_TIME, false);
            editor.putString(Constants.PREF_SETTING_UPDATE, Constants.SETTING_BOTH);
            editor.commit();

            CommonVariables.modeUpdate = Constants.SETTING_BOTH;
            String connectionStyle = CommonMethods.getConnectionStyle(this);
            // check connection style
            if (connectionStyle.isEmpty()) {
                // not have internet connection
                AlertDialog dialog = CommonMethods.getMessage(this,
                        getString(R.string.error),
                        getString(R.string.error_network_message),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                dialog.show();
            } else {
                showDialogProvince();
            }
        } else if (!provinceId.isEmpty()) {
            CommonVariables.selectedProvince = ProvinceDao.getProvinceById(provinceId);

            if (districtId != -1) {
                CommonVariables.selectedDistrict = DistrictDao.getDistrictFromId(districtId);
            }

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    Utils.gotoActivity(SplashScreenActivity.this, MainActivity.class, true);
                }
            };
            handler.postDelayed(runnable, 2000);
        } else {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    showDialogProvince();
                }
            };
            handler.postDelayed(runnable, 3000);
        }
    }

    private void showDialogProvince() {
        ProvinceDialog dialog = new ProvinceDialog(this,
                new ProvinceDialog.onProvinceSelectListener() {
                    @Override
                    public void onSelectedProvince(Province province) {
                        CommonVariables.selectedProvince = province;
                        CommonMethods.saveProvinceIdSharedPref(SplashScreenActivity.this,
                                CommonVariables.selectedProvince);
                        Utils.gotoActivity(SplashScreenActivity.this, MainActivity.class, true);
                    }
                });
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
