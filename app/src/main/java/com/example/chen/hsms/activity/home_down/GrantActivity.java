package com.example.chen.hsms.activity.home_down;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.chen.hsms.R;
import com.example.chen.hsms.activity.home_ohter.FaFangStateActivity;
import com.example.chen.hsms.application.MyApplication;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.data.UserMsg;
import com.example.chen.hsms.bean.data.KeShiName;
import com.example.chen.hsms.bean.data.QXBean;

import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.DateUtil;
import com.example.chen.hsms.utils.KeyBoardUtils;
import com.example.chen.hsms.utils.NetUtils;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.view.CoustomListView;
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
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发放
 */
public class GrantActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_serach)
    TextView tvSerach;

    @BindView(R.id.ll_data)
    LinearLayout llData;
    @BindView(R.id.fl_wudata)
    FrameLayout flWudata;
    @BindView(R.id.tv_grant_num)
    TextView tvGrantNum;
    @BindView(R.id.rcy_grant)
    CoustomListView rcyGrant;
    @BindView(R.id.et_ffjsr)
    EditText etFfjsr;
    @BindView(R.id.et_ffks)
    TextView etFfks;
    @BindView(R.id.fl_grantkeshi)
    FrameLayout flGrantkeshi;

    private List<KeShiName> list_keshi;

    private DateUtil dateUtil;
    private UserMsg userMsg;
    private Intent intent;
    private FaFang2Adapter adapter;
    private String tiaoma = "";

    private String keshiName = "";//科室名称  用于toast提示

    private Map<String, QXBean> map_init = new HashMap<>();//将要发放到中间人的包
    private Map<String, QXBean> map_centre = new HashMap<>();//将要发放到科室的包
    private String keshiId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.state), 0);

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
    public int setLayoutId() {
        return R.layout.activity_grant;
    }

    @Override
    protected void initView() {
        dateUtil = DateUtil.getInstance();
        userMsg = MyApplication.getInstance().getUserMsg();
        list_keshi = MyApplication.getInstance().getList_keshi();
    }

    @Override
    public void initDatas() {
        tvTitle.setText(getResources().getText(R.string.fafang));
        tvRight.setText(getResources().getText(R.string.fafangqingkuang));

    }

    @Override
    public void initListeners() {

    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.et_ffjsr, R.id.fl_grantkeshi, R.id.et_ffks})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                colseKeyBoard();
                finish();
                break;
            case R.id.tv_right:
                colseKeyBoard();
                intent = new Intent(mContext, FaFangStateActivity.class);
                startActivity(intent);
                break;
            case R.id.fl_grantkeshi:
                //ksjsren@1100@STS1
                colseKeyBoard();
                fafangKeshi();
                break;
            case R.id.et_ffks://科室选择
                colseKeyBoard();
                chooseDialog();
                break;
            case R.id.et_ffjsr:

                etFfjsr.setCursorVisible(true);
                break;
        }
    }

    /**
     * 关闭软键盘
     */
    private void colseKeyBoard() {
        KeyBoardUtils.closeKeybord(etFfjsr, mContext);
    }


    //========================================mobi扫描头驱动

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
        try {
            colseKeyBoard();
            String head = getHead(data);
            if (head.equals("")) {
                ToastUtils.showLong(Constant.TAGTOU);
                return;
            }
            if (head.equals(Constant.PTB)) {  //物品包条码
                String[] split = data.split("@");
                tiaoma = split[1];
                tvSerach.setText(tiaoma);
                saoMa(tiaoma);
            } else if (head.startsWith(Constant.ZJR)) {   //zjren@admin
                String[] split = data.split("@");
                String zjren = split[1];
                if (map_init.size() > 0) {
                    List<QXBean> list_zjr = getListQxBeen(map_init);
                    QXBean b = null;
                    for (int i = 0; i < list_zjr.size(); i++) {
                        b = list_zjr.get(i);
                        b.setFaFangJJR1(userMsg.getID());
                        b.setFaFangJJR2(zjren);
                        b.setFaFangJJBZ(1);
                        b.setFaFangJJSJ(dateUtil.getYear_Day());
                    }
                    fafang(1, list_zjr); // 1--发放给中间人  2--发放给科室接收人
                } else {
                    ToastUtils.showLong("扫描要发放到中间人的物品包");
                }
            } else {   //ksjsren@1100@STS1  if (data.startsWith("ksjsren"))
                ToastUtils.showLong(Constant.OTHER);
            }
        } catch (Exception e) {
            log.e(e.getMessage());
        }
    }

    private void fafangKeshi() {
        String ksren = etFfjsr.getText().toString().trim();
        if (keshiId.equals("")) {
            ToastUtils.showLong("接收科室不能为空");
            return;
        }
        if (ksren.equals("")) {
            ToastUtils.showLong("接收人不能为空");
            return;
        }
        keshiName = getIdKeShi(keshiId);
        log.e("科室--" + keshiName);

        if (map_centre.size() > 0) {
            List<QXBean> list_ks = getListQxBeen(map_centre);
            QXBean b = null;
            for (int i = 0; i < list_ks.size(); i++) {
                b = list_ks.get(i);

                b.setFaFangJJR3(ksren);
                b.setFaFangJJSJ2(dateUtil.getYear_Day());
                b.setLingYongKS(keshiId);
                b.setFaFangJJBZ(8);
            }
            fafang(2, list_ks); // 1--发放给中间人  2--发放给科室接收人
        } else {
            ToastUtils.showLong("扫描要发放到科室的物品包");
        }
    }

    //扫码
    private void saoMa(String tiaoma) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = "string¤" + "ID='" + tiaoma + "'";
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
                        List<QXBean> list_fafang = JSON.parseArray(result, QXBean.class);
                        //判断是否失效
                        QXBean bean = list_fafang.get(0);
                        String id = bean.getID();
                        if (id.equals("")) {
                            ToastUtils.showLong(Constant.NOTIAOMA);
                            return;
                        }
                        panDuan(bean);
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


    private void panDuan(final QXBean bean) {
        try {
            if (dateUtil.isDateOneBigger(dateUtil.getYear_Day(), bean.getXiaoDuSXSJ())) {
                ToastUtils.showLong("灭菌时间已过期");
            } else if (bean.getDaBaoZT()!=8) {
                ToastUtils.showLong("未打包，无法发放");
            } else if (bean.getHuiShouJJBZ()==1) {
                ToastUtils.showLong("已回收，无法发放");
            } else if (bean.getFaFangJJBZ()==8) {
                dialogCheXiao(2, bean);     //1代表  已发放到科室  是否撤回
            } else if (bean.getFaFangJJBZ()==0 ) { //0未发放  1 发放中  8 已发放
                if (map_init.containsKey(tiaoma)) {
                    ToastUtils.showLong("物品包已扫描");
                } else {
                    map_init.put(tiaoma, bean);
                    setAdapter(getListQxBeen(map_init));
                }
            } else if (bean.getFaFangJJBZ()==1) {
                if (map_centre.containsKey(tiaoma)) {
                    dialogCheXiao(1, bean);     //1代表已发放到中间人  是否撤销
                } else {
                    map_centre.put(tiaoma, bean);
                    setAdapter(getListQxBeen(map_centre));
                }
            } else {
                ToastUtils.showLong("都不满足以上条件");
                log.e("都不满足以上条件");
            }
        } catch (Exception e) {
            log.e(e.getMessage());
        }
    }

    private void dialogCheXiao(final int num, final QXBean b) {   //1表示撤销中间人   2撤销科室
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setContentView(R.layout.dialog_del);//重点看这获取弹出框内的视图view
        TextView content = (TextView) dialog.findViewById(R.id.content);//重点看这行的Dialog
        if (num == 1) {
            content.setText("物品包已发放给中间人:" + getIdToName(b.getFaFangJJR2()) + ",是否撤销？");
        } else if (num == 2) {
            content.setText("物品包已发放到 " + getIdKeShi(b.getLingYongKS()) + ",接收人:" + getIdToName(b.getFaFangJJR3()) + ",是否撤销？");
        }
        Button bt_yes = (Button) dialog.findViewById(R.id.bt_yes);//重点看这行的Dialog
        Button bt_no = (Button) dialog.findViewById(R.id.bt_no);//重点看这行的Dialog
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (num == 1) {
                    b.setFaFangJJR1("");
                    b.setFaFangJJR2("");
                    b.setFaFangJJBZ(0);
                    b.setFaFangJJSJ("1900-01-01 00:00:00");

                } else if (num == 2) {
                    b.setFaFangJJBZ(1);
                    b.setFaFangJJR3("");
                    b.setFaFangJJSJ2("1900-01-01 00:00:00");
                    b.setLingYongKS("");

                }
                cheOneGrant(num, b);
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
     * 单个撤销发放  --发放中状态
     *
     * @param num
     * @param b
     */
    private void cheOneGrant(final int num, final QXBean b) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String newJson = JSON.toJSONString(b, SerializerFeature.WriteNullStringAsEmpty);
        String s = "DataTable¤" + "[" + newJson + "]";
        params.put(Constant.CODE, WSOpraTypeCode.修改物品包追踪记录);
        params.put(Constant.PARAMETER, s);
        log.e("修改物品包追踪记录--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("获取物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        if (num == 1) {
                            map_init.put(tiaoma, b);
                            setAdapter(getListQxBeen(map_init));
                            mSVProgressHUD.showSuccessWithStatus(tiaoma + "发放中间人撤销成功");
                        } else if (num == 2) {
                            map_centre.put(tiaoma, b);
                            setAdapter(getListQxBeen(map_centre));
                            mSVProgressHUD.showSuccessWithStatus(tiaoma + "发放科室撤销成功");
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

    private void fafang(final int state, final List<QXBean> list_zzjl) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String json = JSON.toJSONString(list_zzjl, SerializerFeature.WriteNullStringAsEmpty);
        String s = "DataTable¤" + json;
        params.put(Constant.CODE, WSOpraTypeCode.修改物品包追踪记录);
        params.put(Constant.PARAMETER, s);
        log.e("批量或单个发放--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("获取物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        if (state == 1) {
                            ToastUtils.showLong("成功发放到中间人");
                            map_init.clear();
                        } else if (state == 2) {
                            ToastUtils.showLong("成功发放到" + keshiName);
                            map_centre.clear();
                        }
                        setAdapter(list_zzjl);
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

    private void chooseDialog() {
        //展示是否撤销的dialog
//        final AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.MyDialog).create();
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
//        dialog.setCanceledOnTouchOutside(false);//yuan
        dialog.setCanceledOnTouchOutside(true);//点击外部消失
        dialog.getWindow().setContentView(R.layout.dialog_keshi);//重点看这获取弹出框内的视图view
        RecyclerView rcy_keshi = (RecyclerView) dialog.findViewById(R.id.rcy_keshi);

        ImageView iv = (ImageView) dialog.findViewById(R.id.iv_close);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        rcy_keshi.setLayoutManager(new LinearLayoutManager(mContext));
        rcy_keshi.addItemDecoration(new RecyclerViewDivider(mContext,LinearLayout.HORIZONTAL,1,ContextCompat.getColor(mContext,R.color.item_view)));
        rcy_keshi.setAdapter(new CommonAdapter<KeShiName>(mContext,  R.layout.item_keshi,list_keshi) {
            @Override
            protected void convert(ViewHolder holder, final KeShiName bean,  int position) {
                holder.setText(R.id.tv_keshiid, bean.getID());
                holder.setText(R.id.tv_keshi, bean.getName());
                holder.setOnClickListener(R.id.fl_keshi, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        keshiId = bean.getID();
                        etFfks.setText(bean.getName());
                    }
                });
            }
        });

        Window window = dialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.9); // 改变的是dialog框在屏幕中的位置而不是大小
        p.width = (int) (d.getWidth()* 0.95 ); // 宽度设置为屏幕的0.65
        window.setAttributes(p);

    }

    @NonNull
    private List<QXBean> getListQxBeen(Map<String, QXBean> map) {
        List<QXBean> list = new ArrayList<>();
        Set<String> get = map.keySet();
        for (String test : get) {
            QXBean b = map.get(test);
            list.add(b);
        }
        return list;
    }

    private void setAdapter(final List<QXBean> list) {
        if (list.size() == 0 || list == null) {
            flWudata.setVisibility(View.VISIBLE);
            llData.setVisibility(View.GONE);
        } else {
            flWudata.setVisibility(View.GONE);
            llData.setVisibility(View.VISIBLE);
            tvGrantNum.setText("共" + list.size() + "个物品包");
            adapter = new FaFang2Adapter(mContext, R.layout.item_grant_lv, list);
            rcyGrant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    toOneWpbDetail(list.get(i));
                }
            });
            rcyGrant.setAdapter(adapter);
            adapter.notifyDataSetInvalidated();
        }
    }


    class FaFang2Adapter extends com.example.chen.hsms.base.CommonAdapter<QXBean> {
        public FaFang2Adapter(Context context, int layoutId, List<QXBean> mDatas) {
            super(context, layoutId, mDatas);
        }

        @Override
        public void convert(com.example.chen.hsms.base.ViewHolder holder, List<QXBean> t, int position, String tag) {
            QXBean bean = t.get(position);
            holder.setText(R.id.tv_ordernum, bean.getID());
            holder.setText(R.id.tv_item_title, bean.getWuPinBMC());
            if (bean.getFaFangJJBZ()==0 ) {
                holder.setText(R.id.tv_state, "未发放");
            } else if (bean.getFaFangJJBZ()==1) {
//        }else  if((bean.getFaFangJJR2()!=null||!bean.getFaFangJJR2().equals(""))&& bean.getFaFangJJSJ2().equals("1900-01-01 00:00:00")){
                holder.setText(R.id.tv_state, "发放中");
            } else if (bean.getFaFangJJBZ()==8) {
                holder.setText(R.id.tv_state, "发放成功");
            }
        }
    }
}