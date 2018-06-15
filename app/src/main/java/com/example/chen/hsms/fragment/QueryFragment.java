package com.example.chen.hsms.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.TimePickerView;
import com.example.chen.hsms.R;
import com.example.chen.hsms.activity.home_ohter.KeShiActivity;
import com.example.chen.hsms.adapter.FaFangZKAdapter;
import com.example.chen.hsms.application.MyApplication;
import com.example.chen.hsms.base.BaseFragment;
import com.example.chen.hsms.bean.data.KeShiName;
import com.example.chen.hsms.bean.local.MessageEvent;
import com.example.chen.hsms.bean.data.QXBean;
import com.example.chen.hsms.bean.local.QXBeanWP;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.DateUtil;
import com.example.chen.hsms.view.CoustomListView;
import com.example.chen.hsms.webservice.Webservice;
import com.example.chen.hsms.ws.Hsms;
import com.example.chen.hsms.ws.WSOpraTypeCode;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class QueryFragment extends BaseFragment implements View.OnClickListener {


    @BindView(R.id.tv_query_keshi)
    TextView tvQueryKeshi;
    @BindView(R.id.tv_query_date)
    TextView tvQueryDate;
    @BindView(R.id.fl_query_date)
    FrameLayout flDate;
    @BindView(R.id.rcy_query)
    RecyclerView rcyQuery;
    @BindView(R.id.fl_query_keshi)
    FrameLayout flQueryKeshi;
    Unbinder unbinder;
    @BindView(R.id.ll_data)
    LinearLayout llData;
    @BindView(R.id.fl_wuData)
    FrameLayout flWuData;
    private TimePickerView pickerView;
    private DateUtil dateUtil;

    private List<KeShiName> list_name;//登陆时缓存的所有科室信息
    //首次进入科室
    private String keshiName;
    private String keshiId;
    private String curDate;
    private FaFangZKAdapter adapter;
    private boolean isInit;
    public QueryFragment() {
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser&&isInit)//判断是否可见 并且是否已经初始化过，以防空指针
        {
            fafangState();
        } else {
            isInit = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query, container, false);
        unbinder = ButterKnife.bind(this, view);
        isInit =true;//设置已经初始化
        dateUtil = DateUtil.getInstance();
        init();
        initData();
        initListener();
        return view;
    }

    private void init() {
        list_name = MyApplication.getInstance().getList_keshi();
        keshiId = list_name.get(0).getID();
        keshiName = list_name.get(0).getName();
        tvQueryKeshi.setText(keshiId + " " + keshiName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //绑定事件接受
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void onMessageEvent(MessageEvent event) {//  net("0000210119");//0000210117
        log.e(event.message + " id  name " + event.wpblist + "  " + event.size);
        if (event.size == 2) {
            keshiId = event.message;
            tvQueryKeshi.setText(event.message + " " + event.wpblist);
            fafangState();
        }
    }

    private void initData() {
        curDate = dateUtil.getDate();
        tvQueryDate.setText(curDate);
        fafangState();
//        rcyQuery.setLayoutManager(new LinearLayoutManager(mContext));
//        rcyQuery.setAdapter(new FaFangTotalAdapter(mContext, list));
    }

    //当前发放情况
    private void fafangState() {
//        string¤where FaFangJJBZ='1' and FaFangJJSJ>='2017-9-1 00:00:00.000'
        showLoading("加载中...");
        new Webservice(mContext, handler_data, Constant.METHODNAME, true) {
            @Override
            public void addProgerty(SoapObject rpc) {
                String s = "string¤" + "where FaFangJJBZ='0' and LingYongKS='" + keshiId + "' and FaFangJJSJ>='" + curDate + " 00:00:00' and FaFangJJSJ<='" + curDate + " 23:59:59'";
                rpc.addProperty(Constant.CODE, WSOpraTypeCode.获取物品包追踪记录);//        7603
                rpc.addProperty(Constant.PARAMETER, s);
                log.e(s);
            }
        };
    }

    private List<QXBean> list_data = new ArrayList<>();
    private Hsms hs;
    private Handler handler_data = new Handler() {
        public void handleMessage(Message msg) {
            dismissLoading();
            switch (msg.what) {
                case 0:
                    hs = (Hsms) msg.obj;
                    if (hs.getReturnCode() == 0) {
                        String result = hs.getReturnJson();
                        list_data = JSON.parseArray(result, QXBean.class);
                        setAdapter(list_data);
                    }
                    log.e(hs.getResturnMsg() + "--" + hs.getReturnCode() + "--" + hs.getReturnID() + "--" + hs.getReturnJson());
                    break;
                default:
                    break;
            }
        }
    };

    private void setAdapter(List<QXBean> list_data) {
        if (list_data.size() == 0 || (list_data.size() == 1 && list_data.get(0).getID().equals(""))) {//null
            flWuData.setVisibility(View.VISIBLE);
            llData.setVisibility(View.GONE);
        } else {
            flWuData.setVisibility(View.GONE);
            llData.setVisibility(View.VISIBLE);


            ArrayList<ArrayList<QXBeanWP>> fianl_list = dateUtil.getFafangdan(list_data);

            int baoNum = 0;

            for (int i = 0; i < fianl_list.size(); i++) {
                ArrayList<QXBeanWP> qxBeanWPs = fianl_list.get(i);
                for (int j = 0; j < qxBeanWPs.size(); j++) {
                    QXBeanWP qxBeanWP = qxBeanWPs.get(j);
                    baoNum += qxBeanWP.getList().size();
                }
            }


            rcyQuery.setLayoutManager(new LinearLayoutManager(mContext));


            rcyQuery.setAdapter(new CommonAdapter<ArrayList<QXBeanWP>>(mContext, R.layout.item_fafang_total_rcy, fianl_list) {
                @Override
                protected void convert(ViewHolder holder, ArrayList<QXBeanWP> bean, int position) {
                    for (int i = 0; i < list_name.size(); i++) {
                        if (bean.get(position).getPc_id().equals(list_name.get(i).getID())) {
                            holder.setText(R.id.tv_keshi, list_name.get(i).getName());
                        }
                    }
                    CoustomListView listView = holder.getView(R.id.lv_fafang);
                    adapter = new FaFangZKAdapter(mContext, R.layout.rcy_item_lv, bean);
                    listView.setAdapter(adapter);
                }
            });
        }
    }

    private void initListener() {
        flQueryKeshi.setOnClickListener(this);
        flDate.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_query_keshi:
                Intent intent = new Intent(mContext, KeShiActivity.class);
                intent.putExtra("keshi", "2");
                startActivity(intent);
                break;
            case R.id.fl_query_date:
                showDateDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 时间控件
     */
    private void showDateDialog() {
        pickerView = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                curDate = dateUtil.dateFormater.format(date);
                tvQueryDate.setText(curDate);

                fafangState();
            }
        }).setDate(dateUtil.getCalendar(tvQueryDate.getText().toString()))
                .setTitleBgColor(Color.WHITE)//标题背景颜色 Night mode
                .setTitleColor(ContextCompat.getColor(mContext, R.color.text_color))//标题文字颜色)//标题文字颜色
                .setTitleText("日期")//标题文字
                .setCancelColor(ContextCompat.getColor(mContext, R.color.text_color_grey))
                .setSubmitColor(ContextCompat.getColor(mContext, R.color.state))
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)//默认全部显示
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isDialog(false)
//                .setDividerColor(ContextCompat.getColor(mContext,R.color.state))
                .build();
        pickerView.show();
    }
}
