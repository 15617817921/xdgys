package com.example.chen.hsms.bean.data;

/**
 * Created by Administrator on 2018/3/26.
 */
  //private boolean isChecked;
public class JiuCuo {
    /**
     * ID : 0000210372
     * WupinBID : S1161
     * WupinBMC : 广泛另包(无纺布)
     * ShenQingKSID : STS1
     * ShenQingKS : 手术室
     * HuiShouDID : S7456
     * ShenQingSJ : 2018-03-21 22:18:54
     * WupinbaoImage : null
     */

    private String ID;
    private String WupinBID;
    private String WupinBMC;
    private String ShenQingKSID;
    private String ShenQingKS;
    private String HuiShouDID;
    private String ShenQingSJ;
    private String WupinbaoImage;
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getWupinBID() {
        return WupinBID;
    }

    public void setWupinBID(String WupinBID) {
        this.WupinBID = WupinBID;
    }

    public String getWupinBMC() {
        return WupinBMC;
    }

    public void setWupinBMC(String WupinBMC) {
        this.WupinBMC = WupinBMC;
    }

    public String getShenQingKSID() {
        return ShenQingKSID;
    }

    public void setShenQingKSID(String ShenQingKSID) {
        this.ShenQingKSID = ShenQingKSID;
    }

    public String getShenQingKS() {
        return ShenQingKS;
    }

    public void setShenQingKS(String ShenQingKS) {
        this.ShenQingKS = ShenQingKS;
    }

    public String getHuiShouDID() {
        return HuiShouDID;
    }

    public void setHuiShouDID(String HuiShouDID) {
        this.HuiShouDID = HuiShouDID;
    }

    public String getShenQingSJ() {
        return ShenQingSJ;
    }

    public void setShenQingSJ(String ShenQingSJ) {
        this.ShenQingSJ = ShenQingSJ;
    }

    public String getWupinbaoImage() {
        return WupinbaoImage;
    }

    public void setWupinbaoImage(String WupinbaoImage) {
        this.WupinbaoImage = WupinbaoImage;
    }
}
