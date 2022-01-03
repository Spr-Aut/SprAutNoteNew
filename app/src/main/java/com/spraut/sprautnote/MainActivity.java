package com.spraut.sprautnote;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.Service;
import android.content.Intent;
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
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hw.ycshareelement.YcShareElement;
import com.spraut.sprautnote.Adapter.MyAdapter;
import com.spraut.sprautnote.AddEdit.AddActivity;
import com.spraut.sprautnote.AddEdit.BatchAddActivity;
import com.spraut.sprautnote.DataBase.Note;
import com.spraut.sprautnote.DataBase.NoteDbOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private Button mBtnAdd;
    private List<Note> mNotes;
    private MyAdapter mMyAdapter;
    private ImageView mIvInfo;
    private ImageView mIvIcon;
    private ImageView mIvSearch;
    private TextView mTvTimeNow;

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
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);



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
            @Override
            public void onClick(View v) {

                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.EFFECT_CLICK);

                Pair pairAdd=new Pair<>(mBtnAdd,"mBtnAdd");
                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairAdd);
                Intent intent=new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent,activityOptions.toBundle());
                /*Intent intent=new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);*/



            }
        });

        //长按添加
        /*mBtnAdd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Pair pairAdd=new Pair<>(mBtnAdd,"mBtnAddBatch");
                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairAdd);
                Intent intent=new Intent(MainActivity.this, BatchAddActivity.class);
                startActivity(intent,activityOptions.toBundle());

                return true;
            }
        });*/


        //关于应用页面
        mIvInfo=findViewById(R.id.iv_bar_main_top_icon);
        mIvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.EFFECT_CLICK);

                Pair pairInfo=new Pair<>(mIvInfo,"mIvInfo");
                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairInfo);
                Intent intent=new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent,activityOptions.toBundle());
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
                vibrator.vibrate(VibrationEffect.EFFECT_CLICK);

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

}