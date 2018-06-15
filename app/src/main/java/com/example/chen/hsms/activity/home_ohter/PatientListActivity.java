package com.example.chen.hsms.activity.home_ohter;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.example.chen.hsms.R;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.local.Bean;
import com.example.chen.hsms.bean.local.MessagePatient;
import com.example.chen.hsms.bean.data.PatientMsg;
import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.webservice.Webservice;
import com.example.chen.hsms.ws.Hsms;
import com.example.chen.hsms.ws.WSOpraTypeCode;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientListActivity extends BaseActivity {

    @BindView(R.id.iv_brlb_back)
    ImageView ivBrlbBack;
    @BindView(R.id.et_brlb_searach)
    EditText etBrlbetSearach;
    //    @BindView(R.id.tv_brlb_cancel)
//    TextView tvBrlbCancel;
    @BindView(R.id.rcy_brlb)
    RecyclerView rcyBrlb;


    private List<Bean> list;
    private PatientListAdapter adapter;
    private String result = "";

    @Override
    public int setLayoutId() {
        return R.layout.activity_patient_list;
    }

    @Override
    protected void initView() {
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.brlb), 0);
    }

    @Override
    public void initDatas() {

//        if (!result.equals("")) {
//            list = new ArrayList<>();
//            for (int i = 0; i < 20; i++) {
//                list.add(new Bean("张三" + i, i + "67891"));
//            }
//
//        }
    }

    public void chaxun(View view) {
        search("90000243");
    }

    private void search(final String id) {
//        showLoading(qingqiu);
        new Webservice(mContext, handler_bingren, Constant.METHODNAME, true) {
            @Override
            public void addProgerty(SoapObject rpc) {
                String s = Constant.STRING + id;
                rpc.addProperty(Constant.CODE, WSOpraTypeCode.查找病人);
                rpc.addProperty(Constant.PARAMETER, s);
                log.e(s);
            }
        };

    }

    private Hsms hs;
    private List<PatientMsg> list_brmsg = new ArrayList<>();

    private Handler handler_bingren = new Handler() {

        public void handleMessage(Message msg) {
            dismissLoading();
            switch (msg.what) {
                case 0:
                    hs = (Hsms) msg.obj;
                    log.e(hs.getResturnMsg() + "--" + hs.getReturnCode() + "--" + hs.getReturnID() + "--" + hs.getReturnJson());
                    if (hs.getReturnCode() == 0) {
                        String result = hs.getReturnJson();
                        list_brmsg = JSON.parseArray(result, PatientMsg.class);

                        rcyBrlb.setLayoutManager(new LinearLayoutManager(mContext));
//                        rcyBrlb.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL, (int) getResources().getDimension(R.dimen.x1), ContextCompat.getColor(mContext, R.color.item_view)));
                        adapter = new PatientListAdapter(mContext, R.layout.item_brlist_lv, list_brmsg);
                        rcyBrlb.setAdapter(adapter);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    class PatientListAdapter extends CommonAdapter<PatientMsg> {

        public PatientListAdapter(Context context, int layoutId, List<PatientMsg> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, final PatientMsg bean, final int position) {
            holder.setText(R.id.tv_item_brlb_name, bean.getXINGMING());
            holder.setText(R.id.tv_item_brlb_number, bean.getJIUZHENKH());
            //1 男  2 女 3 不详
            holder.setText(R.id.tv_item_brlb_sex, isSex(bean.getXINGBIE()));

            holder.setOnClickListener(R.id.rl_item_brlb, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new MessagePatient(bean.getJIUZHENKH(), bean.getXINGMING(), isSex(bean.getXINGBIE())));
                    finish();
                }
            });
        }
    }

    @Override
    public void initListeners() {
        etBrlbetSearach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable e) {
                result = e.toString().trim();
                if (result.equals("") || result.equals(null)) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                } else {
//                    search();
                    initDatas();
                }
                ToastUtils.showShort(result);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_brlb_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_brlb_back:
                finish();
                break;
//            case R.id.tv_brlb_cancel:
//                ToastUtils.showLong("取消");
//                break;
        }
    }
    //========================================mobi扫描头驱动


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
                if (data.length() == 8) {  //物品包条码
//                    etFafang.setText(data);
//                    etFafang.setSelection(etFafang.length());//展示条码
//                    saoMa(data);
                    search(data);
                } else {
//                    ToastUtils.showLong(nowpbtm);
                }
                log.e(data);
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
}
