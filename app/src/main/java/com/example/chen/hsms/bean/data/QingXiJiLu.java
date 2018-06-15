package com.example.chen.hsms.bean.data;

/**
 * Created by Administrator on 2017/8/29 0029.
 */

public class QingXiJiLu {

    /**
     * ID : S246
     * WupinBPiciID : null
     * QingxijiID : 
     * QingxiGuoMC : 
     * QingXiKaishiSJ : null
     * QingXiJieShuSJ : null
     * QingXiGC : null
     * QingXiXLH : null
     * ShiyongZhuantai : null
     * QingXiRenID : null
     * QingXiRen : null
     * QingXiLan : 01
     * QingXiJia : null
     * WupinBzzjlID : 0000210294
     */

    private String ID;
    private String WupinBPiciID;
    private String QingxijiID;
    private String QingxiGuoMC;
    private String QingXiKaishiSJ;
    private String QingXiJieShuSJ;
    private String QingXiGC;
    private String QingXiXLH;
    private String ShiyongZhuantai;
    private String QingXiRenID;
    private String QingXiRen;
    private String QingXiLan;
    private String QingXiJia;
    private String WupinBzzjlID;

    public QingXiJiLu() {

    }

    public QingXiJiLu(String wupinBzzjlID, String qingXiLan) {
        WupinBzzjlID = wupinBzzjlID;
        QingXiLan = qingXiLan;
    }

    public QingXiJiLu(String wupinBzzjlID, String qingXiLan, String qingXiGC, String qingxijiID) {
        WupinBzzjlID = wupinBzzjlID;
        QingXiLan = qingXiLan;
        QingXiGC = qingXiGC;
        QingxijiID = qingxijiID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getWupinBPiciID() {
        return WupinBPiciID;
    }

    public void setWupinBPiciID(String WupinBPiciID) {
        this.WupinBPiciID = WupinBPiciID;
    }

    public String getQingxijiID() {
        return QingxijiID;
    }

    public void setQingxijiID(String QingxijiID) {
        this.QingxijiID = QingxijiID;
    }

    public String getQingxiGuoMC() {
        return QingxiGuoMC;
    }

    public void setQingxiGuoMC(String QingxiGuoMC) {
        this.QingxiGuoMC = QingxiGuoMC;
    }

    public String getQingXiKaishiSJ() {
        return QingXiKaishiSJ;
    }

    public void setQingXiKaishiSJ(String QingXiKaishiSJ) {
        this.QingXiKaishiSJ = QingXiKaishiSJ;
    }

    public String getQingXiJieShuSJ() {
        return QingXiJieShuSJ;
    }

    public void setQingXiJieShuSJ(String QingXiJieShuSJ) {
        this.QingXiJieShuSJ = QingXiJieShuSJ;
    }

    public String getQingXiGC() {
        return QingXiGC;
    }

    public void setQingXiGC(String QingXiGC) {
        this.QingXiGC = QingXiGC;
    }

    public String getQingXiXLH() {
        return QingXiXLH;
    }

    public void setQingXiXLH(String QingXiXLH) {
        this.QingXiXLH = QingXiXLH;
    }

    public String getShiyongZhuantai() {
        return ShiyongZhuantai;
    }

    public void setShiyongZhuantai(String ShiyongZhuantai) {
        this.ShiyongZhuantai = ShiyongZhuantai;
    }

    public String getQingXiRenID() {
        return QingXiRenID;
    }

    public void setQingXiRenID(String QingXiRenID) {
        this.QingXiRenID = QingXiRenID;
    }

    public String getQingXiRen() {
        return QingXiRen;
    }

    public void setQingXiRen(String QingXiRen) {
        this.QingXiRen = QingXiRen;
    }

    public String getQingXiLan() {
        return QingXiLan;
    }

    public void setQingXiLan(String QingXiLan) {
        this.QingXiLan = QingXiLan;
    }

    public String getQingXiJia() {
        return QingXiJia;
    }

    public void setQingXiJia(String QingXiJia) {
        this.QingXiJia = QingXiJia;
    }

    public String getWupinBzzjlID() {
        return WupinBzzjlID;
    }

    public void setWupinBzzjlID(String WupinBzzjlID) {
        this.WupinBzzjlID = WupinBzzjlID;
    }
}
