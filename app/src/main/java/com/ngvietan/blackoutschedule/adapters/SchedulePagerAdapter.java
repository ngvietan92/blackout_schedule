package com.ngvietan.blackoutschedule.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.ngvietan.blackoutschedule.common.Constants;
import com.ngvietan.blackoutschedule.fragments.ScheduleFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by NgVietAn on 03/08/2015.
 */
public class SchedulePagerAdapter extends FragmentStatePagerAdapter {

    private List<Date> dates;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE\ndd/MM",
            new Locale("vi", "VN"));
    private String code;

    public SchedulePagerAdapter(FragmentManager fm, List<Date> dates) {
        super(fm);
        this.dates = dates;
    }

    public SchedulePagerAdapter(FragmentManager fm, List<Date> dates, String code) {
        super(fm);
        this.dates = dates;
        this.code = code;
    }

    public void setCode(String code) {
        this.code = code;
        Log.i("Set Code", code + " ");
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("Get Item", code + " ");
        Fragment fragment;
        if (code != null && !code.isEmpty()) {
            fragment = ScheduleFragment.newInstance(
                    Constants.DATE_DB_FORMAT.format(dates.get(position)), code);
        } else {
            fragment = ScheduleFragment.newInstance(
                    Constants.DATE_DB_FORMAT.format(dates.get(position)));
        }
        return fragment;
    }


    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return dateFormat.format(dates.get(position));
    }
}
