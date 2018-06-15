package com.example.chen.hsms.activity.home_up;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.chen.hsms.R;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.data.WuPinBean;
import com.example.chen.hsms.bean.data.QXBean;
import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.DateUtil;
import com.example.chen.hsms.utils.NetUtils;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.webserviceutils.WebServiceUtils;
import com.example.chen.hsms.ws.Hsms;
import com.example.chen.hsms.ws.WSOpraTypeCode;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WuPinBaoActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_serach)
    TextView tvSerach;

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
    private String tiaoma;
    private String wupinbaoname;
    private DateUtil dateUtil;
    private QXBean bean = null;//追踪条码实体类

    @Override
    public int setLayoutId() {
        return R.layout.activity_wu_pin_bao;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.e("onCreate");
        ButterKnife.bind(this);
        registerBoradcastReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBarcodeReaderReceiver != null) {
            unregisterReceiver(mBarcodeReaderReceiver);
        }
    }

    @Override
    protected void initView() {
        log.e("initView");
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.state), 0);
    }


    @Override
    public void initDatas() {
        tvTitle.setText(getResources().getText(R.string.wupinbaomingxi));
    }


    @Override
    public void initListeners() {
        log.e("initListeners");
    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
            default:
                break;
        }
    }
    private void scanChuLi(String data) {
        String head = getHead(data);
        if (head.equals("")) {
            ToastUtils.showLong(Constant.TAGTOU);
            return;
        }
        if (head.equals(Constant.PTB)) {
            String[] split = data.split("@");
            tiaoma = split[1];
//            showLoading(qingqiu);
            dialog.show();
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    saoma(tiaoma);
                }
            });
        }
    }
    private void saoma(final String id) {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + "ID=" + id;
        params.put(Constant.CODE, WSOpraTypeCode.获取物品包追踪记录);
        params.put(Constant.PARAMETER, s);
        log.e("获取物品包追踪记录--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("获取物品包明细--"+hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<QXBean> list_saoma = JSON.parseArray(result, QXBean.class);
                        bean = list_saoma.get(0);
                        if (bean.getID().equals("")) {
                            dismissLoading();
                            ToastUtils.showLong("此条码已失效");
                            return;
                        }
                        wupinbaoname = bean.getWuPinBMC();
                        chaxun(bean.getWuPinBID());
                    } else {
                        dismissLoading();
                        ToastUtils.showLong(Constant.ERROR);
                    }
                }
            }
        });
    }

    /**
     * 根据物品包id查询详细数据
     *
     * @param id
     */
    public void chaxun(final String id) {
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
                    log.e("获取物品包明细--"+hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        list = JSON.parseArray(result, WuPinBean.class);
                        if (list.get(0).getID().equals("")) {
                            ToastUtils.showLong(Constant.NOTIAOMA);
                        }
                        setAdapter();
                    } else {
                        ToastUtils.showLong(Constant.ERROR);
                    }
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
        rcyWupin.setAdapter(new CommonAdapter<WuPinBean>(WuPinBaoActivity.this, R.layout.item_wupinbao, list) {
            @Override
            protected void convert(ViewHolder holder, final WuPinBean bean, final int position) {
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

    private BroadcastReceiver mBarcodeReaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            log.e("====BarcodeReaderActivity=====ACTION====" + action);
            if (action.equals(Constant.ACTION_BARCODE_READER_VALUE)) {
                String data = intent.getStringExtra(Constant.BORCODE_VALUE);
                scanChuLi(data);
            }
        }
    };



    @Override
    public void onBackPressed() {
        log.e("onBackPressed");
        finish();
    }


    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }
    //==================上面是mobi扫描头驱动=============================
}

//设置 Header 为 Material风格
//        refresh.setRefreshHeader(new DeliveryHeader(mContext));//热气球
//        refresh.setRefreshHeader(new MaterialHeader(mContext));//正常圆圈半箭头
//        refresh.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));
//        refresh.setRefreshHeader(new CircleHeader(mContext));//圆球
//        refresh.setRefreshHeader(new BezierRadarHeader(mContext));//时针圆球
//        refresh.setRefreshHeader(new CircleRefreshHeader(mContext));//圆球弹出掉落
//        refresh.setRefreshHeader(new FalsifyHeader(mContext));//
//        refresh.setRefreshHeader(new StoreHouseHeader(mContext));//字母变换
//设置 Footer 为 球脉冲
//        refresh.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
//        BallPulseFooter footer = new BallPulseFooter(mContext);
//        footer.setPrimaryColors(ContextCompat.getColor(mContext, R.color.black));
//        footer.setSpinnerStyle(SpinnerStyle.FixedBehind);
//        refresh.setRefreshFooter(footer);
