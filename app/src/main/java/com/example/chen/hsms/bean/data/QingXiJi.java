package com.example.chen.hsms.bean.data;

/**
 * Created by Administrator on 2017/8/28 0028.
 */

public class QingXiJi {


    /**
     * ID : 1
     * QingXiGuoMC : 清洗机1
     * DangQianGC : 48
     * DangQianXLH : 48
     * Pressure : 1
     * Temperature : 100
     * MoRenQingXiTime : 10
     * MoRenPrintTimes : 5
     * QingXiZT : 8
     */

    private String ID;
    private String QingXiGuoMC;
    private String DangQianGC;
    private String DangQianXLH;
    private String Pressure;
    private String Temperature;
    private String MoRenQingXiTime;
    private String MoRenPrintTimes;
    private String QingXiZT;
    private String LeiXingID;
    private String LeiXing;
    private int flowremark;

    public int getFlowremark() {
        return flowremark;
    }

    public void setFlowremark(int flowremark) {
        this.flowremark = flowremark;
    }

    public String getLeiXingID() {
        return LeiXingID;
    }

    public void setLeiXingID(String leiXingID) {
        LeiXingID = leiXingID;
    }

    public String getLeiXing() {
        return LeiXing;
    }

    public void setLeiXing(String leiXing) {
        LeiXing = leiXing;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getQingXiGuoMC() {
        return QingXiGuoMC;
    }

    public void setQingXiGuoMC(String QingXiGuoMC) {
        this.QingXiGuoMC = QingXiGuoMC;
    }

    public String getDangQianGC() {
        return DangQianGC;
    }

    public void setDangQianGC(String DangQianGC) {
        this.DangQianGC = DangQianGC;
    }

    public String getDangQianXLH() {
        return DangQianXLH;
    }

    public void setDangQianXLH(String DangQianXLH) {
        this.DangQianXLH = DangQianXLH;
    }

    public String getPressure() {
        return Pressure;
    }

    public void setPressure(String Pressure) {
        this.Pressure = Pressure;
    }

    public String getTemperature() {
        return Temperature;
    }

    public void setTemperature(String Temperature) {
        this.Temperature = Temperature;
    }

    public String getMoRenQingXiTime() {
        return MoRenQingXiTime;
    }

    public void setMoRenQingXiTime(String MoRenQingXiTime) {
        this.MoRenQingXiTime = MoRenQingXiTime;
    }

    public String getMoRenPrintTimes() {
        return MoRenPrintTimes;
    }

    public void setMoRenPrintTimes(String MoRenPrintTimes) {
        this.MoRenPrintTimes = MoRenPrintTimes;
    }

    public String getQingXiZT() {
        return QingXiZT;
    }

    public void setQingXiZT(String QingXiZT) {
        this.QingXiZT = QingXiZT;
    }
}
