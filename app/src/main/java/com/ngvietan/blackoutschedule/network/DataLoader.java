package com.ngvietan.blackoutschedule.network;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
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

    private int androidVersion;
    private boolean isLoading = false;

    public DataLoader(Activity activity, WebView webView, OnLoadSuccessCallback callback) {
        this.activity = activity;
        this.webView = webView;
        this.callback = callback;
        androidVersion = Build.VERSION.SDK_INT;
        settingWebView();
    }

    private void settingWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new BlackoutJavascriptInterface(this),
                "HtmlViewer");
        webView.setWebChromeClient(new WebChromeClient());
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
            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                isLoading = false;
                if (callback != null) {
                    callback.onLoadFinish(false);
                }
            }

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageFinished(WebView view, String url) {
                if (step == 0) {
                    String getHtml = "javascript:window.HtmlViewer.showHTML" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";
                    switch (CommonVariables.selectedProvince.id) {
                        case Province.ID_DANANG:
                            if (androidVersion < Build.VERSION_CODES.KITKAT) {
                                webView.loadUrl(getHtml);
                            } else {
                                webView.evaluateJavascript(getHtml, null);
                            }
                            break;
                        case Province.ID_QUANG_NAM:
                            if (qnaLoop != 0) {
                                if (androidVersion < 19) {
                                    webView.loadUrl(getHtml);
                                } else {
                                    webView.evaluateJavascript(getHtml, null);
                                }
                            } else {
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
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        final String chooseDay = "javascript:document.getElementById('dnn_ctr491_ViewLCD_DN_denngay').value = '" + day + "';";
                        final String clickSubmit = "javascript:(function(){document.getElementById('dnn_ctr491_ViewLCD_DN_btnLayDuLieu').click();})()";
                        if (androidVersion < Build.VERSION_CODES.KITKAT) {
                            webView.loadUrl(chooseDay);
                            webView.loadUrl(clickSubmit);
                        } else {
                            webView.evaluateJavascript(chooseDay, new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    Log.i("Debug", value);
                                    webView.evaluateJavascript(clickSubmit, null);
                                }
                            });

                        }
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
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    final String selectDistrict = "javascript:document.getElementById('lstHuyen').value = '" + qnaLoop++ + "';";
                    final String clickSubmit = "javascript:(function(){document.getElementById('lstHuyen').onchange();})()";
                    if(androidVersion < Build.VERSION_CODES.KITKAT) {
                        webView.loadUrl(selectDistrict);
                        webView.loadUrl(clickSubmit);
                    }else{
                        webView.evaluateJavascript(selectDistrict, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("Debug", value);
                                webView.evaluateJavascript(clickSubmit, null);
                            }
                        });
                    }
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
