package com.ngvietan.blackoutschedule.network;

import android.webkit.JavascriptInterface;

import com.activeandroid.ActiveAndroid;
import com.ngvietan.blackoutschedule.common.CommonVariables;
import com.ngvietan.blackoutschedule.common.Constants;
import com.ngvietan.blackoutschedule.data.BlackoutItemDao;
import com.ngvietan.blackoutschedule.data.DistrictDao;
import com.ngvietan.blackoutschedule.interfaces.OnLoadSuccessCallback;
import com.ngvietan.blackoutschedule.models.BlackoutItem;
import com.ngvietan.blackoutschedule.models.District;
import com.ngvietan.blackoutschedule.models.Province;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by NgVietAn on 04/08/2015.
 */
public class BlackoutJavascriptInterface {

    private OnLoadSuccessCallback callback;

    public BlackoutJavascriptInterface(OnLoadSuccessCallback callback) {
        this.callback = callback;
    }

    @JavascriptInterface
    public void showHTML(String html) {
        if (html != null) {
            boolean result = false;

            switch (CommonVariables.selectedProvince.id) {
                case Province.ID_DANANG:
                    result = saveDaNangSchedule(html);
                    break;
                case Province.ID_QUANG_NAM:
                    result = saveQuangNamSchedule(html);
                    break;
            }

            if (callback != null) {
                callback.onLoadFinish(result);
            }
        } else if (callback != null) {
            callback.onLoadFinish(false);
        }
    }

    private boolean saveDaNangSchedule(String html) {
        Document doc = Jsoup.parse(html);
        Elements rows = doc.select("table#dnn_ctr491_ViewLCD_DN_grvLCD>tbody>tr");
        if (rows.size() == 0) {
            return false;
        }

        List<BlackoutItem> items = new ArrayList<>();
        List<BlackoutItem> blackOutItems = new ArrayList<>();

        District district = null;
        String day = "";
        String districtName = "";
        String eIndex = "";
        String eDetail = "";
        String start = "";
        String end = "";
        for (Element row : rows) {
            // day
            Elements days = row.getElementsByAttributeValueContaining("id", "lblLichNgay");
            if (days != null && days.size() > 0) {
                day = days.get(0).text();
            }

            // district
            Elements districts = row.getElementsByAttributeValueContaining("id", "lblTen_ChiNhanh");
            if (districts != null && districts.size() > 0) {
                districtName = districts.get(0).text();

                // get district in db
                district = DistrictDao.getDistrictFromName(districtName);
                // not exist => save
                if (district == null) {
                    district = new District();
                    district.province = CommonVariables.selectedProvince;
                    district.districtName = districtName;
                    district.save();
                    blackOutItems = new ArrayList<>();
                } else {
                    blackOutItems = BlackoutItemDao.getScheduleOfDay(
                            CommonVariables.selectedProvince,
                            district, day);
                    if (blackOutItems == null) {
                        blackOutItems = new ArrayList<>();
                    }
                }
            }

            // eIndex
            Elements eIndexes = row.getElementsByAttributeValueContaining("id", "lblMa_LoTrinh");
            if (eIndexes != null && eIndexes.size() > 0) {
                eIndex = eIndexes.get(0).text();
            }

            // eDetail
            Elements eDetails = row.getElementsByAttributeValueContaining("id", "lblTen_LoTrinh");
            if (eDetails != null && eDetails.size() > 0) {
                eDetail = eDetails.get(0).text();
            }

            // start
            Elements starts = row.getElementsByAttributeValueContaining("id", "lblTu");
            if (starts != null && starts.size() > 0) {
                start = starts.get(0).text();
            }

            // end
            Elements ends = row.getElementsByAttributeValueContaining("id", "lblDen");
            if (ends != null && ends.size() > 0) {
                end = ends.get(0).text();
            }

            BlackoutItem item = new BlackoutItem(CommonVariables.selectedProvince, day, district,
                    eIndex, eDetail, start, end);
            if (!blackOutItems.contains(item)) {
                items.add(item);
            }
        }

        // insert to database
        if (items.size() != 0) {
            ActiveAndroid.beginTransaction();
            try {
                for (BlackoutItem item : items) {
                    item.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }
        return true;
    }

    private boolean saveQuangNamSchedule(String html) {
        Document doc = Jsoup.parse(html);
        Elements rows = doc.select("table#TableCatDien>tbody>tr");
        if (rows.size() == 0) {
            return false;
        }

        List<BlackoutItem> items = new ArrayList<>();
        List<BlackoutItem> blackOutItems;

        // get district from name
        String districtName = doc.select("select#lstHuyen>option[selected]").text();
        District district = DistrictDao.getDistrictFromName(districtName);

        // save if not exists
        if (district == null) {
            district = new District();
            district.province = CommonVariables.selectedProvince;
            district.districtName = districtName;
            district.save();
            blackOutItems = new ArrayList<>();
        } else {
            blackOutItems = BlackoutItemDao.getAllSchedule(
                    CommonVariables.selectedProvince, district);
            if (blackOutItems == null) {
                blackOutItems = new ArrayList<>();
            }
        }

        String day = "";
        String eIndex = "";
        String eDetail = "";
        String start = "";
        String end = "";

        for (Element row : rows) {

            Elements content = row.getElementsByTag("td");
            if (content.size() == 2) {
                // day time
                Element dayTime = content.get(0);
                if (dayTime.childNodes().size() == 2) {
                    List<Node> list = dayTime.childNodes().get(1).childNodes();
                    if (list.size() >= 3) {
                        day = ((TextNode) list.get(0)).text();
                        try {
                            Date date = Constants.DATE_WEB_FORMAT_2.parse(day);
                            day = Constants.DATE_WEB_FORMAT.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String startEnd = ((TextNode) list.get(2)).text();
                        String[] timeSplit = startEnd.split("-");
                        if (timeSplit.length == 2) {
                            start = startEnd.split("-")[0].replace("h", ":");
                            end = startEnd.split("-")[1].replace("h", ":");
                        }
                    }
                }

                // eDetail
                Element eDetails = content.get(1);
                eDetail = eDetails.text();

                String[] splitDetail = eDetail.split(",");
                for (int i = 0; i < splitDetail.length; i++) {
                    splitDetail[i] = splitDetail[i].trim();
                }
                Arrays.sort(splitDetail);

                for (String indexDetail : splitDetail) {
                    String[] details = indexDetail.split(":");
                    BlackoutItem item;
                    if (details.length == 2) {
                        item = new BlackoutItem(CommonVariables.selectedProvince, day,
                                district,
                                details[0], details[1], start, end);
                    } else {
                        item = new BlackoutItem(CommonVariables.selectedProvince, day,
                                district,
                                details[0], "", start, end);
                    }
                    if (!blackOutItems.contains(item)) {
                        items.add(item);
                    }
                }
            }
        }

        // insert to database
        if (items.size() > 0) {
            ActiveAndroid.beginTransaction();
            try {
                for (BlackoutItem item : items) {
                    item.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }
        return true;
    }
}
