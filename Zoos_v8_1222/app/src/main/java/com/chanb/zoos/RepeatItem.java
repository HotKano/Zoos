package com.chanb.zoos;

import java.io.Serializable;

class RepeatItem implements Serializable {

    String type;
    int data;

    public RepeatItem(int data, String type) {
        this.data = data;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getData() {
        return data;
    }
}
