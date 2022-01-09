package com.spraut.sprautnote.AddEdit;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.hw.ycshareelement.YcShareElement;
import com.hw.ycshareelement.transition.IShareElements;
import com.hw.ycshareelement.transition.ShareElementInfo;
import com.lling.photopicker.PhotoPickerActivity;
import com.spraut.sprautnote.DataBase.NoteDbOpenHelper;
import com.spraut.sprautnote.Image.ImageActivity;
import com.spraut.sprautnote.MainActivity;
import com.spraut.sprautnote.R;
import com.spraut.sprautnote.DataBase.Note;
import com.spraut.sprautnote.widget.WidgetSingleObject22;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddActivity extends AppCompatActivity {
    private Button mBtnDatePicker,mBtnCancel,mBtnConfirm,mBtnAddBatch;
    private EditText editObject,editEvent;
    private TextView tvTime;
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
        photo = findViewById(R.id.iv_photo);

        /*自动弹出键盘*/
        editObject.requestFocus();
        editObject.setFocusable(true);
        editObject.setFocusableInTouchMode(true);
        AddActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        //提前设置被选择的时间为当前时间
        Calendar calendar= Calendar.getInstance();
        YEAR_SELECTED=calendar.get(Calendar.YEAR);
        MONTH_SELECTED=calendar.get(Calendar.MONTH)+1;
        DAY_SELECTED=calendar.get(Calendar.DAY_OF_MONTH);
        /*tvTime.setText(YEAR_SELECTED+"年"+MONTH_SELECTED+"月"+DAY_SELECTED+"日");*/


        //共享元素
        mBtnConfirm.setTransitionName("mBtnAdd");

        mNoteDbOpenHelper=new NoteDbOpenHelper(this);

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

                Note note=new Note();

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


                mNoteDbOpenHelper.insertData(note);

                /*Intent intent=new Intent(AddActivity.this, MainActivity.class);
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(AddActivity.this).toBundle());*/

                //收起软键盘
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager!=null){
                    inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
                }
                finishAfterTransition();

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
                            Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE", null, AddActivity.this, WidgetSingleObject22.class);
                            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{0});
                            sendBroadcast(intent);
                        }
                    }
                }.start();
            }
        });

        //取消按钮
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.EFFECT_CLICK);

                /*Intent intent=new Intent(AddActivity.this, MainActivity.class);
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(AddActivity.this).toBundle());*/

                //收起软键盘
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager!=null){
                    inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
                }
                finishAfterTransition();

            }
        });

        //批量添加页面
        /*mBtnAddBatch=findViewById(R.id.btn_batch);
        mBtnAddBatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair pairAdd=new Pair<>(mBtnAddBatch,"mBtnAddBatch");
                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(AddActivity.this,pairAdd);
                Intent intent=new Intent(AddActivity.this,BatchAddActivity.class);
                startActivity(intent,activityOptions.toBundle());
            }
        });*/

        //批量添加
        /*mBtnConfirm.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //震动
                Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.EFFECT_CLICK);

                Pair pairAdd=new Pair<>(mBtnConfirm,"mBtnAddBatch");
                ActivityOptions activityOptions= ActivityOptions.makeSceneTransitionAnimation(com.spraut.supernote.AddActivity.this,pairAdd);
                Intent intent=new Intent(AddActivity.this,BatchAddActivity.class);
                startActivity(intent,activityOptions.toBundle());
                return true;
            }
        });*/



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

                Intent intent = new Intent(AddActivity.this, PhotoPickerActivity.class);
                intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera);
                intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, selectedMode);
                intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, maxNum);
                startActivityForResult(intent, PICK_PHOTO);
            }
        });

    }//onCreate

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

        photo = findViewById(R.id.iv_photo);
        photo.setImageURI(Uri.fromFile(new File(paths.get(0))));
        Url=paths.get(0);

        /*String s=Url;
        Pattern pattern=Pattern.compile("\t|\n");
        Matcher matcher=pattern.matcher(s);
        s=matcher.replaceAll(",");
        String[] as= s.split(",");*/



        /*对图片的操作*/
        if (Url!=null){
            /*点击查看大图*/
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pair pairAdd = new Pair<>(photo, "image");
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(AddActivity.this, pairAdd);
                    Intent intent = new Intent(AddActivity.this, ImageActivity.class);
                    intent.putExtra(ImageActivity.EXTRA_IMAGE_URL, Url);
                    startActivity(intent, activityOptions.toBundle());
                }
            });

            /*长按图片删除图片*/
            photo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 弹出弹窗
                    Dialog dialog = new Dialog(AddActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                    LayoutInflater layoutInflater=LayoutInflater.from(AddActivity.this);
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

    private void showDatePickDlg() {
        Calendar mcalendar = Calendar.getInstance();     //  获取当前时间    —   年、月、日
        int year = mcalendar.get(Calendar.YEAR);         //  得到当前年
        int month = mcalendar.get(Calendar.MONTH);       //  得到当前月
        final int day = mcalendar.get(Calendar.DAY_OF_MONTH);  //  得到当前日

        new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {      //  日期选择对话框
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
