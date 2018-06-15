package com.example.chen.hsms.webserviceutils;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

import com.example.chen.hsms.application.MyApplication;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.MyLogger;
import com.example.chen.hsms.utils.NetUtils;
import com.example.chen.hsms.utils.SaveUtils;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.ws.Hsms;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.co.senab.photoview.log.Logger;

/**
 * Created by Administrator on 2018/3/1.
 */

public class WebServiceUtils {
    private static final String TAG = "WebServiceUtils";
    // 含有3个线程的线程池
//    private static final ExecutorService executorService = Executors
//            .newFixedThreadPool(3);
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    // 命名空间
    private static final String NAMESPACE = Constant.NAMESPACE;
    //    方法名
    private static final String METHODNAME = Constant.METHODNAME;

    private static final int TIMEOUT = 10000;//超时时间
    private static Hsms hs;
    private static MyLogger log = MyLogger.kLog();


    /**
     * json格式结果
     * <p>
     * //     * @param      WebService的调用方法名
     *
     * @param properties         WebService的参数
     * @param webServiceCallBack 回调接口
     */
    public static void callWebService(final ProgressDialog dialog, boolean isNet, HashMap<String, Object> properties, final WebServiceCallBack webServiceCallBack) {
        if (!isNet) {
            dismissLoading(dialog);
            ToastUtils.showLongSafe(Constant.NONET);
            return;
        }
        if (!SaveUtils.contains(MyApplication.mContext, "url")) {
            ToastUtils.showLongSafe(Constant.IP);
            return;
        }

        String url = (String) SaveUtils.get(MyApplication.mContext, "url", "");
        // 创建HttpTransportSE对象， url timeOut
        final MyAndroidHttpTransport httpTransportSE = new MyAndroidHttpTransport(url, TIMEOUT);

//        final AndroidHttpTransport httpTransportSE = new AndroidHttpTransport(url);原生
        // 创建SoapObject对象
        SoapObject soapObject = new SoapObject(NAMESPACE, METHODNAME);
        // SoapObject添加参数
        if (properties != null) {
            for (Iterator<Map.Entry<String, Object>> it = properties.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Object> entry = it.next();
                soapObject.addProperty(entry.getKey(), entry.getValue());
            }
        }
        // 实例化SoapSerializationEnvelope，传入WebService的SOAP协议的版本号
        final SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        // 设置是否调用的是.Net开发的WebService
        soapEnvelope.setOutputSoapObject(soapObject);
        soapEnvelope.dotNet = true;
        httpTransportSE.debug = true;

        // 用于子线程与主线程通信的Handler
        // 将返回值回调到callBack的参数中
        final Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // 将返回值回调到callBack的参数中
                if (msg.what == 0) {
                    webServiceCallBack.callBack((Hsms) msg.obj);
                } else if (msg.what == -1) {
                    dismissLoading(dialog);
                    ToastUtils.showShortSafe("连接不到主机");
                    log.e("ConnectException");
                } else if (msg.what == -2) {
                    dismissLoading(dialog);
//                    数据库未响应  或者网络网端不正确
                    ToastUtils.showShortSafe("数据库连接失败");
                    log.e("IOException");
                } else if (msg.what == -3) {
                    dismissLoading(dialog);
                    log.e("XmlPullParserException");
                } else if (msg.what == -4) {
                    dismissLoading(dialog);
                    ToastUtils.showShortSafe("请求超时，请查看链接地址是否正确");
                    log.e("failed to connect to");
                }
            }
        };
        final boolean[] exit = {true};
        // 开启线程去访问WebService
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while (exit[0]) {
                    try {
                        log.e("开始执行了");
                        httpTransportSE.call(NAMESPACE + METHODNAME, soapEnvelope);
                        if (soapEnvelope.getResponse() != null) {
                            // 获取服务器响应返回的SoapObject
                            SoapObject resultSoapObject = (SoapObject) soapEnvelope.bodyIn;
                            SoapObject detail = (SoapObject) resultSoapObject.getProperty(METHODNAME
                                    + "Result");
                            if (!detail.toString().equals("anyType{}")) {
                                String xm = detail.getProperty("ReturnCode").toString();
                                String zhuangtai = detail.getProperty("ResturnMsg").toString();
                                String dizhi = detail.getProperty("ReturnID").toString();
                                String vc = detail.getProperty("ReturnJson").toString();
                                hs = new Hsms();
                                hs.setReturnCode(Integer.parseInt(xm));
                                hs.setResturnMsg(zhuangtai);
                                hs.setReturnID(dizhi);
                                hs.setReturnJson(vc);
//                            resultJson = soapEnvelope.getResponse().toString();
//                            Log.e(TAG, METHODNAME + " -- webServices返回结果：" + resultJson);
                            }
                        } else {
                            log.e(soapEnvelope.getResponse().toString() + "空");
                        }
                    } catch (ConnectException e) {
                        exit[0] = false;
                        log.e(e.getMessage());
                        e.printStackTrace();
                        hs = null;
                        if (null != mHandler) {
                            if(e.getMessage().contains("No route to host")){
                                mHandler.sendEmptyMessage(-1);
                            }
                        }
                    } catch (IOException e) {
                        exit[0] = false;
                        log.e(e.getMessage());

                        e.printStackTrace();
                        hs = null;
                        if (null != mHandler) {
                            if (e.getMessage().contains("failed to connect to")) {
                                mHandler.sendEmptyMessage(-4);
                            }
//                            mHandler.sendEmptyMessage(-2);
                        }
                    } catch (XmlPullParserException e) {
                        exit[0] = false;
                        log.e(e.getMessage());
                        e.printStackTrace();
                        hs = null;
                        if (null != mHandler) {
                            mHandler.sendEmptyMessage(-3);
                        }
                    } finally {
                        // 将获取的消息利用Handler发送到主线程
                        mHandler.sendMessage(mHandler.obtainMessage(0, hs));
                        exit[0] = false;
                    }
                }
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    dialog.dismiss();
                    // 取消查询，将回调取消
