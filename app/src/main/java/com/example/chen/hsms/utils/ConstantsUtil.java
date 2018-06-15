package com.example.chen.hsms.utils;

/**
 * 常量工具类
 * Created by hjy on  2017/7/13.
 */
public class ConstantsUtil {
    /**
     * 网络监听
     * @author hjy
     *         created at 2016/11/25 12:03
     */
    public class NetWork {
        //移动：Moblie
        public static final String MOBLIE = "Moblie";
        // Wifi:Wifi
        public static final String WIFI = "Wifi";
        //当前是wifi状态
        public static final int IS_WIFI = 0x001;
        //当前是Mobile状态
        public static final int IS_MOBILE = 0x002;
        //都没有连接
        public static final int NO_CONNECTION = 0x003;
        //关闭wifi
        //public static final int OPEN_WIFI = 0x004;
        //打开wifi
        //public static final int CLOSE_WIFI = 0x005;
    }
}