package com.spraut.sprautnote.Notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.spraut.sprautnote.Adapter.MyAdapter;
import com.spraut.sprautnote.DataBase.Note;
import com.spraut.sprautnote.DataBase.NoteDbOpenHelper;
import com.spraut.sprautnote.MainActivity;
import com.spraut.sprautnote.R;

import java.util.Calendar;
import java.util.List;

/**
 * @Description:
 * @author: SPRAUT
 * @date: 2022/10/22
 */
public class notifyBroadcastReceiver extends BroadcastReceiver {
    private String CHANNEL_ID ="channelID";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("broadcast","收到广播");
        if (intent.getAction().equals("NOTIFICATION")){
            Log.i("broadcast","广播通知开始");
            List<Note> mNotes;
            NoteDbOpenHelper noteDb=new NoteDbOpenHelper(context);
            mNotes= noteDb.queryAllFromDb();
            Note note=mNotes.get(0);
            Calendar mcalendar = Calendar.getInstance();     //  获取当前时间    —   年、月、日
            int year = mcalendar.get(Calendar.YEAR);         //  得到当前年
            int month = mcalendar.get(Calendar.MONTH) + 1 ;       //  得到当前月
            final int day = mcalendar.get(Calendar.DAY_OF_MONTH);  //  得到当前日
            int remain= MyAdapter.day_minus(year,month,day,note.getYear_end(),note.getMonth_end(),note.getDay_end());
            String title=note.getObject();
            String text="";
            if (remain>1){
                text=(remain+"天后");
            }else if (remain==1){
                text=("明天");
            }else if (remain==0){
                text=("今天");
            }else if (remain<0){
                text=(-remain+"天前");
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.app_icon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(MainActivity.notifyId++, builder.build());
            Log.i("broadcast","广播通知结束"+MainActivity.notifyId);
        }
    }
}
