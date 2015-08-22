package com.ngvietan.blackoutschedule.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * District model
 * Created by NgVietAn on 15/08/2015.
 */
@Table(name = "tblDistrict")
public class District extends Model {

    @Column(name = "Province")
    public Province province;

    @Column(name = "DistrictName")
    public String districtName;

    public District() {
        super();
    }

    public District(Province province, String districtName) {
        super();
        this.province = province;
        this.districtName = districtName;
    }

    @Override
    public String toString() {
        return districtName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof District) {
            District district = (District) obj;
            return district.getId().equals(getId());
        }
        return super.equals(obj);
    }
}
