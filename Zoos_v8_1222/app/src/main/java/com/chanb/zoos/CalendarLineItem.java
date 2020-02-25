package com.chanb.zoos;

public class CalendarLineItem {
    private String dayWork;
    private int day;

    // 참고의 Section.
    public CalendarLineItem() {

    }

    // SectionDataModel
    public CalendarLineItem(String dayWork, int day) {
        this.dayWork = dayWork;
        this.day = day;
    }

    public String getDayWork() {
        return dayWork;
    }

    public int getDay() {
        return day;
    }

}
