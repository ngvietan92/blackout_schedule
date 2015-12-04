package com.ngvietan.blackoutschedule.data;

import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.ngvietan.blackoutschedule.common.Constants;
import com.ngvietan.blackoutschedule.models.BlackoutItem;
import com.ngvietan.blackoutschedule.models.District;
import com.ngvietan.blackoutschedule.models.Province;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Data access object for BlackoutItem model
 * Created by NgVietAn on 15/08/2015.
 */
public class BlackoutItemDao {

    /**
     * Get All Schedule in province
     *
     * @param province province
     * @param district district
     * @return list contains schedule of province
     */
    public static List<BlackoutItem> getAllSchedule(Province province, District district) {
        return new Select().from(BlackoutItem.class)
                .where("Province = ?", province.getId())
                .and("District = ?", district.getId())
                .execute();
    }

    /**
     * Get Schedule of day in province
     *
     * @param province province
     * @param day      day
     * @return list contains schedule of day
     */
    public static List<BlackoutItem> getScheduleOfDay(Province province, String day) {
        return new Select().from(BlackoutItem.class)
                .where("Province = ?", province.getId())
                .and("Day = ?", day)
                .orderBy("District ASC")
                .execute();
    }

    /**
     * Get Schedule of day in province at district
     *
     * @param province province
     * @param district district
     * @param day      day
     * @return list contains schedule of day
     */
    public static List<BlackoutItem> getScheduleOfDay(Province province, District district,
            String day) {
        if (province != null && district != null) {
            return new Select().from(BlackoutItem.class)
                    .where("Province = ?", province.getId())
                    .and("District = ?", district.getId())
                    .and("Day = ?", day)
                    .orderBy("Start ASC")
                    .execute();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Get Schedule of day in province at code
     *
     * @param province province
     * @param code     code
     * @param day      day
     * @return list contains schedule of day
     */
    public static List<BlackoutItem> getScheduleOfDay(Province province, String day, String code) {
        if (province != null && !code.isEmpty()) {
            return new Select().from(BlackoutItem.class)
                    .where("Province = ?", province.getId())
                    .and("Day = ?", day)
                    .and("(EIndex LIKE ? COLLATE NOCASE OR EDetailNoAccent LIKE ? COLLATE NOCASE)",
                            "%" + code + "%", "%" + code + "%")
                    .orderBy("Start ASC")
                    .execute();
        } else {
            return new ArrayList<>();
        }
    }

    public static List<Date> getDateScheduleOfCode(Province province, String code,
            String startDay) {
        List<Date> dates = new ArrayList<>();
        if (province != null && !code.isEmpty()) {
            List<BlackoutItem> items = new Select().from(BlackoutItem.class)
                    .where("Province = ?", province.getId())
                    .and("Day >= ?", startDay)
                    .and("(EIndex LIKE ? COLLATE NOCASE OR EDetailNoAccent LIKE ? COLLATE NOCASE)",
                            "%" + code + "%", "%" + code + "%")
                    .groupBy("Day")
                    .execute();
            if (items != null && items.size() > 0) {
                for (BlackoutItem item : items) {
                    Log.i("Search", item.toString());
                    try {
                        dates.add(Constants.DATE_DB_FORMAT.parse(item.day));
                    } catch (ParseException e) {

                    }
                }
            }
        }
        return dates;
    }

    public static void deleteOldSchedule() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
        String before = Constants.DATE_DB_FORMAT.format(calendar.getTime());
        new Delete().from(BlackoutItem.class).where("Day < ?", before).execute();
    }
}
