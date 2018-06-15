package com.example.chen.hsms.bean.local;

import java.util.List;

/**
 * Created by admin on 2017/7/12.
 *假数据
 */

public class Bean {
    private String name;
    private String num;
    /**
     * title : 1
     * s : [{"mz":1,"num":3}]
     */

    private String title;
    private List<SBean> s;

    public Bean(String title, List<SBean> s) {
        this.title = title;
        this.s = s;
    }

    public Bean(String name, String num, String title) {
        this.name = name;
        this.num = num;
        this.title = title;
    }

    public Bean(String name, String num) {
        this.name = name;
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    
    public void setNum(String num) {
        this.num = num;
    }

    public String gettitle() {
        return title;
    }

    public void settitle(String title) {
        this.title = title;
    }

    public List<SBean> getS() {
        return s;
    }

    public void setS(List<SBean> s) {
        this.s = s;
    }

    public static class SBean {
        public SBean(String mz, String num) {
            this.mz = mz;
            this.num = num;
        }

        /**
         * mz : 1
         * num : 3
         */
        private String mz;
        private String num;

        public String getmz() {
            return mz;
        }

        public void setmz(String mz) {
            this.mz = mz;
        }

        public String getnum() {
            return num;
        }

        public void setnum(String num) {
            this.num = num;
        }
    }
}
