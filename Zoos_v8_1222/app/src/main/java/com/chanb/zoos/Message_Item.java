package com.chanb.zoos;

public class Message_Item {
    private String text, from, time;

    public Message_Item(String text, String from, String time) {
        this.text = text;
        this.from = from;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getFrom() {
        return from;
    }

    public String getTime() {
        return time;
    }
}
