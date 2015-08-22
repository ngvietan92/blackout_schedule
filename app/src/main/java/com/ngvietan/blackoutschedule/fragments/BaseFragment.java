package com.ngvietan.blackoutschedule.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ngvietan.blackoutschedule.interfaces.OnSwitchFragment;

/**
 * Created by NgVietAn on 19/03/2015.
 */
public abstract class BaseFragment extends Fragment {

    protected View view;

    protected FragmentActivity activity;

    protected boolean isViewShown = false;

    protected abstract int getLayoutViewId();

    public abstract void onBackPressed();

    protected void initComponents(View v) {
    }

    protected void initData() {
    }

    protected void initData(Bundle savedInstanceState) {
    }

    protected void addListener() {
    }

    protected void setActionBarTitle(String title) {
        if (activity instanceof AppCompatActivity) {
            android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) activity)
                    .getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        } else {
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutViewId(), container, false);
        activity = getActivity();
        setHasOptionsMenu(true);
        initComponents(view);
        initData();
        initData(savedInstanceState);
        addListener();
        return view;
    }

    protected final void switchFragment(Fragment fragment, boolean isBack) {
        switchFragment(fragment, isBack, false);
    }

    protected final void switchFragment(Fragment fragment, boolean isBack, boolean addToBackStack) {
        Activity activity = getActivity();
        if (activity instanceof OnSwitchFragment) {
            ((OnSwitchFragment) activity).switchFragment(fragment, isBack, addToBackStack);
        }
    }
}
