package com.example.chen.hsms.activity.home_down;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.chen.hsms.R;
import com.example.chen.hsms.application.MyApplication;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.data.IdName;
import com.example.chen.hsms.bean.data.QXBean;
import com.example.chen.hsms.bean.data.QingXiJiLu;
import com.example.chen.hsms.bean.data.ZhuiSuData;

import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.view.RecyclerViewDivider;
import com.example.chen.hsms.webservice.Webservice;
import com.example.chen.hsms.ws.Hsms;
import com.example.chen.hsms.ws.WSOpraTypeCode;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 清洗
 */
public class CleanActivity extends BaseActivity {
    @BindView(R.id.iv_qingxi_back)
    ImageView ivQingxiBack;
    @BindView(R.id.tv_saoma)
    TextView tvSaoma;
    @BindView(R.id.et_clean)
    EditText etClean;
    @BindView(R.id.tv_state1)
    TextView tvState1;
    @BindView(R.id.tv_type1)
    TextView tvType1;
    @BindView(R.id.fl_cerrent)
    FrameLayout flCerrent;
    @BindView(R.id.rcy_zhuisu1)
    RecyclerView rcyZhuisu1;
    @BindView(R.id.tv_state2)
    TextView tvState2;
    @BindView(R.id.tv_type2)
    TextView tvType2;
    @BindView(R.id.rcy_zhuisu2)
    RecyclerView rcyZhuisu2;
    @BindView(R.id.ll_data)
    ScrollView llData;
    @BindView(R.id.tv_nodata)
    TextView tvNodata;
    @BindView(R.id.fl_wudata)
    FrameLayout flWudata;
    @BindView(R.id.fl_yff)
    FrameLayout flYff;
    private Hsms hs;


    private String tiaoma = "";
    private final String ZT = "";
    private List<IdName> list_idname;//对比id找出人名字


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        registerBoradcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBoradcastReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBarcodeReaderReceiver != null) {
            unregisterReceiver(mBarcodeReaderReceiver);
        }
    }
    // ========================================mobi扫描头驱动

    public void registerBoradcastReceiver() {
        Log.d(TAG, "====registerBoradcastReceiver====----------cclu====");
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constant.ACTION_BARCODE_READER_VALUE);
        registerReceiver(mBarcodeReaderReceiver, myIntentFilter);
    }

    protected BroadcastReceiver mBarcodeReaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.ACTION_BARCODE_READER_VALUE)) {
                final String data = intent.getStringExtra(Constant.BORCODE_VALUE);
                scanChuLi(data);
            }
        }
    };

    private void scanChuLi(String data) {
        String head = getHead(data);
        if (head.equals("")) {
            ToastUtils.showLong(Constant.TAGTOU);
            return;
        }
        if (head.equals(Constant.PTB)) {  //物品包条码
            String[] split = data.split("@");
            tiaoma = split[1];
            etClean.setText(tiaoma);
            first(tiaoma);
        } else {
            ToastUtils.showLong("只追溯物品包条码");
        }
    }


    @Override
    public void onBackPressed() {
        log.e("onBackPressed");
        finish();
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    //==================上面是mobi扫描头驱动=============================
    @Override
    public int setLayoutId() {
        return R.layout.activity_clean;
    }

    @Override
    protected void initView() {
//        dateUtil = DateUtil.getInstance();
        list_idname = MyApplication.getInstance().getList_idname();
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.state), 0);
    }

    @Override
    public void initDatas() {
        initData();
    }

    private void initData() {

    }

    @Override
    public void initListeners() {

    }

    /**
     * 根据条码查
     *
     * @param id
     */
    private void first(final String id) {
        showLoading(Constant.JIAZAI);
        new Webservice(this, handler, Constant.METHODNAME, true) {
            @Override
            public void addProgerty(SoapObject rpc) {
                String s = Constant.STRING + "ID=" + id;
                rpc.addProperty(Constant.CODE, WSOpraTypeCode.获取物品包追踪记录);
                rpc.addProperty(Constant.PARAMETER, s);
                log.e("根据条码追溯是否发放--" + s);
            }
        };
    }

    private List<QXBean> list_bean = new ArrayList<>();
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    hs = (Hsms) msg.obj;
                    log.e("根据条码追溯是否发放--" + hs.getResturnMsg() + "--" + hs.getReturnCode() + "--" + hs.getReturnID() + "--" + hs.getReturnJson());
                    if (hs.getReturnCode() == 0) {
                        String result = hs.getReturnJson();
                        list_bean.clear();
                        list_bean = JSON.parseArray(result, QXBean.class);
                        QXBean bean = list_bean.get(0);
                        if (bean.equals("")) {
                            dismissLoading();
                            ToastUtils.showLong(Constant.NOTIAOMA);
                        } else {
                            second(bean.getID());
                        }
                    } else {
                        dismissLoading();
                        ToastUtils.showLong(Constant.ERROR);
                    }
                    break;
                default:
                    break;
            }
        }
    };
