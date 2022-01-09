package com.spraut.sprautnote.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

public class SendBroadcast {
    public void SendBroaCcast(Context mContext){
        // 延迟发送更新广播，目的是回到桌面后更新小部件视图
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
//                            e.printStackTrace();
                } finally {
                    // 发送更新广播
                    Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE", null, mContext, WidgetSingleObject22.class);
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{0});
                    mContext.sendBroadcast(intent);
                }
            }
        }.start();
    }
}

