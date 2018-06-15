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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.chen.hsms.R;
import com.example.chen.hsms.adapter.QingDianAdapter;
import com.example.chen.hsms.application.MyApplication;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.data.UserMsg;
import com.example.chen.hsms.bean.data.XiaoDuGuo;
import com.example.chen.hsms.bean.data.QXBean;

import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.DateUtil;
import com.example.chen.hsms.utils.NetUtils;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.view.RecyclerViewDivider;
import com.example.chen.hsms.view.ShowDialog;
import com.example.chen.hsms.webserviceutils.WebServiceUtils;
import com.example.chen.hsms.ws.Hsms;
import com.example.chen.hsms.ws.WSOpraTypeCode;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MieJunActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_serach)
    TextView tvSerach;
    @BindView(R.id.tv_miejun_type)
    TextView tvMiejunType;
    @BindView(R.id.tv_wpbNum_xd)
    TextView tvWpbNum;
    @BindView(R.id.fl_miejun)
    FrameLayout flMiejun;
    @BindView(R.id.rcy_miejun)
    RecyclerView rcyMiejun;
    @BindView(R.id.ll_data)
    LinearLayout llData;
    @BindView(R.id.ll_wuData)
    LinearLayout llWuData;

    private String guociName = "";
    private List<QXBean> listid;
    //绑定消毒

    private UserMsg userMsg;
    private MyApplication app;
    private DateUtil dateUtil;

    private XiaoDuGuo xiaoDuGuo = new XiaoDuGuo();//选中的消毒锅

    private String xiaoDuChe = "";//首先扫描消毒车
    private QingDianAdapter adapter;
    private String cunFangWpb = "";//用于缓存数据对比，看是否打包灭菌
    private String guoId = "";//消毒锅id，判断是否扫码过灭菌锅--进而查询锅是否正在灭菌


    private List<String> list_mjfs = new ArrayList<>();//与扫描灭菌机   灭菌方式不同


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_mie_jun;
    }

    @Override
    protected void initView() {
        userMsg = MyApplication.getInstance().getUserMsg();
        app = MyApplication.getInstance();
        dateUtil = DateUtil.getInstance();

        cunFangWpb = getIsMiejun();
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.state), 0);

    }

    @Override
    public void initDatas() {
        tvTitle.setText(getResources().getText(R.string.miejun));
    }

    @Override
    public void initListeners() {
    }


    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    //========================================mobi扫描头驱动


    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constant.ACTION_BARCODE_READER_VALUE);
        registerReceiver(mBarcodeReaderReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBarcodeReaderReceiver = new BroadcastReceiver() {
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
    //==================上面是mobi扫描头驱动=============================


    private void scanChuLi(String data) {
        String head = getHead(data);
        if (head.equals("")) {
            ToastUtils.showLong(Constant.TAGTOU);
            return;
        }
        if (head.equals(Constant.XDC)) {   //消毒车
            String[] split = data.split("@");
            xiaoDuChe = split[1];
            scanXdChe(xiaoDuChe);
        } else if (head.equals(Constant.MJG)) { //扫描灭菌锅
            //扫描灭菌锅--判断
            scanGuoBofore(data);
        } else {
            ToastUtils.showLong(Constant.MIEJUN);
        }
    }

    /**
     * 扫描消毒车，获取车里面所有物品包
     *
     * @param che
     */
    private void scanXdChe( String che) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + che;//¤
        params.put(Constant.CODE, WSOpraTypeCode.获取消毒车内物品);
        params.put(Constant.PARAMETER, s);
        log.e("获取消毒车内物品--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("修改物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        list_all.clear();
                        list_all = JSON.parseArray(result, QXBean.class);
                        if (list_all.size() == 0 || list_all.get(0).getID().equals("")) {
                            ToastUtils.showLong(xiaoDuChe + "消毒车是空的");
                            xiaoDuChe = "";
                        } else {
                            setAdapter(list_all);
                            mSVProgressHUD.showSuccessWithStatus(xiaoDuChe + "消毒车绑定成功");
                        }
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取消毒车内物品);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.获取消毒车内物品);
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    /**
     * 判断扫描灭菌锅之前
     *
     * @param data
     */
    private void scanGuoBofore(String data) {
        try {
            String[] split = data.split("@");
            xiaoDuGuo = getScanXdg(split[1]);

            int guoci = xiaoDuGuo.getDangQianGC();//当前锅次
            int xuhao = xiaoDuGuo.getDangQianXLH();//当前序号
            guociName = xiaoDuGuo.getXiaoDuGMC();//消毒锅名称
            guoId = xiaoDuGuo.getID();//消毒锅id

            tvMiejunType.setText(guociName + "; " + "锅次" + guoci + "; " + "序号" + xuhao);

            chaGuo(guoId);
        } catch (Exception e) {
            log.e(e.getMessage());
        }
    }

    private void chaGuo(String guoid) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + "where id='" + guoid + "'";
        params.put(Constant.CODE, WSOpraTypeCode.获取消毒锅);
        params.put(Constant.PARAMETER, s);
        log.e("获取消毒锅--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("获取消毒锅--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<XiaoDuGuo> list_guo = JSON.parseArray(result, XiaoDuGuo.class);
                        if (list_guo.get(0).getXiaoDuGZT() == 1) {   //正在灭菌  重灭或结束
                            dialogRestartMJ();
                        } else {                                    //空闲状态  直接灭菌
                            if (list_all.size() > 0 && !list_all.get(0).getID().equals("")) {
                                startMiejun(list_all);
                            } else {
                                ToastUtils.showLong("请先扫描消毒车");
                            }
                        }
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取消毒锅);

                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.获取消毒锅);
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    /**
     * 用于
     * （灭菌中机器坏掉   重新开始灭菌） 是否要重新开始灭菌
     */
    private void dialogRestartMJ() {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
//        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setContentView(R.layout.dialog_del);//重点看这获取弹出框内的视图view
        TextView content = (TextView) dialog.findViewById(R.id.content);//重点看这行的Dialog
        content.setText(guociName + "已处于灭菌状态，重新开始灭菌还是结束灭菌？");
        Button bt_restart = (Button) dialog.findViewById(R.id.bt_yes);//重点看这行的Dialog
        Button bt_end = (Button) dialog.findViewById(R.id.bt_no);//重点看这行的Dialog
        bt_restart.setText("重新开始");
        bt_end.setText("结束灭菌");
        bt_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (list_all.size() > 0 && !list_all.get(0).getID().equals("")) {  //没有其他类型包
                    startMiejun(list_all);
                } else {
                    ToastUtils.showLong("请先扫描消毒车");
                }
            }
        });
        bt_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                wantEndMjguo(guoId);
            }
        });
    }

    /**
     * 根据没灭菌锅id查追踪记录
     *
     * @param id
     */
    private void wantEndMjguo(String id) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + id;
        params.put(Constant.CODE, WSOpraTypeCode.通过消毒锅ID查看消毒物品包);
        params.put(Constant.PARAMETER, s);
        log.e("通过消毒锅ID查看消毒物品包--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("通过消毒锅ID查看消毒物品包--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<QXBean> list = JSON.parseArray(result, QXBean.class);
                        if (!list.get(0).getID().equals("")) {
                            endMjun(list);
                        } else {
                            dismissLoading();
                            ToastUtils.showLong(guoId + "灭菌锅中没有记录");
                        }
                    } else {
                        dismissLoading();
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.通过消毒锅ID查看消毒物品包);
                    }
                } else {
                    dismissLoading();
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.通过消毒锅ID查看消毒物品包);
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    //********     灭菌锅开始   *****************


    private void startMiejun(List<QXBean> list) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        QXBean bean = null;
        for (int i = 0; i < list.size(); i++) {//循环添加已扫描的物品包
            bean = list.get(i);
            bean.setXiaoDuZT(1);//消毒状态
            bean.setXiaoDuRen1(userMsg.getID());//消毒人id
            bean.setXiaoDuKSSJ(dateUtil.getYear_Day());//开始时间

            bean.setXiaoDuGuo(guoId);//消毒锅名称
            bean.setXiaoDuGC(xiaoDuGuo.getDangQianGC() + "");//消毒锅次
            bean.setXiaoDuGXL(xiaoDuGuo.getDangQianXLH() + "");//消毒序列号
            bean.setXiaoDuWD(xiaoDuGuo.getWenDu());//温度
//                    bean.setXiaoDuChe(dateUtil.getTime() + "#" + xiaoDuChe);//消毒车
        }
        String newJson = JSON.toJSONString(list, SerializerFeature.WriteNullStringAsEmpty);
        String s = Constant.DATATABLE + newJson;//¤
        params.put(Constant.CODE, WSOpraTypeCode.修改物品包追踪记录);
        params.put(Constant.PARAMETER, s);
        log.e("修改物品包追踪记录--" + list.size() + "    " + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("修改物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        startUpdata();//更新消毒锅状态
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.修改物品包追踪记录);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.修改物品包追踪记录);
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    //开始消毒  更新消毒锅状态
    private void startUpdata() {
        HashMap<String, Object> params = new HashMap<>();
        xiaoDuGuo.setXiaoDuGZT(1);
        String xiaodug_json = JSON.toJSONString(xiaoDuGuo, SerializerFeature.WriteNullStringAsEmpty);
        String s = "DataSet¤" + "[" + xiaodug_json + "]";//¤
        params.put(Constant.CODE, WSOpraTypeCode.增加消毒锅);
        params.put(Constant.PARAMETER, s);
        log.e("增加消毒锅--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("增加消毒锅--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        ToastUtils.showLong("成功开始灭菌,请耐心等待结束");
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.增加消毒锅);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.增加消毒锅);
                    log.e(Constant.HSMS);
                }
            }
        });

    }
    //********     灭菌锅结束   *****************


    //结束 时  要判断是此灭菌锅 否已经开始灭菌
    public void endMjun(List<QXBean> list) {
        HashMap<String, Object> params = new HashMap<>();
        QXBean bean = null;
        for (int i = 0; i < list.size(); i++) {//循环添加已扫描的物品包
            bean = list.get(i);
            bean.setXiaoDuZT(8);//消毒状态
            bean.setXiaoDuJSSJ(dateUtil.getYear_Day());//结束时间
        }
        String newJson = JSON.toJSONString(list, SerializerFeature.WriteNullStringAsEmpty);
        String s = "DataTable¤" + newJson;//¤
        params.put(Constant.CODE, WSOpraTypeCode.修改物品包追踪记录);
        params.put(Constant.PARAMETER, s);
        log.e("修改物品包追踪记录--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("修改物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        endUp_XiaoDuG();
                    } else {
                        dismissLoading();
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.修改物品包追踪记录);
                    }
                } else {
                    dismissLoading();
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.修改物品包追踪记录);
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    //结束消毒  更改消毒锅状态
    private void endUp_XiaoDuG() {
        HashMap<String, Object> params = new HashMap<>();
        xiaoDuGuo.setXiaoDuGZT(8);
        xiaoDuGuo.setDangQianGC(1 + xiaoDuGuo.getDangQianGC());
        xiaoDuGuo.setDangQianXLH(1 + xiaoDuGuo.getDangQianXLH());
        String xiaodug_json = JSON.toJSONString(xiaoDuGuo, SerializerFeature.WriteNullStringAsEmpty);
        String s = "DataSet¤" + "[" + xiaodug_json + "]";//¤
        params.put(Constant.CODE, WSOpraTypeCode.增加消毒锅);
        params.put(Constant.PARAMETER, s);
        log.e("增加消毒锅--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("修改物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        mSVProgressHUD.showSuccessWithStatus("灭菌成功结束");
                        tvMiejunType.setText("");
                        list_all.clear();
//                        list_miejunbao.clear();
                        xiaoDuChe = "";
                        guoId = "";
                        list_mjfs.clear();
                        setAdapter(list_all);
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.增加消毒锅);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.增加消毒锅);
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    private List<QXBean> list_all = new ArrayList<>();
//    private List<QXBean> list_miejunbao = new ArrayList<>();

    private void setAdapter(List<QXBean> list) {
        if (list.size() > 0 && !list.get(0).getID().equals("")) {//有数据
            llWuData.setVisibility(View.GONE);
            llData.setVisibility(View.VISIBLE);

            tvWpbNum.setText("共" + list.size() + "个物品包");

            rcyMiejun.setLayoutManager(new LinearLayoutManager(this));
            rcyMiejun.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL, (int) getResources().getDimension(R.dimen.x1), ContextCompat.getColor(mContext, R.color.item_view)));
            rcyMiejun.setAdapter(new CommonAdapter<QXBean>(mContext, R.layout.item_miejun_rv, list) {
                @Override
                protected void convert( ViewHolder holder, final QXBean bean,  int position) {
                    holder.setText(R.id.tv_tiaomahao, bean.getID());
                    holder.setText(R.id.tv_item_title, bean.getWuPinBMC());

                    int faFangJJBZ = bean.getFaFangJJBZ();
                    int daBaoZT = bean.getDaBaoZT();
//                    String leiXing = bean.getQingXiLeiXing();

                    if (bean.getXiaoDuZT() == 1) {//	原来 2. XiaoDuZT=1 正在灭菌，跳过
                        holder.setText(R.id.tv_baostate, "灭菌中");
//                        list_miejunbao.add(bean);
                    } else if (faFangJJBZ == 1) {//3. FaFangJJBZ=1已经发放，跳过
                        holder.setText(R.id.tv_baostate, "已发放");
                    } else if (cunFangWpb.equals("0") && daBaoZT == 0) {//4. WSAgentGlobal.GetCanShu("未打包能否灭菌")==0 && DaBaoZT=0 未打包，无法灭菌
                        holder.setText(R.id.tv_baostate, "未打包");
                    }
//                    else if (leiXing.equals("清洗消毒机")) {//
//                        holder.setText(R.id.tv_baostate, "发放包");
//                    }
                    else {
                        holder.setText(R.id.tv_baostate, "灭菌包");
//                        list_miejunbao.add(bean);
                    }


                     LinearLayout ll_item = holder.getView(R.id.ll_miejun_item);
                    ll_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            toOneWpbDetail(bean);
                        }
                    });
                }
            });
        } else {
            llWuData.setVisibility(View.VISIBLE);
            llData.setVisibility(View.GONE);
        }
    }
    /*     暂时无用     */
