package com.spraut.sprautnote;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hw.ycshareelement.YcShareElement;
import com.spraut.sprautnote.Adapter.MyAdapter;
import com.spraut.sprautnote.AddEdit.AddActivity;
import com.spraut.sprautnote.AddEdit.BatchAddActivity;
import com.spraut.sprautnote.Blur.RealtimeBlurView;
import com.spraut.sprautnote.DataBase.Note;
import com.spraut.sprautnote.DataBase.NoteDbOpenHelper;
import com.spraut.sprautnote.Notify.notifyBroadcastReceiver;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {
    private RecyclerView mRecyclerView;
    private Button mBtnAdd;
    private List<Note> mNotes;
    private MyAdapter mMyAdapter;
    private ImageView mIvInfo;
    private ImageView mIvIcon;
    private ImageView mIvSearch;
    private TextView mTvTimeNow;
    public static int notifyId=0;
    private String CHANNEL_ID ="channelID";


    private NoteDbOpenHelper mNoteDbOpenHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //适配MIUI，沉浸小横条和状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //状态栏文字显示为黑色
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        /*window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        createNotificationChannel();

        initView();
        initData();
        initEvent();

        mTvTimeNow=findViewById(R.id.tv_bar_main_top_time);
        Calendar mcalendar = Calendar.getInstance();     //  获取当前时间    —   年、月、日
        int year = mcalendar.get(Calendar.YEAR);         //  得到当前年
        int month = mcalendar.get(Calendar.MONTH) + 1 ;       //  得到当前月
        final int day = mcalendar.get(Calendar.DAY_OF_MONTH);  //  得到当前日
        mTvTimeNow.setText(year+"年"+month+"月"+day+"日");

        mIvIcon=findViewById(R.id.iv_bar_main_top_icon);
        mIvIcon.setTransitionName("icon");

        //添加Item页面
        mBtnAdd=findViewById(R.id.btn_main_add);
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

                Pair pairAdd=new Pair<>(mBtnAdd,"mBtnAdd");
                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairAdd);
                Intent intent=new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent,activityOptions.toBundle());


            }
        });

        //长按添加
        mBtnAdd.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onLongClick(View v) {

                /*Pair pairAdd=new Pair<>(mBtnAdd,"mBtnAddBatch");
                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairAdd);
                Intent intent=new Intent(MainActivity.this, BatchAddActivity.class);
                startActivity(intent,activityOptions.toBundle());*/
                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

                Intent intent = new Intent();
                intent.setAction("NOTIFICATION");
                intent.setComponent(new ComponentName("com.spraut.sprautnote","com.spraut.sprautnote.Notify.notifyBroadcastReceiver"));
                sendBroadcast(intent);
                Log.i("broadcast","发送广播");

                return true;
            }
        });


        //关于应用页面
        mIvInfo=findViewById(R.id.iv_bar_main_top_icon);
        mIvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

                Pair pairInfo=new Pair<>(mIvInfo,"mIvInfo");
                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairInfo);
                Intent intent=new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent,activityOptions.toBundle());

                setAlarm(10,0);
            }
        });

        //搜索页面
        mIvSearch=findViewById(R.id.iv_bar_main_top_search);
        mIvSearch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

                Pair pairSearch=new Pair<>(mIvSearch,"mIvSearch");
                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairSearch);
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent,activityOptions.toBundle());
            }
        });
    }//onCreate

    protected void onResume() {
        super.onResume();
        refreshDataFromDb();
        /*setListLayout();*/
    }

    //刷新数据
    private void refreshDataFromDb() {
        mNotes = getDataFromDB();
        mMyAdapter.refreshData(mNotes);
    }

    //从数据库中取数据
    private List<Note> getDataFromDB() {
        return mNoteDbOpenHelper.queryAllFromDb();
    }

    private void initEvent() {
        mMyAdapter = new MyAdapter(this, mNotes);
        mRecyclerView.setAdapter(mMyAdapter);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mMyAdapter.setViewType(MyAdapter.TYPE_LINEAR_LAYOUT);
        mMyAdapter.notifyDataSetChanged();
    }

    private void initData() {
        mNotes = new ArrayList<>();
        mNoteDbOpenHelper = new NoteDbOpenHelper(this);
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.rc_main);
    }

    //创建通知渠道
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setAlarm(int hour,int minute){
        Log.i("broadcast","设置定时");
        Intent intent=new Intent(MainActivity.this, notifyBroadcastReceiver.class);
        intent.setAction("NOTIFICATION");
        intent.setComponent(new ComponentName("com.spraut.sprautnote","com.spraut.sprautnote.Notify.notifyBroadcastReceiver"));
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE,minute);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        Log.i("broadcast","定时设置完成");

    }

}