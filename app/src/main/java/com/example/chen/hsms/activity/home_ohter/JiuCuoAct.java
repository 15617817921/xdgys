package com.example.chen.hsms.activity.home_ohter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.chen.hsms.R;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.data.JiuCuo;
import com.example.chen.hsms.bean.local.OhterJiuCuo;
import com.example.chen.hsms.bean.data.Hsd_Mx;
import com.example.chen.hsms.bean.data.QXBean;
import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.NetUtils;
import com.example.chen.hsms.utils.StringUtils;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.view.CoustomListView;
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

public class JiuCuoAct extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_shoubao)
    TextView tvShoubao;
    @BindView(R.id.rcy_shoubao)
    RecyclerView rcyShoubao;
    @BindView(R.id.ll_data)
    LinearLayout llData;
    @BindView(R.id.ll_wudata)
    LinearLayout llWudata;
    @BindView(R.id.tv_serach)
    TextView tvSerach;
    private String wpbid;
    private JiuCuoAdapter adapter;
    private List<OhterJiuCuo> list_last = new ArrayList<>();
    private JiuCuoOhterAdapter ohterAdapter;
    private String newTiaoma = "";//选中的新条码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_jiu_cuo;
    }

    @Override
    protected void initView() {
        tvTitle.setText(getResources().getText(R.string.wpbjiucuo));
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.state), 0);
    }

    @Override
    public void initDatas() {
        huoSgouBao();
    }

    @Override
    public void initListeners() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBoradcastReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    /**
     * 扫码--物品包条码
     *
     * @param curtiaoma
     */
    private void saoma(String curtiaoma) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + "where ID='" + curtiaoma + "'";
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
                        List<QXBean> list_zzjl = JSON.parseArray(result, QXBean.class);
                        QXBean bean = list_zzjl.get(0);
                        if (bean.getID().equals("")) {
                            ToastUtils.showLong(Constant.NOTIAOMA);
                        } else {
                            getHuisdMX(bean, huiShouDID);
//                            list_zzjl.get(0).setOldZzjlID(curtiaoma);
//                            list_zzjl.get(0).setID(newTiaoma);
//                            Hsd_Mx hsd_mx = null;
//                            String LiShiZZJLID = "";
//                            for (int i = 0; i < listmx.size(); i++) {
//                                hsd_mx = listmx.get(i);
//                                if (hsd_mx.getWuPinBID().equals(wpbid)) {
//                                    String lszzjl = hsd_mx.getLiShiZZJLID();
//                                    String[] split = lszzjl.split(Constant.FUHAO);
//                                    for (int j = split.length; j > 0; j--) {
//                                        if (split[j].equals("0")) {
//                                            split[j] = curtiaoma;
//                                            break;
//                                        }
//                                    }
//                                    for (int j = 0; j < split.length; j++) {
//                                        LiShiZZJLID += split[j];
//                                    }
//                                    hsd_mx.setLiShiZZJLID(LiShiZZJLID);
//                                    updata(list_zzjl, hsd_mx);
//                                }
//                            }
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

    /**
     * 获取所有首次包
     */
    private void huoSgouBao() {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING;
        params.put(Constant.CODE, WSOpraTypeCode.获取首次生成物品包);
        params.put(Constant.PARAMETER, s);
        log.e("获取首次生成物品包--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();//扫描之后跳转
                if (hsms != null) {
                    log.e("获取物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<JiuCuo> list_shoubao = JSON.parseArray(result, JiuCuo.class);
                        if (list_shoubao.get(0).getID().equals("")) {
                            ToastUtils.showLong("暂无首次包");
                        } else {
                            setAdapter(list_shoubao);
                        }
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取首次生成物品包);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.获取首次生成物品包);
                    log.e(Constant.HSMS);
                }
            }
        });
    }


    private String huiShouDID = "";//单选的回收单id

    private void getHuisdMX( final QXBean qxbean, String hsdid) {    //442   367
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
         String s = Constant.STRING + "a.HuiShouDID='" + hsdid + "'";
        params.put(Constant.CODE, WSOpraTypeCode.获取回收单对应明细);
        params.put(Constant.PARAMETER, s);
        log.e("获取回收单对应明细--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("获取回收单对应明细--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {  //回收单明细已经有new
                        String result = hsms.getReturnJson();
                        List<Hsd_Mx> list_mx = JSON.parseArray(result, Hsd_Mx.class);
                        for (int i = 0; i < list_mx.size(); i++) {   //回收单明细添加 Lishizzjl   追踪记录   旧条码   旧条码追踪没用
                            Hsd_Mx beanMx = list_mx.get(i);
                            String lsZzjl = StringUtils.stringNull(beanMx.getLiShiZZJLID());
                            String newZzjl = StringUtils.stringNull(beanMx.getNewZZJLID());
                            int num = list_mx.get(i).getKeShiSL();
                            if (num == 1) {
                                if (lsZzjl.equals("0")) {
                                    beanMx.setLiShiZZJLID(qxbean.getOldZzjlID());

                                    qxbean.setOldZzjlID(qxbean.getID());
                                    qxbean.setID(newTiaoma);
                                    updata(qxbean, beanMx);
                                    return;
                                }
                            } else if (num > 1) {  //
                                if (lsZzjl.equals("0")) {
                                    beanMx.setLiShiZZJLID(qxbean.getOldZzjlID());

                                    qxbean.setOldZzjlID(qxbean.getID());
                                    qxbean.setID(newTiaoma);
                                    updata(qxbean, beanMx);
                                    return;
                                } else if (lsZzjl.contains("#")) {
                                    String[] split = lsZzjl.split("#");
                                    String liShiZZJLID = "";
                                    for (int j = 0; j < split.length; j++) {
                                        if (split[split.length - 1 - j].equals("0")) {
                                            split[split.length - 1 - j] = qxbean.getID();
                                            break;
                                        }
                                    }
                                    for (int j = 0; j < split.length; j++) {
                                        liShiZZJLID += split[j] + "#";
                                    }
                                    if (liShiZZJLID.endsWith("#")) {
                                        liShiZZJLID = liShiZZJLID.substring(0, liShiZZJLID.length() - 1);
                                    }
                                    beanMx.setLiShiZZJLID(liShiZZJLID);

                                    qxbean.setOldZzjlID(qxbean.getID());
                                    qxbean.setID(newTiaoma);

                                    updata(qxbean, beanMx);
                                    return;
                                }
                            }
                        }
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取回收单对应明细);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.获取回收单对应明细);
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    private void updata(QXBean bean, Hsd_Mx hsdMx) {
        String json_zzjl = JSON.toJSONString(bean, SerializerFeature.WriteNullStringAsEmpty);
        String json_hsdmx = JSON.toJSONString(hsdMx, SerializerFeature.WriteNullStringAsEmpty);
        log.e(json_zzjl + "--" + json_hsdmx);
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.DATASET + "[" + json_zzjl + "]§[" + json_hsdmx + "]";
        params.put(Constant.CODE, WSOpraTypeCode.绑定旧物品包和新条码);
        params.put(Constant.PARAMETER, s);
        log.e("绑定旧物品包和新条码--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("绑定旧物品包和新条码--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        ToastUtils.showLong(newTiaoma + "物品包修改成功");
                        huoSgouBao();
                    } else {
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取回收单对应明细);
                    }
                } else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.获取回收单对应明细);
                    log.e(Constant.HSMS);
                }
            }
        });
    }
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
//            String tiama= split[1];
            tvSerach.setText(split[1]);
            saoma(split[1]);
