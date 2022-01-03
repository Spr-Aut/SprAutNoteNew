package com.spraut.sprautnote.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import com.spraut.sprautnote.Adapter.MyAdapter;
import com.spraut.sprautnote.DataBase.Note;
import com.spraut.sprautnote.DataBase.NoteDbOpenHelper;
import com.spraut.sprautnote.R;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Implementation of App Widget functionality.
 */
public class FirstWidget extends AppWidgetProvider {
    private static Set idsSet=new HashSet();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.first_widget);
        NoteDbOpenHelper noteDbOpenHelper=new NoteDbOpenHelper(context);
        List<Note> mNote=noteDbOpenHelper.queryAllFromDb();
        if (mNote.size()>0){
            Calendar mcalendar = Calendar.getInstance();     //  获取当前时间    —   年、月、日
            int year = mcalendar.get(Calendar.YEAR);         //  得到当前年
            int month = mcalendar.get(Calendar.MONTH) + 1 ;       //  得到当前月
            final int day = mcalendar.get(Calendar.DAY_OF_MONTH);  //  得到当前日

            int year_aim=mNote.get(0).getYear_end();
            int month_aim=mNote.get(0).getMonth_end();
            int day_aim=mNote.get(0).getDay_end();

            int remain= day_minus(year,month,day,year_aim,month_aim,day_aim);
            views.setTextViewText(R.id.tv_widget_object, mNote.get(0).getObject()+"");
            views.setTextViewText(R.id.tv_widget_event, mNote.get(0).getEvent()+"");
            views.setTextViewText(R.id.tv_widget_date, year_aim+"年"+month_aim+"月"+day_aim+"日");
            views.setTextViewText(R.id.tv_widget_remain,mNote.get(0).getDate_end()+"");
            if (remain>0){
                views.setTextViewText(R.id.tv_widget_remain,remain+"天后");
            }else if (remain==0){
                views.setTextViewText(R.id.tv_widget_remain,"今天");
            }else if (remain<0){
                views.setTextViewText(R.id.tv_widget_remain,-remain+"天前");
            }
            if(remain<=3&&remain>=0){
                views.setTextColor(R.id.tv_widget_remain,Color.argb(255,255,0,0));
            }else if (remain<0){
                views.setTextColor(R.id.tv_widget_remain,Color.argb(255,82,136,245));
            }else if (remain>3){
                views.setTextColor(R.id.tv_widget_remain,Color.argb(255,117,117,117));
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }else {
            views.setTextViewText(R.id.tv_widget_object, "");
            views.setTextViewText(R.id.tv_widget_event, "");
            views.setTextViewText(R.id.tv_widget_date, "");

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            idsSet.add(Integer.valueOf(appWidgetId));
        }
        /*final int counter=appWidgetIds.length;
        Log.i("TAG","总共有"+counter);*/
        int appID;
        Iterator iterator=idsSet.iterator();
        while (iterator.hasNext()){
            appID=((Integer)iterator.next()).intValue();
            updateAppWidget(context,appWidgetManager,appID);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.i("TAG","onDeleted(): appWidgetIds.length=" + appWidgetIds.length);
        for (int appWidgetId : appWidgetIds) {
            idsSet.remove(Integer.valueOf(appWidgetId));
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        int widget_id;
        Log.i("TAG", "onReceive : action = " + intent.getAction());
        if (intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")){

            Log.i("TAG", "111111111111111i got it");
        }
        /*if (intent.getIntExtra("test",0)==1){

        }*/
    }

    public static int day_minus(int year_start, int month_start, int day_start, int year_end, int month_end, int day_end) {
        int y2, m2, d2;
        int y1, m1, d1;
        /*用于判断日期是否大于3月（2月是判断闰年的标识），还用于纪录到3月的间隔月数*/
        m1 = (month_start + 9) % 12;
        /*如果是1月和2月，则不包括当前年（因为是计算到0年3月1日的天数）*/
        y1 = year_start - m1 / 10;
    /*365*y1 是不算闰年多出那一天的天数， y1/4 - y1/100 + y1/400  是加所有闰年多出的那一天
    (day_start - 1) 用于计算当前日到1日的间隔天数。*/
        d1 = 365 * y1 + y1 / 4 - y1 / 100 + y1 / 400 + (m1 * 306 + 5) / 10 + (day_start - 1);
        /*(m2*306 + 5)/10 用于计算到当前月到3月1日间的天数 306=365-31-28（1月和2月），5是全年中不是31天月份的个数*/
        m2 = (month_end + 9) % 12;
        y2 = year_end - m2 / 10;
        d2 = 365 * y2 + y2 / 4 - y2 / 100 + y2 / 400 + (m2 * 306 + 5) / 10 + (day_end - 1);
        return (d2 - d1);
    }
}