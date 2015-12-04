package com.ngvietan.blackoutschedule.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Blackout Item model
 * Created by NgVietAn on 28/07/2015.
 */
@Table(name = "tblBlackout")
public class BlackoutItem extends Model {

    @Column(name = "Province")
    public Province province;

    @Column(name = "Day")
    public String day;

    @Column(name = "District")
    public District district;

    @Column(name = "EIndex")
    public String eIndex;

    @Column(name = "EDetail")
    public String eDetail;

    @Column(name = "Start")
    public String start;

    @Column(name = "End")
    public String end;

    @Column(name = "EDetailNoAccent")
    public String eDetailNoAccent;

    public BlackoutItem() {
        super();
    }

    public BlackoutItem(Province province, String day, District district, String eIndex,
            String eDetail, String eDetailNoAccent, String start, String end) {
        super();
        this.province = province;
        this.day = day;
        this.district = district;
        this.eIndex = eIndex;
        this.eDetail = eDetail;
        this.eDetailNoAccent = eDetailNoAccent;
        this.start = start;
        this.end = end;
    }

    public void saveIfNotExist() {
        BlackoutItem item = new Select().from(BlackoutItem.class)
                .where("Province = ?", province.getId())
                .and("District = ?", district.getId())
                .and("Day = ?", day)
                .and("EIndex = ?", eIndex)
                .and("EDetail = ?", eDetail)
                .and("Start = ?", start)
                .and("End = ?", end)
                .executeSingle();

        if (item == null) {
            this.save();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlackoutItem) {
            BlackoutItem item = (BlackoutItem) obj;
            return item.district.equals(district)
                    && item.day.equals(day)
                    && item.eIndex.equals(eIndex)
                    && item.eDetail.equals(eDetail)
                    && item.start.equals(start)
                    && item.end.equals(end);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(day);
        builder.append("  ").append(district);
        builder.append("  ").append(eIndex);
        builder.append("  ").append(eDetail);
        builder.append("  ").append(start);
        builder.append("  ").append(end);
        return builder.toString();
    }
}
