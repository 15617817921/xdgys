package com.example.chen.hsms.bean.local;

import com.example.chen.hsms.bean.data.JiuCuo;

import java.util.List;

/**
 * Created by Administrator on 2018/3/26.
 */

public class OhterJiuCuo {
    private String hsd_id;
    private String hsd_time;
    private String keshi;
    private List<JiuCuo> list_jiucuo;

    public String getHsd_time() {
        return hsd_time;
    }

    public String getKeshi() {
        return keshi;
    }

    public void setKeshi(String keshi) {
        this.keshi = keshi;
    }

    public void setHsd_time(String hsd_time) {
        this.hsd_time = hsd_time;
    }

    public String getHsd_id() {
        return hsd_id;
    }

    public void setHsd_id(String hsd_id) {
        this.hsd_id = hsd_id;
    }

    public List<JiuCuo> getList_jiucuo() {
        return list_jiucuo;
    }

    public void setList_jiucuo(List<JiuCuo> list_jiucuo) {
        this.list_jiucuo = list_jiucuo;
    }
}
