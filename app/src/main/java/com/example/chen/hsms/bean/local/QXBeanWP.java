package com.example.chen.hsms.bean.local;

import com.example.chen.hsms.bean.data.Hsd_Mx;
import com.example.chen.hsms.bean.data.QXBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/31 0031.
 */

public class QXBeanWP {
    private String pc_id;
   private String wp_id;
   private String wp_name;
   private String keshi_name;
   private String hsd_id;
   private String liShiZZJLID;
   private int num;
    private List<Hsd_Mx> list_qingdian;

    public String getLiShiZZJLID() {
        return liShiZZJLID;
    }

    public void setLiShiZZJLID(String liShiZZJLID) {
        this.liShiZZJLID = liShiZZJLID;
    }

    public String getHsd_id() {
        return hsd_id;
    }

    public void setHsd_id(String hsd_id) {
        this.hsd_id = hsd_id;
    }

    public String gethsd_id() {
        return hsd_id;
    }

    public void sethsd_id(String hsd_id) {
        this.hsd_id = hsd_id;
    }

    public String getKeshi_name() {
        return keshi_name;
    }

    public void setKeshi_name(String keshi_name) {
        this.keshi_name = keshi_name;
    }

    public List<Hsd_Mx> getList_qingdian() {
        return list_qingdian;
    }

    public void setList_qingdian(List<Hsd_Mx> list_qingdian) {
        this.list_qingdian = list_qingdian;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    private List<QXBean> list=new ArrayList<>();

    public String getWp_name() {
        return wp_name;
    }

    public void setWp_name(String wp_name) {
        this.wp_name = wp_name;
    }

    public String getPc_id() {
        return pc_id;
    }

    public void setPc_id(String pc_id) {
        this.pc_id = pc_id;
    }

    public String getWp_id() {
        return wp_id;
    }

    public void setWp_id(String wp_id) {
        this.wp_id = wp_id;
    }

    public List<QXBean> getList() {
        return list;
    }

    public void setList(List<QXBean> list) {
        this.list = list;
    }
}
