package com.example.chen.hsms.activity.home_ohter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.chen.hsms.R;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.data.WuPinBean;
import com.example.chen.hsms.bean.data.Hsd_Mx;
import com.example.chen.hsms.bean.data.QXBean;
import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.DateUtil;
import com.example.chen.hsms.utils.NetUtils;
import com.example.chen.hsms.utils.ToastUtils;
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

//清洗篮维护
public class WuPinBaoActivity2 extends BaseActivity {

    @BindView(R.id.iv_wupbmingx_back)
    ImageView ivWupbmingxBack;
    @BindView(R.id.tv_wpbmx_saoma)
    TextView tvTakePhoto;
    @BindView(R.id.tv_search)
    EditText etSearch;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.fl_show)
    FrameLayout flShow;
    @BindView(R.id.rcy_wupin)
    RecyclerView rcyWupin;

    private List<WuPinBean> list;//真数据
    private int total = 0;//总数量


    private Intent intent;
    private List<Hsd_Mx> list_all_wpb = new ArrayList<>();
    private DateUtil dateUtil;

    private String wupinbaoname = "";
    private String wupinbaoid = "";
    private String tioma = "";


    @Override
    public int setLayoutId() {
        return R.layout.activity_wu_pin_bao2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.e("onCreate");
        ButterKnife.bind(this);
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
    protected void initView() {
        log.e("initView");
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.state), 0);
        dateUtil = DateUtil.getInstance();
    }


    @Override
    public void initDatas() {
        log.e("initDatas");//tag
        intent = getIntent();
        String tag = intent.getStringExtra("tag");//  1-->各个item点击进入  2-->清点 ==扫码进入

        tioma = intent.getStringExtra("id");
        wupinbaoid = intent.getStringExtra("wupinbaoid");
        wupinbaoname = intent.getStringExtra("wupinbaoname");

        tvType.setText(wupinbaoname);//物品包名称
        etSearch.setText(tioma);
        if (tag.equals("1")) {
//            showLoading(qingqiu);
            chaxun(wupinbaoid);
        }
    }

    @Override
    public void initListeners() {
        log.e("initListeners");
    }

    @OnClick({R.id.iv_wupbmingx_back, R.id.tv_wpbmx_saoma})
    public void onViewClicked(View view) {
        log.e("onViewClicked");

        switch (view.getId()) {
            case R.id.iv_wupbmingx_back:
                finish();
                break;
            case R.id.tv_wpbmx_saoma:

                break;
            default:
                break;
        }
    }

    public void chaxun( String id) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + "|WuPinBaoID='" + id + "'";
        params.put(Constant.CODE, WSOpraTypeCode.获取物品包明细);
        params.put(Constant.PARAMETER, s);
        log.e("获取物品包明细--" + s);

        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("获取物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        list = JSON.parseArray(result, WuPinBean.class);
                        setAdapter();
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
    private void setAdapter() {
        total = 0;
        flShow.setVisibility(View.VISIBLE);
        for (int i = 0; i < list.size(); i++) {
            total += list.get(i).getShuLiang();
        }
        tvType.setText(wupinbaoname);//物品包名称
        tvNumber.setText("共" + total + "件");//物品包里面

        rcyWupin.setLayoutManager(new LinearLayoutManager(this));
        rcyWupin.setAdapter(new CommonAdapter<WuPinBean>(this, R.layout.item_wupinbao, list) {
            @Override
            protected void convert(ViewHolder holder,  WuPinBean bean,  int position) {
                holder.setText(R.id.tv_item_name, bean.getQiXieMC());
                holder.setText(R.id.tv_item_num, bean.getShuLiang() + "");
                holder.setText(R.id.tv_wpbmx_dw, getDanWei(bean.getDanWei()));
            }
        });
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
                 String data = intent.getStringExtra(Constant.BORCODE_VALUE);
                log.e(data);
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
            tioma = split[1];
            showLoading("请求中...");
            saoma(data);
        } else {
            ToastUtils.showLong(Constant.NOWPBTM);
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


    private void saoma( String id) {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + "ID=" + id;
        params.put(Constant.CODE, WSOpraTypeCode.获取物品包追踪记录);
        params.put(Constant.PARAMETER, s);
        log.e("获取物品包追踪记录--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
                    @Override
                    public void callBack(Hsms hsms) {
                        dismissLoading();
                        if (hsms != null) {
                            log.e("获取清洗篮中物品包--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                            if (hsms.getReturnCode() == 0) {
                                String result = hsms.getReturnJson();
                                List<QXBean> list_saoma = JSON.parseArray(result, QXBean.class);
                                bean = list_saoma.get(0);
                                if (bean.getID().equals("")) {
                                    dismissLoading();
                                    ToastUtils.showLong("无效的条码");
                                    return;
                                }
                                wupinbaoname = bean.getWuPinBMC();
                                tioma = bean.getID();

                                tvType.setText(wupinbaoname);//物品包名称
                                etSearch.setText(tioma);

                                chaxun(bean.getWuPinBID());

                            } else {
                                ShowDialog.setShowErro(mContext, hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                                ToastUtils.showLong(Constant.NODATA + WSOpraTypeCode.获取物品包追踪记录);
                            }
                        } else {
                            ToastUtils.showLong(Constant.HSMS + WSOpraTypeCode.获取物品包追踪记录);
                            log.e(Constant.HSMS);
                        }
                    }
                }

        );
    }

    private QXBean bean = null;//追踪条码实体类
}
