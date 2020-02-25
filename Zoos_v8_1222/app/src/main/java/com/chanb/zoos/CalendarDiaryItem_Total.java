package com.chanb.zoos;

import java.util.ArrayList;

public class CalendarDiaryItem_Total {
    private String date;
    private ArrayList<CalendarDiaryItem_Case> imgView;

    // 참고의 Section.
    public CalendarDiaryItem_Total() {

    }

    // SectionDataModel
    public CalendarDiaryItem_Total(String date, ArrayList<CalendarDiaryItem_Case> imgView) {
        this.date = date;
        this.imgView = imgView;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<CalendarDiaryItem_Case> getImageView() {
        return imgView;
    }

    public void setImgView(ArrayList<CalendarDiaryItem_Case> imgView) {
        this.imgView = imgView;
    }

}

