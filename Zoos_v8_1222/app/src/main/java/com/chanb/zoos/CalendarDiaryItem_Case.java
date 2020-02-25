package com.chanb.zoos;

public class CalendarDiaryItem_Case {
    private String no, title, content, year, month, date, time, code;

    // SingleItemModel item.
    public CalendarDiaryItem_Case(String no, String title, String content, String year, String month, String date, String time, String code) {
        this.no = no;
        this.title = title;
        this.content = content;
        this.year = year;
        this.month = month;
        this.date = date;
        this.time = time;
    }


    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCode() {
        return code;
    }


}

