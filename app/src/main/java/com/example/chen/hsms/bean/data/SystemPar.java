package com.example.chen.hsms.bean.data;

/**
 * Created by admin on 2017/7/11.
 */

public class SystemPar {
//    {"ID":"104","CanShuMC":"手术室科室ID","CanShuZhi":"STS1","MoRenZhi":"11","CanShuLX":"ALL","CanShuSM":""}
    /**
     * ID : 1
     * CanShuMC : 医院名称
     * CanShuZhi : 福建省妇幼保健院
     * MoRenZhi : 未知
     * CanShuLX : All
     * CanShuSM :
     */

    private String ID;
    private String CanShuMC;
    private String CanShuZhi;
    private String MoRenZhi;
    private String CanShuLX;
    private String CanShuSM;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCanShuMC() {
        return CanShuMC;
    }

    public void setCanShuMC(String CanShuMC) {
        this.CanShuMC = CanShuMC;
    }

    public String getCanShuZhi() {
        return CanShuZhi;
    }

    public void setCanShuZhi(String CanShuZhi) {
        this.CanShuZhi = CanShuZhi;
    }

    public String getMoRenZhi() {
        return MoRenZhi;
    }

    public void setMoRenZhi(String MoRenZhi) {
        this.MoRenZhi = MoRenZhi;
    }

    public String getCanShuLX() {
        return CanShuLX;
    }

    public void setCanShuLX(String CanShuLX) {
        this.CanShuLX = CanShuLX;
    }

    public String getCanShuSM() {
        return CanShuSM;
    }

    public void setCanShuSM(String CanShuSM) {
        this.CanShuSM = CanShuSM;
    }
}
