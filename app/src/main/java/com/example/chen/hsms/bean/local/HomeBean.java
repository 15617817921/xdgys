package com.example.chen.hsms.bean.local;

import android.graphics.drawable.Drawable;

/**
 * Created by admin on 2017/7/6.
 */

public class HomeBean {
    private Drawable drawable;
    private String  name;
    public HomeBean(Drawable drawable, String name) {
        this.drawable = drawable;
        this.name = name;
    }
    public Drawable getDrawable() {
        return drawable;
    }

    public String getName() {
        return name;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public void setName(String name) {
        this.name = name;
    }
}
