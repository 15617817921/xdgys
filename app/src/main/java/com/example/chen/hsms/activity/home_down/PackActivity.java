package com.example.chen.hsms.activity.home_down;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.chen.hsms.R;
import com.example.chen.hsms.application.MyApplication;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.base.CommonAdapter;
import com.example.chen.hsms.base.ViewHolder;
import com.example.chen.hsms.bean.data.UserMsg;
import com.example.chen.hsms.bean.data.QXBean;

import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.DateUtil;
import com.example.chen.hsms.utils.NetUtils;
import com.example.chen.hsms.utils.StringUtils;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.view.CoustomListView;
import com.example.chen.hsms.view.ShowDialog;
import com.example.chen.hsms.webserviceutils.WebServiceUtils;
import com.example.chen.hsms.ws.Hsms;
import com.example.chen.hsms.ws.WSOpraTypeCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 打包
 */
public class PackActivity extends BaseActivity {

    @BindView(R.id.tv_pack_num)
    TextView tvPackNum;
    @BindView(R.id.rcy_pack)
    CoustomListView rcyPack;
    @BindView(R.id.ll_data)
    LinearLayout llData;
    @BindView(R.id.fl_wudata)
    FrameLayout llWudata;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_serach)
    TextView tvSerach;

    private String curTime;
    private UserMsg userMsg;
    private DateUtil dateUtil;
    private MyApplication app;
    private MeiJunAdapter adapter;
    private QXBean bean;
    private int isCancleDabao = -1;//用于判断是否撤销打包   0--撤销   1--打包
    private String tiaoma_chexiao = "";//撤销的条码

    private String cunFangWpb = "";//用于缓存数据对比，看是否打包灭菌
    private String che = "";//消毒车
    private int tagche = -1;

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
        return R.layout.activity_pack;
    }

    @Override
    protected void initView() {
        userMsg = MyApplication.getInstance().getUserMsg();
        app = MyApplication.getInstance();
        dateUtil = DateUtil.getInstance();

        cunFangWpb = getIsMiejun();
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.state), 0);

