package com.example.chen.hsms.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.chen.hsms.R;
import com.example.chen.hsms.activity.home_down.GrantActivity;
import com.example.chen.hsms.activity.home_down.MieJunActivity;
import com.example.chen.hsms.activity.home_down.PackActivity;
import com.example.chen.hsms.activity.home_down.QingDianActivity;
import com.example.chen.hsms.activity.home_down.ZhuiSuActivity;
import com.example.chen.hsms.activity.home_up.FaFangDanActivity;
import com.example.chen.hsms.activity.home_up.UserRegActivity;
import com.example.chen.hsms.activity.home_up.WuPinBaoActivity;
import com.example.chen.hsms.adapter.HomeAdapter;
import com.example.chen.hsms.application.MyApplication;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.data.BaseData;
import com.example.chen.hsms.bean.data.GoodClass;
import com.example.chen.hsms.bean.local.HomeBean;
import com.example.chen.hsms.bean.data.IdName;
import com.example.chen.hsms.bean.data.SystemPar;
import com.example.chen.hsms.bean.data.XiaoDuGuo;
import com.example.chen.hsms.bean.data.KeShiName;
import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.NetUtils;
import com.example.chen.hsms.utils.SaveUtils;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.view.CoustomGridView;
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

public class HomeActivity extends BaseActivity {
    @BindView(R.id.tv_home_exit)
    TextView tvHomeExit;
    @BindView(R.id.rcy_top)
    RecyclerView rcyTop;

