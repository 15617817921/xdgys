package com.example.chen.hsms.activity.home_up;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.chen.hsms.activity.home_ohter.PatientListActivity;
import com.example.chen.hsms.adapter.QingDianAdapter;
import com.example.chen.hsms.application.MyApplication;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.data.UserMsg;
import com.example.chen.hsms.bean.local.MessagePatient;
import com.example.chen.hsms.bean.data.QXBean;
import com.example.chen.hsms.bean.local.QXBeanWP;

import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.DateUtil;
import com.example.chen.hsms.view.CoustomListView;
import com.example.chen.hsms.view.RecyclerViewDivider;
import com.example.chen.hsms.webservice.Webservice;
import com.example.chen.hsms.ws.Hsms;
import com.example.chen.hsms.ws.WSOpraTypeCode;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 使用人登记
 */
public class UserRegActivity extends BaseActivity {

    @BindView(R.id.fl_choose)
    FrameLayout flChoose;
    @BindView(R.id.tv_choose)
    TextView tvChoose;
    @BindView(R.id.tv_useGood)
    TextView tvUseGood;
    @BindView(R.id.rcy_userreg)
    RecyclerView rcyUserreg;
    @BindView(R.id.ll_data)
    LinearLayout llData;
    @BindView(R.id.fl_wuData)
    FrameLayout flWuData;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_serach)
    TextView tvSerach;

    private DateUtil dateUtil;
    private QXBean bean;
    private UserMsg userMsg;
    private MyApplication app;
    private QingDianAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        //绑定事件接受
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销事件接受
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMessageEvent(MessagePatient m) {
        log.e("=====" + m.getBrid());
        tvChoose.setText(m.getBrid() + "  " + m.getBrname() + "  " + m.getBrsex());
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_user_reg;
    }

    @Override
    protected void initView() {
        dateUtil = DateUtil.getInstance();
        app = MyApplication.getInstance();
        userMsg = app.getUserMsg();
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.state), 0);


