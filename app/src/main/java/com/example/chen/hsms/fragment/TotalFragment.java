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
public class TotalFragment extends BaseFragment implements View.OnClickListener {


    @BindView(R.id.fl_ffhz_keshi)
    FrameLayout flKeshi;
    @BindView(R.id.fl_ffhz_date)
    FrameLayout flFfhzDate;
    @BindView(R.id.rcy_ffhz)
    RecyclerView rcyFfhz;
    @BindView(R.id.tv_show)
    TextView tvShow;
    @BindView(R.id.tv_total_keshi)
    TextView tvTotalKeshi;
    Unbinder unbinder;
    @BindView(R.id.tv_ffhz_date)
    TextView tvFfhzDate;
    @BindView(R.id.ll_data)
    LinearLayout llData;
    @BindView(R.id.fl_wuData)
    FrameLayout llWuData;
    //    private ArrayList<Bean> list;
    private DateUtil dateUtil;
    private TimePickerView pickerView;

    private String curDate;
    private FaFangZKAdapter adapter;
    private List<KeShiName> list_name = new ArrayList<>();
    //首次进入科室
    private String keshiId;
    private String keshiName;
    private boolean isInit;

    public TotalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_total, container, false);
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
        keshiId=list_name.get(0).getID();
        keshiName =list_name.get(0).getName();
        tvTotalKeshi.setText(keshiId + " " +keshiName);
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
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {//  net("0000210119");//0000210117
        log.e( event.message + " id  name " + event.wpblist+"  "+event.size);
        if(event.size==1){
            keshiId = event.message;
            tvTotalKeshi.setText(event.message + " " + event.wpblist);
            fafangState();
        }
    }


    private void initData() {
        curDate = dateUtil.getDate();
        tvFfhzDate.setText(curDate);

//        tvShow.setText("当天已发放" + list.size() + "个科室，" + num + "个包");
    }

    //当前发放情况
    private void fafangState() {
//        string¤where FaFangJJBZ='1' and FaFangJJSJ>='2017-9-1 00:00:00.000'
        showLoading("加载中...");
        new Webservice(mContext, handler_data, Constant.METHODNAME, true) {
            @Override
            public void addProgerty(SoapObject rpc) {
                String s = "string¤" + "where FaFangJJBZ='1' and LingYongKS='" + keshiId + "' and FaFangJJSJ>='" + curDate + " 00:00:00' and FaFangJJSJ<='" + curDate + " 23:59:59'";
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
                    log.e( hs.getResturnMsg() + "--" + hs.getReturnCode() + "--" + hs.getReturnID() + "--" + hs.getReturnJson());
                    break;
                default:
                    break;
            }
        }
    };

    private void setAdapter(List<QXBean> list_data) {
        if (list_data.size() == 0 || (list_data.size() == 1 && list_data.get(0).getID().equals(""))) {//null
            llWuData.setVisibility(View.VISIBLE);
            llData.setVisibility(View.GONE);
        } else {
            llWuData.setVisibility(View.GONE);
            llData.setVisibility(View.VISIBLE);


//            ArrayList<String> pici_list = new ArrayList<>();
//            ArrayList<ArrayList<QXBean>> result = new ArrayList<>();
//
//            for (int i = 0; i < list_data.size(); i++) {
//                String s = list_data.get(i).getLingYongKS();
//                if (!s.equals("")) {
//                    //先去匹配pici_list
//                    if (pici_list.size() == 0) {
//                        pici_list.add(s);
//                    }
//                    int status = 0;
//                    for (int j = 0; j < pici_list.size(); j++) {
//                        if (s.equals(pici_list.get(j)) && i != 0) {
//                            status = 1;
//                            break;
//                        }
//                    }
//                    if (status == 0) {
//                        pici_list.add(s);
//                        ArrayList<QXBean> pipei = new ArrayList<>();
//                        for (int z = 0; z < list_data.size(); z++) {
//                            if (s.equals(list_data.get(z).getLingYongKS())) {
//                                pipei.add(list_data.get(z));
//                            }
//                        }
//                        result.add(pipei);
//                    }
//                }
//
//
//            }
////            String json = JSON.toJSONString(result, SerializerFeature.WriteNullStringAsEmpty);
////            log.e( json + "");
//
//            ArrayList<ArrayList<QXBeanWP>> fianl_list = new ArrayList<>();
//
//            //循环第一次分类结果，每一次分类结果为ArrayList<QXBeanWP>
//            for (int z = 0; z < result.size(); z++) {
//                ArrayList<String> wpid_list = null;
//                wpid_list = new ArrayList<>();
//                //找出当前批次下，所有的物品ID保存盗wpid_list中
//                for (int i = 0; i < result.get(z).size(); i++) {
//                    String wpid = result.get(z).get(i).getWuPinBID();
//                    if (wpid_list.size() == 0) {
//                        wpid_list.add(wpid);
//                    } else {
//                        int status = 0;
//                        for (int j = 0; j < wpid_list.size(); j++) {
//                            if (wpid.equals(wpid_list.get(j))) {
//                                status = 1;
//                                break;
//                            }
//                        }
//                        if (status == 0) {
//                            wpid_list.add(wpid);
//                        }
//                    }
//                }
//                //循环wpid_list，去匹配每一条。没一个物品ID形成一个QXBeanWP
//                //每一个批次有很多个物品ID，所以形成ArrayList<QXBeanWP>
//                ArrayList<QXBeanWP> wp_result = new ArrayList<>();
//                for (int w = 0; w < wpid_list.size(); w++) {
//                    String wpid = wpid_list.get(w);
//
//                    ArrayList<QXBean> one_list = null;
//                    one_list = new ArrayList<>();
//                    one_list = result.get(z);
//
//                    QXBeanWP wp = new QXBeanWP();
//                    ArrayList<QXBean> result_list = null;
//                    result_list = new ArrayList<>();
//                    String wp_name = "";
//                    for (int k = 0; k < one_list.size(); k++) {
//                        if (wpid.equals(one_list.get(k).getWuPinBID())) {
//                            result_list.add(one_list.get(k));
//                            wp_name = one_list.get(k).getWuPinBMC();
//                        }
//                    }
//                    wp.setList(result_list);
//                    wp.setWp_id(wpid);
//                    wp.setWp_name(wp_name);
//                    wp.setPc_id(result.get(z).get(0).getLingYongKS());
//                    wp_result.add(wp);
//                }
//                //在同以批次下，添加分类好的wp_result
//                fianl_list.add(wp_result);
//            }
//            String json2 = JSON.toJSONString(fianl_list, SerializerFeature.WriteNullStringAsEmpty);
//            Log.e("第2次", json2 + "");
            ArrayList<ArrayList<QXBeanWP>> fianl_list = dateUtil.getFafangdan(list_data);

            int baoNum = 0;

            for (int i = 0; i < fianl_list.size(); i++) {
                ArrayList<QXBeanWP> qxBeanWPs = fianl_list.get(i);
                for (int j = 0; j < qxBeanWPs.size(); j++) {
                    QXBeanWP qxBeanWP = qxBeanWPs.get(j);
                    baoNum += qxBeanWP.getList().size();
                }
            }
            tvShow.setText("当天已发放" + fianl_list.size() + "个科室，" + baoNum + "个包");


            rcyFfhz.setLayoutManager(new LinearLayoutManager(mContext));


            rcyFfhz.setAdapter(new CommonAdapter<ArrayList<QXBeanWP>>(mContext, R.layout.item_fafang_total_rcy, fianl_list) {
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
        flKeshi.setOnClickListener(this);
        flFfhzDate.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_ffhz_date:
                showDateDialog();
                break;
            case R.id.fl_ffhz_keshi:
                Intent intent = new Intent(mContext, KeShiActivity.class);
                intent.putExtra("keshi", "1");
                startActivity(intent);
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
                tvFfhzDate.setText(curDate);
                fafangState();
            }
        }).setDate(dateUtil.getCalendar(tvFfhzDate.getText().toString()))
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
