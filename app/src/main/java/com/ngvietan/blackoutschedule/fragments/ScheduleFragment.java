package com.ngvietan.blackoutschedule.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ngvietan.blackoutschedule.R;
import com.ngvietan.blackoutschedule.adapters.Blackout2Adapter;
import com.ngvietan.blackoutschedule.common.CommonVariables;
import com.ngvietan.blackoutschedule.data.BlackoutItemDao;
import com.ngvietan.blackoutschedule.models.BlackoutItem;
import com.tonicartos.superslim.LayoutManager;

import java.util.List;

/**
 * Created by NgVietAn on 03/08/2015.
 */
public class ScheduleFragment extends BaseFragment {

    public static final String DAY = "day";
    public static final String CODE = "code";

    private LinearLayout llMessage;
    private LinearLayout llLoading;
    private TextView txtNoData;

    private RecyclerView rcvSchedule;
    private List<BlackoutItem> blackoutItems;
    private Blackout2Adapter adapter;
    private String day;
    private String code;

    public static ScheduleFragment newInstance(String day) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle params = new Bundle();
        params.putString(DAY, day);
        fragment.setArguments(params);
        return fragment;
    }

    public static ScheduleFragment newInstance(String day, String code) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle params = new Bundle();
        params.putString(DAY, day);
        params.putString(CODE, code);
        fragment.setArguments(params);
        return fragment;
    }

    @Override
    protected int getLayoutViewId() {
        return R.layout.fragment_schedule;
    }

    @Override
    protected void initComponents(View v) {
        super.initComponents(v);


        llMessage = (LinearLayout) v.findViewById(R.id.llMessage);
        llLoading = (LinearLayout) v.findViewById(R.id.llLoading);
        txtNoData = (TextView) v.findViewById(R.id.txtNoData);

        day = getArguments().getString(DAY);
        code = getArguments().getString(CODE);
        Log.i("Fragment", day + " " + code);
        rcvSchedule = (RecyclerView) v.findViewById(R.id.rcvSchedule);

        if (code != null && !code.isEmpty()) {
            blackoutItems = BlackoutItemDao.getScheduleOfDay(CommonVariables.selectedProvince, day,
                    code);
        } else {
            blackoutItems = BlackoutItemDao.getScheduleOfDay(CommonVariables.selectedProvince,
                    CommonVariables.selectedDistrict, day);
        }
        adapter = new Blackout2Adapter(getActivity(), blackoutItems);
        rcvSchedule.setItemAnimator(new DefaultItemAnimator());
        rcvSchedule.setAdapter(adapter);
        rcvSchedule.setLayoutManager(new LayoutManager(getActivity()));
        rcvSchedule.setHasFixedSize(true);

        checkData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && blackoutItems != null) {
            checkData();
        }
    }

    private void checkData() {
        if (blackoutItems.size() == 0) {
            rcvSchedule.setVisibility(View.GONE);
            llMessage.setVisibility(View.VISIBLE);

            if (CommonVariables.loader.isLoading()) {
                llLoading.setVisibility(View.VISIBLE);
                txtNoData.setVisibility(View.GONE);
            } else {
                llLoading.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
            }
        } else {
            llMessage.setVisibility(View.GONE);
            rcvSchedule.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
