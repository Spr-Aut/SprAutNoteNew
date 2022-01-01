package com.spraut.sprautnote.AddEdit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.spraut.sprautnote.DataBase.NoteDbOpenHelper;
import com.spraut.sprautnote.MainActivity;
import com.spraut.sprautnote.R;
import com.spraut.sprautnote.DataBase.Note;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BatchAddActivity extends AppCompatActivity {
    private Button mBtnCancel,mBtnConfirm;
    private EditText editBatchAdd;

    private NoteDbOpenHelper mNoteDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_add);

        //适配MIUI，沉浸小横条和状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //状态栏文字显示为黑色
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        /*window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        editBatchAdd=findViewById(R.id.edit_batch_add);

        mNoteDbOpenHelper=new NoteDbOpenHelper(this);
        mBtnConfirm=findViewById(R.id.btn_batch_add_confirm);
        mBtnCancel=findViewById(R.id.btn_batch_add_cancel);

        //共享元素
        mBtnConfirm.setTransitionName("mBtnAddBatch");

        //点击确认
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editBatchAdd.getText().toString().trim().equals("")){
                    Toast.makeText(BatchAddActivity.this,"请输入",Toast.LENGTH_SHORT).show();
                }else {
                    String s=editBatchAdd.getText().toString();
                    Pattern pattern=Pattern.compile("\t|\n");
                    Matcher matcher=pattern.matcher(s);
                    s=matcher.replaceAll(",");
                    String[] as= s.split(",");

                    for (int i=0;i<as.length;i=i+3){
                        Note note=new Note();
                        note.setObject(as[i+1]);
                        note.setEvent(as[i+2]);

                        /*String[] ad=as[i].split(".");*/
                        int year,month,day;

                    /*year=Integer.parseInt(ad[0]);
                    month=Integer.parseInt(ad[1]);
                    day=Integer.parseInt(ad[2]);*/

                        year=Integer.parseInt(as[i].substring(0,4));
                        month=Integer.parseInt(as[i].substring(5,7));
                        day=Integer.parseInt(as[i].substring(8));

                        note.setYear_end(year);
                        note.setMonth_end(month);
                        note.setDay_end(day);

                        String date_end = null;

                        if((month<10)&&(day>=10)){
                            date_end=(year+"")+("0"+month+"")+(day+"");
                        }else if ((day<10)&&(month>=10)){
                            date_end=(year+"")+(month+"")+("0"+day+"");
                        }else if((month<10)&&(day<10)){
                            date_end=(year+"")+("0"+month+"")+("0"+day+"");
                        }else {
                            date_end=(year+"")+(month+"")+(day+"");
                        }
                        note.setDate_end(Integer.parseInt(date_end));

                        mNoteDbOpenHelper.insertData(note);
                    }
                    Intent intent=new Intent(BatchAddActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        //点击取消
        //取消按钮
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}