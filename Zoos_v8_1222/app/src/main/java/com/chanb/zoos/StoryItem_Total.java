package com.chanb.zoos;

import java.util.ArrayList;

public class StoryItem_Total {
    private String date;
    private ArrayList<StoryItem_Img> imgView;

    // 참고의 Section.
    public StoryItem_Total() {

    }

    // SectionDataModel
    public StoryItem_Total(String date, ArrayList<StoryItem_Img> imgView) {
        this.date = date;
        this.imgView = imgView;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<StoryItem_Img> getImageView() {
        return imgView;
    }

    public void setImgView(ArrayList<StoryItem_Img> imgView) {
        this.imgView = imgView;
    }

}

