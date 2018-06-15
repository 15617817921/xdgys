package com.example.chen.hsms.application;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.WindowManager;

import com.example.chen.hsms.bean.data.BaseData;
import com.example.chen.hsms.bean.data.GoodClass;
import com.example.chen.hsms.bean.data.IdName;
import com.example.chen.hsms.bean.data.SystemPar;
import com.example.chen.hsms.bean.data.UserMsg;
import com.example.chen.hsms.bean.data.XiaoDuGuo;
import com.example.chen.hsms.bean.data.KeShiName;
import com.example.chen.hsms.receiver.NetStateReceiver;
import com.example.chen.hsms.utils.ConstantsUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;


/**
 * Created by youxi on 2016-9-12.
 */
public class MyApplication extends Application {
    protected static final String SEPARATE = "¤";
    private static MyApplication singleton;
    public static  int i=0;
    //登录信息 用户信息
   UserMsg userMsg;

    public UserMsg getUserMsg() {
        return userMsg;
    }

    public void setUserMsg(UserMsg userMsg) {
        this.userMsg = userMsg;
    }

    //缓存物品分类
    List<GoodClass> list_gc=new ArrayList<>();
    //缓存人名id
    List<IdName> list_idname=new ArrayList<>();

    public List<BaseData> getList_bd() {
        return list_bd;
    }

    //缓存基础数据
    List<BaseData> list_bd=new ArrayList<>();
    // 缓存系统参数
    List<SystemPar> list_sp=new ArrayList<>();
    //缓存灭菌相关
    List<XiaoDuGuo> list_xdg=new ArrayList<>();
    //科室
    List<KeShiName> list_keshi=new ArrayList<>();



    public static Context mContext;

    public List<IdName> getList_idname() {
        return list_idname;
    }

    public void setList_idname(List<IdName> list_idname) {
        this.list_idname = list_idname;
    }

    public List<KeShiName> getList_keshi() {
        return list_keshi;
    }

    public void setList_keshi(List<KeShiName> list_keshi) {
        this.list_keshi = list_keshi;
    }

    public void setList_gc(List<GoodClass> list_gc) {
        this.list_gc = list_gc;
    }


    public void setList_bd(List<BaseData> list_bd) {
        this.list_bd = list_bd;
    }

    public List<SystemPar> getList_sp() {
        return list_sp;
    }

    public void setList_sp(List<SystemPar> list_sp) {
        this.list_sp = list_sp;
    }

    public List<XiaoDuGuo> getList_xdg() {
        return list_xdg;
    }

    public void setList_xdg(List<XiaoDuGuo> list_xdg) {
        this.list_xdg = list_xdg;
    }


    /**
     *  XML解析标记
     */
    public static final int END = 1;
    public static final int NODE = 2;
    public static final int TUPIAN = 3;
    //网络监听广播
    private NetStateReceiver mNetWorkReceiver;
    //表示是否连接
    public static boolean isConnected;
    //表示网络类型（移动数据或者wifi）移动：Moblie Wifi:Wifi
    public static String netWorkState;

    @Override
    public void onCreate() {
        super.onCreate();
//        initJpush();
        singleton=this;
        mContext = getApplicationContext();
        init();
//        refresh();
    }

    private void init() {
        /**
         * 必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回
         * 第一个参数：应用程序上下文
         * 第二个参数：如果发现滑动返回后立即触摸界面时应用崩溃，请把该界面里比较特殊的 View 的 class 添加到该集合中，目前在库中已经添加了 WebView 和 SurfaceView
         */
        BGASwipeBackHelper.init(this, null);
        netWorkReceiver();
    }


    private void refresh() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                //指定为经典Header，默认是 贝塞尔雷达Header
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
//        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
//            @NonNull
//            @Override
//            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
//                BezierRadarHeader header = new BezierRadarHeader(context);
//                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
//                return header;
//            }
//        });
    }

    public static MyApplication getInstance(){
        return singleton;
    }

    private WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getWindowParams() {
        return windowParams;
    }
    /**
     * 网络状态广播注册
     * @author hjy
     * created at 2016/12/12 15:30
     */
    private void netWorkReceiver()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        mNetWorkReceiver = new NetStateReceiver(netWorkHandler);
        registerReceiver(mNetWorkReceiver,filter);
    }

    //网络监听Handler
    private Handler netWorkHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case ConstantsUtil.NetWork.IS_WIFI:
                    netWorkState = ConstantsUtil.NetWork.WIFI;
                    isConnected = true;
                    break;
                case ConstantsUtil.NetWork.IS_MOBILE:
                    netWorkState = ConstantsUtil.NetWork.MOBLIE;
                    isConnected = true;
                    break;
                case ConstantsUtil.NetWork.NO_CONNECTION:
                    isConnected = false;
                    break;
                default:break;
            }
        }
    };
    public String getKeShiName(String id){
        String name="";
        try {
            if(list_keshi.size()>0){
                KeShiName bean =null;
                for (int j = 0; j < list_keshi.size(); j++) {
                    bean = list_keshi.get(j);
                    if(id.equals(bean.getID())){
                        name=bean.getName();
                    }
                }
            }
        }catch (Exception e){
            Log.e("app",e.getMessage());
        }
        return name;
    }

}
