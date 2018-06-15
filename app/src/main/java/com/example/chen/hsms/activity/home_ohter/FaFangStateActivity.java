package com.example.chen.hsms.activity.home_ohter;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.chen.hsms.R;
import com.example.chen.hsms.adapter.FaFangZKAdapter;
import com.example.chen.hsms.application.MyApplication;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.data.KeShiName;
import com.example.chen.hsms.bean.data.QXBean;
import com.example.chen.hsms.bean.local.QXBeanWP;
import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.DateUtil;
import com.example.chen.hsms.utils.NetUtils;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.view.CoustomListView;
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

public class FaFangStateActivity extends BaseActivity {
    @BindView(R.id.rcy_fafang_state)
    RecyclerView rcyFafangState;
    @BindView(R.id.tv_show)
    TextView tvShow;
    @BindView(R.id.iv_fafang_back)
    ImageView ivFafangBack;

    @BindView(R.id.ll_data)
    LinearLayout llData;
    @BindView(R.id.ll_wuData)
    FrameLayout llWuData;

    private FaFangZKAdapter adapter;
    private List<KeShiName> list_name = new ArrayList<>();
    private MyApplication app;
    private DateUtil dateUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }


    @Override
    public int setLayoutId() {
        return R.layout.activity_fa_fang_state;
    }

    @Override
    protected void initView() {

//        Bundle bundle = getIntent().getExtras();
//        list_data = (List<QXBean>) bundle.getSerializable("list_data");
//        String json = JSON.toJSONString(list_data, SerializerFeature.WriteNullStringAsEmpty);

        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.liang),0);
        app = MyApplication.getInstance();
        dateUtil = DateUtil.getInstance();
        list_name = app.getList_keshi();
    }


    @Override
    public void initDatas() {
        fafangState(dateUtil.getDate() + " 00:00:00");

    }

    //当天发放情况
    private void fafangState( String data) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = "string¤" + "where FaFangJJBZ='8' and FaFangJJSJ>='" + data + "'";
         params.put(Constant.CODE, WSOpraTypeCode.获取物品包追踪记录);//        7603
         params.put(Constant.PARAMETER, s);
        log.e("获取物品包追踪记录--"+s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e(hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        list_data = JSON.parseArray(result, QXBean.class);
                        setAdapter();
                    } else {
                        ToastUtils.showLong(Constant.MSGERRO);
                    }
                }
            }
        });
    }

    private List<QXBean> list_data = new ArrayList<>();

    private void setAdapter() {
        if (list_data.size() == 0 || (list_data.size() == 1 && list_data.get(0).getID().equals(""))) {//null
            llWuData.setVisibility(View.VISIBLE);
            llData.setVisibility(View.GONE);
        } else {
            llWuData.setVisibility(View.GONE);
            llData.setVisibility(View.VISIBLE);


            ArrayList<String> pici_list = new ArrayList<>();
            ArrayList<ArrayList<QXBean>> result = new ArrayList<>();

            for (int i = 0; i < list_data.size(); i++) {
                String s = list_data.get(i).getLingYongKS();
                if (!s.equals("")) {
                    //先去匹配pici_list
                    if (pici_list.size() == 0) {
                        pici_list.add(s);
                    }
                    int status = 0;
                    for (int j = 0; j < pici_list.size(); j++) {
                        if (s.equals(pici_list.get(j)) && i != 0) {
                            status = 1;
                            break;
                        }
                    }
                    if (status == 0) {
                        pici_list.add(s);
                        ArrayList<QXBean> pipei = new ArrayList<>();
                        for (int z = 0; z < list_data.size(); z++) {
                            if (s.equals(list_data.get(z).getLingYongKS())) {
                                pipei.add(list_data.get(z));
                            }
                        }
                        result.add(pipei);
                    }
                }

            }
            String json = JSON.toJSONString(result, SerializerFeature.WriteNullStringAsEmpty);
            log.e( json + "");

            ArrayList<ArrayList<QXBeanWP>> fianl_list = new ArrayList<>();

            //循环第一次分类结果，每一次分类结果为ArrayList<QXBeanWP>
            for (int z = 0; z < result.size(); z++) {
                ArrayList<String> wpid_list = null;
                wpid_list = new ArrayList<>();
                //找出当前批次下，所有的物品ID保存盗wpid_list中
                for (int i = 0; i < result.get(z).size(); i++) {
                    String wpid = result.get(z).get(i).getWuPinBID();
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
                //循环wpid_list，去匹配每一条。没一个物品ID形成一个QXBeanWP
                //每一个批次有很多个物品ID，所以形成ArrayList<QXBeanWP>
                ArrayList<QXBeanWP> wp_result = new ArrayList<>();
                for (int w = 0; w < wpid_list.size(); w++) {
                    String wpid = wpid_list.get(w);

                    ArrayList<QXBean> one_list = null;
                    one_list = new ArrayList<>();
                    one_list = result.get(z);

                    QXBeanWP wp = new QXBeanWP();
                    ArrayList<QXBean> result_list = null;
                    result_list = new ArrayList<>();
                    String wp_name = "";
                    for (int k = 0; k < one_list.size(); k++) {
                        if (wpid.equals(one_list.get(k).getWuPinBID())) {
                            result_list.add(one_list.get(k));
                            wp_name = one_list.get(k).getWuPinBMC();
                        }
                    }
                    wp.setList(result_list);
                    wp.setWp_id(wpid);
                    wp.setWp_name(wp_name);
                    wp.setPc_id(result.get(z).get(0).getLingYongKS());
                    wp_result.add(wp);
                }
                //在同以批次下，添加分类好的wp_result
                fianl_list.add(wp_result);

            }
            String json2 = JSON.toJSONString(fianl_list, SerializerFeature.WriteNullStringAsEmpty);
            log.e( json2 + "");

            int baoNum = 0;

            for (int i = 0; i < fianl_list.size(); i++) {
                ArrayList<QXBeanWP> qxBeanWPs = fianl_list.get(i);
                for (int j = 0; j < qxBeanWPs.size(); j++) {
                    QXBeanWP qxBeanWP = qxBeanWPs.get(j);
                    baoNum += qxBeanWP.getList().size();
                }
            }
            tvShow.setText("当天已发放" + fianl_list.size() + "个科室，" + baoNum + "个包");

            rcyFafangState.setLayoutManager(new LinearLayoutManager(mContext));

//        rcyFafangState.setAdapter(new FaFangTotalAdapter(mContext, list));

            rcyFafangState.setAdapter(new CommonAdapter<ArrayList<QXBeanWP>>(mContext, R.layout.item_fafang_total_rcy, fianl_list) {
                @Override
                protected void convert(ViewHolder holder, ArrayList<QXBeanWP> bean, int position) {
                    for (int i = 0; i < bean.size(); i++) {
                        holder.setText(R.id.tv_keshi, app.getKeShiName(bean.get(i).getPc_id()));
                    }
                    CoustomListView listView = holder.getView(R.id.lv_fafang);
                    adapter = new FaFangZKAdapter(mContext, R.layout.rcy_item_lv, bean);
                    listView.setAdapter(adapter);
                }
            });
        }
    }

    @Override
    public void initListeners() {
        ivFafangBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
