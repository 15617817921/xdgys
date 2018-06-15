package com.example.chen.hsms.activity.home_down;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.chen.hsms.R;
import com.example.chen.hsms.activity.home_ohter.JiuCuoAct;
import com.example.chen.hsms.activity.home_ohter.WuPinBaoActivity3;
import com.example.chen.hsms.adapter.QingDAdapter;
import com.example.chen.hsms.adapter.QingDSciAdapter;
import com.example.chen.hsms.application.MyApplication;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.data.UserMsg;
import com.example.chen.hsms.bean.data.Hsd_Mx;
import com.example.chen.hsms.bean.data.QXBean;
import com.example.chen.hsms.bean.local.QXBeanWP;
import com.example.chen.hsms.bean.data.QingDian;
import com.example.chen.hsms.bean.data.QingXiJi;
import com.example.chen.hsms.bean.data.QingXiJiLu;
import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.DateUtil;
import com.example.chen.hsms.utils.NetUtils;
import com.example.chen.hsms.utils.StringUtils;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.view.CoustomListView;
import com.example.chen.hsms.view.MyTextView;
import com.example.chen.hsms.view.RecyclerViewDivider;
import com.example.chen.hsms.view.ShowDialog;
import com.example.chen.hsms.webserviceutils.WebServiceUtils;
import com.example.chen.hsms.ws.Hsms;
import com.example.chen.hsms.ws.WSOpraTypeCode;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 清点  加入第一次制包操作
 */
public class QingDianActivity extends BaseActivity {

    @BindView(R.id.tvQqingdianNum)
    TextView tvQqingdianNum;
    @BindView(R.id.lv_weishenhe)
    RecyclerView lvWeishenhe;
    @BindView(R.id.lv_shenhe)
    RecyclerView lvShenhe;
    @BindView(R.id.ll_data)
    LinearLayout llData;
    @BindView(R.id.ll_wudata)
    LinearLayout llWudata;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_serach)
    TextView tvSerach;
    @BindView(R.id.tv_qingdian_qingkong)
    TextView tvQingdianQingkong;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.simpleMarqueeView)
    MyTextView simpleMarqueeView;
