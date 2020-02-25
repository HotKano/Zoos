package com.chanb.zoos;

public class SaveItem { // item in Save_act
    private String title, image, no, type;

    public SaveItem(String title, String image, String no, String type) {
        this.title = title;
        this.image = image;
        this.no = no;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getType() {
        return type;
    }

}

