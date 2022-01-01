package com.spraut.sprautnote.DataBase;

import java.io.Serializable;

public class Note implements Serializable {
    private String object;
    private String event;
    private int year_end,month_end,day_end;
    private int date_end;
    private String id;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getYear_end() {
        return year_end;
    }

    public void setYear_end(int year_end) {
        this.year_end = year_end;
    }

    public int getMonth_end() {
        return month_end;
    }

    public void setMonth_end(int month_end) {
        this.month_end = month_end;
    }

    public int getDay_end() {
        return day_end;
    }

    public void setDay_end(int day_end) {
        this.day_end = day_end;
    }

    public int getDate_end() {
        return date_end;
    }

    public void setDate_end(int date_end) {
        this.date_end = date_end;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }


    @Override
    public String toString() {
        return "Note{" +
                "object='" + object + '\'' +
                ", event='" + event + '\'' +
                ", year_end=" + year_end +
                ", month_end=" + month_end +
                ", day_end=" + day_end +
                ", date_end=" + date_end +
                ", id=" + id +
                ", url=" + url +
                '}';
    }
}
