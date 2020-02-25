package com.chanb.zoos;


import java.util.ArrayList;
import java.util.List;

/**
 * 일자 정보를 담기 위한 클래스 정의
 *
 * @author Mike
 */
public class CalendarItem {

    private int yearValue;
    private int monthValue;
    private int dayValue;
    public List<CalendarLineItem> list;

    public CalendarItem() {

    }

    public CalendarItem(int year, int month, int day) {
        this.yearValue = year;
        this.monthValue = month;
        this.dayValue = day;
    }

    public int getDay() {
        return dayValue;
    }

    public int getYear() {
        return yearValue;
    }

    public int getMonth() {
        return monthValue;
    }

    public void setDay(int day) {
        this.dayValue = day;
    }
}
