package com.example.chen.hsms.webservice;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.utils.SaveUtils;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.ws.Hsms;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import java.net.ConnectException;
/**
 * Created by CHEN on 2017-6-14.
 */

public abstract class Webservice {
    public static final String NAMESPACE = "http://tempuri.org/";
    // WebService地址
    private String URL;

    // "http://192.168.19.114/EOIISService.asmx"

    /**
     * web service通信
     *
     * @param context
     * @param handler
     * @param methodName
     *            方法名
     * @param flag
     *            true:get data that should be resolved ;false:send data
     * @param
     *
     */
    @SuppressWarnings("rawtypes")
    public Webservice(Context context, Handler handler, String methodName,
                      boolean flag) {//http://192.168.7.25:3030/HSMS_Service.asmx
        if(!SaveUtils.contains(context,"url")){
            ToastUtils.showLongSafe("请先打开设置页面配置链接地址");
            return;
        }
        URL= (String) SaveUtils.get(context,"url","");
//        URL = "http://"
//                + context.getSharedPreferences("HSMS", Context.MODE_PRIVATE)
//                .getString("IP", "172.20.10.3:3030/HSMS_Service.asmx");
//                .getString("IP", "192.168.7.25:3030/HSMS_Service.asmx");
//                .getString("IP", "192.168.7.175/hsms/HSMS_Service.asmx");
        Log.e("登录url", URL);
        tongXun(context, handler, methodName, flag);
    }

    public int getMethod() {
        return -10;
    }

    /**
     * 以webservice获取数据
     *
     */
    @SuppressWarnings("rawtypes")
    private void tongXun(final Context context, final Handler handler,
                         final String methodName, final boolean flag) {
            Thread thread1 = new Thread() {
                @Override
                public void run() {
                     Log.e("thread","thread-ni");
                    webServiceThread(context, handler, methodName, flag);
                }

            };
            thread1.start();
    }

    /**
     * 设置webservice要传递的属性
     */
    public abstract void addProgerty(SoapObject rpc);
    /**
     * webservice 线程
     *
     * @param context
     * @param handler
     * @param methodName
     * @param flag
     *            true:get data that should be resolved ;false:send data
     * @param
     *            The class to be reflected
     */

    // GetGzltjResponse{GetGzltjResult=anyType{Djrc=53; Jsrc=3; Pysl=8;
    // Yhxx=anyType{Id=30; Yhdm=1000; Yhlx=2; Yhxm=管理员; }; Zsrc=7; Zxrc=0; }; }
    @SuppressWarnings("rawtypes")
    public void webServiceThread(final Context context, final Handler handler,
                                 String methodName, boolean flag ) {
        try {
            // System.out.println("rpc------");
            // 指定webservice的命名空间和调用的方法名
            Log.e("thrad","thread-hao");
            Object obj1 = null;
            SoapObject rpc = new SoapObject(NAMESPACE, methodName);
            addProgerty(rpc);
            MyHttptran ht = new MyHttptran(URL, 5000);

            ht.debug = true;
            // 生成调用Webservice方法的SOAP请求信息.该信息由SoapSerializationEnvelope对象描述
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);

            Log.e("thread", NAMESPACE + methodName);
            try {
                ht.call(NAMESPACE + methodName, envelope);
            }
            catch (ConnectException ex)
            {
                Log.e("thread 超时未连接",  ex.getMessage());
            }

            if (envelope.bodyIn.toString().startsWith("SoapFault")) {
                SoapFault sf = (SoapFault) (envelope.bodyIn);
                Log.e("fault", sf.faultstring);
                if (handler != null) {
                    Message message = Message.obtain(handler);
                    // message.obj = year;
                    message.what = 3;
                    message.obj = sf.faultstring;
                    message.sendToTarget();
                }
                return;
            }
            // 得到返回结果
            Log.e("bodyIn", envelope.bodyIn.toString());
            SoapObject result = (SoapObject) envelope.bodyIn;
            if (result.toString().equals(methodName + "Response{}")) {
                // pd = false;
                if (null != handler) {
                    // send message to the main thread
                    Message message = Message.obtain(handler);
                    // message.obj = year;
                    message.what = 1;
                    message.arg1 = getMethod();
                    message.sendToTarget();
                }
                return;
            }
//            if (parse == null) {
//                if (null != handler) {
//                    Message message = Message.obtain(handler);
//                    message.obj = result.getProperty(methodName + "Result")
//                            .toString();
//                    message.what = 0;
//                    message.arg1 = getMethod();
//                    message.sendToTarget();
//                    return;
//                }
//            }
            if (result.getProperty(methodName + "Result").toString()
                    .equals("true")) {
                if (null != handler) {
                    Message message = Message.obtain(handler);
                    message.obj = result.getProperty(methodName + "Result")
                            .toString();
                    message.what = 0;
                    message.arg1 = getMethod();
                    message.sendToTarget();
                }
                return;
            }
            SoapObject detail = (SoapObject) result.getProperty(methodName
                    + "Result");
            if (detail.toString().equals("anyType{}")) {
                if (null != handler) {
                    Message message = Message.obtain(handler);
                    message.what = 2;
                    message.arg1 = getMethod();
                    message.sendToTarget();
                }
                return;
            } else {
                String xm =detail.getProperty("ReturnCode").toString();
                String zhuangtai =detail.getProperty("ResturnMsg").toString();
                String dizhi =detail.getProperty("ReturnID").toString();
                String vc =detail.getProperty("ReturnJson").toString();
                Hsms hs = new Hsms();
                hs.setReturnCode(Integer.parseInt(xm));
                hs.setResturnMsg(zhuangtai);
                hs.setReturnID(dizhi);
                hs.setReturnJson(vc);
                //obj1 = parse.doing(detail, cla);
                // 发送消息回主线程
                if (null != handler) {
                    Message message = Message.obtain(handler);
                    message.obj = hs;
                    message.what = 0;
                    message.arg1 = getMethod();
                    message.sendToTarget();
                    return;
                }
            }
        } catch (ConnectException e) {
            e.printStackTrace();
            if (null != handler) {
                handler.sendEmptyMessage(-1);
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            if (null != handler) {
                handler.sendEmptyMessage(-1);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            if (null != handler)
                handler.sendEmptyMessage(-1);
        } catch (Exception e) {
            e.printStackTrace();
            if (null != handler) {
                Message message = Message.obtain(handler);
                message.obj = e.getMessage();
                message.what = -2;
                message.sendToTarget();
            }
        }
    }
}
