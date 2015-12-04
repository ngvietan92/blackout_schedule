package com.ngvietan.blackoutschedule.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ngvietan.blackoutschedule.R;
import com.ngvietan.blackoutschedule.adapters.DistrictSpinnerAdapter;
import com.ngvietan.blackoutschedule.adapters.SchedulePagerAdapter;
import com.ngvietan.blackoutschedule.common.CommonMethods;
import com.ngvietan.blackoutschedule.common.CommonVariables;
import com.ngvietan.blackoutschedule.common.Constants;
import com.ngvietan.blackoutschedule.data.BlackoutItemDao;
import com.ngvietan.blackoutschedule.dialogs.ProvinceDialog;
import com.ngvietan.blackoutschedule.dialogs.SettingDialog;
import com.ngvietan.blackoutschedule.interfaces.OnLoadSuccessCallback;
import com.ngvietan.blackoutschedule.models.District;
import com.ngvietan.blackoutschedule.models.Province;
import com.ngvietan.blackoutschedule.network.DataLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends BaseActivity implements OnLoadSuccessCallback {

    private CoordinatorLayout container;

    private Toolbar toolbar;
    private TabLayout tabLayout;

    private ViewPager pagerSchedule;
    private WebView webView;

    private Spinner toolbarSpinner;
    private DistrictSpinnerAdapter districtAdapter;
    private List<District> districts;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private View headerNavigation;
    private TextView txtHeader;
    private ImageView imvHeader;
    private View spinnerContainer;

    private LinearLayout searchLayout;
    private SearchView searchView;
    private ImageView btnBack;

    private SchedulePagerAdapter scheduleAdapter;
    private ArrayList<Date> days;

    private boolean loadData = false;
    private boolean loadForeground = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initComponents() {
        super.initComponents();

        container = (CoordinatorLayout) findViewById(R.id.container);
        webView = (WebView) findViewById(R.id.webViewContent);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        pagerSchedule = (ViewPager) findViewById(R.id.pagerSchedule);
        searchLayout = (LinearLayout) findViewById(R.id.searchLayout);
        searchView = (SearchView) findViewById(R.id.searchView);
        btnBack = (ImageView) findViewById(R.id.btnBack);

        CommonVariables.loader = new DataLoader(this, webView, this);

        initToolbar();

        initViewPagerTab();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name,
                R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (searchLayout.getVisibility() == View.VISIBLE) {
                    hideSearchView();
                }
            }

        };
        drawerLayout.setDrawerListener(drawerToggle);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        initActionBar();

        initDrawer();

        initSearchView();

        initGoogleAdMob();
    }

    private void initToolbar() {
        // set toolbar to action bar
        setSupportActionBar(toolbar);

        // init spinner district
        if (spinnerContainer == null) {
            spinnerContainer = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner,
                    toolbar, false);
            ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            toolbar.addView(spinnerContainer, lp);
            toolbarSpinner = (Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
        }


        // not init
        if (districtAdapter == null) {
            // get all district
            districts = CommonVariables.selectedProvince.getAllDistricts();
            if (districts == null) {
                districts = new ArrayList<>();
            }
            // init adapter
            districtAdapter = new DistrictSpinnerAdapter(toolbar.getContext(), districts);
            toolbarSpinner.setAdapter(districtAdapter);
        }
        // is initialized before
        else {
            // clear old districts
            districts.clear();
            // get new district
            List<District> newDistrict = CommonVariables.selectedProvince.getAllDistricts();
            if (newDistrict != null) {
                districts.addAll(newDistrict);
            }
            // update adapter
            districtAdapter.notifyDataSetChanged();
        }

        // set selection
        if (CommonVariables.selectedDistrict != null
                && CommonVariables.selectedDistrict.province.equals(
                CommonVariables.selectedProvince)) {
            for (int i = 0; i < districts.size(); i++) {
                District district = districts.get(i);
                if (district.equals(CommonVariables.selectedDistrict)) {
                    toolbarSpinner.setSelection(i);
                    break;
                }
                if (i == districts.size() - 1) {
                    CommonVariables.selectedDistrict = districts.get(0);
                }
            }
        } else {
            if (districts != null && districts.size() != 0) {
                CommonVariables.selectedDistrict = districts.get(0);
                CommonMethods.saveDistrictIdSharedPref(this, CommonVariables.selectedDistrict);
                toolbarSpinner.setSelection(0);
            } else {
                CommonVariables.selectedDistrict = null;
            }
        }
    }

    private void initViewPagerTab() {
        days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DAY_OF_YEAR);
        for (int i = 0; i < 8; i++) {
            calendar.set(Calendar.DAY_OF_YEAR, currentDate + i);
            days.add(calendar.getTime());
        }

        loadForeground = BlackoutItemDao.getScheduleOfDay(CommonVariables.selectedProvince,
                Constants.DATE_DB_FORMAT.format(days.get(0))).size() == 0;

        loadData = BlackoutItemDao.getScheduleOfDay(CommonVariables.selectedProvince,
                Constants.DATE_DB_FORMAT.format(days.get(days.size() - 1))).size() == 0;

        if (!loadData || !loadForeground) {
            scheduleAdapter = new SchedulePagerAdapter(getSupportFragmentManager(), days);
            pagerSchedule.setAdapter(scheduleAdapter);
            tabLayout.setupWithViewPager(pagerSchedule);
        }

    }

    private void initDrawer() {
        if (headerNavigation == null) {
            headerNavigation = navigationView.findViewById(R.id.drawerHeader);
            txtHeader = (TextView) headerNavigation.findViewById(R.id.txtProvince);
            imvHeader = (ImageView) headerNavigation.findViewById(R.id.imvProvince);
        }

        txtHeader.setText(CommonVariables.selectedProvince.name);
        switch (CommonVariables.selectedProvince.id) {
            case Province.ID_DANANG:
                imvHeader.setImageResource(R.drawable.bg_da_nang);
                break;
            case Province.ID_QUANG_NAM:
                imvHeader.setImageResource(R.drawable.bg_quang_nam);
                break;
        }
    }

    @Override
    protected void initActionBar() {
        super.initActionBar();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initGoogleAdMob() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    }

    @Override
    protected void setListeners() {
        super.setListeners();

        drawerLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_settings:
                                showSetting();
                                break;
                            case R.id.action_province:
                                showProvince();
                                break;
                        }
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });

        toolbarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CommonVariables.selectedDistrict = districts.get(position);
                CommonMethods.saveDistrictIdSharedPref(MainActivity.this,
                        CommonVariables.selectedDistrict);

                if (scheduleAdapter != null) {
                    scheduleAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (CommonVariables.selectedDistrict == null && districts.size() > 0) {
                    CommonVariables.selectedDistrict = districts.get(0);

                    if (scheduleAdapter != null) {
                        scheduleAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchLayout.getVisibility() == View.VISIBLE) {
                    hideSearchView();
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideSearchView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonVariables.loader != null && !CommonVariables.loader.isLoading()) {
            initData();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        if (loadData) {
            if (checkInternetConnection(loadForeground)) {
                CommonVariables.loader.getSchedule(days.get(days.size() - 1), loadForeground);
                if (scheduleAdapter != null && !loadForeground) {
                    scheduleAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_load:
                if (CommonVariables.loader != null && !CommonVariables.loader.isLoading()) {
                    loadData = true;
                    initData();
                }
                break;
            case R.id.action_search:
                searchLayout.setVisibility(View.VISIBLE);
                searchView.setQuery("", false);
                searchView.requestFocus();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadFinish(boolean isSuccess) {
        if (isSuccess && !this.isFinishing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // update district list
                    districts.clear();
                    districts.addAll(CommonVariables.selectedProvince.getAllDistricts());
                    districtAdapter.notifyDataSetChanged();
                    if (CommonVariables.selectedDistrict == null) {

                        CommonVariables.selectedDistrict = districts.get(0);

                        CommonMethods.saveDistrictIdSharedPref(MainActivity.this,
                                CommonVariables.selectedDistrict);

                    }

                    if (loadData && loadForeground) {
                        scheduleAdapter = new SchedulePagerAdapter(getSupportFragmentManager(),
                                days);
                        pagerSchedule.setAdapter(scheduleAdapter);
                        tabLayout.setupWithViewPager(pagerSchedule);
                    } else if (loadData) {
                        scheduleAdapter.notifyDataSetChanged();
                    }
                    loadData = false;
                    loadForeground = false;
                }
            });
        } else if (!isSuccess && loadForeground) {
            AlertDialog dialog = CommonMethods.getMessage(this,
                    getString(R.string.error),
                    getString(R.string.error_network_message3),
                    getString(R.string.global_yes),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadData = true;
                            loadForeground = true;
                            initData();
                        }
                    },
                    getString(R.string.global_no),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            dialog.show();
        }
    }

    private void showSetting() {
        SettingDialog settingDialog = new SettingDialog(MainActivity.this);
        settingDialog.show();
    }

    private void showProvince() {
        ProvinceDialog provinceDialog = new ProvinceDialog(
                MainActivity.this);
        provinceDialog.setOnProvinceSelectListener(
                new ProvinceDialog.onProvinceSelectListener() {
                    @Override
                    public void onSelectedProvince(Province province) {
                        if (!province.equals(
                                CommonVariables.selectedProvince)) {
                            updateData(province);
                        }
                    }
                });
        provinceDialog.show();
    }

    private void updateData(Province province) {
        if (CommonVariables.loader != null && CommonVariables.loader.isLoading()) {
            CommonVariables.loader.finishLoad(true);
        }
        ProgressDialog dialog = ProgressDialog.show(this, "", getString(R.string.global_wait));
        CommonVariables.selectedProvince = province;
        CommonVariables.selectedDistrict = null;
        CommonMethods.saveProvinceIdSharedPref(MainActivity.this, CommonVariables.selectedProvince);
        CommonMethods.saveDistrictIdSharedPref(MainActivity.this, CommonVariables.selectedDistrict);
        initToolbar();
        initViewPagerTab();
        initDrawer();
        dialog.dismiss();
        initData();
    }

    private boolean checkInternetConnection(boolean loadForeground) {
        String connectionType = CommonMethods.getConnectionStyle(this);

        // not connection
        if (connectionType.isEmpty()) {
            if (loadForeground) {
                AlertDialog dialog = CommonMethods.getMessage(this,
                        getString(R.string.error),
                        getString(R.string.error_network_message2),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CommonMethods.clearSharedPref(MainActivity.this);
                                finish();
                            }
                        });
                dialog.show();
            } else {
                Snackbar.make(container, R.string.error_no_internet, Snackbar.LENGTH_SHORT).show();
            }
            return false;
        }

        // different type of connection
        if (!CommonVariables.modeUpdate.equals(Constants.SETTING_BOTH)
                && !CommonVariables.modeUpdate.equals(connectionType)) {
            if (loadForeground) {
                AlertDialog dialog = CommonMethods.getMessage(this,
                        getString(R.string.error),
                        getString(R.string.error_network_message3),
                        null);
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.global_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.global_accept),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showSetting();
                            }
                        });
                dialog.show();
            } else {
                Snackbar.make(container, R.string.error_type_internet,
                        Snackbar.LENGTH_SHORT).show();
            }
            return false;
        }

        if (!loadForeground) {
            Snackbar.make(container, R.string.action_load,
                    Snackbar.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else if (searchLayout.getVisibility() == View.VISIBLE) {
            hideSearchView();
        } else {
            super.onBackPressed();
        }
    }

    private void hideSearchView() {
        searchLayout.setVisibility(View.GONE);
    }

}
