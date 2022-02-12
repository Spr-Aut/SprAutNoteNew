package com.spraut.sprautnote.AddEdit;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hw.ycshareelement.YcShareElement;
import com.hw.ycshareelement.transition.IShareElements;
import com.hw.ycshareelement.transition.ShareElementInfo;
import com.hw.ycshareelement.transition.TextViewStateSaver;
import com.lling.photopicker.PhotoPickerActivity;
import com.spraut.sprautnote.DataBase.Note;
import com.spraut.sprautnote.DataBase.NoteDbOpenHelper;
import com.spraut.sprautnote.Image.ImageActivity;
import com.spraut.sprautnote.MainActivity;
import com.spraut.sprautnote.R;
import com.spraut.sprautnote.widget.SendBroadcast;
import com.spraut.sprautnote.widget.WidgetSingleObject22;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditActivity extends Activity {
    private Note note;

    private Button mBtnDatePicker,mBtnCancel,mBtnConfirm;
    private EditText editObject,editEvent;
    private TextView tvTime;
    private ViewGroup mLinear;
    private CardView mCardView;
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
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition slide_top = TransitionInflater.from(this).inflateTransition(android.R.transition.slide_top);
        Transition slide_bottom=TransitionInflater.from(this).inflateTransition(android.R.transition.slide_bottom);
        getWindow().setEnterTransition(slide_top);
        getWindow().setExitTransition(slide_top);

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
        mCardView=findViewById(R.id.card_basic_add);
        mBtnAddPhoto=findViewById(R.id.btn_photo_picker);
        photo = findViewById(R.id.iv_photo);



        initData();


        //日历按钮
        mBtnDatePicker.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

                showDatePickDlg();
            }

        });

        //确认按钮
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

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

                /*Intent intent=new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(EditActivity.this).toBundle());*/

                //收起软键盘
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager!=null){
                    inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
                }
                finishAfterTransition();

                new SendBroadcast().SendBroaCcast(EditActivity.this);
            }
        });

        //取消按钮
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

                /*Intent intent=new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(EditActivity.this).toBundle());*/

                //收起软键盘
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager!=null){
                    inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
                }
                finishAfterTransition();
            }
        });


        /*“选择图片”点击事件*/
        mBtnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

                int hasStoragePermission= ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int PERMISSION_STORAGE_REQUEST_CODE = 0;

                if (hasStoragePermission!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EditActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_STORAGE_REQUEST_CODE);
                }else {
                    PERMISSION_STORAGE_REQUEST_CODE=1;
                }
                if (PERMISSION_STORAGE_REQUEST_CODE==1){
                    //收起软键盘
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager!=null){
                        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
                    }

                    int selectedMode = PhotoPickerActivity.MODE_SINGLE;
                    boolean showCamera = false;
                    int maxNum = PhotoPickerActivity.DEFAULT_NUM;

                    Intent intent = new Intent(EditActivity.this, PhotoPickerActivity.class);
                    intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera);
                    intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, selectedMode);
                    intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, maxNum);
                    startActivityForResult(intent, PICK_PHOTO);
                }else {
                    Toast.makeText(EditActivity.this,"添加图片需要授予本应用读取照片的权限",Toast.LENGTH_SHORT).show();
                }
            }
        });


        /*对图片的操作*/
        if (Url!=null){
            /*点击查看大图*/
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

            /*长按图片删除图片*/
            photo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 弹出弹窗
                    Dialog dialog = new Dialog(EditActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                    LayoutInflater layoutInflater=LayoutInflater.from(EditActivity.this);
                    View dialogView=layoutInflater.inflate(R.layout.add_image_delete_dialog_layout,null);

                    //dialog背景
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    WindowManager.LayoutParams layoutParams=dialog.getWindow().getAttributes();
                    layoutParams.dimAmount=0.0f;
                    dialog.getWindow().setAttributes(layoutParams);

                    TextView tvDelete = dialogView.findViewById(R.id.tv_delete_image);

                    //点击删除
                    tvDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //震动
                            Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

                            Url=null;
                            photo.setImageURI(null);
                            dialog.dismiss();
                        }
                    });

                    dialog.setContentView(dialogView);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    return true;
                }
            });
        }
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
            mCardView.setTransitionName("Card");


        }
        mNoteDbOpenHelper=new NoteDbOpenHelper(this);
    }



    private void showDatePickDlg() {
        Calendar mcalendar = Calendar.getInstance();     //  获取当前时间    —   年、月、日
        int year = mcalendar.get(Calendar.YEAR);         //  得到当前年
        int month = mcalendar.get(Calendar.MONTH);       //  得到当前月
        final int day = mcalendar.get(Calendar.DAY_OF_MONTH);  //  得到当前日

        new DatePickerDialog(EditActivity.this,R.style.ThemeDialog, new DatePickerDialog.OnDateSetListener() {      //  日期选择对话框
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
