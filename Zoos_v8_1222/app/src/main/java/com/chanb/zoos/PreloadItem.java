package com.chanb.zoos;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PreloadItem implements Serializable {

    String data, type;

    public PreloadItem(String data, String type) {
        this.data = data;
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }
}
