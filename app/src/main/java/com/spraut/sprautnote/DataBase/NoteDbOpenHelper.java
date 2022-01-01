package com.spraut.sprautnote.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.spraut.sprautnote.DataBase.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteDbOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME="noteSQLite.db";
    private static final String TABLE_NAME_NOTE="note";

    /*private String object;
    private String event;
    private int year_end,month_end,day_end;
    private int date_end;
    private int id;*/

    private static final String CREATE_TABLE_SQL="create table " + TABLE_NAME_NOTE +" (id integer primary key autoincrement, object text, event text, year_end Integer, month_end Integer, day_end Integer, date_end Integer, url text)";

    public NoteDbOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*if (oldVersion==1&&newVersion==2){
            String sql = "alter table " + TABLE_NAME_NOTE +" add url text";
            db.execSQL(sql);
        }*/
    }

    public long insertData(Note note){
        SQLiteDatabase db=getWritableDatabase();

        ContentValues values=new ContentValues();

        values.put("object",note.getObject());
        values.put("event",note.getEvent());
        values.put("year_end",note.getYear_end());
        values.put("month_end",note.getMonth_end());
        values.put("day_end",note.getDay_end());
        values.put("date_end",note.getDate_end());
        values.put("url",note.getUrl());
        /*values.put("id",note.getId());*/

        return db.insert(TABLE_NAME_NOTE,null,values);
    }

    public int deleteFromDbById(String id){
        SQLiteDatabase db=getWritableDatabase();
        return db.delete(TABLE_NAME_NOTE,"id like ?",new String[]{id});
    }

    public int updateData(Note note){
        SQLiteDatabase db=getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("object",note.getObject());
        values.put("event",note.getEvent());
        values.put("year_end",note.getYear_end());
        values.put("month_end",note.getMonth_end());
        values.put("day_end",note.getDay_end());
        values.put("date_end",note.getDate_end());
        values.put("url",note.getUrl());

        return db.update(TABLE_NAME_NOTE,values,"id like ?",new String[]{note.getId()});
    }

    //搜索全部
    public List<Note> queryAllFromDb(){
        SQLiteDatabase db=getWritableDatabase();
        List<Note> noteList=new ArrayList<>();

        Cursor cursor=db.query(TABLE_NAME_NOTE,null,null,null,null,null,"date_end ASC");
        if (cursor!=null){
            while (cursor.moveToNext()){
                String object=cursor.getString(cursor.getColumnIndex("object"));
                String event=cursor.getString(cursor.getColumnIndex("event"));
                int year_end=cursor.getInt(cursor.getColumnIndex("year_end"));
                int month_end=cursor.getInt(cursor.getColumnIndex("month_end"));
                int day_end=cursor.getInt(cursor.getColumnIndex("day_end"));
                int date_end=cursor.getInt(cursor.getColumnIndex("date_end"));
                String id=cursor.getString(cursor.getColumnIndex("id"));
                String url=cursor.getString(cursor.getColumnIndex("url"));

                Note note=new Note();
                note.setObject(object);
                note.setEvent(event);
                note.setYear_end(year_end);
                note.setMonth_end(month_end);
                note.setDay_end(day_end);
                note.setDate_end(date_end);
                note.setId(id);
                note.setUrl(url);

                noteList.add(note);
            }
            cursor.close();
        }
        return noteList;
    }

    //特定搜索
    public List<Note> queryTitleFromDb(String Object){
        SQLiteDatabase db=getWritableDatabase();
        List<Note> noteList=new ArrayList<>();

        Cursor cursor=db.query(TABLE_NAME_NOTE,null,"object like ?",new String[]{"%"+Object+"%"},null,null,"date_end ASC");
        if (cursor!=null){
            while (cursor.moveToNext()){
                String object=cursor.getString(cursor.getColumnIndex("object"));
                String event=cursor.getString(cursor.getColumnIndex("event"));
                int year_end=cursor.getInt(cursor.getColumnIndex("year_end"));
                int month_end=cursor.getInt(cursor.getColumnIndex("month_end"));
                int day_end=cursor.getInt(cursor.getColumnIndex("day_end"));
                int date_end=cursor.getInt(cursor.getColumnIndex("date_end"));
                String id=cursor.getString(cursor.getColumnIndex("id"));
                String url=cursor.getString(cursor.getColumnIndex("url"));

                Note note=new Note();
                note.setObject(object);
                note.setEvent(event);
                note.setYear_end(year_end);
                note.setMonth_end(month_end);
                note.setDay_end(day_end);
                note.setDate_end(date_end);
                note.setId(id);
                note.setUrl(url);

                noteList.add(note);
            }
            cursor.close();
        }
        return noteList;
    }














}//class
