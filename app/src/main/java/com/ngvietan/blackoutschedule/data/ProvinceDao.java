package com.ngvietan.blackoutschedule.data;

import com.activeandroid.query.Select;
import com.ngvietan.blackoutschedule.models.Province;

import java.util.List;

/**
 * Data access object for Province model
 * Created by NgVietAn on 15/08/2015.
 */
public class ProvinceDao {

    /**
     * Get Province from province id
     *
     * @param provinceId province id
     * @return province which have province id
     */
    public static Province getProvinceById(String provinceId) {
        return new Select().from(Province.class)
                .where("ProvinceId = ?", provinceId)
                .executeSingle();
    }

    /**
     * Get all provinces in database
     *
     * @return list contains all provinces
     */
    public static List<Province> getAllProvinces() {
        return new Select().from(Province.class).execute();
    }
}