//    private void bindXiaoDuGuo() {
//        QXBean bean = null;
//        for (int i = 0; i < list_miejunbao.size(); i++) {
//            bean = list_miejunbao.get(i);
//            if (!xiaoDuGuo.getMieJunFS().equals(bean.getMieJunFS())) {  //	1.消毒锅.MieJunFS !=追踪记录.MieJunFS 继续或着跳过
//                list_mjfs.add(bean.getID());
//            }
//        }
//        if (list_mjfs.size() == 0) {  //灭菌方式完全一样  开始灭菌
//            startMiejun(list_miejunbao);
//        } else {                     //把灭菌方式不同的全部列举出来  是继续绑定还是挑选出来
//            String jihe = "";
//            for (int i = 0; i < list_mjfs.size(); i++) {
//                jihe += list_mjfs.get(i) + ",";
//            }
//            dialogMieJunFS(jihe);
//        }
//    }

    /**
     * 消毒锅灭菌方式不同，是取出再操作还是直接执行
     *
     * @param jihe
     */
    private void dialogMieJunFS( String jihe) {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setContentView(R.layout.dialog_del);//重点看这获取弹出框内的视图view
        TextView content = (TextView) dialog.findViewById(R.id.content);//重点看这行的Dialog
        content.setText(jihe + " 与该消毒锅灭菌方式不同，是否继续灭菌？");
        Button bt_yes = (Button) dialog.findViewById(R.id.bt_yes);//重点看这行的Dialog
        Button bt_no = (Button) dialog.findViewById(R.id.bt_no);//重点看这行的Dialog
        bt_yes.setText("灭菌");
//        bt_no.setText("灭菌");
        bt_yes.setOnClickListener(new View.OnClickListener() {  //先取出再继续
            @Override
            public void onClick(View view) {
                dialog.dismiss();
//                QXBean b = null;
//                for (int i = 0; i < list_miejunbao.size(); i++) {
//                    b = list_miejunbao.get(i);
//                    if (jihe.contains(b.getID())) {
//                        list_miejunbao.remove(i);
//                    }
//                }
//                startMiejun(list_miejunbao);
            }
        });
        bt_no.setOnClickListener(new View.OnClickListener() { //不做任何操作，继续执行
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void isBindMiejunbao() {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setContentView(R.layout.dialog_del);//重点看这获取弹出框内的视图view
        TextView content = (TextView) dialog.findViewById(R.id.content);//重点看这行的Dialog
        content.setText("只对灭菌包进行灭菌，是否继续？");
        Button bt_yes = (Button) dialog.findViewById(R.id.bt_yes);//重点看这行的Dialog
        Button bt_no = (Button) dialog.findViewById(R.id.bt_no);//重点看这行的Dialog
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
//                startMiejun(list_miejunbao);
//                bindXiaoDuGuo();
                setAdapter(list_all);
            }
        });
        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