//----------------------------发放过的----------------------

    /**
     * 发放过的清洗记录
     *
     * @param newma
     */
    private void ffQxjl(final String newma) {
        new Webservice(this, handler_ffqxjl, Constant.METHODNAME, true) {
            @Override
            public void addProgerty(SoapObject rpc) {
                String s = Constant.STRING + "" + "|('" + newma + "')";
                rpc.addProperty(Constant.CODE, WSOpraTypeCode.获取清洗篮中物品包);
                rpc.addProperty(Constant.PARAMETER, s);
                log.e("查询发放过的清洗记录--" + s);
            }
        };
    }

    private Handler handler_ffqxjl = new Handler() {
        public void handleMessage(Message msg) {
            dismissLoading();
            switch (msg.what) {
                case 0:
                    hs = (Hsms) msg.obj;
                    log.e("查询发放过的清洗记录--" + hs.getResturnMsg() + "--" + hs.getReturnCode() + "--" + hs.getReturnID() + "--" + hs.getReturnJson());
                    if (hs.getReturnCode() == 0) {
                        String result = hs.getReturnJson();
                        List<QingXiJiLu> list_qxjl = JSON.parseArray(result, QingXiJiLu.class);
                        List<ZhuiSuData> list2 = zhengLi(list_bean, list_qxjl);
                        setAdapter(null, list2);
                    } else {
                        ToastUtils.showLong(Constant.ERROR);
                    }
                    break;
                default:
                    break;
            }
        }
    };
