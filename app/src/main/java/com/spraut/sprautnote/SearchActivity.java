package com.spraut.sprautnote;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spraut.sprautnote.Adapter.MyAdapter;
import com.spraut.sprautnote.DataBase.Note;
import com.spraut.sprautnote.DataBase.NoteDbOpenHelper;


import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private List<Note> mNotes;
    private MyAdapter mMyAdapter;
    private ImageView mIvSearch;

    private NoteDbOpenHelper mNoteDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        //适配MIUI，沉浸小横条和状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //状态栏文字显示为黑色
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        mIvSearch=findViewById(R.id.iv_search_top_search);
        mIvSearch.setTransitionName("mIvSearch");

        initView();
        initData();
        initEvent();

        searchView=findViewById(R.id.search_view);
        int magId = getResources().getIdentifier("android:id/search_mag_icon",null, null);

        ImageView magImage = (ImageView) searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                mNotes=mNoteDbOpenHelper.queryTitleFromDb(newText);
                mMyAdapter.refreshData(mNotes);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mNotes=mNoteDbOpenHelper.queryTitleFromDb(newText);
                mMyAdapter.refreshData(mNotes);
                return true;
            }
        });

    }//onCreate

    protected void onResume() {
        super.onResume();
        /*refreshDataFromDb();*/
    }

    //刷新数据
    private void refreshDataFromDb() {
        mNotes = getDataFromDB();
        mMyAdapter.refreshData(mNotes);
    }

    //从数据库中取数据
    private List<Note> getDataFromDB() {
        return mNoteDbOpenHelper.queryTitleFromDb("");
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
        mRecyclerView = findViewById(R.id.rc_search);
    }


}//class