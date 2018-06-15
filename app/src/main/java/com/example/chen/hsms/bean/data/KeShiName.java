package com.example.chen.hsms.bean.data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/1 0001.
 */

public class KeShiName implements Serializable{
    /**
     * ID : 1002
     * JiChuSJID : 3
     * JiChuSJName : 科室
     * Name : 手术室(住院)
     * ZuoFeiBZ : 0
     * ChaXunMa :
     */

    private String ID;
    private String JiChuSJID;
    private String JiChuSJName;
    private String Name;
    private int ZuoFeiBZ;
    private String ChaXunMa;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getJiChuSJID() {
        return JiChuSJID;
    }

    public void setJiChuSJID(String JiChuSJID) {
        this.JiChuSJID = JiChuSJID;
    }

    public String getJiChuSJName() {
        return JiChuSJName;
    }

    public void setJiChuSJName(String JiChuSJName) {
        this.JiChuSJName = JiChuSJName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getZuoFeiBZ() {
        return ZuoFeiBZ;
    }

    public void setZuoFeiBZ(int ZuoFeiBZ) {
        this.ZuoFeiBZ = ZuoFeiBZ;
    }

    public String getChaXunMa() {
        return ChaXunMa;
    }

    public void setChaXunMa(String ChaXunMa) {
        this.ChaXunMa = ChaXunMa;
    }



}
