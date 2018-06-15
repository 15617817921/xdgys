package com.example.chen.hsms.utils;

import android.text.TextUtils;

import com.example.chen.hsms.bean.data.Hsd_Mx;
import com.example.chen.hsms.bean.data.QXBean;
import com.example.chen.hsms.bean.local.QXBeanWP;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期时间的工具。
 */
public class DateUtil {


    private static DateUtil util;
    private SimpleDateFormat formatter;
    private Calendar calendar;

    public static DateUtil getInstance() {
        if (util == null) {
            util = new DateUtil();
        }
        return util;

    }

    private DateUtil() {
        super();
    }

    public SimpleDateFormat dateFormater = new SimpleDateFormat(
            "yyyy/MM/dd");


    public Date getDate(String dateStr) {
        Date date = new Date();
        if (TextUtils.isEmpty(dateStr)) {
            return date;
        }
        try {
            date = dateFormater.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Calendar getCalendar(String dateStr) {
        // 声明一个Date类型的对象
        Date date = null;
        // 声明格式化日期的对象
        SimpleDateFormat format = null;
        if (dateStr != null) {
            // 创建日期的格式化类型
            format = new SimpleDateFormat("yyyy/MM/dd");
            // 创建一个Calendar类型的对象
            calendar = Calendar.getInstance();
            // forma.parse()方法会抛出异常
            try {
                //解析日期字符串，生成Date对象
                date = format.parse(dateStr);
                // 使用Date对象设置此Calendar对象的时间
                calendar.setTime(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return calendar;
    }

    /**
     * 时间
     * 年月日时分秒
     *
     * @return
     */
    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String date = formatter.format(curDate);
        return date;
    }

    /**
     * 时间
     * 年月日
     *
     * @return
     */
    public String getYear_Day() {
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        String date = formatter.format(curDate);
//        String nyr = date.substring(0, date.length() - 8);
        return date;
    }

    public String getTimeToDate(String time) {//14位20170906 101230

        String n = time.substring(0, 4);
        String y = time.substring(4, 6);
        String r = time.substring(6, 8);
        String s = time.substring(8, 10);
        String f = time.substring(10, 12);
        String m = time.substring(12, time.length());
        return n + "-" + y + "-" + r + " " + s + ":" + f + ":" + m;
    }

    public String getTime() {//14位
        formatter = new SimpleDateFormat("yyyy/MM/ddHH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        String date = formatter.format(curDate);

        String s1 = date.replace("/", "");
        String s2 = s1.replace(":", "");
        return s2.trim();
    }

    /**
     * 时间
     * 时分秒
     *
     * @return
     */
    public String getHour_s() {
        formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        String date = formatter.format(curDate);
        String sfm = date.substring(date.length() - 8, date.length());

        return sfm;
    }
    /**
     * 时间
     * 时分秒
     *
     * @return
     */
    public String getNian_miao() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmssSSS");
        String hm =formatter.format(new Date());

        return hm;
    }

    /**
     * 比较两个日期的大小，日期格式为yyyy-MM-dd
     *
     * @param str1 the first date
     * @param str2 the second date
     * @return true <br/>false
     */
    public boolean isDateOneBigger(String str1, String str2) {
        boolean isBigger = false;
        formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = formatter.parse(str1);
            dt2 = formatter.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() > dt2.getTime()) {
            isBigger = true;
        } else if (dt1.getTime() < dt2.getTime()) {
            isBigger = false;
        }
        return isBigger;
    }

    public List<List<QXBeanWP>> getToKeShiWuPinB(List<QXBean> list_qingdian) {
        ArrayList<String> pici_list = new ArrayList<>();
        List<List<QXBean>> result = new ArrayList<>();

        for (int i = 0; i < list_qingdian.size(); i++) {
            String s = list_qingdian.get(i).getLingYongKS();//科室id 对比科室是否相同
            //先去匹配pici_list
            if (pici_list.size() == 0) {
                pici_list.add(s);
            }
            int status = 0;
            for (int j = 0; j < pici_list.size(); j++) {
                if (s.equals(pici_list.get(j)) && i != 0) {
                    status = 1;
                    break;
                }
            }

            if (status == 0) {
                pici_list.add(s);
                List<QXBean> pipei = new ArrayList<>();
                for (int z = 0; z < list_qingdian.size(); z++) {
                    if (s.equals(list_qingdian.get(z).getLingYongKS())) {
                        pipei.add(list_qingdian.get(z));
                    }
                }
                result.add(pipei);
            }
        }

        List<List<QXBeanWP>> fianl_list = new ArrayList<>();

        //循环第一次分类结果，每一次分类结果为ArrayList<QXBeanWP>
        for (int z = 0; z < result.size(); z++) {
            ArrayList<String> wpid_list = null;
            wpid_list = new ArrayList<>();
            //找出当前频次下，所有的物品ID保存盗wpid_list中
            for (int i = 0; i < result.get(z).size(); i++) {
                String wpid = result.get(z).get(i).getWuPinBID();
                if (wpid_list.size() == 0) {
                    wpid_list.add(wpid);
                } else {
                    int status = 0;
                    for (int j = 0; j < wpid_list.size(); j++) {
                        if (wpid.equals(wpid_list.get(j))) {
                            status = 1;
                            break;
                        }
                    }
                    if (status == 0) {
                        wpid_list.add(wpid);
                    }
                }
            }
            //循环wpid_list，去匹配每一条。没一个物品ID形成一个QXBeanWp
            //每一个批次有很多个物品ID，所以形成ArrayList<QXBeanWP>
            List<QXBeanWP> wp_result = new ArrayList<>();
            for (int w = 0; w < wpid_list.size(); w++) {
                String wpid = wpid_list.get(w);

                List<QXBean> one_list = null;
                one_list = new ArrayList<>();
                one_list = result.get(z);

                QXBeanWP wp = new QXBeanWP();
                List<QXBean> result_list = null;
                result_list = new ArrayList<>();
                String wp_name = "";
                for (int k = 0; k < one_list.size(); k++) {
                    if (wpid.equals(one_list.get(k).getWuPinBID())) {
                        result_list.add(one_list.get(k));
                        wp_name = one_list.get(k).getWuPinBMC();
                    }
                }
                wp.setList(result_list);//数量
                wp.setWp_id(wpid);//物品包id
                wp.setWp_name(wp_name);//物品包名称
                wp.setPc_id(result.get(z).get(0).getLingYongKS());//科室
                wp_result.add(wp);
            }
            //在同以批次下，添加分类好的wp_result
            fianl_list.add(wp_result);
        }
        return fianl_list;
    }

    //清洗
    public ArrayList<ArrayList<QXBeanWP>> getZuangHuan(List<QXBean> list_huoqpici) {
        ArrayList<String> pici_list = new ArrayList<>();
        ArrayList<ArrayList<QXBean>> result = new ArrayList<>();

        for (int i = 0; i < list_huoqpici.size(); i++) {
            String s = list_huoqpici.get(i).getPiCi();
            //先去匹配pici_list
            if (pici_list.size() == 0) {
                pici_list.add(s);
            }
            int status = 0;
            for (int j = 0; j < pici_list.size(); j++) {
                if (s.equals(pici_list.get(j)) && i != 0) {
                    status = 1;
                    break;
                }
            }

            if (status == 0) {
                pici_list.add(s);
                ArrayList<QXBean> pipei = new ArrayList<>();
                for (int z = 0; z < list_huoqpici.size(); z++) {
                    if (s.equals(list_huoqpici.get(z).getPiCi())) {
                        pipei.add(list_huoqpici.get(z));
                    }
                }
                result.add(pipei);
            }
        }

        ArrayList<ArrayList<QXBeanWP>> fianl_list = new ArrayList<>();

        //循环第一次分类结果，每一次分类结果为ArrayList<QXBeanWP>
        for (int z = 0; z < result.size(); z++) {
            ArrayList<String> wpid_list = null;
            wpid_list = new ArrayList<>();
            //找出当前频次下，所有的物品ID保存盗wpid_list中
            for (int i = 0; i < result.get(z).size(); i++) {
                String wpid = result.get(z).get(i).getWuPinBID();
                if (wpid_list.size() == 0) {
                    wpid_list.add(wpid);
                } else {
                    int status = 0;
                    for (int j = 0; j < wpid_list.size(); j++) {
                        if (wpid.equals(wpid_list.get(j))) {
                            status = 1;
                            break;
                        }
                    }
                    if (status == 0) {
                        wpid_list.add(wpid);
                    }
                }
            }
            //循环wpid_list，去匹配每一条。没一个物品ID形成一个QXBeanWp
            //每一个批次有很多个物品ID，所以形成ArrayList<QXBeanWP>
            ArrayList<QXBeanWP> wp_result = new ArrayList<>();
            for (int w = 0; w < wpid_list.size(); w++) {
                String wpid = wpid_list.get(w);

                ArrayList<QXBean> one_list = null;
                one_list = new ArrayList<>();
                one_list = result.get(z);

                QXBeanWP wp = new QXBeanWP();
                ArrayList<QXBean> result_list = null;
                result_list = new ArrayList<>();
                String wp_name = "";
                for (int k = 0; k < one_list.size(); k++) {
                    if (wpid.equals(one_list.get(k).getWuPinBID())) {
                        result_list.add(one_list.get(k));
                        wp_name = one_list.get(k).getWuPinBMC();
                    }
                }
                wp.setList(result_list);
                wp.setWp_id(wpid);
                wp.setWp_name(wp_name);
                wp.setPc_id(result.get(z).get(0).getPiCi());
                wp_result.add(wp);
            }
            //在同以批次下，添加分类好的wp_result
            fianl_list.add(wp_result);
        }
        return fianl_list;
    }

    //请领发放单
    public ArrayList<ArrayList<QXBeanWP>> getFafangdan(List<QXBean> list_data){
        ArrayList<String> pici_list = new ArrayList<>();
        ArrayList<ArrayList<QXBean>> result = new ArrayList<>();

        for (int i = 0; i < list_data.size(); i++) {
            String s = list_data.get(i).getLingYongKS();
            if (!s.equals("")) {
                //先去匹配pici_list
                if (pici_list.size() == 0) {
                    pici_list.add(s);
                }
                int status = 0;
                for (int j = 0; j < pici_list.size(); j++) {
                    if (s.equals(pici_list.get(j)) && i != 0) {
                        status = 1;
                        break;
                    }
                }
                if (status == 0) {
                    pici_list.add(s);
                    ArrayList<QXBean> pipei = new ArrayList<>();
                    for (int z = 0; z < list_data.size(); z++) {
                        if (s.equals(list_data.get(z).getLingYongKS())) {
                            pipei.add(list_data.get(z));
                        }
                    }
                    result.add(pipei);
                }
            }


        }
//            String json = JSON.toJSONString(result, SerializerFeature.WriteNullStringAsEmpty);
//            log.e( json + "");

        ArrayList<ArrayList<QXBeanWP>> fianl_list = new ArrayList<>();

        //循环第一次分类结果，每一次分类结果为ArrayList<QXBeanWP>
        for (int z = 0; z < result.size(); z++) {
            ArrayList<String> wpid_list = null;
            wpid_list = new ArrayList<>();
            //找出当前批次下，所有的物品ID保存盗wpid_list中
            for (int i = 0; i < result.get(z).size(); i++) {
                String wpid = result.get(z).get(i).getWuPinBID();
                if (wpid_list.size() == 0) {
                    wpid_list.add(wpid);
                } else {
                    int status = 0;
                    for (int j = 0; j < wpid_list.size(); j++) {
                        if (wpid.equals(wpid_list.get(j))) {
                            status = 1;
                            break;
                        }
                    }
                    if (status == 0) {
                        wpid_list.add(wpid);
                    }
                }
            }
            //循环wpid_list，去匹配每一条。没一个物品ID形成一个QXBeanWP
            //每一个批次有很多个物品ID，所以形成ArrayList<QXBeanWP>
            ArrayList<QXBeanWP> wp_result = new ArrayList<>();
            for (int w = 0; w < wpid_list.size(); w++) {
                String wpid = wpid_list.get(w);

                ArrayList<QXBean> one_list = null;
                one_list = new ArrayList<>();
                one_list = result.get(z);

                QXBeanWP wp = new QXBeanWP();
                ArrayList<QXBean> result_list = null;
                result_list = new ArrayList<>();
                String wp_name = "";
                for (int k = 0; k < one_list.size(); k++) {
                    if (wpid.equals(one_list.get(k).getWuPinBID())) {
                        result_list.add(one_list.get(k));
                        wp_name = one_list.get(k).getWuPinBMC();
                    }
                }
                wp.setList(result_list);
                wp.setWp_id(wpid);
                wp.setWp_name(wp_name);
                wp.setPc_id(result.get(z).get(0).getLingYongKS());
                wp_result.add(wp);
            }
            //在同以批次下，添加分类好的wp_result
            fianl_list.add(wp_result);
        }
        return fianl_list;
    }
    //清点页面--所有物品包分类
    public List<List<QXBeanWP>>  getToQingDian(List<Hsd_Mx> list_huoqpici) {


        ArrayList<String> keshi_list = new ArrayList<>();
        List<List<Hsd_Mx>> result = new ArrayList<>();

        for (int i = 0; i < list_huoqpici.size(); i++) {
            String s = list_huoqpici.get(i).getHuiShouDID();//回收单是否相同
            //先去匹配pici_list
            if (keshi_list.size() == 0) {
                keshi_list.add(s);
            }
            int status = 0;
            for (int j = 0; j < keshi_list.size(); j++) {
                if (s.equals(keshi_list.get(j)) && i != 0) {
                    status = 1;
                    break;
                }
            }

            if (status == 0) {
                keshi_list.add(s);
                List<Hsd_Mx> pipei = new ArrayList<>();
                for (int z = 0; z < list_huoqpici.size(); z++) {
                    if (s.equals(list_huoqpici.get(z).getHuiShouDID())) {
                        pipei.add(list_huoqpici.get(z));
                    }
                }
                result.add(pipei);
            }
        }


        List<List<QXBeanWP>> fianl_list = new ArrayList<>();

        //循环第一次分类结果，每一次分类结果为ArrayList<QXBeanWP>
        for (int z = 0; z < result.size(); z++) {
            ArrayList<String> wpid_list = null;
            wpid_list = new ArrayList<>();
            //找出当前频次下，所有的物品ID保存盗wpid_list中
            for (int i = 0; i < result.get(z).size(); i++) {
                String wpid = result.get(z).get(i).getWuPinBID();
                if (wpid_list.size() == 0) {
                    wpid_list.add(wpid);
                } else {
                    int status = 0;
                    for (int j = 0; j < wpid_list.size(); j++) {
                        if (wpid.equals(wpid_list.get(j))) {
                            status = 1;
                            break;
                        }
                    }
                    if (status == 0) {
                        wpid_list.add(wpid);
                    }
                }
            }
            //循环wpid_list，去匹配每一条。没一个物品ID形成一个QXBeanWp
            //每一个批次有很多个物品ID，所以形成ArrayList<QXBeanWP>
            List<QXBeanWP> wp_result = new ArrayList<>();
            for (int w = 0; w < wpid_list.size(); w++) {
                String wpid = wpid_list.get(w);

                List<Hsd_Mx> one_list = new ArrayList<>();
                one_list = result.get(z);

                QXBeanWP wp = new QXBeanWP();
                List<Hsd_Mx> result_list = null;
                result_list = new ArrayList<>();
                String wp_name = "";
                String keshi = "";
                String hsdid = "";
                String lszzjl = "";
//                int num = 0;
                for (int k = 0; k < one_list.size(); k++) {
                    if (wpid.equals(one_list.get(k).getWuPinBID())) {
                        result_list.add(one_list.get(k));
                        wp_name = one_list.get(k).getWuPinBMC();
                        keshi = one_list.get(k).getShenQingKS();
                        hsdid = one_list.get(k).getHuiShouDID();
                        lszzjl = one_list.get(k).getLiShiZZJLID();
                    }
                }
                wp.setList_qingdian(result_list);//数量
                wp.setWp_id(wpid);//物品包id
                wp.setWp_name(wp_name);//物品包名称
                wp.setKeshi_name(keshi);
                wp.sethsd_id(hsdid);
                wp.setNum(result_list.size());//
                wp.setLiShiZZJLID(lszzjl);//
                wp.setPc_id(result.get(z).get(0).getHuiShouDID());//回收单

                wp_result.add(wp);
            }
            //在同以批次下，添加分类好的wp_result
            fianl_list.add(wp_result);
        }
//        String listno = JSON.toJSONString(fianl_list, SerializerFeature.DisableCircularReferenceDetect);
//        MyLogger.kLog().e( "--"+listno);

//        List<QXBeanWP> list_last = new ArrayList<>();
//        for (int i = 0; i < fianl_list.size(); i++) {
//            List<QXBeanWP> cuoList = fianl_list.get(i);
//            for (int j = 0; j < cuoList.size(); j++) {
//                list_last.add(cuoList.get(j));
//            }
//        }
        return fianl_list;
    }

    //清点页面--所有物品包分类
    public List<List<QXBeanWP>> getToQingDian1(List<Hsd_Mx> list_huoqpici) {
        ArrayList<String> keshi_list = new ArrayList<>();
        List<List<Hsd_Mx>> result = new ArrayList<>();

        for (int i = 0; i < list_huoqpici.size(); i++) {
            String s = list_huoqpici.get(i).getHuiShouDID();//回收单是否相同
            //先去匹配pici_list
            if (keshi_list.size() == 0) {
                keshi_list.add(s);
            }
            int status = 0;
            for (int j = 0; j < keshi_list.size(); j++) {
                if (s.equals(keshi_list.get(j)) && i != 0) {
                    status = 1;
                    break;
                }
            }

            if (status == 0) {
                keshi_list.add(s);
                List<Hsd_Mx> pipei = new ArrayList<>();
                for (int z = 0; z < list_huoqpici.size(); z++) {
                    if (s.equals(list_huoqpici.get(z).getHuiShouDID())) {
                        pipei.add(list_huoqpici.get(z));
                    }
                }
                result.add(pipei);
            }
        }


        List<List<QXBeanWP>> fianl_list = new ArrayList<>();

        //循环第一次分类结果，每一次分类结果为ArrayList<QXBeanWP>
        for (int z = 0; z < result.size(); z++) {
            ArrayList<String> wpid_list = null;
            wpid_list = new ArrayList<>();
            //找出当前频次下，所有的物品ID保存盗wpid_list中
            for (int i = 0; i < result.get(z).size(); i++) {
                String wpid = result.get(z).get(i).getWuPinBID();
                if (wpid_list.size() == 0) {
                    wpid_list.add(wpid);
                } else {
                    int status = 0;
                    for (int j = 0; j < wpid_list.size(); j++) {
                        if (wpid.equals(wpid_list.get(j))) {
                            status = 1;
                            break;
                        }
                    }
                    if (status == 0) {
                        wpid_list.add(wpid);
                    }
                }
            }
            //循环wpid_list，去匹配每一条。没一个物品ID形成一个QXBeanWp
            //每一个批次有很多个物品ID，所以形成ArrayList<QXBeanWP>
            List<QXBeanWP> wp_result = new ArrayList<>();
            for (int w = 0; w < wpid_list.size(); w++) {
                String wpid = wpid_list.get(w);

                List<Hsd_Mx> one_list = new ArrayList<>();
                one_list = result.get(z);

                QXBeanWP wp = new QXBeanWP();
                List<Hsd_Mx> result_list = null;
                result_list = new ArrayList<>();
                String wp_name = "";
                String keshi = "";
                int num = 0;
                for (int k = 0; k < one_list.size(); k++) {
                    if (wpid.equals(one_list.get(k).getWuPinBID())) {
                        result_list.add(one_list.get(k));
                        wp_name = one_list.get(k).getWuPinBMC();
                        keshi = one_list.get(k).getShenQingKS();
                        num = one_list.get(k).getKeShiSL();

                    }
                }
                wp.setList_qingdian(result_list);//数量
                wp.setWp_id(wpid);//物品包id
                wp.setWp_name(wp_name);//物品包名称
                wp.setKeshi_name(keshi);
                wp.setNum(num);//
                wp.setPc_id(result.get(z).get(0).getHuiShouDID());//回收单
                wp_result.add(wp);
            }
            //在同以批次下，添加分类好的wp_result
            fianl_list.add(wp_result);
        }
        return fianl_list;
    }
    //                                List<String> list_zzjl = new ArrayList<>();
//                                QingXiJiLu qxjl = null;
//                                for (int i = 0; i < list_qxjl_jia.size(); i++) {
//                                    qxjl = list_qxjl_jia.get(i);
//                                    String bzzjlID = qxjl.getWupinBzzjlID();
//                                    if (list_zzjl.size() == 0) {
//                                        list_zzjl.add(bzzjlID);
//                                    } else {
//                                        for (int j = 0; j < list_zzjl.size(); j++) {
//                                            if (!bzzjlID.equals(list_zzjl.get(j))) {
//                                                list_zzjl.add(bzzjlID);
//                                            }
//                                        }
//                                    }
//                                }
}
