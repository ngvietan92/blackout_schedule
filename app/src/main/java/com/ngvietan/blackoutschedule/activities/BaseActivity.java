package com.ngvietan.blackoutschedule.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;

/**
 * Created by NgVietAn on 02/08/2015.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        initActionBar();
        initComponents();
        setListeners();
        initData();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // disable menu button
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Initial Action bar
     */
    protected void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    /**
     * Set title action bar
     *
     * @param title title of action bar
     */
    protected void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(title);
        }
    }


    /**
     * Initial component on layout
     */
    protected void initComponents() {

    }

    /**
     * Initial state of components or get default data
     */
    protected void initData() {
    }

    /**
     * Add listener
     */
    protected void setListeners() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Get layout content view id
     *
     * @return id of container view content
     */
    protected abstract int getContentViewId();


}
