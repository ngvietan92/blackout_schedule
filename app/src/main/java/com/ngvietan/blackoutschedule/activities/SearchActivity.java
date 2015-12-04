package com.ngvietan.blackoutschedule.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ngvietan.blackoutschedule.R;
import com.ngvietan.blackoutschedule.adapters.SchedulePagerAdapter;
import com.ngvietan.blackoutschedule.common.CommonVariables;
import com.ngvietan.blackoutschedule.common.Constants;
import com.ngvietan.blackoutschedule.data.BlackoutItemDao;
import com.ngvietan.blackoutschedule.data.SearchRecentProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Search activity
 * Created by NgVietAn on 23/11/2015.
 */
public class SearchActivity extends BaseActivity {

    private SearchView searchView;
    private ImageView btnBack;

    private LinearLayout llMessage;
    private LinearLayout llLoading;
    private TextView txtNoData;

    private CoordinatorLayout container;
    private TabLayout tabLayout;
    private ViewPager pagerSchedule;

    private SchedulePagerAdapter scheduleAdapter;
    private List<Date> days;

    private String code;

    private SearchRecentSuggestions searchRecentSuggestions;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_search;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchRecentSuggestions = new SearchRecentSuggestions(this, SearchRecentProvider.AUTHORITY,
                SearchRecentProvider.MODE);
        handleIntent(getIntent());
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        searchView = (SearchView) findViewById(R.id.searchView);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        pagerSchedule = (ViewPager) findViewById(R.id.pagerSchedule);
        llMessage = (LinearLayout) findViewById(R.id.llMessage);
        llLoading = (LinearLayout) findViewById(R.id.llLoading);
        txtNoData = (TextView) findViewById(R.id.txtNoData);
        container = (CoordinatorLayout) findViewById(R.id.container);

        llMessage.requestFocus();
        days = new ArrayList<>();
        initSearchView();
        initGoogleAdMob();
    }

    private void initGoogleAdMob() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equalsIgnoreCase(code)) {
                    code = query;
                }
                search();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return newText.equalsIgnoreCase(code);
            }
        });
    }

    private void initViewPagerTab() {
        if (scheduleAdapter == null) {
            scheduleAdapter = new SchedulePagerAdapter(getSupportFragmentManager(), days, code);
            pagerSchedule.setAdapter(scheduleAdapter);
        } else {
            scheduleAdapter.setCode(code);
            scheduleAdapter.notifyDataSetChanged();
        }
        tabLayout.removeAllTabs();
        tabLayout.setupWithViewPager(pagerSchedule);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchView.setQuery(query, false);
            code = query;
            search();
        }
    }

    private void search() {
        container.setVisibility(View.GONE);
        llMessage.setVisibility(View.VISIBLE);
        llLoading.setVisibility(View.VISIBLE);
        txtNoData.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //save suggestion
                searchRecentSuggestions.saveRecentQuery(code, null);

                //search day with code
                Calendar calendar = Calendar.getInstance();
                String firstDay = Constants.DATE_DB_FORMAT.format(calendar.getTime());
                days.clear();
                days.addAll(BlackoutItemDao.getDateScheduleOfCode(CommonVariables.selectedProvince,
                        code, firstDay));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //check data
                        if (days.size() > 0) {
                            initViewPagerTab();
                            llMessage.setVisibility(View.GONE);
                            container.setVisibility(View.VISIBLE);
                        } else {
                            llLoading.setVisibility(View.GONE);
                            txtNoData.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }, 3000);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
