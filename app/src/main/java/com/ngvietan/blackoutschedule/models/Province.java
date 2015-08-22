package com.ngvietan.blackoutschedule.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Province model
 * Created by NgVietAn on 02/08/2015.
 */
@Table(name = "tblProvince")
public class Province extends Model {

    public static final String ID_DANANG = "dng";
    public static final String ID_QUANG_NAM = "qna";
    public static final String ID_HANOI = "hno";
    public static final String ID_HOCHIMINH = "hcm";

    @Column(name = "ProvinceId")
    public String id;

    @Column(name = "ProvinceName")
    public String name;


    public Province() {
        super();
    }

    public Province(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }


    public List<District> getAllDistricts() {
        return getMany(District.class, "Province");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Province) {
            Province province = (Province) obj;
            return province.id.equals(id);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return name;
    }
}
