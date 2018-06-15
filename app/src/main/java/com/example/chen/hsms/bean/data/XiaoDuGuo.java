package com.example.chen.hsms.bean.data;

/**
 * Created by admin on 2017/7/11.
 */

public class XiaoDuGuo {
//    [{"ID":"M0001","XiaoDuGMC":"灭菌机1","DangQianGC":1,"YaLi":"205.8","WenDu":"132",
//            "XiaoDuCS":"","XiaoDuGZT":0,"DangQianXLH":49,"MieJunFS":"S125","ChiXuSJ":""},
    /**
     * ID : M0001
     * XiaoDuGMC : 灭菌机1
     * DangQianGC : 3
     * YaLi : 205.8
     * WenDu : 132
     * XiaoDuCS :
     * XiaoDuGZT : 1
     * DangQianXLH : 1705
     * MieJunFS : S125
     * ChiXuSJ :
     */

    private String ID;
    private String XiaoDuGMC;
    private int DangQianGC;
    private String YaLi;
    private String WenDu;
    private String XiaoDuCS;
    private int XiaoDuGZT;
    private int DangQianXLH;
    private String MieJunFS;
    private String ChiXuSJ;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getXiaoDuGMC() {
        return XiaoDuGMC;
    }

    public void setXiaoDuGMC(String XiaoDuGMC) {
        this.XiaoDuGMC = XiaoDuGMC;
    }

    public int getDangQianGC() {
        return DangQianGC;
    }

    public void setDangQianGC(int DangQianGC) {
        this.DangQianGC = DangQianGC;
    }

    public String getYaLi() {
        return YaLi;
    }

    public void setYaLi(String YaLi) {
        this.YaLi = YaLi;
    }

    public String getWenDu() {
        return WenDu;
    }

    public void setWenDu(String WenDu) {
        this.WenDu = WenDu;
    }

    public String getXiaoDuCS() {
        return XiaoDuCS;
    }

    public void setXiaoDuCS(String XiaoDuCS) {
        this.XiaoDuCS = XiaoDuCS;
    }

    public int getXiaoDuGZT() {
        return XiaoDuGZT;
    }

    public void setXiaoDuGZT(int XiaoDuGZT) {
        this.XiaoDuGZT = XiaoDuGZT;
    }

    public int getDangQianXLH() {
        return DangQianXLH;
    }

    public void setDangQianXLH(int DangQianXLH) {
        this.DangQianXLH = DangQianXLH;
    }

    public String getMieJunFS() {
        return MieJunFS;
    }

    public void setMieJunFS(String MieJunFS) {
        this.MieJunFS = MieJunFS;
    }

    public String getChiXuSJ() {
        return ChiXuSJ;
    }

    public void setChiXuSJ(String ChiXuSJ) {
        this.ChiXuSJ = ChiXuSJ;
    }
}
