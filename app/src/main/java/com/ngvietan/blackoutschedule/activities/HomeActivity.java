package com.ngvietan.blackoutschedule.activities;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ngvietan.blackoutschedule.R;
import com.ngvietan.blackoutschedule.common.CommonMethods;
import com.ngvietan.blackoutschedule.common.CommonVariables;
import com.ngvietan.blackoutschedule.common.Constants;
import com.ngvietan.blackoutschedule.data.ProvinceDao;
import com.ngvietan.blackoutschedule.models.Province;
import com.ngvietan.blackoutschedule.utils.Utils;

import java.util.List;

/**
 * Created by NgVietAn on 02/08/2015.
 */
public class HomeActivity extends BaseActivity {

    private ListView lvProvince;
    private List<Province> provinces;
    private ArrayAdapter<Province> adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initComponents() {
        super.initComponents();

        lvProvince = (ListView) findViewById(R.id.lvProvince);
        provinces = ProvinceDao.getAllProvinces();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, provinces);
        lvProvince.setAdapter(adapter);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        lvProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommonVariables.selectedProvince = provinces.get(position);
                CommonMethods.saveProvinceIdSharedPref(HomeActivity.this, CommonVariables.selectedProvince);
                Utils.gotoActivity(HomeActivity.this, MainActivity.class, true);
            }
        });
    }
}