    List<HomeBean> list_up = new ArrayList<>();
    List<HomeBean> list_downn = new ArrayList<>();
    @BindView(R.id.gv_bottom)
    CoustomGridView gvBottom;
    private Resources re;
    HomeAdapter adapter;
    private List<GoodClass> list1 = new ArrayList<>();
    private List<BaseData> list2 = new ArrayList<>();
    private List<SystemPar> list3 = new ArrayList<>();
    private List<XiaoDuGuo> list4 = new ArrayList<>();
    private List<KeShiName> list5 = new ArrayList<>();
    private List<IdName> list7 = new ArrayList<>();
    private int num = 0;//缓存数据总个数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        BarUtils.setColor(this, ContextCompat.getColor(mContext, R.color.state), 0);
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        try{
            re = getResources();
            TypedArray text_up = re.obtainTypedArray(R.array.home_text_up);
            TypedArray icon_up = re.obtainTypedArray(R.array.home_icon_up);
            for (int i = 0; i < re.getStringArray(R.array.home_text_up).length; i++) {
                list_up.add(new HomeBean(re.getDrawable(icon_up.getResourceId(i, 0)), text_up.getString(i)));
            }
            text_up.recycle();
            icon_up.recycle();
            TypedArray text_down = re.obtainTypedArray(R.array.home_text_down);
            TypedArray icon_down = re.obtainTypedArray(R.array.home_icon_down);
            for (int i = 0; i < re.getStringArray(R.array.home_text_down).length; i++) {
                list_downn.add(new HomeBean(re.getDrawable(icon_down.getResourceId(i, 0)), text_down.getString(i)));
            }
            text_down.recycle();
            icon_down.recycle();
        }catch (Exception e){
            log.e(e.getMessage());
        }

    }

    @Override
    public void initDatas() {
        isHuancunData();
        rcyTop.setLayoutManager(new GridLayoutManager(this, 3));
        rcyTop.setAdapter(new CommonAdapter<HomeBean>(HomeActivity.this, R.layout.home_top_gv, list_up) {
            @Override
            protected void convert(ViewHolder holder, final HomeBean homeBean, final int position) {
                holder.setImageDrawable(R.id.iv_icon, list_up.get(position).getDrawable());
                holder.setText(R.id.tv_text, list_up.get(position).getName());
                holder.setOnClickListener(R.id.item_home_up, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (position) {
                            case 0:
                                Intent intent = new Intent(mContext, WuPinBaoActivity.class);
                                intent.putExtra("tag", "1");
                                startActivity(intent);
                                break;
                            case 1:
                                gotoAtivity(UserRegActivity.class, null);
                                break;
                            case 2:
                                gotoAtivity(FaFangDanActivity.class, null);
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        });
        adapter = new HomeAdapter(HomeActivity.this, list_downn, R.layout.home_below_gv);
        gvBottom.setAdapter(adapter);
    }

    @Override
    public void initListeners() {
        gvBottom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = list_downn.get(i).getName();
                switch (name) {
                    case "清点":
//                        gotoAtivity(QingDian3Activity.class, null);
                        gotoAtivity(QingDianActivity.class, null);
                        break;
                    case "清洗":
                        ToastUtils.showLong("清洗功能整合在清点页面");
//                        gotoAtivity(CleanActivity.class, null);
                        break;
                    case "打包":
                        gotoAtivity(PackActivity.class, null);
                        break;
                    case "灭菌":
                        gotoAtivity(MieJunActivity.class, null);
                        break;
                    case "发放":
//                        gotoAtivity(GrantActivity2.class, null);//无中间人，直接发放到科室
                        gotoAtivity(GrantActivity.class, null);//无中间人，直接发放到科室   手动输入科室和接收工号
                        break;
//                    case "回收":
//                        gotoAtivity(RecoveryActivity.class, null);
//                        break;
                    case "追溯":
                        gotoAtivity(ZhuiSuActivity.class, null);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 判断是否缓存过基础数据
     */
    private void isHuancunData() {
//        showLoading("获取基础数据中...");
//        cachedThreadPool.execute(r1);
        if (!SaveUtils.contains(mContext, "1")) {
            showLoading("获取基础数据中...");
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    wupinfenlei();
                    jichushuji();
                    xitongcanshu();
                    xiaoduguo();
                    huoquKeShi();
                    getidname();
                }
            });
        } else {
            String s1 = (String) SaveUtils.get(mContext, "1", "");
            list1 = JSON.parseArray(s1, GoodClass.class);
            MyApplication.getInstance().setList_gc(list1);

            String s2 = (String) SaveUtils.get(mContext, "2", "");
            list2 = JSON.parseArray(s2, BaseData.class);
            MyApplication.getInstance().setList_bd(list2);

            String s3 = (String) SaveUtils.get(mContext, "3", "");
            list3 = JSON.parseArray(s3, SystemPar.class);
            MyApplication.getInstance().setList_sp(list3);

            String s4 = (String) SaveUtils.get(mContext, "4", "");
            list4 = JSON.parseArray(s4, XiaoDuGuo.class);
            MyApplication.getInstance().setList_xdg(list4);

            String s5 = (String) SaveUtils.get(mContext, "5", "");
            list5 = JSON.parseArray(s5, KeShiName.class);
            MyApplication.getInstance().setList_keshi(list5);


            String s7 = (String) SaveUtils.get(mContext, "7", "");
            list7 = JSON.parseArray(s7, IdName.class);
            MyApplication.getInstance().setList_idname(list7);

            log.e(list1.size() + " " + list2.size() + " " + list3.size() + " " + list4.size() + " " + list5.size() + " " + list7.size() + " ");
        }
    }


    @OnClick({R.id.tv_home_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_home_exit:
                gotoAtivity(LoginActivity.class, null);
                finish();
                break;
            default:
                break;
        }
    }
    /**
     * 1
     * 获取物品分类
     */
    public void wupinfenlei() {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + Constant.XING;
        params.put(Constant.CODE, WSOpraTypeCode.获取物品分类);
        params.put(Constant.PARAMETER, s);
        log.e("获取物品分类--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                isCached();
                if (hsms != null) {
                    log.e("获取物品分类--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String json = hsms.getReturnJson();
                        list1 = JSON.parseArray(json, GoodClass.class);
                        log.e(list1.size() + "获取物品分类");
                        MyApplication.getInstance().setList_gc(list1);
                        SaveUtils.put(mContext, "1", json);
                    } else {
                        ToastUtils.showLong(Constant.ERROR);
                    }
                } else {
                    ToastUtils.showLong(Constant.IP);
                    log.e(Constant.IP);
                }
            }
        });
    }

    private void isCached() {
        if (num == 5) {
            dismissLoading();
        } else {
            num++;
        }
    }

    /**
     * 2
     * 获取基础数据明细
     */
    public void jichushuji() {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + Constant.XING;
        params.put(Constant.CODE, WSOpraTypeCode.获取基础数据明细);
        params.put(Constant.PARAMETER, s);
        log.e("获取基础数据明细--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                isCached();
                if (hsms != null) {
                    log.e("获取基础数据明细--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String json = hsms.getReturnJson();
                        list2 = JSON.parseArray(json, BaseData.class);
                        log.e(list2.size() + "获取基础数据明细");
                        MyApplication.getInstance().setList_bd(list2);

                        SaveUtils.put(mContext, "2", json);
                    } else {
                        ToastUtils.showLong(Constant.ERROR);
                    }
                } else {
                    ToastUtils.showLong(Constant.IP);
                    log.e(Constant.IP);
                }
            }
        });
    }

    /**
     * 3
     * 获取系统参数
     */
    public void xitongcanshu() {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + Constant.XING;
        params.put(Constant.CODE, WSOpraTypeCode.获取系统参数);
        params.put(Constant.PARAMETER, s);
        log.e("获取系统参数--" + s);

        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                isCached();
                if (hsms != null) {
                    log.e("获取系统参数--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String json = hsms.getReturnJson();
                        list3 = JSON.parseArray(json, SystemPar.class);
                        log.e(list3.size() + "获取系统参数");
                        MyApplication.getInstance().setList_sp(list3);//条码识别配置  

                        SaveUtils.put(mContext, "3", json);
                    } else {
                        ToastUtils.showLong(Constant.ERROR);
                    }
                } else {
                    ToastUtils.showLong(Constant.IP);
                    log.e(Constant.IP);
                }
            }
        });
    }

    /**
     * 4
     * 获取消毒锅
     */
    public void xiaoduguo() {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + Constant.XING;
        params.put(Constant.CODE, WSOpraTypeCode.获取消毒锅);
        params.put(Constant.PARAMETER, s);
        log.e("获取消毒锅--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                isCached();
                if (hsms != null) {
                    log.e("获取消毒锅--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String json = hsms.getReturnJson();
                        list4 = JSON.parseArray(json, XiaoDuGuo.class);
                        log.e(list4.size() + "获取消毒锅");
                        MyApplication.getInstance().setList_xdg(list4);
                        SaveUtils.put(mContext, "4", json);
                    } else {
                        ToastUtils.showLong(Constant.ERROR);
                    }
                } else {
                    ToastUtils.showLong(Constant.IP);
                    log.e(Constant.IP);
                }
            }
        });
    }

    /**
     * 5
     * 获取科室
     */
    private void huoquKeShi() {
        HashMap<String, Object> params = new HashMap<>();
        String s = "string¤" + "3";//¤
        params.put(Constant.CODE, WSOpraTypeCode.获取基础数据明细);
        params.put(Constant.PARAMETER, s);
        log.e("获取科室--获取基础数据明细--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                isCached();
                if (hsms != null) {
                    log.e("获取科室--获取基础数据明细--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        list5 = JSON.parseArray(result, KeShiName.class);
                        log.e(list5.size() + "获取科室");
                        MyApplication.getInstance().setList_keshi(list5);
                        SaveUtils.put(mContext, "5", result);
                    } else {
                        ToastUtils.showLong(Constant.ERROR);
                    }
                } else {
                    ToastUtils.showLong(Constant.IP);
                    log.e(Constant.IP);
                }
            }
        });

    }

    /**
     * 7
     * 缓存人名id
     */
    private void getidname() {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + "";
        params.put(Constant.CODE, WSOpraTypeCode.获取用户缓存信息);
        params.put(Constant.PARAMETER, s);
        log.e("缓存人名id--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                isCached();
                if (hsms != null) {
                    log.e("缓存人名id--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        list7 = JSON.parseArray(result, IdName.class);
                        log.e(list7.size() + "缓存人名id");
                        MyApplication.getInstance().setList_idname(list7);
                        SaveUtils.put(mContext, "7", result);
                    } else {
                        ToastUtils.showLong(Constant.ERROR);
                    }
                } else {
                    ToastUtils.showLong(Constant.IP);
                    log.e(Constant.IP);
                }
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 再次点击退出
     */
    private void exit() {
        if ((System.currentTimeMillis() - lastBackTime) > 2000) {
            ToastUtils.showLong(Constant.EXIT);
            lastBackTime = System.currentTimeMillis();
        } else {
            log.e("exit application");
            this.finish();
            System.exit(0);
        }
    }
}
