package com.ngvietan.blackoutschedule.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ngvietan.blackoutschedule.common.Constants;
import com.ngvietan.blackoutschedule.fragments.ScheduleFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by NgVietAn on 03/08/2015.
 */
public class SchedulePagerAdapter extends FragmentPagerAdapter {

    private List<Date> dates;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE\ndd/MM",
            new Locale("vi", "VN"));

    public SchedulePagerAdapter(FragmentManager fm, List<Date> dates) {
        super(fm);
        this.dates = dates;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return ScheduleFragment.newInstance(Constants.DATE_WEB_FORMAT.format(dates.get(position)));
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
