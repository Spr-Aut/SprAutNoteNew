package com.spraut.sprautnote;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Service;
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


    private NoteDbOpenHelper mNoteDbOpenHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //??????MIUI??????????????????????????????
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //??????????????????????????????
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        /*window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);



        initView();
        initData();
        initEvent();

        mTvTimeNow=findViewById(R.id.tv_bar_main_top_time);
        Calendar mcalendar = Calendar.getInstance();     //  ??????????????????    ???   ???????????????
        int year = mcalendar.get(Calendar.YEAR);         //  ???????????????
        int month = mcalendar.get(Calendar.MONTH) + 1 ;       //  ???????????????
        final int day = mcalendar.get(Calendar.DAY_OF_MONTH);  //  ???????????????
        mTvTimeNow.setText(year+"???"+month+"???"+day+"???");

        mIvIcon=findViewById(R.id.iv_bar_main_top_icon);
        mIvIcon.setTransitionName("icon");

        //??????Item??????
        mBtnAdd=findViewById(R.id.btn_main_add);
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                //??????
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));


                Pair pairAdd=new Pair<>(mBtnAdd,"mBtnAdd");
                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairAdd);
                Intent intent=new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent,activityOptions.toBundle());
                /*Intent intent=new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);*/

            }
        });

        //????????????
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


        //??????????????????
        mIvInfo=findViewById(R.id.iv_bar_main_top_icon);
        mIvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??????
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

                Pair pairInfo=new Pair<>(mIvInfo,"mIvInfo");
                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairInfo);
                Intent intent=new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent,activityOptions.toBundle());
            }
        });

        //????????????
        mIvSearch=findViewById(R.id.iv_bar_main_top_search);
        mIvSearch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //??????
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

    //????????????
    private void refreshDataFromDb() {
        mNotes = getDataFromDB();
        mMyAdapter.refreshData(mNotes);
    }

    //????????????????????????
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