//        initToolBarRight("打包", ContextCompat.getColor(mContext, R.color.white), "扫码", ContextCompat.getColor(mContext, R.color.white));
    }

    @Override
    public void initDatas() {
        tvTitle.setText(getResources().getText(R.string.dabao));
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

    //========================mobi扫描头驱动=====================


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
        if (head.equals(Constant.PTB)) {  //物品包条码
            String[] split = data.split("@");
            String tiaoma = split[1];
            if (tagche == 1) { //证明已经车
                map_ma.clear();
                tagche = -1;
            }
            saoma(tiaoma);
        } else if (head.equals(Constant.XDC)) {//消毒车
            if (map_ma.size() == 0) {
                ToastUtils.showLong("请先扫描物品包");
            } else {
                String[] split = data.split("@");
                che = split[1];
                bindCheBefore(che);
                tagche = 1;
            }
        } else {
            ToastUtils.showLong(Constant.DABAO);
        }
    }

    /*             扫码         start                   */
    private void saoma(String id) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + "id='" + id + "'";
        params.put(Constant.CODE, WSOpraTypeCode.获取物品包追踪记录);
        params.put(Constant.PARAMETER, s);
        log.e("获取物品包追踪记录--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("获取物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<QXBean> listid = JSON.parseArray(result, QXBean.class);
                        bean = listid.get(0);
                        if (bean.getID().equals("")) {
                            ToastUtils.showLong(Constant.NOTIAOMA);
                            return;
                        }
                        curTime = dateUtil.getYear_Day();
                        int daBaoZT = bean.getDaBaoZT();
                        int faFangJJBZ = bean.getFaFangJJBZ();
                        if (faFangJJBZ == 1) {//3. FaFangJJBZ=1已经发放，跳过
                            ToastUtils.showLong("物品包已发放");
                        } else if (daBaoZT == 8 &&bean.getFlowZT()==1) {// 第二次扫描时 1==清洗消毒一体
                            dialoag();
                            map_ma.put(bean.getID(), bean);//打包成功保存
                            setAdapter(map_ma);
//                            cheXiaoCaoZuo();
                        } else if (daBaoZT == 0) { //  正常发放
                            bean.setDaBaoZT(8);
                            bean.setDaBaoRen1(userMsg.getID());
//                            bean.setDaBaoRen2(userMsg.getYongHuXM());
                            bean.setDaBaoSJ1(curTime);
                            bean.setDaBaoSJ2(curTime);

                            isCancleDabao = 1;
                            dabaoOrCancle(bean);
                        } else if (daBaoZT == 8 && !cunFangWpb.equals("1")) {// 原来	cs_cunFangWpb = WSAgent.GetParaValue("物品包自动存库");
//                        } else if (bean.getXiaoDuZT().equals("8") && cunFangWpb.equals("2")) {//2.	cs_cunFangWpb = WSAgent.GetParaValue("物品包自动存库");
                            isCancleDabao = 0;
                            cheXiaoCaoZuo();//单个打包    撤销打包
                        } else if (bean.getXiaoDuZT() == 1) {  //消毒中
                            ToastUtils.showLong("灭菌中，不能撤销");
                        } else if (bean.getXiaoDuZT() == 8) {  //消毒中
                            ToastUtils.showLong("灭菌结束，可直接发放");
                        }
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

    /*             扫码         end                   */

    //打包或撤销操作
    private void dabaoOrCancle(final QXBean b) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String json = JSON.toJSONString(b, SerializerFeature.WriteNullStringAsEmpty);
        String s = Constant.DATATABLE + "[" + json + "]";//¤
        params.put(Constant.CODE, WSOpraTypeCode.修改物品包追踪记录);
        params.put(Constant.PARAMETER, s);
        log.e("修改物品包追踪记录--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("修改物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        if (isCancleDabao == 0) {
                            if (map_ma.containsKey(PackActivity.this.bean.getID())) {  //临时保存移除批次
                                map_ma.remove(PackActivity.this.bean.getID());
                            }
                            mSVProgressHUD.showSuccessWithStatus("撤销成功");
                            setAdapter(map_ma);
                        } else if (isCancleDabao == 1) {
                            tvSerach.setText(bean.getID());
                            map_ma.put(b.getID(), b);//打包成功保存
                            log.e(map_ma.size() + "打包成功保存");
                            if (b.getFlowZT()==0) {
                                mSVProgressHUD.showSuccessWithStatus("打包成功");
                            } else {
                                dialoag();
                            }
                            setAdapter(map_ma);
                        }
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

    private void bindCheBefore(String che) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + che;//¤
        params.put(Constant.CODE, WSOpraTypeCode.获取消毒车内物品);
        params.put(Constant.PARAMETER, s);
        log.e("获取消毒车内物品--" + s);

        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("获取消毒车内物品--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {

                        String result = hsms.getReturnJson();
                        List<QXBean> list_all = JSON.parseArray(result, QXBean.class);
                        if (list_all.size() == 0 || list_all.get(0).getID().equals("")) {  //消毒车是空的  继续绑定
                            List<QXBean> list_data = new ArrayList<>();
                            QXBean bean = null;
                            Set<String> key_ma = map_ma.keySet();
                            for (String test : key_ma) {
                                bean = map_ma.get(test);
                                list_data.add(bean);
                            }
                            bindXiaoDuChe(list_data);
                        } else {
                            List<QXBean> list_chewai = new ArrayList<>();
                            List<QXBean> list_cheli = new ArrayList<>();
                            for (int i = 0; i < list_all.size(); i++) {
                                if (map_ma.containsKey(list_all.get(i).getID())) {  //车内已经有此条码
                                    list_cheli.add(map_ma.get(list_all.get(i).getID()));
                                }
                            }

                            if (list_cheli.size() != 0 && list_cheli.size() < list_all.size()) {  //批量< 车内
                                dismissLoading();
                                dialogUnbindChe(list_cheli);
                            } else if (list_cheli.size() == map_ma.size()) {  //批量撤销
                                dismissLoading();
                                dialogUnbindChe(list_cheli);
                            } else {           //批量绑定
                                List<QXBean> list_data = new ArrayList<>();
                                QXBean bean = null;
                                Set<String> key_ma = map_ma.keySet();
                                for (String test : key_ma) {
                                    bean = map_ma.get(test);
                                    list_data.add(bean);
                                }
                                bindXiaoDuChe(list_data);
                            }
                        }
                    } else {
                        dismissLoading();
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取消毒车内物品);
                    }
                } else {
                    dismissLoading();
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.获取消毒车内物品);
                    log.e(Constant.HSMS);
                }
            }
        });
    }


    /**
     * 撤销车内的物品包绑定车
     *
     * @param list_pici
     */
    private void dialogUnbindChe(final List<QXBean> list_pici) {
        String ss = "";
        for (int i = 0; i < list_pici.size(); i++) {
            ss += list_pici.get(i).getID() + ",";
        }
        String ends = StringUtils.stringSubEnds(ss);
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setContentView(R.layout.dialog_del);//重点看这获取弹出框内的视图view
        TextView content = (TextView) dialog.findViewById(R.id.content);//重点看这行的Dialog
        content.setText("提示是否撤销" + ends + "对" + che + "的绑定");
        Button bt_yes = (Button) dialog.findViewById(R.id.bt_yes);//重点看这行的Dialog
        Button bt_no = (Button) dialog.findViewById(R.id.bt_no);//重点看这行的Dialog
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                unbindChe(list_pici);
            }
        });
        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    /**
     * 批次解绑车
     *
     * @param list_pici
     */
    private void unbindChe( List<QXBean> list_pici) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        QXBean bean = null;
        for (int i = 0; i < list_pici.size(); i++) {
            bean = list_pici.get(i);
            bean.setXiaoDuChe("");
            if (map_ma.containsKey(bean.getID())) {
                map_ma.put(bean.getID(), bean);
            }
        }
        log.e(map_ma.size() + "批次解绑车");
        String json = JSON.toJSONString(list_pici, SerializerFeature.WriteNullStringAsEmpty);
        String s = Constant.DATATABLE + json;//¤
        params.put(Constant.CODE, WSOpraTypeCode.修改物品包追踪记录);
        params.put(Constant.PARAMETER, s);
        log.e("批量或单个撤销车--" + s);

        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("批量或单个撤销车--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
//                        if (map_ispici.size() > 1) {
//                            ToastUtils.showLong("批量撤销成功");
//                        } else {
                        ToastUtils.showLong("成功撤销" + che + "的绑定");
//                        }
                        setAdapter(map_ma);
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

    private LinkedHashMap<String, QXBean> map_ma = new LinkedHashMap<>();//保存所扫过的物品包条码
    private LinkedHashMap<String, QXBean> map_allma = new LinkedHashMap<>();//前面条吗  后存车


    /**
     *
     */
    private void dialoag() {
        tvSerach.setText(bean.getID());
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
//        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setContentView(R.layout.dialog_tishi);//重点看这获取弹出框内的视图view
        TextView content = (TextView) dialog.findViewById(R.id.content);//重点看这行的Dialog
        content.setText(bean.getID() + " 清洗消毒已完成，打包后可直接发放 ");
        Button bt_yes = (Button) dialog.findViewById(R.id.bt_know);//重点看这行的Dialog

        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }


    private void cheXiaoCaoZuo() {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setContentView(R.layout.dialog_del);//重点看这获取弹出框内的视图view
        TextView content = (TextView) dialog.findViewById(R.id.content);//重点看这行的Dialog
        content.setText(bean.getID() + " 已打包，是撤销打包还是绑定消毒车?");
        Button bt_yes = (Button) dialog.findViewById(R.id.bt_yes);//重点看这行的Dialog
        Button bt_no = (Button) dialog.findViewById(R.id.bt_no);//重点看这行的Dialog
        bt_yes.setText("撤销");
        bt_no.setText("绑车");
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //      0撤销：	DaBaoZT=0，DaBaoRen1= ""，DaBaoRen2= ""，DaBaoSJ1= "1900-1-1 00:00:00"，DaBaoSJ2= "1900-1-1 00:00:00"
                tiaoma_chexiao = bean.getID();

                bean.setDaBaoZT(0);
                bean.setDaBaoRen1("");
                bean.setDaBaoRen2("");
                bean.setXiaoDuChe("");
                bean.setDaBaoSJ1("1900-1-1 00:00:00");
                bean.setDaBaoSJ2("1900-1-1 00:00:00");

                dabaoOrCancle(bean);
            }
        });
        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                map_ma.put(bean.getID(), bean);
                setAdapter(map_ma);
                log.e(map_ma.size() + "绑定消毒车");
                ToastUtils.showShort("物品包" + bean.getID() + "扫描成功");
                tvSerach.setText(bean.getID());
            }
        });
    }

    /**
     * 绑定消毒车，即批量 更改物品包追踪记录
     *
     * @param list
     */
    private void bindXiaoDuChe(List<QXBean> list) {
        QXBean bean = null;
        for (int i = 0; i < list.size(); i++) {
            bean = list.get(i);
            bean.setXiaoDuChe(che);
            map_ma.put(bean.getID(), bean);
        }
        log.e(map_ma.size() + "绑定消毒车");
        HashMap<String, Object> params = new HashMap<>();
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
                        setAdapter(map_ma);
                        ToastUtils.showLong("成功绑定到" + che + "号消毒车");
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

    private void setAdapter(LinkedHashMap<String, QXBean> map) {
        List<QXBean> list = getList(map);
//        if (s.equals("cxche")) {  //去除批次绑定的消毒车
//            Set<String> key = map.keySet();
//            QXBean bean = null;
//            for (String test : key) {
//                bean = map.get(test);
//                if (map.containsValue(bean)) {
//                    bean.setXiaoDuChe("");
//                } else {
//                    bean = map.get(test);
//                }
//                list.add(bean);
//            }
//        } else if (s.equals("dabao")) {  //单个打包成功
//            Set<String> key = map.keySet();
//            QXBean b = null;
//            for (String test : key) {
//                b = map.get(test);
//                if (map_ispici.containsValue(bean)) {
//                    b.setXiaoDuChe("");
//                } else {
//                    b = map.get(test);
//                }
//                list.add(b);
//            }
//        } else {
//            list = getList(map);
//        }
        String newJson = JSON.toJSONString(list, SerializerFeature.WriteNullStringAsEmpty);
        log.e("====" + newJson);
//        QXBean b = null;
//        for (int i = 0; i < list.size(); i++) {
//            b = list.get(i);
//            if (!getQxlxid_lx(bean.getQingXiLeiXing()).equals(Constant.QXXIAODUJI)) {
//                b.setXiaoDuChe(xdc);
//            }
//        }
        if (list.size() > 0) {//有数据
            llWudata.setVisibility(View.GONE);
            llData.setVisibility(View.VISIBLE);
            tvPackNum.setVisibility(View.VISIBLE);
            tvPackNum.setText("共" + list.size() + "个物品包");

            final List<QXBean> list_data = list;
            adapter = new MeiJunAdapter(mContext, R.layout.item_dabao_lv, list_data);
            rcyPack.setAdapter(adapter);
            rcyPack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    toOneWpbDetail(list_data.get(i));
                }
            });
            adapter.notifyDataSetInvalidated();

        } else {
            llWudata.setVisibility(View.VISIBLE);
            llData.setVisibility(View.GONE);
            tvPackNum.setVisibility(View.GONE);
        }
    }

    private List<QXBean> getList(Map<String, QXBean> map_ma) {
        Set<String> key = map_ma.keySet();
        for (String test : key) {
            bean = map_ma.get(test);
            map_allma.put(test, bean);
        }
        List<QXBean> list_data = new ArrayList<>();
        QXBean bean = null;
        Set<String> key_ma = map_allma.keySet();
        for (String test : key_ma) {
            bean = map_allma.get(test);
            list_data.add(bean);
        }
        return list_data;
    }
