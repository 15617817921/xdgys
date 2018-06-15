package com.example.chen.hsms.bean.local;

/**
 * Created by Administrator on 2017/12/1.
 */

public class MessagePatient {
    private String brid;
    private String brname;
    private String brsex;

    public MessagePatient(String brid, String brname, String brsex) {
        this.brid = brid;
        this.brname = brname;
        this.brsex = brsex;
    }

    public String getBrid() {
        return brid;
    }

    public void setBrid(String brid) {
        this.brid = brid;
    }

    public String getBrname() {
        return brname;
    }

    public void setBrname(String brname) {
        this.brname = brname;
    }

    public String getBrsex() {
        return brsex;
    }

    public void setBrsex(String brsex) {
        this.brsex = brsex;
    }
}
