package com.example.chen.hsms.bean.data;

/**
 * Created by Administrator on 2018/1/16.
 */

public class ZhuiSuData {
    private String shujuName;

    private String shuju;
    private String xmid;
    private boolean isImage;

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public ZhuiSuData(String shujuName, String shuju, String xmid, boolean isImage) {
        this.shujuName = shujuName;
        this.shuju = shuju;
        this.xmid = xmid;
        this.isImage = isImage;
    }

    public ZhuiSuData(String shujuName, String shuju, String xmid) {
        this.shujuName = shujuName;
        this.shuju = shuju;
        this.xmid = xmid;
    }

    public String getShujuName() {
        return shujuName;
    }

    public void setShujuName(String shujuName) {
        this.shujuName = shujuName;
    }

    public String getShuju() {
        return shuju;
    }

    public void setShuju(String shuju) {
        this.shuju = shuju;
    }

    public String getXmid() {
        return xmid;
    }

    public void setXmid(String xmid) {
        this.xmid = xmid;
    }
}
