package com.example.chen.hsms.bean.data;

/**
 * Created by admin on 2017/7/11.
 */

public class BaseData {
    /**
     * ID : S103
     * JiChuSJID : 11
     * JiChuSJName : null
     * Name : 把
     * ZuoFeiBZ : 0
     * ChaXunMa :
     */

    private String ID;
    private String JiChuSJID;
    private Object JiChuSJName;
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

    public Object getJiChuSJName() {
        return JiChuSJName;
    }

    public void setJiChuSJName(Object JiChuSJName) {
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
