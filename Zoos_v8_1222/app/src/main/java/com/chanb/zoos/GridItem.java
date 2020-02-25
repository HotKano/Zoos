package com.chanb.zoos;

public class GridItem { // topCareItem
    private String url, no, title, tag, nickname, check;

    public GridItem(String url, String no, String title, String tag, String nickname, String check) {
        this.url = url;
        this.no = no;
        this.title = title;
        this.tag = tag;
        this.nickname = nickname;
        this.check = check;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLike_check() {
        return check;
    }

    public void setLike_check(String check) {
        this.check = check;
    }


}
