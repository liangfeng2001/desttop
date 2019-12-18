package com.gprotechnologies.gprodesktop.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gprotechnologies.gprodesktop.R;
import com.gprotechnologies.gprodesktop.bean.AppInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @auther Kason
 * @create 2019-12-11 9:58
 */
public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();

    public static List<AppInfo> getAppList(Context context, String reg) {
        Map<String, AppInfo> map = new HashMap<>();
        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = pm.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo packageInfo : activities) {
            String packName = packageInfo.activityInfo.packageName;
            Log.e(TAG, "getAppList: " + packName);
            if (packName.equals(context.getPackageName())) {
                continue;
            }
            //过滤应用
            if (!packName.matches(reg))
                continue;
            AppInfo mInfo = new AppInfo();
            mInfo.setIco(packageInfo.activityInfo.applicationInfo.loadIcon(pm));
            mInfo.setName(packageInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
            mInfo.setPackageName(packName);
            // 为应用程序的启动Activity 准备Intent
            Intent launchIntent = new Intent(Intent.ACTION_MAIN);
            launchIntent.setComponent(new ComponentName(packName, packageInfo.activityInfo.name));
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            mInfo.setIntent(launchIntent);
            map.put(packName, mInfo);
        }
        List<AppInfo> list = new ArrayList<>();
        for (Map.Entry<String, AppInfo> stringAppInfoEntry : map.entrySet()) {
            list.add(stringAppInfoEntry.getValue());
        }
        return list;
    }


    public static void launchApp(Context context, AppInfo appInfo) {
        context.startActivity(appInfo.getIntent());
    }


}
