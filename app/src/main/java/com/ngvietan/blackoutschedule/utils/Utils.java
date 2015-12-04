package com.ngvietan.blackoutschedule.utils;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ngvietan.blackoutschedule.R;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

public class Utils {

    public static final int OPEN_WIRELESS = 100;

    public static void gotoActivity(Activity activity, Class<?> cls, Bundle bundle, boolean isTop,
            boolean isFinish, int enterAnim, int exitAnim) {
        Intent riIntent = new Intent(activity, cls);
        if (isTop) {
            riIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        }
        if (bundle != null) {
            riIntent.putExtras(bundle);
        }

        activity.startActivity(riIntent);
        activity.overridePendingTransition(enterAnim, exitAnim);

        if (isFinish) {
            activity.finish();
        }
    }

    public static void gotoActivity(Activity activity, Class<?> cls, Bundle bundle, boolean isTop,
            boolean isBack, boolean isFinish) {
        Intent riIntent = new Intent(activity, cls);
        if (isTop) {
            riIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        }
        if (bundle != null) {
            riIntent.putExtras(bundle);
        }

        activity.startActivity(riIntent);

        if (isBack) {
            activity.overridePendingTransition(R.anim.in_from_left, android.R.anim.fade_out);
        }
        else {
            activity.overridePendingTransition(R.anim.in_from_right, android.R.anim.fade_out);
        }
        if (isFinish) {
            activity.finish();
        }
    }

    public static void gotoActivity(Activity activity, Class<?> cls, Bundle bundle, boolean isBack,
            boolean isFinish) {
        gotoActivity(activity, cls, bundle, false, isBack, isFinish);
    }

    public static void gotoActivity(Activity activity, Class<?> cls, boolean isFinish,
            boolean isBack, boolean isTop) {
        gotoActivity(activity, cls, null, isTop, isBack, isFinish);
    }

    public static void gotoActivity(Activity activity, Class<?> cls, Bundle bundle,
            boolean isFinish) {
        gotoActivity(activity, cls, bundle, false, false, isFinish);
    }

    public static void gotoActivity(Activity activity, Class<?> cls, Bundle bundle) {
        gotoActivity(activity, cls, bundle, false, false);
    }

    public static void gotoActivity(Activity activity, Class<?> cls, boolean isFinish) {
        gotoActivity(activity, cls, null, isFinish);
    }

    public static void gotoActivity(Activity activity, Class<?> cls) {
        gotoActivity(activity, cls, null);
    }

    public static void goBackActivity(Activity activity) {
        activity.overridePendingTransition(R.anim.in_from_left, android.R.anim.fade_out);
    }

    public static void openWirelessSetting(Activity activity) {
        Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        activity.startActivityForResult(intent, OPEN_WIRELESS);
    }

    /**
     * Remove accent of string
     * @param s
     * @return
     */
    public static String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String noAccent = pattern.matcher(temp).replaceAll("");
        noAccent = noAccent.replace("Đ", "D");
        noAccent = noAccent.replace("đ", "d");
        return noAccent;
    }
}
