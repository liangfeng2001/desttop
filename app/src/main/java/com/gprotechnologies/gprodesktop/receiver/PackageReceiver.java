package com.gprotechnologies.gprodesktop.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gprotechnologies.gprodesktop.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;

public class PackageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getDataString();
        // 安装
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            EventBus.getDefault().post(MessageEvent.APP_UPDATE);
        }
        // 覆盖安装
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
            EventBus.getDefault().post(MessageEvent.APP_UPDATE);
        }
        // 移除
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            EventBus.getDefault().post(MessageEvent.APP_UPDATE);
        }

    }
}
