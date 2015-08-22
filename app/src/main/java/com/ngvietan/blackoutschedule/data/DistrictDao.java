package com.ngvietan.blackoutschedule.data;

import com.activeandroid.query.Select;
import com.ngvietan.blackoutschedule.models.District;

/**
 * Data access object for District model
 * Created by NgVietAn on 15/08/2015.
 */
public class DistrictDao {

    /**
     * Get district from district name
     *
     * @param name name of district
     * @return district which match with name
     */
    public static District getDistrictFromName(String name) {
        return new Select().from(District.class).where("DistrictName = ?", name).executeSingle();
    }

    /**
     * Get district from district id
     *
     * @param id id of district
     * @return district object
     */
    public static District getDistrictFromId(long id) {
        return new Select().from(District.class).where("Id = ?", id).executeSingle();
    }
}
