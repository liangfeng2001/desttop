package com.gprotechnologies.gprodesktop.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gprotechnologies.gprodesktop.event.MessageEvent;
import com.gprotechnologies.gprodesktop.utils.ShapUtils;

import org.greenrobot.eventbus.EventBus;

import static com.gprotechnologies.gprodesktop.consts.AppConst.EK_MODE;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            ShapUtils.put(EK_MODE, false);
            EventBus.getDefault().post(MessageEvent.APP_UPDATE);
        }
        Log.e("ac", "onReceive: "+intent.getAction() );
    }
}