//            if (!huiShouDID.equals("")) {
//                getHuisdMX(huiShouDID, split[1]);
//            } else {
//                ToastUtils.showLong("请选择要更改的物品包");
//            }
        } else {
            ToastUtils.showLong(Constant.NOWPBTM);
        }
    }

    /**
     * 对拿到的物品包进行分类
     *
     * @param list_huoqpici
     * @return
     */
    public List<OhterJiuCuo> getJiuCuo(List<JiuCuo> list_huoqpici) {
        ArrayList<String> keshi_list = new ArrayList<>();
        List<List<JiuCuo>> result = new ArrayList<>();

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
                List<JiuCuo> pipei = new ArrayList<>();
                for (int z = 0; z < list_huoqpici.size(); z++) {
                    if (s.equals(list_huoqpici.get(z).getHuiShouDID())) {
                        pipei.add(list_huoqpici.get(z));
                    }
                }
                result.add(pipei);
            }
        }
        List<List<OhterJiuCuo>> fianl_list = new ArrayList<>();

        //循环第一次分类结果，每一次分类结果为ArrayList<QXBeanWP>
        for (int z = 0; z < result.size(); z++) {
            ArrayList<String> wpid_list = null;
            wpid_list = new ArrayList<>();
            //找出当前频次下，所有的物品ID保存盗wpid_list中
            for (int i = 0; i < result.get(z).size(); i++) {
                String wpid = result.get(z).get(i).getHuiShouDID();
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
            List<OhterJiuCuo> wp_result = new ArrayList<>();
            for (int w = 0; w < wpid_list.size(); w++) {
                String wpid = wpid_list.get(w);

                List<JiuCuo> one_list = new ArrayList<>();
                one_list = result.get(z);

                OhterJiuCuo wp = new OhterJiuCuo();
                List<JiuCuo> result_list = new ArrayList<>();
                String time = "";
                String hsdid = "";
                String keshi = "";
//                int num = 0;
                for (int k = 0; k < one_list.size(); k++) {
                    if (wpid.equals(one_list.get(k).getHuiShouDID())) {
                        result_list.add(one_list.get(k));
                        time = one_list.get(k).getShenQingSJ();
                        hsdid = one_list.get(k).getHuiShouDID();
                        keshi = one_list.get(k).getShenQingKS();
                    }
                }
                wp.setList_jiucuo(result_list);//数量
                wp.setHsd_time(time);//物品包id
                wp.setKeshi(keshi);
                wp.setHsd_id(hsdid);
                wp_result.add(wp);
            }
            fianl_list.add(wp_result);
        }
        List<OhterJiuCuo> list_last = new ArrayList<>();
        for (int i = 0; i < fianl_list.size(); i++) {
            List<OhterJiuCuo> cuoList = fianl_list.get(i);
            for (int j = 0; j < cuoList.size(); j++) {
                list_last.add(cuoList.get(j));
            }
        }
        return list_last;
    }

    private void setAdapter( List<JiuCuo> list) {
        if (list.size() == 0 || list.get(0).getHuiShouDID().equals("") || list == null) {
            llData.setVisibility(View.GONE);
            llWudata.setVisibility(View.VISIBLE);
        } else {
            llData.setVisibility(View.VISIBLE);
            llWudata.setVisibility(View.GONE);

            tvShoubao.setText("共" + list.size() + "个首次物品包");

            list_last = getJiuCuo(list);
//            String json_huisd = JSON.toJSONString(list_last, SerializerFeature.WriteNullStringAsEmpty);
//            log.e(json_huisd);
            rcyShoubao.setNestedScrollingEnabled(false);
            rcyShoubao.setLayoutManager(new LinearLayoutManager(this));
            ohterAdapter = new JiuCuoOhterAdapter(mContext, R.layout.item_jiucuo_recy, list_last);
            rcyShoubao.setAdapter(ohterAdapter);
        }
    }

    /**
     * 外层适配器
     */
    class JiuCuoOhterAdapter extends CommonAdapter<OhterJiuCuo> {
        public JiuCuoOhterAdapter(Context context, int layoutId, List<OhterJiuCuo> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder,  OhterJiuCuo bean, int position) {
            holder.setText(R.id.tv_keshiname, bean.getKeshi() + "   (" + bean.getHsd_id() + ")");
            holder.setText(R.id.tv_time, bean.getHsd_time());

            CoustomListView rcy = holder.getView(R.id.rcy_jiucuo);
            final List<JiuCuo> list_jiucuo = bean.getList_jiucuo();
            adapter = new JiuCuoAdapter(mContext, R.layout.item_jiucuo_rcy_gc, list_jiucuo);
            rcy.setAdapter(adapter);
            rcy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    list_jiucuo.get(i).setChecked(true);//点击的设为选中
                    updataData(list_jiucuo.get(i));
                    wpbid = list_jiucuo.get(i).getWupinBID();
                    huiShouDID = list_jiucuo.get(i).getHuiShouDID();
                    newTiaoma = list_jiucuo.get(i).getID();
                }
            });
        }
    }
