package com.ngvietan.blackoutschedule.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ngvietan.blackoutschedule.R;
import com.ngvietan.blackoutschedule.adapters.BlackoutAdapter;
import com.ngvietan.blackoutschedule.common.CommonVariables;
import com.ngvietan.blackoutschedule.data.BlackoutItemDao;
import com.ngvietan.blackoutschedule.models.BlackoutItem;
import com.tonicartos.superslim.LayoutManager;

import java.util.List;

/**
 * Created by NgVietAn on 03/08/2015.
 */
public class ScheduleFragment extends BaseFragment {

    public static final String TITLE = "title";

    private LinearLayout llMessage;
    private LinearLayout llLoading;
    private TextView txtNoData;

    private RecyclerView rcvSchedule;
    private List<BlackoutItem> blackoutItems;
    private BlackoutAdapter adapter;
    private String day;

    public static ScheduleFragment newInstance(String text) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle params = new Bundle();
        params.putString(TITLE, text);
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

        day = getArguments().getString(TITLE);
        rcvSchedule = (RecyclerView) v.findViewById(R.id.rcvSchedule);

        blackoutItems = BlackoutItemDao.getScheduleOfDay(CommonVariables.selectedProvince,
                CommonVariables.selectedDistrict, day);
        adapter = new BlackoutAdapter(getActivity(), blackoutItems);
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
