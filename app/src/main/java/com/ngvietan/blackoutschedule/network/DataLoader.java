package com.ngvietan.blackoutschedule.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ngvietan.blackoutschedule.R;
import com.ngvietan.blackoutschedule.common.CommonVariables;
import com.ngvietan.blackoutschedule.common.Constants;
import com.ngvietan.blackoutschedule.interfaces.OnLoadSuccessCallback;
import com.ngvietan.blackoutschedule.models.Province;

import java.util.Date;

/**
 * Created by NgVietAn on 06/08/2015.
 */
public class DataLoader implements OnLoadSuccessCallback {

    private Activity activity;
    private OnLoadSuccessCallback callback;
    private WebView webView;

    private boolean isForeground = false;
    private ProgressDialog progressDialog;

    private int step = 0;
    private Date dayTo = null;

    private int qnaLoop = 0;

    private boolean isLoading = false;

    public DataLoader(Activity activity, WebView webView, OnLoadSuccessCallback callback) {
        this.activity = activity;
        this.webView = webView;
        this.callback = callback;
        settingWebView();
    }

    private void settingWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new BlackoutJavascriptInterface(this),
                "HtmlViewer");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (isForeground && (progressDialog == null || !progressDialog.isShowing())) {
                    progressDialog = ProgressDialog.show(activity, "",
                            activity.getString(R.string.global_loading));
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (step == 0) {
                    switch (CommonVariables.selectedProvince.id) {
                        case Province.ID_DANANG:
                            webView.loadUrl(
                                    "javascript:window.HtmlViewer.showHTML" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                            break;
                        case Province.ID_QUANG_NAM:
                            if (qnaLoop != 0) {
                                webView.loadUrl(
                                        "javascript:window.HtmlViewer.showHTML" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                            }else{
                                qnaLoop++;
                                getQuangNamSchedule();
                            }
                            break;
                    }

                } else {
                    getSchedule(dayTo, isForeground);
                }
            }
        });
        webView.getSettings().setUserAgentString(
                "Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)");
        webView.requestFocus();
    }

    public void getSchedule(Date dayTo, boolean isForeground) {
        isLoading = true;
        this.isForeground = isForeground;
        this.dayTo = dayTo;

        switch (CommonVariables.selectedProvince.id) {
            case Province.ID_DANANG:
                getDaNangSchedule();
                break;
            case Province.ID_QUANG_NAM:
                getQuangNamSchedule();
                break;
        }
    }

    private void getDaNangSchedule() {
        switch (step) {
            case 0:
                step = 1;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("http://dnp.com.vn/Home/Lichcatdien/tabid/67/Default.aspx");
                    }
                });
                break;
            case 1:
                step--;
                final String day = Constants.DATE_WEB_FORMAT.format(dayTo);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl(
                                "javascript:document.getElementById('dnn_ctr491_ViewLCD_DN_denngay').value = '" +
                                        day + "';");
                        webView.loadUrl(
                                "javascript:(function(){document.getElementById('dnn_ctr491_ViewLCD_DN_btnLayDuLieu').click();})()");
                    }
                });
                break;

        }
    }

    private void getQuangNamSchedule() {
        if (qnaLoop == 0) {
            step = 1;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("http://www.eqn.com.vn/lichcatdien/catdien.php");
                }
            });
            qnaLoop++;
        } else {
            step = 0;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(
                            "javascript:document.getElementById('lstHuyen').value = '" + qnaLoop++ + "';");
                    webView.loadUrl(
                            "javascript:(function(){document.getElementById('lstHuyen').onchange();})()");
                }
            });

        }
    }

    @Override
    public void onLoadFinish(boolean isSuccess) {
        if (CommonVariables.selectedProvince.id.equals(Province.ID_QUANG_NAM)) {
            if (qnaLoop == 18) {
                qnaLoop = 0;
                finishLoad(isSuccess);
            } else {
                if (!isSuccess) {
                    qnaLoop++;
                }
                getQuangNamSchedule();
            }
        } else {
            finishLoad(isSuccess);
        }
    }

    public void finishLoad(boolean isSuccess) {
        if (isForeground) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
        isLoading = false;
        if (!isSuccess) {
            getSchedule(dayTo, isForeground);
        }

        if (callback != null) {
            callback.onLoadFinish(isSuccess);
        }
    }

    public boolean isLoading() {
        return isLoading;
    }
}