//  id  old   zzjl   前
//    mxid  oldzzjl  newzzjl

    /**
     * 内层适配器
     */
    class JiuCuoAdapter extends com.example.chen.hsms.base.CommonAdapter<JiuCuo> {

        public JiuCuoAdapter(Context context, int layoutId, List<JiuCuo> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(com.example.chen.hsms.base.ViewHolder holder, List<JiuCuo> t, int position, String tag) {
            JiuCuo bean = t.get(position);
            holder.setText(R.id.tv_shoucima, bean.getID());
            holder.setText(R.id.tv_shoucib, bean.getWupinBMC());
            ImageView image = holder.getView(R.id.iv_choose);

            if (bean.isChecked()) {//状态选中
                image.setImageResource(R.drawable.btn_choose_on);
            } else {
                image.setImageResource(R.drawable.btn_choose_null);
            }
        }
    }

    /**
     * 更改数据源 实现单选操作
     *
     * @param bean
     */
    private void updataData(JiuCuo bean) {
        try {
            String hsdid = bean.getHuiShouDID();
            String tiaoma = bean.getID();
            if (list_last.size() > 0 && list_last != null) {
                for (int i = 0; i < list_last.size(); i++) {
                    OhterJiuCuo ohterJC = list_last.get(i);
                    List<JiuCuo> list_jiucuo = ohterJC.getList_jiucuo();
                    if (ohterJC.getHsd_id().equals(hsdid)) {
                        for (int j = 0; j < list_jiucuo.size(); j++) {
                            if (list_jiucuo.get(j).getID().equals(tiaoma)) {
                                list_jiucuo.get(j).setChecked(true);
                            } else {
                                list_jiucuo.get(j).setChecked(false);
                            }
                        }
                    } else {
                        for (int j = 0; j < list_jiucuo.size(); j++) {
                            list_jiucuo.get(j).setChecked(false);
                        }
                    }
                }
                ohterAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            log.e("更改数据源 实现单选操作" + e.getMessage());
        }
    }

    private String ss = "[\n" +
            "    {\n" +
            "        \"ID\": \"0000210372\",\n" +
            "        \"WupinBID\": \"S1161\",\n" +
            "        \"WupinBMC\": \"广泛另包(无纺布)\",\n" +
            "        \"ShenQingKSID\": \"STS1\",\n" +
            "        \"ShenQingKS\": \"手术室\",\n" +
            "        \"HuiShouDID\": \"S7456\",\n" +
            "        \"ShenQingSJ\": \"2018-03-21 22:18:54\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"ID\": \"0000210373\",\n" +
            "        \"WupinBID\": \"S1161\",\n" +
            "        \"WupinBMC\": \"广泛另包(无纺布)\",\n" +
            "        \"ShenQingKSID\": \"STS1\",\n" +
            "        \"ShenQingKS\": \"手术室\",\n" +
            "        \"HuiShouDID\": \"S7456\",\n" +
            "        \"ShenQingSJ\": \"2018-03-21 22:18:54\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"ID\": \"0000210374\",\n" +
            "        \"WupinBID\": \"S1161\",\n" +
            "        \"WupinBMC\": \"广泛另包(无纺布)\",\n" +
            "        \"ShenQingKSID\": \"STS1\",\n" +
            "        \"ShenQingKS\": \"手术室\",\n" +
            "        \"HuiShouDID\": \"S7457\",\n" +
            "        \"ShenQingSJ\": \"2018-03-21 22:19:01\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"ID\": \"0000210375\",\n" +
            "        \"WupinBID\": \"S1161\",\n" +
            "        \"WupinBMC\": \"广泛另包(无纺布)\",\n" +
            "        \"ShenQingKSID\": \"STS1\",\n" +
            "        \"ShenQingKS\": \"手术室\",\n" +
            "        \"HuiShouDID\": \"S7457\",\n" +
            "        \"ShenQingSJ\": \"2018-03-21 22:19:01\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"ID\": \"0000210376\",\n" +
            "        \"WupinBID\": \"S1161\",\n" +
            "        \"WupinBMC\": \"广泛另包(无纺布)\",\n" +
            "        \"ShenQingKSID\": \"STS1\",\n" +
            "        \"ShenQingKS\": \"手术室\",\n" +
            "        \"HuiShouDID\": \"S7459\",\n" +
            "        \"ShenQingSJ\": \"2018-03-22 11:48:39\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"ID\": \"0000210377\",\n" +
            "        \"WupinBID\": \"S1161\",\n" +
            "        \"WupinBMC\": \"广泛另包(无纺布)\",\n" +
            "        \"ShenQingKSID\": \"STS1\",\n" +
            "        \"ShenQingKS\": \"手术室\",\n" +
            "        \"HuiShouDID\": \"S7459\",\n" +
            "        \"ShenQingSJ\": \"2018-03-22 11:48:39\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"ID\": \"0000210378\",\n" +
            "        \"WupinBID\": \"S1161\",\n" +
            "        \"WupinBMC\": \"广泛另包(无纺布)\",\n" +
            "        \"ShenQingKSID\": \"STS1\",\n" +
            "        \"ShenQingKS\": \"手术室\",\n" +
            "        \"HuiShouDID\": \"S7459\",\n" +
            "        \"ShenQingSJ\": \"2018-03-22 11:48:39\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"ID\": \"0000210379\",\n" +
            "        \"WupinBID\": \"S1161\",\n" +
            "        \"WupinBMC\": \"广泛另包(无纺布)\",\n" +
            "        \"ShenQingKSID\": \"STS1\",\n" +
            "        \"ShenQingKS\": \"手术室\",\n" +
            "        \"HuiShouDID\": \"S7460\",\n" +
            "        \"ShenQingSJ\": \"2018-03-23 10:07:59\"\n" +
            "    }\n" +
            "]";
}