//    @BindView(R.id.marqueeView)
//    MarqueeView marqueeView;


    //    private QXBean bean;
    private DateUtil dateUtil;
    private QingDAdapter adapter_yes;
    private MyApplication app;
    private String huiShouDID;//提交审核状态  单个回收单
    private String tiaoma;//物品包条码
    private UserMsg userMsg;
    private String tagClear = "";//扫描架后为1，再次扫描篮子以前数据清空
    private String ji = "";//全局清洗机
    private Intent intent;

    private List<QXBean> list_zzjl = new ArrayList<>();//追踪记录集合---往里面添加字段

    private List<QingXiJi> list_qingxiji = new ArrayList<>();//扫描清洗机   单个获取


    private List<Hsd_Mx> list_hsd_mx = new ArrayList<>();//获取回收单对应明细
    private List<QingDian> list_huisd = new ArrayList<>();//全部回收单
    private int num = 0;//判断完成了几个线程

    private String lan = "";//全局清洗篮
    private String jia = "";//全局清洗架


    private Map<String, List<QingXiJiLu>> map_lan_qxjl = new HashMap<>();//保存每个篮子及对应清洗记录
    private Map<String, List<QingXiJiLu>> map_jia_qxjl = new HashMap<>();//保存每个架及对应清洗记录


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //绑定事件接受
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        map_lan_qxjl.clear();//挂起是清空已扫描过的篮子
        map_jia_qxjl.clear();//架
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        log.e("onResume");
        registerBoradcastReceiver();
        getAllWuPinBao();//获取所有物品包
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销事件接受
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMessageEvent(Hsd_Mx b) {
        log.e("=====" + b.getIsScan());
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_qingdian;
    }

    @Override
    protected void initView() {
        app = MyApplication.getInstance();
        dateUtil = DateUtil.getInstance();
        userMsg = app.getUserMsg();
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.state), 0);

        tvTitle.setText(getResources().getText(R.string.qingdian));
        tvRight.setText(getResources().getText(R.string.updata));
        simpleMarqueeView.setScrollMode(MyTextView.SCROLL_FAST);
    }

    @Override
    public void onStart() {
        super.onStart();
//        marqueeView.startFlipping();
    }

    @Override
    public void onStop() {
        super.onStop();
//        marqueeView.stopFlipping();
    }

    @Override
    public void initDatas() {
        dialog.show();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                showHuiShouD();//回收单
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_qingdian_qingkong, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_qingdian_qingkong: //清空绑定的清洗架
                qingkong();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right: //纠错物品包的old追踪记录
                gotoAtivity(JiuCuoAct.class, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void initListeners() {
    }

    /*             进入页面开始获取回收单和对应明细---     start               */

    /**
     * 获取回收单对应明细
     */
    private void getAllWuPinBao() {
        HashMap<String, Object> params = new HashMap<>();
        String s = "string¤ShenQingSJ>='" + dateUtil.getDate() + " 00:00:00'";
//        String s = "string¤ShenQingSJ>='" + "2018-3-27 00:00:00'";
        params.put(Constant.CODE, WSOpraTypeCode.获取回收单对应明细);
        params.put(Constant.PARAMETER, s);
        log.e("获取回收单对应明细--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                isCached();
                if (hsms != null) {
                    log.e("获取回收单对应明细--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        list_hsd_mx = JSON.parseArray(result, Hsd_Mx.class);
                        setAdapter(list_hsd_mx);
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取回收单对应明细);
                    }
                } else {
                    ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取回收单对应明细);
                    log.e(Constant.HSMS);
                }
            }
        });
    }


    /**
     * 获取回收单
     */
    private void showHuiShouD() {
        HashMap<String, Object> params = new HashMap<>();
        String s = "string¤ShenQingSJ>='" + dateUtil.getDate() + " 00:00:00'";
//        String s = "string¤ShenQingSJ>='" + "2018-3-27 00:00:00'";
        params.put(Constant.CODE, WSOpraTypeCode.获取回收单2);
        params.put(Constant.PARAMETER, s);
        log.e("获取回收单2--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                isCached();
                if (hsms != null) {
                    log.e("获取回收单2--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        list_huisd = JSON.parseArray(result, QingDian.class);
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取回收单2);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.获取回收单2);
                    log.e(Constant.HSMS);
                }
            }
        });
    }


    /**
     * 判断两个线程是否完成
     */
    private void isCached() {
        if (num == 1) {
            dismissLoading();
        } else {
            num++;
        }
    }
    /*             进入页面开始获取回收单和对应明细---     end               */


    /*                清空清洗架     start                   */

    /**
     * 撤销刚刚绑定清洗架错误的清洗蓝，可以重新绑定清洗架。
     */
    public void qingkong() {
        if (map_jia_qxjl.size() == 0 || map_jia_qxjl == null) {
            ToastUtils.showLong("请扫描要清空的清洗架");
        } else {
            dialog.show();
            clearJia(jia);
        }
    }

    /**
     * 清空清洗架
     *
     * @param qxj
     */
    private void clearJia( String qxj) {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + qxj;
        params.put(Constant.CODE, WSOpraTypeCode.清空清洗架);
        params.put(Constant.PARAMETER, s);
        log.e("清空清洗架--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("清空清洗架--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        map_jia_qxjl.clear();
                        map_lan_qxjl.clear();
                        mSVProgressHUD.showSuccessWithStatus(jia + "号清洗架成功清空");
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.清空清洗架);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.清空清洗架);
                    log.e(Constant.HSMS);
                }
            }
        });
    }
    /*                清空清洗架  end                        */



   /*                mobi扫描头驱动  start                     */

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constant.ACTION_BARCODE_READER_VALUE);
        registerReceiver(mReceiver, myIntentFilter);
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.ACTION_BARCODE_READER_VALUE)) {
                String data = intent.getStringExtra(Constant.BORCODE_VALUE);
                scanChuLi(data);
            } else {
                log.e(action);
            }
        }
    };


    @Override
    public void onBackPressed() {
        log.e("onBackPressed");
        finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }
   /*                mobi扫描头驱动  end                     */

    /**
     * 扫描处理
     *
     * @param data
     */
    private void scanChuLi(String data) {
        String head = getHead(data);
        if (head.equals("")) {
            ToastUtils.showLong(Constant.TAGTOU);
            return;
        }

        if (head.equals(Constant.PTB)) {  //物品包条码
            String[] split = data.split("@");
            tvSerach.setText(split[1]);
            tiaoma = split[1];
            saoma(tiaoma);
        } else if (head.equals(Constant.QXL)) { //篮
            String[] split = data.split("@");

            if (tagClear.equals("1")) {   //所有数据清空
                tagClear = "";
            }

            lan = split[1].substring(0, 3);
            if (map_lan_qxjl.containsKey(lan)) {
                ToastUtils.showLong("不要重复扫描清洗蓝");
            } else {
                qingXiLan(lan);
            }
            return;
        } else if (head.equals(Constant.QXJ)) {  //架     2
            String[] split = data.split("@");
            tagClear = "1";
            jia = split[1];
            dialog.show();
            searchJia(jia);// 1 判断架子是否为空
        } else if (head.equals(Constant.QXG)) {  //清洗机
            String[] split = data.split("@");
            ji = split[1];
            qingXiJi(ji);
        } else {
            ToastUtils.showLong(Constant.QINGDIAN);
        }
    }

    /**
     * 查询清洗机里是否有记录
     *
     * @param qxji
     */
    private void wantEndQxji(String qxji) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + qxji;
        params.put(Constant.CODE, WSOpraTypeCode.通过清洗机ID查看清洗记录);
        params.put(Constant.PARAMETER, s);
        log.e("通过清洗机ID查看清洗记录--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("获取清洗机--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<QingXiJiLu> list_qxjl_jia = JSON.parseArray(result, QingXiJiLu.class);
                        map_jia_qxjl.clear();
                        map_jia_qxjl.put("", list_qxjl_jia);
                        qingxiBefrore();//直接机结束
                    } else {
                        dismissLoading();
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong("清洗锅" + ji + "没有任何记录"+ WSOpraTypeCode.通过清洗机ID查看清洗记录);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.通过清洗机ID查看清洗记录);
                    log.e(Constant.HSMS);
                }
            }
        });

    }




    /*               扫码       end              */




   /*                清洗篮扫描  start                     */

    /**
     * 开始扫描清洗篮==判断是否有物品
     *
     * @param qxl
     */
    private void qingXiLan(final String qxl) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + qxl + "|" + "";
        params.put(Constant.CODE, WSOpraTypeCode.获取清洗篮中物品包);
        params.put(Constant.PARAMETER, s);
        log.e("获取清洗篮中物品包--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("获取清洗篮中物品包--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<QingXiJiLu> list_chalan = JSON.parseArray(result, QingXiJiLu.class);
                        log.e("当前篮子的记录--" + list_chalan.size());
                        if (list_chalan.get(0).getID().equals("")) {
                            ToastUtils.showLong(qxl + "号清洗篮中空空如也");
                            return;
                        }
                        ToastUtils.showLong(qxl + "号篮扫描成功");
                        map_lan_qxjl.put(qxl, list_chalan);//保存每个篮子及对应清洗记录
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取清洗篮中物品包);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.获取清洗篮中物品包);
                    log.e(Constant.HSMS);
                }
            }
        });
    }

  /*                清洗篮扫描  end                        */


    /*             清洗架扫描---     start               */
    private int jia_state = 0;//判断是否能开始扫描清洗机

    /**
     * 扫描清洗架获取清洗篮是否为空 --获取物品包记录
     *
     * @param qingxijia
     */
    private void searchJia( String qingxijia) {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + qingxijia + "|" + "";
        params.put(Constant.CODE, WSOpraTypeCode.获取清洗篮中物品包);
        params.put(Constant.PARAMETER, s);
        log.e("获取清洗篮中物品包--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("获取清洗篮中物品包--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<QingXiJiLu> list_qxjl_jia = JSON.parseArray(result, QingXiJiLu.class);
                        log.e("清洗架上的清洗记录数量--" + list_qxjl_jia.size());
                        if (jia_state == 1) {
                            dismissLoading();
                            if (list_qxjl_jia.get(0).getID().equals("")) {

                            }

                            map_jia_qxjl.clear();
                            map_jia_qxjl.put(jia, list_qxjl_jia);// 清空之前储存，后储存清洗架上的清洗记录
                            jia_state = 0;
                            mSVProgressHUD.showSuccessWithStatus("绑定" + jia + "号清洗架成功，可以扫描清洗机了");
                        } else {
                            if (list_qxjl_jia.get(0).getID().equals("")) {   //清洗架为空  --判断是否扫过篮
                                if (map_lan_qxjl.size() > 0) {  //已扫描篮子
                                    lanBindJia(jia);
                                } else {
                                    dismissLoading();
                                    ToastUtils.showLong("清洗架是空的,先去扫描篮子吧");
                                }
                            } else {                                       //清洗架有篮子   --  2 是否接下来扫描清洗机
                                if (map_lan_qxjl.size() > 0) {  //已扫描篮子--继续绑定
                                    lanBindJia(jia);
                                } else {
                                    dismissLoading();
                                    map_jia_qxjl.clear();
                                    map_jia_qxjl.put(jia, list_qxjl_jia);// 清空之前储存，后储存清洗架上的清洗记录
                                    mSVProgressHUD.showSuccessWithStatus("绑定" + jia + "号清洗架成功，可以扫描清洗机了");
                                }
                            }
                        }
                    } else {
                        dismissLoading();
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取清洗篮中物品包);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.获取清洗篮中物品包);
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    /**
     * 篮子绑定架
     *
     * @param qxj
     */
    private void lanBindJia(String qxj) {
        HashMap<String, Object> params = new HashMap<>();
        List<QingXiJiLu> list_qxjia = new ArrayList<>();
        Set<String> get = map_lan_qxjl.keySet();
        for (String test : get) {
            List<QingXiJiLu> list_qxjl = map_lan_qxjl.get(test);
            QingXiJiLu jiLu = null;
            for (int i = 0; i < list_qxjl.size(); i++) {
                jiLu = list_qxjl.get(i);
                jiLu.setQingXiJia(qxj);
                list_qxjia.add(jiLu);
            }
        }
        log.e("所有清洗篮的清洗记录==" + list_qxjia.size());
        String qingxijilu = JSON.toJSONString(list_qxjia, SerializerFeature.WriteNullStringAsEmpty);
        String s = "DataTable¤" + qingxijilu;
        params.put(Constant.CODE, WSOpraTypeCode.清洗篮绑定清洗架);
        params.put(Constant.PARAMETER, s);
        log.e("清洗篮绑定清洗架--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("清洗篮绑定清洗架--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        //绑定之后 清空之前扫描的篮子  再重新获取一下清洗架上的物品
                        jia_state = 1;
                        map_lan_qxjl.clear();
                        searchJia(jia);
                    } else {
                        dismissLoading();
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.清洗篮绑定清洗架);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.清洗篮绑定清洗架);
                    log.e(Constant.HSMS);
                }
            }
        });
    }
  /*             清洗架扫描---     end               */


   /*                清洗机扫描  start                     */

    /**
     * 扫描清洗机
     *
     * @param id
     */
    private void qingXiJi( String id) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = "string¤" + id;
        params.put(Constant.CODE, WSOpraTypeCode.获取清洗机);
        params.put(Constant.PARAMETER, s);
        log.e("获取清洗机--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("获取清洗机--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        list_qingxiji = JSON.parseArray(result, QingXiJi.class);
                        if (list_qingxiji.get(0).getQingXiZT().equals("1")) {//判断是否在清洗中 1为清洗中，8为清洗结束
                            dismissLoading();
                            dialogRestartQingxi();
                        } else {     //空闲中
                            if (map_jia_qxjl.size() > 0) {
                                qingxiBefrore();//开始准备清洗
                            } else {
                                dismissLoading();
                                ToastUtils.showLong("请先扫描清洗架");
                            }
                        }
                    } else {
                        dismissLoading();
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取清洗机);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.获取清洗机);
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    private List<QingXiJiLu> list_qxjl_jia = new ArrayList<>();//洗架上的清晰记录集合

    /**
     * 1，查处清洗记录里的条码集合
     * 2，查出清洗架上的清晰记录集合
     */
    private void qingxiBefrore() {
        String listma = "";
        Set<String> get = map_jia_qxjl.keySet();
        for (String test : get) {
            List<QingXiJiLu> list_qxjl = map_jia_qxjl.get(test);
            QingXiJiLu jiLu = null;
            for (int i = 0; i < list_qxjl.size(); i++) {
                jiLu = list_qxjl.get(i);
                list_qxjl_jia.add(jiLu);
                listma += jiLu.getWupinBzzjlID() + ",";
            }
        }
        String s = StringUtils.stringSubEnds(listma);
        chaZZJL(s);
        log.e("批次条码" + s);
    }

    private int isEndTag = 0;//1代表直接扫机

    /**
     * 用于
     * （清洗中机器坏掉   重新开始灭菌） 是否要重新开始灭菌
     */
    private void dialogRestartQingxi() {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setContentView(R.layout.dialog_del);//重点看这获取弹出框内的视图view
        TextView content = (TextView) dialog.findViewById(R.id.content);//重点看这行的Dialog
        content.setText("机器当前处于清洗中，重新开始清洗还是结束清洗？");
        Button bt_restart = (Button) dialog.findViewById(R.id.bt_yes);//重点看这行的Dialog
        Button bt_end = (Button) dialog.findViewById(R.id.bt_no);//重点看这行的Dialog
        bt_restart.setText("重新开始");
        bt_end.setText("结束清洗");
        bt_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (map_jia_qxjl.size() > 0) {
                    qingxiBefrore();
//                    huoquQingXiJiZzjl(ji);
                } else {
                    ToastUtils.showLong("请先扫描清洗架");
                }

            }
        });
        bt_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                isEndTag = 1;
                wantEndQxji(ji);//确定结束清洗

            }
        });
    }


    //2 --查询物品追踪记录
    private void chaZZJL( String listma) {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + "where ID in(" + listma + ")";
        params.put(Constant.CODE, WSOpraTypeCode.获取物品包追踪记录);
        params.put(Constant.PARAMETER, s);
        log.e("获取物品包追踪记录--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("获取物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();

                        list_zzjl = JSON.parseArray(result, QXBean.class);
                        if (list_zzjl.get(0).getID().equals("")) {
                            dismissLoading();
                            ToastUtils.showLong("数据库查询不到相对应的数据");
                        } else {
                            if (isEndTag == 1) {  //1表示直接扫机结束
                                end();
                                isEndTag = 0;
                            } else {
                                startQingXi();    //架---机  开始清洗
                            }
                        }
                    } else {
                        dismissLoading();
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取物品包追踪记录);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.获取物品包追踪记录);
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    /**
     * 开始清洗  第一步  修改物品包追踪记录状态
     */
    private void startQingXi() {
        HashMap<String, Object> params = new HashMap<>();
        QingXiJi qingXiJi = list_qingxiji.get(0);//两个记录都要加字段
        QXBean qxBean = null;
        for (int i = 0; i < list_zzjl.size(); i++) {
            qxBean = list_zzjl.get(i);
            //添加物品包清洗机   1 为空直接添加   2不空 （1）正常流程 （2）1机坏--放2机洗
            int qxzt = qxBean.getQingXiZT();
            String qxj = StringUtils.stringNull(qxBean.getQingXiJi());
            if (qxj.equals("")) {  //ji=""
                qxBean.setQingXiJi(ji);
            } else {
                if (qxj.contains(",")) {  //ji="1,2"
                    String[] split = qxj.split(",");
                    if (!StringUtils.useList(split, ji)) {  //3
                        qxBean.setQingXiJi(split[0] + "," + ji);
                    }
                } else {      //ji="1"
                    if (qxj.equals(ji)) {   //1
                        qxBean.setQingXiJi(ji);
                    } else {              //2
                        qxBean.setQingXiJi(qxj + "," + ji);
                    }
                }
            }

            //如果是清洗消毒机，  消毒开始时间
            if (qingXiJi.getFlowremark() == 1) {
                qxBean.setXiaoDuZT(1);//消毒状态
                qxBean.setXiaoDuRen1(userMsg.getID());//消毒人id
                qxBean.setXiaoDuKSSJ(dateUtil.getYear_Day());//开始时间
            }
            //加入清洗类型
            qxBean.setFlowZT(qingXiJi.getFlowremark());

            if (StringUtils.stringNull(qxBean.getQingXiKSSJ()).equals("")) {
                qxBean.setQingXiKSSJ(dateUtil.getYear_Day());
            }
            qxBean.setQingXiZT(1);
            qxBean.setQingXiGC("0");
            qxBean.setQingXiXLH("0");
            qxBean.setQingXiRen1(userMsg.getID());


        }


        List<QingXiJiLu> list_json = new ArrayList<>();
        QingXiJiLu qxjl = null;//设置值
        for (int i = 0; i < list_qxjl_jia.size(); i++) {
            qxjl = list_qxjl_jia.get(i);

            qxjl.setQingxijiID(qingXiJi.getID());
            qxjl.setQingxiGuoMC(qingXiJi.getQingXiGuoMC());
            qxjl.setQingXiGC(qingXiJi.getDangQianGC());
            qxjl.setQingXiXLH(qingXiJi.getDangQianXLH());
            qxjl.setWupinBPiciID(dateUtil.getNian_miao());
            qxjl.setShiyongZhuantai(1 + "");

            if (qxjl.getQingXiKaishiSJ() == null || qxjl.getQingXiKaishiSJ().equals("")) {
                qxjl.setQingXiKaishiSJ(dateUtil.getYear_Day());//时间
            }

            qxjl.setQingXiRenID(userMsg.getID());//
            qxjl.setQingXiRen(userMsg.getYongHuXM());

            list_json.add(qxjl);
        }
        String json_zhuizong = JSON.toJSONString(list_zzjl, SerializerFeature.WriteNullStringAsEmpty);
        String json_qingxi = JSON.toJSONString(list_json, SerializerFeature.WriteNullStringAsEmpty);
        String s = "DataSet¤" + json_zhuizong + "§" + json_qingxi;
        params.put(Constant.CODE, WSOpraTypeCode.修改物品包清洗记录_同批次物品);
        params.put(Constant.PARAMETER, s);
        log.e("修改物品包清洗记录_同批次物品");
        log.e("追踪记录的数量--" + list_zzjl.size() + "  清洗架的清洗记录" + list_json.size() + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("获取物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        map_jia_qxjl.clear();
                        ToastUtils.showLong("成功开始清洗，请等待清洗结束");
                        startUpdata();
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.修改物品包清洗记录_同批次物品);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.修改物品包清洗记录_同批次物品);
                    log.e(Constant.HSMS);
                }
            }
        });
    }


    /**
     * 开始清洗  第一步  修改清洗机清洗状态
     */

    private void startUpdata() {
        HashMap<String, Object> params = new HashMap<>();
        QingXiJi qingXiJi = list_qingxiji.get(0);
        qingXiJi.setQingXiZT("1");
        String json = JSON.toJSONString(list_qingxiji, SerializerFeature.WriteNullStringAsEmpty);
        String s = "DataSet¤" + json;
        params.put(Constant.CODE, WSOpraTypeCode.添加清洗机);
        params.put(Constant.PARAMETER, s);
        log.e("添加清洗机--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("添加清洗机--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        log.e("更新清洗机状态成功");
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.添加清洗机);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.添加清洗机);
                    log.e(Constant.HSMS);
                }
            }
        });

    }
    // ---------清洗机扫描   结束----------


    //***************结束清洗    ****************

    /**
     * 结束 第一步 修改追踪记录状态
     */
    private void end() {
        HashMap<String, Object> params = new HashMap<>();
        QingXiJi qingXiJi = list_qingxiji.get(0);//两个记录都要加字段   所对应的开始清洗的清洗机
        QXBean qxBean = null;
        for (int i = 0; i < list_zzjl.size(); i++) {
            qxBean = list_zzjl.get(i);
            //如果是清洗消毒机，  消毒结束需要加
            if (qingXiJi.getFlowremark() == 1) {
                qxBean.setXiaoDuZT(8);//消毒状态
                qxBean.setXiaoDuJSSJ(dateUtil.getYear_Day());//结束时间
            }
            qxBean.setQingXiJSSJ(dateUtil.getYear_Day());
            qxBean.setQingXiZT(8);
            qxBean.setQingXiGC("0");
            qxBean.setQingXiXLH("0");
        }
        List<QingXiJiLu> list_json = new ArrayList<>();
        QingXiJiLu qxjl = null;//设置值
        for (int i = 0; i < list_qxjl_jia.size(); i++) {
            qxjl = list_qxjl_jia.get(i);

            qxjl.setQingXiGC(0 + "");
            qxjl.setQingXiXLH(0 + "");
            qxjl.setShiyongZhuantai(8 + "");
            qxjl.setQingXiJieShuSJ(dateUtil.getYear_Day());
            list_json.add(qxjl);
        }

        String json_zhuizong = JSON.toJSONString(list_zzjl, SerializerFeature.WriteNullStringAsEmpty);
        String json_qingxi = JSON.toJSONString(list_json, SerializerFeature.WriteNullStringAsEmpty);
        String s = "DataSet¤" + json_zhuizong + "§" + json_qingxi;

        params.put(Constant.CODE, WSOpraTypeCode.修改物品包清洗记录_同批次物品);
        params.put(Constant.PARAMETER, s);
        log.e("修改物品包清洗记录_同批次物品--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("修改物品包清洗记录_同批次物品--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        endTianJiaQingXiJi();
                    } else {
                        dismissLoading();
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.修改物品包清洗记录_同批次物品);
                    }
                } else {
                    dismissLoading();
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.修改物品包清洗记录_同批次物品);

                    log.e(Constant.HSMS);
                }
            }
        });
    }

    /**
     * 结束  第二步  修改清洗机状态
     */
    //结束修改清洗机状态
    private void endTianJiaQingXiJi() {
        HashMap<String, Object> params = new HashMap<>();
        QingXiJi qingXiJi = list_qingxiji.get(0);
        qingXiJi.setQingXiZT("8");
        String json = JSON.toJSONString(list_qingxiji, SerializerFeature.WriteNullStringAsEmpty);
        String s = Constant.DATASET + json;
        params.put(Constant.CODE, WSOpraTypeCode.添加清洗机);
        params.put(Constant.PARAMETER, s);
        log.e("添加清洗机--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("添加清洗机--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getResturnMsg();//清洗结束  返回
                        mSVProgressHUD.showSuccessWithStatus("已成功结束清洗");

                        list_zzjl.clear();//新条码追踪到的记录
                        list_qxjl_jia.clear();//所扫描的架的物品包
                        list_qingxiji.clear();//清洗机
                        map_jia_qxjl.clear();
                        map_lan_qxjl.clear();
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.添加清洗机);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.添加清洗机);
                    log.e(Constant.HSMS);
                }
            }
        });
    }
    //***************结束清洗  end  ****************

      /*               扫码       start              */

    /**
     * 扫码--物品包条码
     *
     * @param tiaoma
     */
    private void saoma( String tiaoma) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = "string¤" + "where ID='" + tiaoma + "'";
        params.put(Constant.CODE, WSOpraTypeCode.获取物品包追踪记录);
        params.put(Constant.PARAMETER, s);
        log.e("获取物品包追踪记录--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();//扫描之后跳转
                if (hsms != null) {
                    log.e("获取物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<QXBean> list_saoma = JSON.parseArray(result, QXBean.class);
                        QXBean bean = list_saoma.get(0);

                        if (bean.getID().equals("")) {
                            ToastUtils.showLong(Constant.NOTIAOMA);
                            return;
                        }
                        saoMaChuLi(bean);
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取物品包追踪记录);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.获取物品包追踪记录);
                    log.e(Constant.HSMS);
                }
            }
        });
    }


    private void saoMaChuLi(QXBean bean) {
        //对比科室，物品包id名称
        Hsd_Mx hsd_mx = null;
        for (int i = 0; i < list_hsd_mx.size(); i++) {
            hsd_mx = list_hsd_mx.get(i);
            //科室相同  物品包id相同
            log.e("科室id" + bean.getLingYongKS() + "==" + hsd_mx.getShenQingKSID() + "  物品包id" + bean.getWuPinBID() + "==" + hsd_mx.getWuPinBID());
            if (bean.getLingYongKS().equals(hsd_mx.getShenQingKSID()) && bean.getWuPinBID().equals(hsd_mx.getWuPinBID())) {
                huiShouDID = hsd_mx.getHuiShouDID();  // s780 2手术包  s780  2贵重包 .....
                //假如 3个科室  1 1个物品包   2   2相同物品包  3  3个物品包
                String zzjl = StringUtils.stringNull(hsd_mx.getLiShiZZJLID());                       //  "##"
                String newzzjl = StringUtils.stringNull(hsd_mx.getNewZZJLID());                       //  "1#2#3"
                int keShiSL = hsd_mx.getKeShiSL();
                if (zzjl.contains(tiaoma)) {    //重复扫描
                    ToastUtils.showLong("物品包已扫描");
                    shengChengTiaoMa(1, bean, tiaoma, hsd_mx);//生成新条码  再提交
                    log.e("物品包已扫描");
                    return;
                } else {
                    if (hsd_mx.getIsScan() != 1) {
                        if (keShiSL == 1) {
                            if (zzjl.equals("")) {
                                hsd_mx.setIsScan(1);
                                hsd_mx.setLiShiZZJLID(tiaoma);//添加旧条码
                                hsd_mx.setGYSSL(hsd_mx.getGYSSL() + 1);//扫码制包数量+1
                                hsd_mx.setZhiBaoSYL(hsd_mx.getZhiBaoSYL() + 1);
                                shengChengTiaoMa(0, bean, tiaoma, hsd_mx);//生成新条码  再提交
                                return;
                            }
                        } else if (keShiSL > 1) {
                            if (zzjl.equals("")) {          //第一个物品包满足
                                hsd_mx.setLiShiZZJLID(tiaoma);//添加旧条码
                                hsd_mx.setGYSSL(hsd_mx.getGYSSL() + 1);//扫码制包数量+1
                                hsd_mx.setZhiBaoSYL(hsd_mx.getZhiBaoSYL() + 1);

                                shengChengTiaoMa(0, bean, tiaoma, hsd_mx);//生成新条码  再提交
                                return;
                            } else if (!zzjl.contains(Constant.FUHAO) && !zzjl.equals("")) { //仅仅有一条数据｛1 是首次 0    2  正常 ｝
                                String allzzjl = zzjl + Constant.FUHAO + tiaoma;
                                String[] split = allzzjl.split("#");
                                if (split.length == keShiSL) {
                                    hsd_mx.setIsScan(1);
                                }
                                hsd_mx.setLiShiZZJLID(allzzjl);//添加旧条码
                                hsd_mx.setGYSSL(hsd_mx.getGYSSL() + 1);//扫码制包数量+1
                                hsd_mx.setZhiBaoSYL(hsd_mx.getZhiBaoSYL() + 1);

                                shengChengTiaoMa(0, bean, tiaoma, hsd_mx);//生成新条码  再提交
                                return;
                            } else if (zzjl.contains(Constant.FUHAO)) {   // //包含#  ｛1 含有首次 0#0  2 正常1#2#｝

                                hsd_mx.setGYSSL(hsd_mx.getGYSSL() + 1);//扫码制包数量+1
                                hsd_mx.setZhiBaoSYL(hsd_mx.getZhiBaoSYL() + 1);

                                String allzzjl = zzjl + Constant.FUHAO + tiaoma;
                                String[] split = allzzjl.split("#");
                                if (split.length == keShiSL) {
                                    hsd_mx.setIsScan(1);
                                }
                                hsd_mx.setLiShiZZJLID(allzzjl);//添加旧条码
                                shengChengTiaoMa(0, bean, tiaoma, hsd_mx);// 生成条码  再提交提交
                                return;
                            }
                        }
                    }//重复判断
                }//回收单是否审核掉 iscan
            }//根据科室和物品包id判断
        }//循环所有回收单明细
    }

    private void isCommit() {
        int wpbidTotalNum = 0;//所有相同回收单数量｛s780 2手术包  s780  2贵重包｝
        int wpbidNum = 0;    //满足的数量  s780 2手术包 已扫完
        for (int j = 0; j < list_hsd_mx.size(); j++) {
            Hsd_Mx hsd = null;
            if (list_hsd_mx.get(j).getHuiShouDID().equals(huiShouDID)) {
                wpbidTotalNum++;
                hsd = list_hsd_mx.get(j);
                if (hsd.getIsScan() == 1) {
                    wpbidNum++;
                }
            }
        }
        if (wpbidTotalNum == wpbidNum && wpbidNum != 0) {
            QingDian qingDian = null;
            for (int k = 0; k < list_huisd.size(); k++) {
                qingDian = list_huisd.get(k);
                if (qingDian.getHuiShouDID().equals(huiShouDID)) {
                    qingDian.setShenHeZT(1);
                    qingDian.setShenHeKS(userMsg.getKeShiMC());
                    qingDian.setShenHeKSID(userMsg.getKeShiID());
                    qingDian.setShenHeRen(userMsg.getYongHuXM());
                    qingDian.setShenHeRenID(userMsg.getID());
                    qingDian.setShenHeSJ(dateUtil.getYear_Day());

                    saveState(list_hsd_mx, list_huisd);
                    log.e("更新为审核状态1");
                    return;
                }
            }
        } else {
            saveState(list_hsd_mx, list_huisd);
        }
    }

    /**
     * 生成新条码
     *
     * @param tag  1已扫描 不用提交  0 需要提交
     * @param bean
     * @param ma
     * @param hsd
     */
    private void shengChengTiaoMa(final int tag, final QXBean bean, String ma, final Hsd_Mx hsd) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + ma + "|" + userMsg.getID() + "||" + hsd.getHuiShouDID();
        params.put(Constant.CODE, WSOpraTypeCode.制包单审核制包);
        params.put(Constant.PARAMETER, s);
        log.e("制包单审核制包-生成新条码" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("制包单审核制包-生成新条码--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<QXBean> list_saoma = JSON.parseArray(result, QXBean.class);
                        if (!list_saoma.get(0).getID().equals("")) {
                            String image = StringUtils.stringNull(list_saoma.get(0).getWupinBaoImage());
                            String newma = list_saoma.get(0).getID();
                            startIntent(bean, newma, image);
                            log.e(newma + "新条码--照片" + image);
                            if (tag != 1) {
                                String newzzjl = StringUtils.stringNull(hsd.getNewZZJLID());
                                log.e("--" + newzzjl);
                                if (newzzjl.equals("")) {
                                    hsd.setNewZZJLID(newma);
                                } else {
                                    String zongnewZzjl = newzzjl + Constant.FUHAO + newma;
                                    hsd.setNewZZJLID(zongnewZzjl);
                                }
                                isCommit();//提交
                            }
                        } else {
                            dismissLoading();
                            ToastUtils.showLong("未生成新条码");
                        }
                    } else {
                        dismissLoading();
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.制包单审核制包);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.制包单审核制包);
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    /**
     * 提交每个物品包扫描成功的状态，以及所对应的回收单
     *
     * @param list_wupb
     * @param list_huisd
     */
    private void saveState(List<Hsd_Mx> list_wupb, List<QingDian> list_huisd) {
        HashMap<String, Object> params = new HashMap<>();
        //                QingDian qd = null;
//                for (int i = 0; i < list_huisd.size(); i++) {
//                    qd = list_huisd.get(i);
//
//                    qd.setShenHeZT(0);
//                    qd.setShenHeKS("");
//                    qd.setShenHeKSID("");
//                    qd.setShenHeRen("");
//                    qd.setShenHeRenID("");
//                    qd.setShenHeSJ("");
//                }
//
//                for (int i = 0; i < list_wupb.size(); i++) {
//                    list_wupb.get(i).setLiShiZZJLID("");
//                    list_wupb.get(i).setIsScan(0);
//                }
        //---------
        String json_huisd = JSON.toJSONString(list_huisd, SerializerFeature.WriteNullStringAsEmpty);
        String json_wupb = JSON.toJSONString(list_wupb, SerializerFeature.WriteNullStringAsEmpty);

        String s = "DataSet¤" + json_huisd + "§" + json_wupb; //另包433    贵重 361
        params.put(Constant.CODE, WSOpraTypeCode.更新回收单和明细);
        params.put(Constant.PARAMETER, s);
        log.e("更新回收单和明细--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("更新回收单和明细--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        setAdapter(list_hsd_mx);
                        //是否需要实时更新回收单明细
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.更新回收单和明细);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.更新回收单和明细);
                    log.e(Constant.HSMS);
                }
            }
        });
    }


    //---------扫描物品包条码，审核，跳转详情  结束----------
    private void setAdapter(List<Hsd_Mx> list_hsd_mx) {
        if (list_hsd_mx.size() == 0 || list_hsd_mx.get(0).getHuiShouDID().equals("") || list_hsd_mx == null) {
            llData.setVisibility(View.GONE);
            llWudata.setVisibility(View.VISIBLE);
        } else {
            llData.setVisibility(View.VISIBLE);
            llWudata.setVisibility(View.GONE);

            Hsd_Mx hsd_mx = null;
            List<Hsd_Mx> list_no = new ArrayList<>();
            List<Hsd_Mx> list_yes = new ArrayList<>();

            int num = 0;
            for (int i = 0; i < list_hsd_mx.size(); i++) {
                hsd_mx = list_hsd_mx.get(i);

                int shehezt = hsd_mx.getShenHeZT();//审核状态
                int isScan = hsd_mx.getIsScan();//此条是否审核
                int shiSL = hsd_mx.getKeShiSL();//包的数量
                String zzjl = StringUtils.stringNull(hsd_mx.getLiShiZZJLID());
                String newzzjl = StringUtils.stringNull(hsd_mx.getNewZZJLID());
                if (isScan == 1) {   //针对第一次审核显示问题   和已审核直接显示
                    for (int j = 0; j < shiSL; j++) {
                        list_yes.add(hsd_mx);
                    }
                } else {  //      1 zzjl ""   2 0   3 1#1  0#1 0#0
//                    if(shiSL==1){}
                    if (zzjl.equals("")) {
                        for (int j = 0; j < shiSL; j++) {
                            list_no.add(hsd_mx);
                        }
                    } else if (zzjl.equals("0")) { //只有一个首次，其他未审核
                        list_yes.add(hsd_mx);
                        for (int j = 0; j < shiSL - 1; j++) {
                            list_no.add(hsd_mx);
                        }
                    } else if (zzjl.contains("#")) {  //多个包===至少两个包
                        String[] split = zzjl.split("#");
                        if (split.length == shiSL) {    //全部扫描完成
                            for (int j = 0; j < shiSL; j++) {
                                list_yes.add(hsd_mx);
                            }
                        } else if (shiSL > split.length) {
                            for (int j = 0; j < split.length; j++) {
                                list_yes.add(hsd_mx);
                            }
                            for (int j = 0; j < shiSL - split.length; j++) {
                                list_no.add(hsd_mx);
                            }
                        }
                    }
                }
                num += shiSL;
            }
            log.e(list_yes.size() + "  " + list_no.size());
            tvQqingdianNum.setText("共" + num + "个物品包");


            List<List<QXBeanWP>> list_shenhe = dateUtil.getToQingDian(list_yes);
            List<List<QXBeanWP>> list_weishenhe = dateUtil.getToQingDian(list_no);
////
            String json_weishenhe = JSON.toJSONString(list_weishenhe, SerializerFeature.DisableCircularReferenceDetect);
            String json_shenhe = JSON.toJSONString(list_shenhe, SerializerFeature.DisableCircularReferenceDetect);
            log.e(json_weishenhe + " # " + json_shenhe);
            lvWeishenhe.setNestedScrollingEnabled(false);
            lvWeishenhe.setLayoutManager(new LinearLayoutManager(this));
            lvWeishenhe.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL, 1, ContextCompat.getColor(mContext, R.color.item_view)));
            lvWeishenhe.setAdapter(new CommonAdapter<List<QXBeanWP>>(mContext, R.layout.item_qingdian, list_weishenhe) {
                @Override
                protected void convert(ViewHolder holder, final List<QXBeanWP> bean,  int position) {
                    holder.setText(R.id.tv_picihao, bean.get(0).getKeshi_name() + "(" + bean.get(0).getHsd_id() + ")");
                    CoustomListView lv_qingxi = holder.getView(R.id.lv_qingxi);
                    QingDSciAdapter adapter_no = new QingDSciAdapter(mContext, R.layout.item_qingdian_lv, bean);
                    lv_qingxi.setAdapter(adapter_no);
                    lv_qingxi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            toFirstDetail(bean.get(0));
                        }
                    });

                }
            });

            lvShenhe.setNestedScrollingEnabled(false);
            lvShenhe.setLayoutManager(new LinearLayoutManager(this));
            lvShenhe.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL, (int) getResources().getDimension(R.dimen.x1), ContextCompat.getColor(mContext, R.color.item_view)));
            lvShenhe.setAdapter(new CommonAdapter<List<QXBeanWP>>(mContext, R.layout.item_qingdian, list_shenhe) {
                @Override
                protected void convert(ViewHolder holder, final List<QXBeanWP> bean,  int position) {
                    holder.setText(R.id.tv_picihao, bean.get(0).getKeshi_name() + "(" + bean.get(0).getHsd_id() + ")");
                    CoustomListView lv_qingxi = holder.getView(R.id.lv_qingxi);
                    adapter_yes = new QingDAdapter(mContext, R.layout.item_qingdian_lv, bean);
                    lv_qingxi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            toFirstDetail(bean.get(0));
                        }
                    });
                    lv_qingxi.setAdapter(adapter_yes);
                }
            });
        }
    }

    /**
     * 扫描对比成功--进入物品包明细页面
     *
     * @param b
     * @param newma
     * @param image
     */
    private void startIntent(QXBean b, String newma, String image) {
        String wpbId = b.getWuPinBID();
        String tiaoma = b.getID();
        String wpbName = b.getWuPinBMC();
        if (!wpbId.equals("") && !wpbId.equals(null)) {//list_hsd_mx
            intent = new Intent(mContext, WuPinBaoActivity3.class);
            intent.putExtra("tag", "2");
            intent.putExtra("wupinbaoid", wpbId);
            intent.putExtra("wupinbaoname", wpbName);
            intent.putExtra("tiaoma", tiaoma);
            intent.putExtra("newimage", image);
            intent.putExtra("newma", newma);
            log.e(tiaoma + "--" + image + "--" + newma);
            Bundle bundle = new Bundle();
            bundle.putSerializable("list_huisdmx", (Serializable) list_hsd_mx);
            bundle.putSerializable("list_huisd", (Serializable) list_huisd);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }


    /**
     * 从合并后 ==每个物品包item --详细数据
     *
     * @param b
     */
    public void toFirstDetail(QXBeanWP b) {
        Intent intent = new Intent(mContext, WuPinBaoActivity3.class);
        intent.putExtra("tag", "3");
        intent.putExtra("wupinbaoid", b.getWp_id());
        intent.putExtra("wupinbaoname", b.getWp_name());
        log.e(b.getNum() + "--");
        Bundle bundle = new Bundle();
        bundle.putSerializable("list_huisdmx", (Serializable) list_hsd_mx);
        bundle.putSerializable("list_huisd", (Serializable) list_huisd);
        intent.putExtras(bundle);

        startActivity(intent);
    }

}