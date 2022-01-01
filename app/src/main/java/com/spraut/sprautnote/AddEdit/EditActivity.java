package com.spraut.sprautnote.AddEdit;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.lling.photopicker.PhotoPickerActivity;
import com.spraut.sprautnote.DataBase.Note;
import com.spraut.sprautnote.DataBase.NoteDbOpenHelper;
import com.spraut.sprautnote.Image.ImageActivity;
import com.spraut.sprautnote.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    private Note note;

    private Button mBtnDatePicker,mBtnCancel,mBtnConfirm;
    private EditText editObject,editEvent;
    private TextView tvTime;
    private ViewGroup mLinear;
    private int YEAR_SELECTED=2021,MONTH_SELECTED=10,DAY_SELECTED=25;

    private static final int PICK_PHOTO = 1;
    private Button mBtnAddPhoto;
    private ImageView photo;
    private List<String> mResults;
    private String Url = null;


    private NoteDbOpenHelper mNoteDbOpenHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_add);



        //适配MIUI，沉浸小横条和状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //状态栏文字显示为黑色
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        /*window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        mBtnDatePicker=findViewById(R.id.btn_datepicker);
        mBtnCancel=findViewById(R.id.btn_cancel);
        mBtnConfirm=findViewById(R.id.btn_confirm);
        tvTime=findViewById(R.id.edit_time);
        editObject=findViewById(R.id.edit_object);
        editEvent=findViewById(R.id.edit_event);
        mLinear=findViewById(R.id.LinearLayout_edit_msg);
        photo = findViewById(R.id.iv_photo);


        initData();

        /*点击查看大图*/
        if (Url!=null){
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pair pairAdd = new Pair<>(photo, "image");
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(EditActivity.this, pairAdd);
                    Intent intent = new Intent(EditActivity.this, ImageActivity.class);
                    intent.putExtra(ImageActivity.EXTRA_IMAGE_URL, Url);
                    startActivity(intent, activityOptions.toBundle());
                }
            });
        }




        //日历按钮
        mBtnDatePicker.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.EFFECT_CLICK);

                showDatePickDlg();
            }

        });

        //确认按钮
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.EFFECT_CLICK);

                String date_end = null;
                if ((MONTH_SELECTED<10)&&(DAY_SELECTED>=10)){
                    date_end=(YEAR_SELECTED+"")+("0"+MONTH_SELECTED+"")+(DAY_SELECTED+"");
                }else if ((DAY_SELECTED<10)&&(MONTH_SELECTED>=10)){
                    date_end=(YEAR_SELECTED+"")+(MONTH_SELECTED+"")+("0"+DAY_SELECTED+"");
                }else if ((MONTH_SELECTED<10)&&(DAY_SELECTED<10)){
                    date_end=(YEAR_SELECTED+"")+("0"+MONTH_SELECTED+"")+("0"+DAY_SELECTED+"");
                }else {
                    date_end=(YEAR_SELECTED+"")+(MONTH_SELECTED+"")+(DAY_SELECTED+"");
                }

                note.setObject(editObject.getText().toString());
                note.setEvent(editEvent.getText().toString());
                note.setYear_end(YEAR_SELECTED);
                note.setMonth_end(MONTH_SELECTED);
                note.setDay_end(DAY_SELECTED);
                note.setDate_end(Integer.parseInt(date_end));
                note.setUrl(Url);


                mNoteDbOpenHelper.updateData(note);
                

                finish();
            }
        });

        //取消按钮
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.EFFECT_CLICK);

                finish();
            }
        });


        /*“选择图片”点击事件*/
        mBtnAddPhoto=findViewById(R.id.btn_photo_picker);
        mBtnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.EFFECT_CLICK);

                int selectedMode = PhotoPickerActivity.MODE_SINGLE;
                boolean showCamera = false;
                int maxNum = PhotoPickerActivity.DEFAULT_NUM;

                Intent intent = new Intent(EditActivity.this, PhotoPickerActivity.class);
                intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera);
                intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, selectedMode);
                intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, maxNum);
                startActivityForResult(intent, PICK_PHOTO);
            }
        });
    }
    //onCreate 结束

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_PHOTO){
            if(resultCode == RESULT_OK){
                ArrayList<String> result = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                showResult(result);
            }
        }
    }

    private void showResult(ArrayList<String> paths) {
        if (mResults == null) {
            mResults = new ArrayList<>();
        }
        mResults.clear();
        mResults.addAll(paths);

        Url=paths.get(0);
        photo.setImageURI(Uri.fromFile(new File(Url)));


    }


    private void initData() {
        Intent intent=getIntent();
        note=(Note)intent.getSerializableExtra("note");
        if(note!=null){
            YEAR_SELECTED=note.getYear_end();
            MONTH_SELECTED=note.getMonth_end();
            DAY_SELECTED=note.getDay_end();
            tvTime.setText(YEAR_SELECTED+"年"+MONTH_SELECTED+"月"+DAY_SELECTED+"日");
            editObject.setText(note.getObject());
            editEvent.setText(note.getEvent());
            Url=note.getUrl();

            /*延迟显示图片，避免过度动画卡顿*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (note.getUrl()!=null){
                        photo.setImageURI(Uri.fromFile(new File(Url)));

                    }

                }
            },400);


            //共享元素
            tvTime.setTransitionName("tvTime");
            editObject.setTransitionName("editObject");
            editEvent.setTransitionName("editEvent");
            mLinear.setTransitionName("Item");

        }
        mNoteDbOpenHelper=new NoteDbOpenHelper(this);
    }



    private void showDatePickDlg() {
        Calendar mcalendar = Calendar.getInstance();     //  获取当前时间    —   年、月、日
        int year = mcalendar.get(Calendar.YEAR);         //  得到当前年
        int month = mcalendar.get(Calendar.MONTH);       //  得到当前月
        final int day = mcalendar.get(Calendar.DAY_OF_MONTH);  //  得到当前日

        new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {      //  日期选择对话框
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //  这个方法是得到选择后的 年，月，日，分别对应着三个参数 — year、month、dayOfMonth
                tvTime.setText(year + "年" + (month+1) + "月" + dayOfMonth + "日");
                YEAR_SELECTED = year;
                MONTH_SELECTED = (month)+1;
                DAY_SELECTED = dayOfMonth;
            }
        }, year, month, day).show();   //  弹出日历对话框时，默认显示 年，月，日
    }
}