//        initToolBar("使用人登记", ContextCompat.getColor(mContext,R.color.white));
//        initToolBarRight("使用人登记", ContextCompat.getColor(mContext, R.color.white), "扫码", ContextCompat.getColor(mContext, R.color.white));

    }

    /**
     * 扫描病人床 条码
     *
     * @param view
     */
    public void dengji(View view) {
        commit();
    }

    private void commit() {
        showLoading("加载中...");
        //获取服务器时间
        new Webservice(mContext, handler_shijian, Constant.METHODNAME, true) {
            @Override
            public void addProgerty(SoapObject rpc) {//获取服务器时间    string¤           00
                String s = Constant.STRING + "";
                rpc.addProperty(Constant.CODE, WSOpraTypeCode.获取服务器时间);
                rpc.addProperty(Constant.PARAMETER, s);
            }
        };
    }

    private String fuwuqiDate;
    private Handler handler_shijian = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    hs = (Hsms) msg.obj;
                    if (hs.getReturnCode() == 0) {
                        String result = hs.getReturnJson();
                        fuwuqiDate = dateUtil.getTimeToDate(result);
                        upData();
                    }
                    log.e(hs.getResturnMsg() + "--" + hs.getReturnCode() + "--" + hs.getReturnID() + "--" + hs.getReturnJson());
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public void initDatas() {
        tvTitle.setText(getResources().getText(R.string.shiyongrdj));
        tvRight.setText(getResources().getText(R.string.saoma));
    }

    @Override
    public void initListeners() {
    }
    @OnClick({R.id.iv_back, R.id.tv_right, R.id.fl_choose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                break;
            case R.id.fl_choose:
                gotoAtivity(PatientListActivity.class, null);
                break;
        }
    }



    private void setAdapter(List<QXBean> list) {
        if (list.size() > 0 && !list.get(0).getID().equals("")) {//有数据
            flWuData.setVisibility(View.GONE);
            llData.setVisibility(View.VISIBLE);

            tvUseGood.setText("共" + list.size() + "个物品包");


            List<List<QXBeanWP>> list_last = dateUtil.getToKeShiWuPinB(list);

            String json = JSON.toJSONString(list_last, SerializerFeature.WriteNullStringAsEmpty);
            log.e(json);

            rcyUserreg.setLayoutManager(new LinearLayoutManager(this));
            rcyUserreg.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL, (int) getResources().getDimension(R.dimen.x1), ContextCompat.getColor(mContext, R.color.item_view)));
            rcyUserreg.setAdapter(new CommonAdapter<List<QXBeanWP>>(mContext, R.layout.item_qingdian, list_last) {
                @Override
                protected void convert(final ViewHolder holder, final List<QXBeanWP> bean, final int position) {
                    for (int i = 0; i < bean.size(); i++) {
                        holder.setText(R.id.tv_picihao, app.getKeShiName(bean.get(i).getPc_id()));
                    }
                    final CoustomListView lv_qingxi = holder.getView(R.id.lv_qingxi);
                    adapter = new QingDianAdapter(mContext, R.layout.item_qingdian_lv, bean);
                    lv_qingxi.setAdapter(adapter);
                }
            });
        } else {
            flWuData.setVisibility(View.VISIBLE);
            llData.setVisibility(View.GONE);
        }
    }

    //扫码
    private void saoma(final String tiaoma) {
        showLoading("扫码中...");
        new Webservice(this, handler_saoma, Constant.METHODNAME, true) {// ------
            @Override
            public void addProgerty(SoapObject rpc) {
//                WSOpraTypeCode.获取物品包追踪记录  "string"+"¤"+"ID='" 0000001469 "'“;
                String s = Constant.STRING + "ID='" + tiaoma + "'";
                rpc.addProperty(Constant.CODE, WSOpraTypeCode.获取物品包追踪记录);
                rpc.addProperty(Constant.PARAMETER, s);
                log.e(s);
            }
        };
    }

    private Hsms hs;
    private List<QXBean> list_saoma;
    private List<QXBean> list_data = new ArrayList<>();
    private Handler handler_saoma = new Handler() {  //----------扫码
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    dismissLoading();
                    hs = (Hsms) msg.obj;
                    log.e(hs.getResturnMsg() + "--" + hs.getReturnCode() + "--" + hs.getReturnID() + "--" + hs.getReturnJson());
                    if (hs.getReturnCode() == 0) {
                        String result = hs.getReturnJson();
                        list_saoma = JSON.parseArray(result, QXBean.class);

                        bean = list_saoma.get(0);
                        for (int i = 0; i < list_data.size(); i++) {
                            if (bean.getID().equals(list_data.get(i).getID())) {
                                mSVProgressHUD.showInfoWithStatus("物品包已扫描");
//                                ToastUtils.showLong("物品包已扫描");
                                return;
                            }
                        }
                        list_data.add(bean);//添加数据源
                        setAdapter(list_data);//设置数据

                        //之前需要扫码 拿到     ShiYongBRID (病人ID), ShiYongBRXM (病人姓名), ShouShuDID (手术单id),
                    } else {
                        log.e("操作返回标志失败");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void upData() {

        String shiYongBRID = "0001";
        String shiYongBRXM = "张三";
        String shouShuDID = "01";
        tvChoose.setText(shiYongBRXM + " " + shiYongBRID);
        for (int i = 0; i < list_data.size(); i++) {
            bean = list_data.get(i);

            bean.setShiYongBRID(shiYongBRID);
            bean.setShiYongBRXM(shiYongBRXM);
            bean.setShouShuDID(shouShuDID);

            bean.setShiYongSJ(fuwuqiDate);
            bean.setShiYongBZ(1);
            bean.setShiYongRen1(userMsg.getID());
        }
        final String json = JSON.toJSONString(list_data, SerializerFeature.WriteNullStringAsEmpty);
//         ShiYongBRID (病人ID), ShiYongBRXM (病人姓名), ShouShuDID (手术单id),
//        ShiYongSJ (服务器时间), ShiYongBZ (1) ,ShiYongRen1（登陆人ID）
//        SysOperaCodes. 修改物品包追踪记录  "DataTable"+"¤"+"物品包追踪记录表的json数据; 7602
        new Webservice(this, handler_updata, Constant.METHODNAME, true) {// ------
            @Override
            public void addProgerty(SoapObject rpc) {
                String s = Constant.DATATABLE + json;
                rpc.addProperty(Constant.CODE, WSOpraTypeCode.修改物品包追踪记录);
                rpc.addProperty(Constant.PARAMETER, s);
                log.e(s);
            }
        };
    }

    private Handler handler_updata = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    dismissLoading();
                    hs = (Hsms) msg.obj;
                    log.e(hs.getResturnMsg() + "--" + hs.getReturnCode() + "--" + hs.getReturnID() + "--" + hs.getReturnJson());
                    if (hs.getReturnCode() == 0) {
                        String result = hs.getReturnJson();
                        mSVProgressHUD.showSuccessWithStatus("登记成功");

                        tvChoose.setText("");
                        list_data.clear();
                        setAdapter(list_data);
                    } else {
                        log.e("操作返回标志失败");
                    }
                    break;
                default:
                    break;
            }
        }
    };



}
