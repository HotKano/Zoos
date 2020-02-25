package com.chanb.zoos;

public class CardItem {

    private String writer, timer, like, watch, img, title, like_check, no;

    public CardItem(String title, String writer, String timer, String like, String watch, String img, String no, String check) {
        this.title = title;
        this.writer = writer;
        this.timer = timer;
        this.like = like;
        this.watch = watch;
        this.img = img;
        this.no = no;
        this.like_check = check;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWatch() {
        return watch;
    }

    public void setWatch(String watch) {
        this.watch = watch;
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


    public String getLike_check() {
        return like_check;
    }


}