//    private List<QXBean> getList(Map<String, QXBean> map_data) {
//        List<QXBean> list_data = new ArrayList<>();
//        QXBean bean = null;
//        Set<String> key = map_data.keySet();
//        for (String test : key) {
//            bean = map_data.get(test);
//            if (getQxlxid_lx(bean.getQingXiLeiXing()).equals(Constant.QXXIAODUJI) && bean.getXiaoDuChe().equals("")) {
//                bean.setXiaoDuChe("");//消毒车
//            } else {
//                bean.setXiaoDuChe(che);//消毒车
//            }
//            list_data.add(bean);
//        }
//        return list_data;
//    }


    class MeiJunAdapter extends CommonAdapter<QXBean> {
        public MeiJunAdapter(Context context, int layoutId, List<QXBean> mDatas) {
            super(context, layoutId, mDatas);
        }

        @Override
        public void convert(ViewHolder holder, List<QXBean> t, int position, String tag) {
            QXBean bean = t.get(position);
            holder.setText(R.id.tv_ordernum, bean.getID());
            if (bean.getFlowZT()==1) {
                holder.setText(R.id.tv_leixing, "发放包");
            } else if (!StringUtils.stringNull(bean.getXiaoDuChe()).equals("")) {
                holder.setText(R.id.tv_leixing, bean.getXiaoDuChe() + "车");
            } else if (StringUtils.stringNull(bean.getXiaoDuChe()).equals("")) {
                holder.setText(R.id.tv_leixing, "");
            }
            holder.setText(R.id.tv_item_title, bean.getWuPinBMC());
        }
    }
}
