package com.chanb.zoos;


public class StoryItem_Img {
    private String img, no, imgView;

    // SingleItemModel item.
    public StoryItem_Img() {

    }

    public StoryItem_Img(String no, String img) {
        this.no = no;
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getImageView() {
        return imgView;
    }

    public void setImgView(String imgView) {
        this.imgView = imgView;
    }

}

