package com.example.chen.hsms.bean.local;

/**
 * Created by admin on 2017/8/16.
 */

public class MessageEvent {
    public  String message;
    public  String wpblist;
    public  int size;

    public MessageEvent(String message, String wpblist) {
        this.message = message;
        this.wpblist = wpblist;
    }

    public MessageEvent(String message, int size, String wpblist) {
        this.message = message;
        this.size = size;
        this.wpblist = wpblist;
    }
}