//                    mHandler.sendEmptyMessage(-4);//发送信号中断操作
                    exit[0] = false;
                    log.e("回调取消");
                }
                return false;
            }
        });
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                log.e("超市");
//                if(dialog.isShowing()){
//                    dialog.dismiss();
//                    exit[0] =false;
//                    ToastUtils.showShortSafe(Constant.CHAOSHI);
//                }
//            }
//        },TIMEOUT);
    }

    private static void dismissLoading(ProgressDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    /**
     * 返回的xml 格式的接口
     * @param methodName
     * @param soaps
     * @param soapCalback
     */
//    public static void callWebServiceSoap(final String methodName,
//                                          List<Soap> soaps,
//                                          final WebServiceCallBackSoap soapCalback) {
//
//        String http = (String) SPUtils.get(BaseApplication.context, Common.KEY_HTTP, URLTool.HTTP);
//        String ip = (String) SPUtils.get(BaseApplication.context, Common.KEY_IP, URLTool.IP);
//        String port = (String) SPUtils.get(BaseApplication.context, Common.KEY_PORT, URLTool.PORT);
//        //服务器地址
//        String baseURL = http + ip + ":" + port + URLTool.WEB_SERVICE;
//
//        // 创建HttpTransportSE对象，传递WebService服务器地址  url timeOut
////        final HttpTransportSE httpTransportSE = new HttpTransportSE(URLTool.BASE_URL,5000);
//        final AndroidHttpTransport httpTransportSE = new AndroidHttpTransport(Constants.URL, TIMEOUT);
//        // 创建SoapObject对象
//        SoapObject soapObject = new SoapObject(NAMESPACE, methodName);
//        LogUtils.e(TAG, "NAME_SPACE:" + soaps.toString() + NAMESPACE + "\nURL:" + baseURL + "" + "\nparams:" + soaps.toString() + "\nwebServices请求开始\nmethodName: " + methodName);
//
//        // SoapObject添加参数
//        for (Soap soap : soaps) {
//            soapObject.addProperty(soap.getName(), soap.getValue());
//        }
//
//        // 实例化SoapSerializationEnvelope，传入WebService的SOAP协议的版本号
//        final SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(
//                SoapEnvelope.VER11);
//        // 设置是否调用的是.Net开发的WebService
//        soapEnvelope.setOutputSoapObject(soapObject);
//        soapEnvelope.dotNet = true;
//        httpTransportSE.debug = true;
//
//        // 用于子线程与主线程通信的Handler
//        final Handler mHandler = new Handler() {
//
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                // 将返回值回调到callBack的参数中
//                soapCalback.callBackSoap((SoapObject) msg.obj);
//            }
//
//        };
//
//        // 开启线程去访问WebService
//        executorService.submit(new Runnable() {
//
//            @Override
//            public void run() {
//                SoapObject resultSoapObject = null;
//                try {
//                    httpTransportSE.call(NAMESPACE + methodName, soapEnvelope);
//                    if (soapEnvelope.getResponse() != null) {
//                        // 获取服务器响应返回的SoapObject
//                        resultSoapObject = (SoapObject) soapEnvelope.getResponse();
//                        LogUtils.e(methodName+" -- webServices返回结果："+resultSoapObject.toString());
//                    }
//                } catch (HttpResponseException e) {
//                    e.printStackTrace();
//                    LogUtils.e(TAG, e.getMessage());
//                    resultSoapObject = null;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    LogUtils.e(TAG, e.getMessage());
//                    resultSoapObject = null;
//                } catch (XmlPullParserException e) {
//                    e.printStackTrace();
//                    LogUtils.e(TAG, e.getMessage());
//                    resultSoapObject = null;
//                } finally {
//                    // 将获取的消息利用Handler发送到主线程
//                    mHandler.sendMessage(mHandler.obtainMessage(0, resultSoapObject));
//                }
//            }
//        });
//    }


    /**
     * @author xiaanming
     */
    public interface WebServiceCallBack {
        void callBack(Hsms result);
    }

    /**
     * xml格式结果接口
     */
    public interface WebServiceCallBackSoap {
        public void callBackSoap(SoapObject soapObject);
    }
}

