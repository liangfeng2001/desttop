package com.gprotechnologies.gprodesktop.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.gprotechnologies.gprodesktop.bean.AppInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description
 * @auther Kason
 * @create 2019-12-11 9:58
 */
public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();

    public static List<AppInfo> getAppList(Context context) {
        Map<String, AppInfo> map = new HashMap<>();
        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = pm.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo info : activities) {
            String packName = info.activityInfo.packageName;
            Log.e(TAG, "getAppList: " + packName);
            if (packName.equals(context.getPackageName())) {
                continue;
            }
            //过滤应用
            if (!packName.matches("(com.gpro\\w*.\\w*.\\w*)|(com.android.settings)|(com.android.rk)"))
                continue;
            AppInfo mInfo = new AppInfo();
            mInfo.setIco(info.activityInfo.applicationInfo.loadIcon(pm));
            mInfo.setName(info.activityInfo.applicationInfo.loadLabel(pm).toString());
            mInfo.setPackageName(packName);
            // 为应用程序的启动Activity 准备Intent
            Intent launchIntent = new Intent();
            launchIntent.setComponent(new ComponentName(packName,
                    info.activityInfo.name));
            mInfo.setIntent(launchIntent);
            map.put(packName, mInfo);
        }
        List<AppInfo> list = new ArrayList<>();
        for (Map.Entry<String, AppInfo> stringAppInfoEntry : map.entrySet()) {
            list.add(stringAppInfoEntry.getValue());
        }
        return list;
    }
}