//----------------------------发放过的----------------------


    /**
     * 根据扫描条码查是否产生新条码
     *
     * @param oldma
     */
    private void second(final String oldma) {
        new Webservice(this, handler_xintiaoma, Constant.METHODNAME, true) {
            @Override
            public void addProgerty(SoapObject rpc) {
                String s = Constant.STRING + oldma;
                rpc.addProperty(Constant.CODE, WSOpraTypeCode.获取旧条码生成的新追踪记录);
                rpc.addProperty(Constant.PARAMETER, s);
                log.e(s);
            }
        };
    }

    private List<QXBean> list_xindata = new ArrayList<>();
    private String fftiaoma = "";//显示发放条码
    private String wfftiaoma = "";//未发放
    private Handler handler_xintiaoma = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    hs = (Hsms) msg.obj;
                    log.e("追溯物品包--" + hs.getResturnMsg() + "--" + hs.getReturnCode() + "--" + hs.getReturnID() + "--" + hs.getReturnJson());
                    if (hs.getReturnCode() == 0) {
                        String result = hs.getReturnJson();
                        list_xindata.clear();//每次先清空再赋值
                        list_xindata = JSON.parseArray(result, QXBean.class);
                        log.e(list_xindata.size() + "追溯物品包");
                        fftiaoma = tiaoma;
                        wfftiaoma = list_xindata.get(0).getID();
                        String s = fftiaoma + "@" + list_xindata.get(0).getID();
                        String[] split = s.split("@");
                        for (int i = 0; i < split.length; i++) {
                            synchronized (this) {
                                qingxiJilu(split[i]);
                            }
                        }
                    } else {  //去查找发放过的记录  只查清洗记录整理即可
                        fftiaoma = list_bean.get(0).getID();
                        ffQxjl(list_bean.get(0).getID());
                    }
                    log.e(fftiaoma + "--发放条码");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 根据新条码查询清洗记录
     *
     * @param newma
     */
    private void qingxiJilu(final String newma) {
        new Webservice(this, handler_chalan, Constant.METHODNAME, true) {
            @Override
            public void addProgerty(SoapObject rpc) {
                String s = Constant.STRING + "" + "|('" + newma + "')";
                rpc.addProperty(Constant.CODE, WSOpraTypeCode.获取清洗篮中物品包);
                rpc.addProperty(Constant.PARAMETER, s);
                log.e("查询清洗记录--" + s);
            }
        };
    }

    private int num = 0;
    private List<ZhuiSuData> list2 = new ArrayList<>();
    private List<ZhuiSuData> list1 = new ArrayList<>();
    private Handler handler_chalan = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    hs = (Hsms) msg.obj;
                    log.e("查询清洗记录--" + hs.getResturnMsg() + "--" + hs.getReturnCode() + "--" + hs.getReturnID() + "--" + hs.getReturnJson());
                    if (hs.getReturnCode() == 0) {
                        String result = hs.getReturnJson();
                        final List<QingXiJiLu> list_qxjl = JSON.parseArray(result, QingXiJiLu.class);
                        QingXiJiLu qxjl = list_qxjl.get(0);
                        if (qxjl.getWupinBzzjlID().equals(fftiaoma)) {
                            num++;
                            list2.clear();
                            list2 = zhengLi(list_bean, list_qxjl);//发放追溯
                        } else if (qxjl.getWupinBzzjlID().equals(wfftiaoma)) {
                            num++;
                            list1.clear();
                            list1 = zhengLi(list_xindata, list_qxjl);//当前追溯
                        }
                        if (num == 2) {
                            dismissLoading();
                            num = 0;
                            setAdapter(list1, list2);
                        }
                    } else {
                        dismissLoading();
                        ToastUtils.showLong(Constant.ERROR);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private List<ZhuiSuData> zhengLi(List<QXBean> list_xindata, List<QingXiJiLu> list_qxjl) {
        List<ZhuiSuData> list_data = new ArrayList<>();
        QXBean bean = list_xindata.get(0);
        String qdren = bean.getHuiShouQDR1();
        list_data.add(new ZhuiSuData("清点人", "", qdren));

        if (list_qxjl.size() == 1 && !list_qxjl.get(0).getID().equals("")) {
            QingXiJiLu qxjl = list_qxjl.get(0);

            String qxlan = qxjl.getQingXiLan();
            if (!qxlan.equals("") || qxlan != null) {
                list_data.add(new ZhuiSuData("清洗篮", qxlan, qdren));
                String qxjia = qxjl.getQingXiJia();
                if (!qxjia.equals("") || qxjia != null) {
                    list_data.add(new ZhuiSuData("清洗架", qxjia, ""));
                    String qxji = bean.getQingXiJi();
                    String qxren = bean.getQingXiRen1();
                    if (!qxji.equals("") || qxji != null) {
                        list_data.add(new ZhuiSuData("清洗机", qxji, qxren));
                    }
                }
            }
            dabMjun(list_data, bean);

        } else if (list_qxjl.size() == 2) {
            String qxlan = "";
            String qxjia = "";
            for (int i = 0; i < list_qxjl.size(); i++) {
                QingXiJiLu qxjl = list_qxjl.get(i);
                qxlan += qxjl.getQingXiLan() + ",";
                if (!qxjl.getQingXiJia().equals("")) {
                    qxjia += qxjl.getQingXiJia() + ",";
                }
            }
            if (!qxlan.equals("")) {
                String lan = subString(qxlan);
                list_data.add(new ZhuiSuData("清洗篮", lan, qdren));//清洗机
                if (!qxjia.trim().equals(",,")) {
                    String jia = subString(qxjia);
                    if (jia.contains(",")) {
                        String[] split = jia.split(",");
                        if (split[0].equals(split[1])) {
                            list_data.add(new ZhuiSuData("清洗架", split[0], ""));
                        } else {
                            list_data.add(new ZhuiSuData("清洗架", jia, ""));
                        }
                    } else {
                        list_data.add(new ZhuiSuData("清洗架", jia, ""));
                    }

                    String qxji = bean.getQingXiJi();
                    if (!qxji.equals("")) {
//                        String qxlx = getQxlxid_lx(bean.getQingXiLeiXing());
                        String qxren = bean.getQingXiRen1();
//                        list_data.add(new ZhuiSuData("清洗机", qxji + "(" + qxlx + ")", qxren));
                    }
                }
            }
            dabMjun(list_data, bean);
        }
        return list_data;
    }


    private void dabMjun(List<ZhuiSuData> list_data, QXBean bean) {
        int dbzt = bean.getDaBaoZT();
        String dbren = bean.getDaBaoRen1();
        if (dbzt == 8) {     //   未打包  打包人
            list_data.add(new ZhuiSuData("打包人", "", dbren));
        }
        String che = bean.getXiaoDuChe();
        if (!che.equals("") && che != null) {      //   消毒车
            list_data.add(new ZhuiSuData("消毒车", che, dbren));
        }
        int xdzt = bean.getXiaoDuZT();
        String xdren = bean.getXiaoDuRen1();
        String mjguo = bean.getXiaoDuGuo();
        if (xdzt == 8) {         //   未消毒
            list_data.add(new ZhuiSuData("灭菌锅", mjguo, xdren));
        }
        int ffzt = bean.getFaFangJJBZ();
        String ffren1 = bean.getFaFangJJR1();
        String ffren2 = bean.getFaFangJJR2();
        String ffren3 = bean.getFaFangJJR3();
        if (ffzt == 1) {
            if (ffren1 != null || !ffren1.equals("")) {
                list_data.add(new ZhuiSuData("供应室发放人", "", ffren1));
                if (ffren2 != null || !ffren2.equals("")) {
                    list_data.add(new ZhuiSuData("中间发放人", "", ffren2));
                    if (ffren3 == null || ffren3.equals("")) {

                    } else {
                        list_data.add(new ZhuiSuData("科室接收人", "", ffren3));
                    }
                }
            }
        } else {
            if (ffren1 != null & !ffren1.equals("")) {
                list_data.add(new ZhuiSuData("供应室发放人", "", ffren1));
                if (ffren2 != null & !ffren2.equals("")) {
                    list_data.add(new ZhuiSuData("中间发放人", "", ffren2));
                    if (ffren3 != null & !ffren3.equals("")) {
                        list_data.add(new ZhuiSuData("科室接收人", "", ffren3));
                    }
                }
            }
        }
    }


    /**
     * 截取字符串
     *
     * @param qxlan
     * @return
     */
    private String subString(String qxlan) {
        String s = "";
        if (qxlan.endsWith(",")) {
            s = qxlan.substring(0, qxlan.length() - 1);
        }
        return s;
    }

    private void setAdapter(List<ZhuiSuData> list_current, List<ZhuiSuData> list_fafang) {
        if (list_fafang != null && list_fafang.size() > 0) { //有数据
            flWudata.setVisibility(View.GONE);
            llData.setVisibility(View.VISIBLE);
            flCerrent.setVisibility(View.GONE);
            rcyZhuisu1.setVisibility(View.GONE);
            if (list_current != null && list_current.size() > 0) {
                flCerrent.setVisibility(View.VISIBLE);
                rcyZhuisu1.setVisibility(View.VISIBLE);

//                tvState2.setText(wfftiaoma);
                rcyZhuisu1.setNestedScrollingEnabled(false);
                rcyZhuisu1.setLayoutManager(new LinearLayoutManager(this));
                rcyZhuisu1.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL, (int) getResources().getDimension(R.dimen.x1), ContextCompat.getColor(mContext, R.color.item_view)));
                rcyZhuisu1.setAdapter(new CommonAdapter<ZhuiSuData>(mContext, R.layout.item_zhuisu_rv, list_current) {
                    @Override
                    protected void convert(ViewHolder holder, ZhuiSuData b, int position) {
                        holder.setText(R.id.tv_shuju, b.getShujuName());
                        holder.setText(R.id.tv_qixiename, b.getShuju());
                        holder.setText(R.id.tv_name, idToName(b.getXmid()));
                    }
                });
            }


            //发放完追溯
//            tvState2.setText(fftiaoma);


            rcyZhuisu2.setNestedScrollingEnabled(false);
            rcyZhuisu2.setLayoutManager(new LinearLayoutManager(this));
            rcyZhuisu2.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL, (int) getResources().getDimension(R.dimen.x1), ContextCompat.getColor(mContext, R.color.item_view)));
            rcyZhuisu2.setAdapter(new CommonAdapter<ZhuiSuData>(mContext, R.layout.item_zhuisu_rv, list_fafang) {
                @Override
                protected void convert(ViewHolder holder, ZhuiSuData b, int position) {
                    holder.setText(R.id.tv_shuju, b.getShujuName());
                    holder.setText(R.id.tv_qixiename, b.getShuju());
                    holder.setText(R.id.tv_name, idToName(b.getXmid()));
                }
            });
        } else {
            flWudata.setVisibility(View.VISIBLE);
            llData.setVisibility(View.GONE);
        }

    }


    @OnClick({R.id.iv_qingxi_back, R.id.tv_saoma})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_qingxi_back:
                finish();
                break;
            case R.id.tv_saoma:
                break;
        }
    }

    private String idToName(String id) {
        String name = "";
        IdName idName = null;
        for (int i = 0; i < list_idname.size(); i++) {
            idName = list_idname.get(i);
            if (idName.getYonghugh().equals(id)) {
                name = idName.getYonghuxm();
                break;
            }
        }
        return name;
    }
}
