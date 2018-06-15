package com.example.chen.hsms.bean.data;

/**
 * Created by Administrator on 2018/1/16.
 */

public class IdName {

    /**
     * yonghugh : 3275
     * yonghuxm : 郑容华
     * keshiid : ST06
     * keshimc : 产二科
     */

    private String yonghugh;
    private String yonghuxm;
    private String keshiid;
    private String keshimc;
    private boolean isChecked;

    public IdName() {

    }

    public IdName(String keshimc) {
        this.keshimc = keshimc;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getYonghugh() {
        return yonghugh;
    }

    public void setYonghugh(String yonghugh) {
        this.yonghugh = yonghugh;
    }

    public String getYonghuxm() {
        return yonghuxm;
    }

    public void setYonghuxm(String yonghuxm) {
        this.yonghuxm = yonghuxm;
    }

    public String getKeshiid() {
        return keshiid;
    }

    public void setKeshiid(String keshiid) {
        this.keshiid = keshiid;
    }

    public String getKeshimc() {
        return keshimc;
    }

    public void setKeshimc(String keshimc) {
        this.keshimc = keshimc;
    }
}
