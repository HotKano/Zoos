
package com.chanb.zoos;

public class CareItem {
    private String text, seen, time, petName, year, month, date;

    public CareItem(String text, String seen, String time, String petName, String year, String month, String date) {
        this.text = text;
        this.seen = seen;
        this.time = time;
        this.petName = petName;
        this.year = year;
        this.month = month;
        this.date = date;
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

    public String getTime() {
        return time;
    }

    public String getPetName() {
        return petName;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDate() {
        return date;
    }

}
