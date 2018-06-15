package com.example.chen.hsms.bean.local;


import java.io.Serializable;

/**
 * Created by Administrator on 2018/2/26.
 */

public class ImageText implements Serializable {
    private String text;
    private String image;

    public ImageText( ) {

    }

    public ImageText(String text, String image) {
        this.text = text;
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
