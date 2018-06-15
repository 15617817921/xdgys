package com.example.chen.hsms.utils;

/**
 * Created by admin on 2017/7/10.
 */

public class Constant {
    public static final String NAMESPACE = "http://tempuri.org/";//命名空间
    public static final String METHODNAME = "Interf";//方法名

    //扫描
    public static final String ACTION_BARCODE_READER_VALUE = "com.neusoft.action.scanner.read";
    public static final String BORCODE_VALUE = "scanner_value";

    public static final String LOGIN = "正在登录...";

    public static final String ERROR = "服务器返回数据错误";

    public static final String NOTIAOMA = "无效的条码";
    
    public static final String CONNECTEXCEPTION = "请检查网络是否在同一网端";
    public static final String FANHUINULL = "返回结果为空";
    public static final String USERNULL = "用户名或密码不能为空";
    public static final String MSGERRO = "用户名或密码错误";
    public static final String SUCCSE = "登录成功";
    public static final String OTHER = "请核对条码是否满足条件";
    public static final String CHAOSHI = "请求超时";
    public static final String JIAZAI = "加载中...";
    public static final String NONET = "网络似乎开小差了";
    public static final String IP = "请先打开设置页面配置链接地址";
    public static final String HSMS = "hsms结果为空";

    public static final String NODATA = "返回数据错误";

    public static final String EXIT = "再次点击退出";
    public static final String FUHAO = "#";
    public static final String SCB = "  (首次包)";



    public static final String UESRNAME = "uesrname";//账户密码
    public static final String PASSWORD = "password";

    public static final String XING = "*";//拼接
    public static final String STRING = "string¤";//拼接
    public static final String DATATABLE = "DataTable¤";//拼接
    public static final String DATASET = "DataSet¤";//拼接
    public static final String CODE = "para_operaCode";//参数1
    public static final String PARAMETER = "para_parameter";//参数2
    public static final String TEST_ID = "0000001469";
    public static final String BS64 = "data:image/jpeg;base64,";
    public static final String MODEL = "Camus";

    //用于灭菌页面
    public static final String ISDABMIEJUN = "未打包能否灭菌";//"ptbao@";

//    public static final String WPBAOHEAD = "物品包条码识别头";//"ptbao@";
//    public static final String QXLANHEAD = "清洗篮条码识别头";//"ptbao@";
//    public static final String QXJIAHEAD = "清洗架条码识别头";//"ptbao@";
//    public static final String QXJIHEAD = "清洗锅条码识别头";//"qxguo@";
//    public static final String MIEJUNHEAD = "灭菌锅条码识别头";//"mjguo@";
//    public static final String XDCHEHEAD = "消毒车条码识别头";//"ptbao@";

//    public static final String PTBAOHEAD = "普通包条码识别头";//"ptbao@";


    //清洗类型
    public static final String QINGXIJI = "清洗机";//"ptbao@";
    public static final String QXXIAODUJI = "清洗消毒机";//"ptbao@";

    //标识头参数值
    public static final String QLD = "qldan";//请领单;
    public static final String PTB = "ptbao";//物品包
    public static final String QXL = "qxlan";//清洗篮
    public static final String QXJ = "qxjia";//清洗架
    public static final String QXG = "qxguo";//清洗锅
    public static final String XDC = "xdche";//消毒车
    public static final String MJG = "mjguo";//灭菌锅
    public static final String ZJR = "zjren";//灭菌锅

    public static final String TAGTOU = "系统参数中未能识别此标识头";

    //扫描条码
    public static final String QINGDIAN = "请核对是否为物品包或清洗篮、架、机的条码";
    public static final String QINGXI = "请核对是否为清洗篮、架、机的条码";
    public static final String DABAO = "请核对是否为物品包或消毒车条码";
    public static final String MIEJUN = "请核对是否为消毒车或灭菌锅条码";
    public static final String NOWPBTM = "请核对是否是物品包条码";

}
