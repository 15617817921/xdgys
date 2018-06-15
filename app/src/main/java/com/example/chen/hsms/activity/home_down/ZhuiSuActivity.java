package com.example.chen.hsms.activity.home_down;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.chen.hsms.R;
import com.example.chen.hsms.activity.home_ohter.SearchImageAct;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.data.QXBean;
import com.example.chen.hsms.bean.data.QingXiJiLu;
import com.example.chen.hsms.bean.data.ZhuiSuData;

import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.NetUtils;
import com.example.chen.hsms.utils.StringUtils;
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

public class ZhuiSuActivity extends BaseActivity {

    @BindView(R.id.ll_data)
    LinearLayout llData;
    @BindView(R.id.rcy_zhuisu)
    RecyclerView rcyZhuisu;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_nodata)
    TextView tvNodata;
    @BindView(R.id.fl_wudata)
    FrameLayout flWudata;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_serach)
    TextView tvSerach;
    @BindView(R.id.fl_cerrent)
    FrameLayout flCerrent;


    private String tiaoma = "";
    private  String ZT = "";
    private String image = "";//当前


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        registerBoradcastReceiver();
        log.e("onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBarcodeReaderReceiver != null) {
            unregisterReceiver(mBarcodeReaderReceiver);
        }
        log.e("onDestroy");
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_zhui_su;
    }

    @Override
    protected void initView() {
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.state), 0);
    }

    @Override
    public void initDatas() {
        tvTitle.setText(getResources().getText(R.string.zhuisu));
    }

    @Override
    public void initListeners() {
    }

    @OnClick({R.id.iv_back, R.id.fl_cerrent})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.fl_cerrent:
                log.e(image);
                if (!image.equals("")) {
                    startImage(tvState.getText().toString().trim());
                }
                break;
        }
    }

    //========================================mobi扫描头驱动
    public void registerBoradcastReceiver() {
        log.e("广播注册");
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constant.ACTION_BARCODE_READER_VALUE);
        registerReceiver(mBarcodeReaderReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBarcodeReaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            log.e("接收广播" + action);
            if (action.equals(Constant.ACTION_BARCODE_READER_VALUE)) {
                String data = intent.getStringExtra(Constant.BORCODE_VALUE);
                log.e(data);
                scanChuLi(data);
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
            tiaoma = split[1];
            tvSerach.setText(tiaoma);
            first(tiaoma);
        } else {
            ToastUtils.showLong(Constant.NOWPBTM);
        }
    }

    /**
     * 根据条码查
     *
     * @param id
     */
    private void first( String id) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + "ID=" + id;
        params.put(Constant.CODE, WSOpraTypeCode.获取物品包追踪记录);
        params.put(Constant.PARAMETER, s);
        log.e("根据条码追溯是否发放--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e(hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        list_bean.clear();
                        list_bean = JSON.parseArray(result, QXBean.class);
                        QXBean bean = list_bean.get(0);
                        image = bean.getWupinBaoImage();
                        if (bean.getID().equals("")) {
                            dismissLoading();
                            ToastUtils.showLong(Constant.NOTIAOMA);
                        } else {
                            qingxiJilu(bean.getID());
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

    private List<QXBean> list_bean = new ArrayList<>();

    /**
     * 根据新条码查询清洗记录
     *
     * @param newma
     */
    private void qingxiJilu( String newma) {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + "" + "|('" + newma + "')";
        params.put(Constant.CODE, WSOpraTypeCode.获取清洗篮中物品包);
        params.put(Constant.PARAMETER, s);
        log.e("查询清洗记录--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e(hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<QingXiJiLu> list_qxjl = JSON.parseArray(result, QingXiJiLu.class);
                        list2 = zhengLi(list_bean, list_qxjl);//发放追溯
                        setAdapter(list2);
                    } else {
                        dismissLoading();
                        ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                        ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取清洗篮中物品包);
                    }
                }else {
                    ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.获取清洗篮中物品包);
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    private List<ZhuiSuData> list2 = new ArrayList<>();


    private List<ZhuiSuData> zhengLi(List<QXBean> list_xindata, List<QingXiJiLu> list_qxjl) {
        List<ZhuiSuData> list_data = new ArrayList<>();
        QXBean bean = list_xindata.get(0);
        String qdren = bean.getHuiShouQDR1();
        list_data.add(new ZhuiSuData("清点人", "", qdren, false));

        if (list_qxjl.size() == 1 && !list_qxjl.get(0).getID().equals("")) {
            QingXiJiLu qxjl = list_qxjl.get(0);

            String qxlan = qxjl.getQingXiLan();
            if (!qxlan.equals("") || qxlan != null) {
                list_data.add(new ZhuiSuData("清洗篮", qxlan, qdren, false));
                String qxjia = qxjl.getQingXiJia();
                if (!qxjia.equals("") || qxjia != null) {
                    list_data.add(new ZhuiSuData("清洗架", qxjia, "", false));
                    String qxji = bean.getQingXiJi();
                    String qxren = bean.getQingXiRen1();
                    if (!qxji.equals("") || qxji != null) {
                        list_data.add(new ZhuiSuData("清洗机", qxji, qxren, true));
                    }
                }
            }
            dabMjun(list_data, bean);

        } else if (list_qxjl.size() == 2) {
            String qxlan = "";
            String qxjia = "";
            String photo = "";//清洗机是否拍照  只要有一个就行
            for (int i = 0; i < list_qxjl.size(); i++) {
                QingXiJiLu qxjl = list_qxjl.get(i);
                qxlan += qxjl.getQingXiLan() + ",";
                if (!qxjl.getQingXiJia().equals("")) {
                    qxjia += qxjl.getQingXiJia() + ",";
                }
            }
            if (!qxlan.equals("")) {
                String lan = subString(qxlan);
                list_data.add(new ZhuiSuData("清洗篮", lan, qdren, false));//清洗机
                if (!qxjia.trim().equals(",,")) {
                    String jia = subString(qxjia);
                    if (jia.contains(",")) {
                        String[] split = jia.split(",");
                        if (split[0].equals(split[1])) {
                            list_data.add(new ZhuiSuData("清洗架", split[0], "", false));
                        } else {
                            list_data.add(new ZhuiSuData("清洗架", jia, "", false));
                        }
                    } else {
                        list_data.add(new ZhuiSuData("清洗架", jia, "", false));
                    }

                    String qxji = bean.getQingXiJi();

                    if (!qxji.equals("")) {
//                        String qxlx = getQxlxid_lx(bean.getQingXiLeiXing());
                        String qxren = bean.getQingXiRen1();
                        log.e(photo);
                        if (photo.equals("")) {
                            list_data.add(new ZhuiSuData("清洗机", qxji, qxren, false));
                        } else {
                            list_data.add(new ZhuiSuData("清洗机", qxji, qxren, true));
                        }
                        if (bean.getQingXiZT()==8) {
                            dabMjun(list_data, bean);
                        }
                    }
                }
            }

        }
        return list_data;
    }


    private void dabMjun(List<ZhuiSuData> list_data, QXBean bean) {
        try {
            int dbzt = bean.getDaBaoZT();
            String dbren = bean.getDaBaoRen1();
            if (dbzt==8) {     //   未打包  打包人
                list_data.add(new ZhuiSuData("打包人", "", dbren, false));
            }

            String che = bean.getXiaoDuChe();
            if (che == null || che.equals("")) {      //   消毒车
            } else {
                list_data.add(new ZhuiSuData("消毒车", che, dbren, false));
                int xdzt = bean.getXiaoDuZT();
                String mjguo = bean.getXiaoDuGuo();
                String xdren = bean.getXiaoDuRen1();
                list_data.add(new ZhuiSuData("灭菌锅", mjguo, xdren, false));
            }

            int ffzt = bean.getFaFangJJBZ();
            String ffren1 = bean.getFaFangJJR1();
            String ffren2 = bean.getFaFangJJR2();
            String ffren3 = bean.getFaFangJJR3();
            if (ffzt==1) {
                if (ffren1 != null || !ffren1.equals("")) {
                    list_data.add(new ZhuiSuData("供应室发放人", "", ffren1, false));
                    if (ffren2 != null || !ffren2.equals("")) {
                        list_data.add(new ZhuiSuData("中间发放人", "", ffren2, false));
                        if (ffren3 == null || ffren3.equals("")) {

                        } else {
                            list_data.add(new ZhuiSuData("科室接收人", "", ffren3, false));
                        }
                    }
                }
            } else {
                if (ffren1 != null & !ffren1.equals("")) {
                    list_data.add(new ZhuiSuData("供应室发放人", "", ffren1, false));
                    if (ffren2 != null & !ffren2.equals("")) {
                        list_data.add(new ZhuiSuData("中间发放人", "", ffren2, false));
                        if (ffren3 == null) {
                            return;
                        }
                        list_data.add(new ZhuiSuData("科室接收人", "", ffren3, false));
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
            log.e(e.getMessage());
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

    private void setAdapter(List<ZhuiSuData> list_current) {
        if (list_current != null && list_current.size() > 0) { //有数据
            flWudata.setVisibility(View.GONE);
            llData.setVisibility(View.VISIBLE);

            setText(list_bean, tvState, tvType);
            rcyZhuisu.setNestedScrollingEnabled(false);
            rcyZhuisu.setLayoutManager(new LinearLayoutManager(this));
            rcyZhuisu.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL, (int) getResources().getDimension(R.dimen.x1), ContextCompat.getColor(mContext, R.color.item_view)));
            rcyZhuisu.setAdapter(new CommonAdapter<ZhuiSuData>(mContext, R.layout.item_zhuisu_rv, list_current) {
                @Override
                protected void convert(ViewHolder holder, ZhuiSuData b, int position) {
                    setMenth(holder, b, 1);
                }
            });
        } else {
            flWudata.setVisibility(View.VISIBLE);
            llData.setVisibility(View.GONE);
        }
    }

    private void setMenth(ViewHolder holder,  ZhuiSuData b,  int i) {
        holder.setText(R.id.tv_shuju, b.getShujuName());//1008
        holder.setText(R.id.tv_qixiename, b.getShuju());//1008
        holder.setText(R.id.tv_name, getIdToName(b.getXmid()));
    }

    private void startImage(String id) {
        Intent intent = new Intent(mContext, SearchImageAct.class);
        intent.putExtra("tiaoma", id);
        startActivity(intent);
    }

    private void setText(List<QXBean> list_data, TextView tv_state, TextView tv) {
        try {
            QXBean b = list_data.get(0);
            tv_state.setText(b.getID());
            //物品包有照片
            if (!StringUtils.stringNull(b.getWupinBaoImage()).equals("")) {
                Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.pic);
                drawable.setBounds(0, 0, 32, 32);
                tv_state.setCompoundDrawables(null, null, drawable, null);
            }
            if (!StringUtils.stringNull(b.getHuiShouQDR1()).equals("")) {
                tv.setText(ZT + "清点中");
//            upUi(ZT + "清点中");
                if (b.getQingXiZT()==1) {
                    tv.setText(ZT + "清洗中");
                } else if (b.getQingXiZT()==8) {
                    tv.setText(ZT + "已清洗");
                    if (b.getDaBaoZT()==8) {
                        tv.setText(ZT + "已打包");
                        if (b.getFlowZT()==1) {
                            if (b.getFaFangJJBZ()==0) {
                                tv.setText(ZT + "未发放");
                            } else if (b.getFaFangJJBZ()==1) {
                                tv.setText(ZT + "发放中");
                            } else if (b.getFaFangJJBZ()==8) {
                                tv.setText(ZT + "已发放");
                            }
                        } else {
                            if (b.getXiaoDuZT() == 1) {
                                tv.setText(ZT + "灭菌中");
                            } else if (b.getXiaoDuZT() == 8) {
                                tv.setText(ZT + "已灭菌");
                                if (b.getFaFangJJBZ()==0l) {
                                    tv.setText(ZT + "未发放");
                                } else if (b.getFaFangJJBZ()==1) {
                                    tv.setText(ZT + "发放中");
                                } else if (b.getFaFangJJBZ()==8) {
                                    tv.setText(ZT + "已发放");
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.e(e.getMessage());
        }
    }
}



