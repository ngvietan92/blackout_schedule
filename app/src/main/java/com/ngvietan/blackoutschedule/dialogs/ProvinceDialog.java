package com.ngvietan.blackoutschedule.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ngvietan.blackoutschedule.R;
import com.ngvietan.blackoutschedule.data.ProvinceDao;
import com.ngvietan.blackoutschedule.models.Province;

import java.util.List;

/**
 * Select province dialog
 * Created by NgVietAn on 17/08/2015.
 */
public class ProvinceDialog extends AppCompatDialog {

    private ListView lvProvince;
    private List<Province> provinces;
    private ArrayAdapter<Province> adapter;

    private onProvinceSelectListener callback;

    public ProvinceDialog(Context context) {
        super(context);
    }

    public ProvinceDialog(Context context, onProvinceSelectListener listener) {
        super(context);
        this.callback = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_province);
        initComponents();
        setListener();
    }

    private void initComponents() {
        lvProvince = (ListView) findViewById(R.id.lvProvince);
        provinces = ProvinceDao.getAllProvinces();
        adapter = new ArrayAdapter<>(getContext(), R.layout.row_province, provinces);
        lvProvince.setAdapter(adapter);
    }

    private void setListener() {
        lvProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Province province = provinces.get(position);
                ProvinceDialog.this.dismiss();
                if (callback != null) {
                    callback.onSelectedProvince(province);
                }
            }
        });

    }

    public void setOnProvinceSelectListener(onProvinceSelectListener listener) {
        this.callback = listener;
    }

    public interface onProvinceSelectListener {
        public void onSelectedProvince(Province province);
    }
}
