package com.chanb.zoos;

public class MessageList_Item {
    private String text, from, seen, time, nickname;

    public MessageList_Item(String text, String from, String seen, String time, String nickname) {
        this.text = text;
        this.from = from;
        this.seen = seen;
        this.time = time;
        this.nickname = nickname;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSeen() {
        return seen;
    }

    public String getFrom() {
        return from;
    }

    public String getTime() {
        return time;
    }

    public String getNickname() {
        return nickname;
    }


}
