package com.ngvietan.blackoutschedule.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ngvietan.blackoutschedule.R;
import com.ngvietan.blackoutschedule.common.CommonMethods;
import com.ngvietan.blackoutschedule.common.CommonVariables;
import com.ngvietan.blackoutschedule.common.Constants;

/**
 * Setting dialog
 * Created by NgVietAn on 16/08/2015.
 */
public class SettingDialog extends AppCompatDialog implements View.OnClickListener {

    private RadioGroup rgrSetting;
    private Button btnCancel;
    private Button btnSave;

    public SettingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setContentView(R.layout.dialog_setting);
        initComponents();
    }

    private void initComponents() {
        rgrSetting = (RadioGroup) findViewById(R.id.rgrSetting);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSave = (Button) findViewById(R.id.btnAccept);

        switch (CommonVariables.modeUpdate) {
            case Constants.SETTING_WIFI:
                ((RadioButton) findViewById(R.id.rbtWifi)).setChecked(true);
                break;
            case Constants.SETTING_MOBILE:
                ((RadioButton) findViewById(R.id.rbtMobile)).setChecked(true);
                break;
            case Constants.SETTING_BOTH:
                ((RadioButton) findViewById(R.id.rbtBoth)).setChecked(true);
                break;
        }

        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancel:
                this.dismiss();
                break;
            case R.id.btnAccept:
                saveSetting();
                break;
        }
    }

    private void saveSetting() {
        switch (rgrSetting.getCheckedRadioButtonId()) {
            case R.id.rbtWifi:
                CommonVariables.modeUpdate = Constants.SETTING_WIFI;
                break;
            case R.id.rbtMobile:
                CommonVariables.modeUpdate = Constants.SETTING_MOBILE;
                break;
            case R.id.rbtBoth:
                CommonVariables.modeUpdate = Constants.SETTING_BOTH;
                break;
        }
        CommonMethods.putStringSharedPref(getContext(), Constants.PREF_SETTING_UPDATE,
                CommonVariables.modeUpdate);
        this.dismiss();
    }
}
