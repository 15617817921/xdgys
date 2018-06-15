package com.example.chen.hsms.activity.home_ohter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.chen.hsms.R;
import com.example.chen.hsms.application.MyApplication;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.data.JiuCuo;
import com.example.chen.hsms.bean.data.UserMsg;
import com.example.chen.hsms.bean.data.WuPinBean;
import com.example.chen.hsms.bean.data.Hsd_Mx;
import com.example.chen.hsms.bean.data.QXBean;
import com.example.chen.hsms.bean.data.QingDian;
import com.example.chen.hsms.bean.data.QingXiJiLu;
import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.DateUtil;
import com.example.chen.hsms.utils.ImageUtils;
import com.example.chen.hsms.utils.NetUtils;
import com.example.chen.hsms.utils.StringUtils;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.view.RecyclerViewDivider;
import com.example.chen.hsms.webserviceutils.WebServiceUtils;
import com.example.chen.hsms.ws.Hsms;
import com.example.chen.hsms.ws.WSOpraTypeCode;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//4439 443  444
public class WuPinBaoActivity3 extends BaseActivity {
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.fl_show)
    FrameLayout flShow;
    @BindView(R.id.rcy_wupin)
    RecyclerView rcyWuDetail;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_serach)
    TextView tvSerach;
    @BindView(R.id.tv_search_bao)
    TextView tvSearchBao;
    @BindView(R.id.tv_clean_bao)
    TextView tvCleanBao;


    private int total = 0;//总数量
    private String wupinbaoid = "";
    private String tiaoma = "";//当前操作的条码


    private Intent intent;
    private List<Hsd_Mx> list_hsd_mx = new ArrayList<>();//所有回收单明细 每个包含至少一个相同包
    private List<QingDian> list_huisd = new ArrayList<>();//全部回收单 扫描变动更新
    private String wupinbaoname;
    private DateUtil dateUtil;
    private UserMsg userMsg;

    private String qixilan = "";//当前扫描的清洗篮
    //    private String image = "";//当前扫码生成的新条码是否拍照


    private LinkedHashMap map_ma = new LinkedHashMap();////单个 或 批量扫描{new   old}  //前边拍照绑篮  后边展示在输入框
    private LinkedHashMap map_image = new LinkedHashMap();//针对当前物品包拍照或替换
    private static String IMAGE = "image";

    @Override
    public int setLayoutId() {
        return R.layout.activity_wu_pin_bao3;
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
        dateUtil = DateUtil.getInstance();
        userMsg = MyApplication.getInstance().getUserMsg();

        tvTitle.setText(getResources().getText(R.string.wupinbaomingxi));
        tvRight.setText(getResources().getText(R.string.paizhao));
    }


    @Override
    public void initDatas() {
        log.e("initDatas");
        intent = getIntent();
        String biaoshi = intent.getStringExtra("tag");//  1-->各个item点击进入  2-->清点 ==扫码进入

        wupinbaoid = intent.getStringExtra("wupinbaoid");
        wupinbaoname = intent.getStringExtra("wupinbaoname");

        Bundle bundle = intent.getExtras();
        list_hsd_mx = (List<Hsd_Mx>) bundle.getSerializable("list_huisdmx");//所有回收单里面的物品包集合，用于当前页面扫码 传回已扫描的
        list_huisd = (List<QingDian>) bundle.getSerializable("list_huisd");//所有回收单里面的物品包集合，用于当前页面扫码 传回已扫描的

        tvType.setText(wupinbaoname);//物品包名称

        if (biaoshi.equals("3")) {  //手点进入
            log.w("3手点进入进入物品包明细");

            if (wupinbaoid != null) {
                dialog.show();
                cachedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        chaxun(wupinbaoid);
                    }
                });
            }
        } else if (biaoshi.equals("2")) {   //扫码进入
            log.w("2扫码进入物品包明细");

            tiaoma = intent.getStringExtra("tiaoma");
            String newimage = intent.getStringExtra("newimage");//新条码是否拍照
            String newma = intent.getStringExtra("newma");

            addMaImage(newimage, newma, tiaoma);
            String allTiao = showAllTiao(map_ma);
            log.e(tiaoma + "--照片" + newimage + "新条码" + newma + "展示条码" + allTiao);
            tvSerach.setText(allTiao);
            if (wupinbaoid != null) {
                dialog.show();
                chaxun(wupinbaoid);
            }
        }
    }

    /**
     * @param newimage
     * @param newma
     * @param oldma
     */
    private void addMaImage(String newimage, String newma, String oldma) {
        if (newimage.equals("")) {
            map_image.put(IMAGE, newma);
        } else {
            map_image.put(IMAGE, newimage + Constant.FUHAO + newma);
        }

        map_ma.put(newma, oldma);
    }

    @Override
    public void initListeners() {
        log.e("initListeners");
    }

    @OnClick({R.id.iv_back, R.id.tv_search_bao, R.id.tv_clean_bao, R.id.tv_right, R.id.tv_serach})
    public void onViewClicked(View view) {
        log.e("onViewClicked");
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_serach://查看当前扫描过的条码
                dialogShowMa();
                break;
            case R.id.tv_clean_bao://清空前扫描过的条码
                if (map_image.size() == 0 && map_ma.size() == 0) {
                    ToastUtils.showShort("当前没有物品包可清空");
                } else {
                    map_ma.clear();
                    map_image.clear();
                    tvSerach.setText("");
                    mSVProgressHUD.showSuccessWithStatus("物品包清空成功");
//                    ToastUtils.showShort("");
                }
                break;
            case R.id.tv_search_bao:
                huoSgouBao();
                break;
            case R.id.tv_right:
                if (map_image.size() > 0) {
                    String image_id = (String) map_image.get(IMAGE);
                    log.e(image_id);
                    if (image_id.contains(Constant.FUHAO)) {
                        String[] spilt = image_id.split(Constant.FUHAO);
                        tiaoma = spilt[1];
                        dialogUpImage(spilt[1]);
                    } else {
                        tiaoma = image_id;
                        takePotho();
                    }
                } else {
                    ToastUtils.showLong("选择要拍照的物品包");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 查看条码的弹窗
     */
    private void dialogShowMa() {
        if (map_ma.size() > 3) {
            final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
            dialog.show();
//          dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setContentView(R.layout.dialog_tishi_tiaoma);//重点看这获取弹出框内的视图view
            GridView gv_showma = (GridView) dialog.findViewById(R.id.gv_showma);//重点看这行的Dialog
            List<String> listTiao = showListTiao(map_ma);
            gv_showma.setAdapter(new com.example.chen.hsms.base.CommonAdapter<String>(mContext, R.layout.falow_tv, listTiao) {
                @Override
                public void convert(com.example.chen.hsms.base.ViewHolder holder, List<String> t, int position, String tag) {
                    holder.setText(R.id.tv, t.get(position));
                }
            });
            dialog.findViewById(R.id.bt_know).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
    }

    /**
     * 获取所有首次包
     */
    private void huoSgouBao() {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING;
        params.put(Constant.CODE, WSOpraTypeCode.获取首次生成物品包);
        params.put(Constant.PARAMETER, s);
        log.e("获取首次生成物品包--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();//扫描之后跳转
                if (hsms != null) {
                    log.e("获取物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<JiuCuo> list_shoubao = JSON.parseArray(result, JiuCuo.class);
                        if (list_shoubao.get(0).getID().equals("")) {
                            ToastUtils.showLong("暂无首次包");
                        } else {
                            chooseDialog(list_shoubao);
                        }
                    } else {
                        ToastUtils.showLong(Constant.NODATA);
                    }
                } else {
                    log.e(Constant.HSMS);
                }
            }
        });
    }


    private LinkedHashMap map_choose_image = new LinkedHashMap();//选择首次包拍照的数量（只能选择一个）
    private LinkedHashMap map_choose_ma = new LinkedHashMap();//选择首次包的数量

    private void chooseDialog(List<JiuCuo> list) {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
//        dialog.setCanceledOnTouchOutside(false);//yuan
        dialog.setCanceledOnTouchOutside(true);//点击外部消失
        dialog.getWindow().setContentView(R.layout.dialog_choose);//重点看这获取弹出框内的视图view
        RecyclerView rcyScbShow = (RecyclerView) dialog.findViewById(R.id.rcy_scbShow);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setText("首次物品包选择");


        rcyScbShow.setLayoutManager(new LinearLayoutManager(mContext));
        rcyScbShow.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayout.HORIZONTAL, 1, ContextCompat.getColor(mContext, R.color.view)));
        rcyScbShow.setAdapter(new CommonAdapter<JiuCuo>(mContext, R.layout.item_choose, list) {
            @Override
            protected void convert(ViewHolder holder, final JiuCuo bean, int position) {
                TextView tv_first_id = holder.getView(R.id.tv_first_id);
                tv_first_id.setText(bean.getID());
                if (!StringUtils.stringNull(bean.getWupinbaoImage()).equals("")) {
                    ImageView image_show = holder.getView(R.id.iv_image_item);
                    image_show.setVisibility(View.VISIBLE);
                }
                holder.setText(R.id.tv_first_bao, bean.getWupinBMC());

                ImageView image = holder.getView(R.id.iv_choose);
                RelativeLayout rl_item_gv = holder.getView(R.id.rl_item);

                if (bean.isChecked()) {//状态选中
                    image.setImageResource(R.drawable.btn_choose_on);
                } else {
                    image.setImageResource(R.drawable.btn_choose_null);
                }

                rl_item_gv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String image = StringUtils.stringNull(bean.getWupinbaoImage());
                        if (bean.isChecked()) {
                            bean.setChecked(false);

                            if (map_choose_image.containsKey(bean.getID())) {
                                map_choose_image.remove(bean.getID());
                            }
                            if (map_choose_ma.containsKey(bean.getID())) {
                                map_choose_ma.remove(bean.getID());
                            }
                        } else {
                            bean.setChecked(true);
                            if (image.equals("")) {
                                map_choose_image.put(bean.getID(), bean.getID());
                            } else {
                                map_choose_image.put(bean.getID(), image + Constant.FUHAO + bean.getID());
                            }
                            log.e(image + Constant.FUHAO + bean.getID());
                            map_choose_ma.put(bean.getID(), bean.getID());
                        }

                        log.e(image + "当前" + map_choose_image.size() + "--" + map_choose_ma.size() + "选择包的数量" + map_image.size() + "--" + map_ma.size());
                        notifyDataSetChanged();
                        //单选
//                    for (IdName bean : datas) {//全部设为未选中
//                        bean.setChecked(false);
//                    }
//                    idName.setChecked(true);//点击的设为选中
//                    notifyDataSetChanged();
                    }
                });
            }
        });
        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                map_choose_image.clear();
                map_choose_ma.clear();
                log.e("当前" + map_choose_image.size() + "--" + map_choose_ma.size() + "选择包的数量" + map_image.size() + "--" + map_ma.size());

            }
        });
        dialog.findViewById(R.id.tv_cancle)
                .setOnClickListener(new View.OnClickListener() { //拍照
                                        @Override
                                        public void onClick(View view) {
                                            map_choose_image.clear();
                                            map_choose_ma.clear();
                                            dialog.dismiss();
                                            log.e("当前选择image--条码" + map_choose_image.size() + "--" + map_choose_ma.size() + "全局的image--条码" + map_image.size() + "--" + map_ma.size());
                                        }
                                    }

                );
        dialog.findViewById(R.id.tv_true)
                .setOnClickListener(new View.OnClickListener() { //绑篮子
                                        @Override
                                        public void onClick(View view) {

                                            //把当前选的条码加入
                                            Set<String> get = map_choose_ma.keySet();
                                            for (String test : get) {
                                                String o = (String) map_choose_ma.get(test);
                                                log.e(test + "--" + o);
                                                if (!map_ma.containsKey(o)) {
                                                    map_ma.put(o, o);
                                                }
                                            }
                                            //把当前选的条码 image 加入 最终只加入一个
                                            Set<String> get_image = map_choose_image.keySet();//一个值
                                            for (String test_image : get_image) {
//                tiaoma = test_image;
                                                String image_id = (String) map_choose_image.get(test_image);//id 或 图片#id
                                                map_image.put(IMAGE, image_id);
                                            }
                                            String allMa = showAllTiao(map_ma);
                                            tvSerach.setText(allMa);
                                            map_choose_ma.clear();
                                            map_choose_image.clear();
                                            dialog.dismiss();
                                            log.e("当前选择image--条码" + map_choose_image.size() + "--" + map_choose_ma.size() + "全局的image--条码" + map_image.size() + "--" + map_ma.size());
                                        }
                                    }

                );
//        Window window = dialog.getWindow();
//        WindowManager m = getWindowManager();
//        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
//        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
////        p.height = (int) (d.getHeight() * 0.9); // 改变的是dialog框在屏幕中的位置而不是大小
//        p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.65
//        window.setAttributes(p);
    }

    /**
     * 所扫描条码集合
     *
     * @param map
     * @return
     */
    private String showAllTiao(Map map) {
        String alltiaoma = "";
        Set<String> get = map.keySet();
        for (String test : get) {
            String o = (String) map.get(test);
            alltiaoma += o + ",";
        }
        alltiaoma = StringUtils.stringSubEnds(alltiaoma);
        log.e(alltiaoma);
        return alltiaoma;
    }

    /**
     * 输入框展示 所有条码集合
     *
     * @param map
     * @return
     */
    private List<String> showListTiao(Map map) {
        List<String> list = new ArrayList<>();
        Set<String> get = map.keySet();
        for (String test : get) {
            String o = (String) map.get(test);
            list.add(o);
        }
        return list;
    }


    /**
     * 根据物品包id查询详细数据
     *
     * @param id
     */
    public void chaxun(String id) {
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
                    log.e("获取物品包明细--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<WuPinBean> list_qxmx = JSON.parseArray(result, WuPinBean.class);
                        setAdapter(list_qxmx);
                    } else {
                        ToastUtils.showLong(Constant.NODATA);
                    }
                } else {
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    //~~~~~~~~~~~~~~~~~~~ 生成新条码接  返回整条数据  开始  ~~~~~~~~~~~~~~~~


    //~~~~~~~~~~~~~~~~~~~ 生成新条码接  返回整条数据  结束  ~~~~~~~~~~~~~~~~

    //~~~~~~~~~~~~~~~~~~~ 拍照或更改    开始  ~~~~~~~~~~~~~~~~

    /**
     * 是否替换已经上传过的照片
     *
     * @param tiaoma
     */
    private void dialogUpImage(String tiaoma) {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setContentView(R.layout.dialog_del);//重点看这获取弹出框内的视图view
        TextView content = (TextView) dialog.findViewById(R.id.content);//重点看这行的Dialog
        content.setText(tiaoma + "物品包已拍照上传，是否要替换？");
        Button bt_yes = (Button) dialog.findViewById(R.id.bt_yes);//重点看这行的Dialog
        Button bt_no = (Button) dialog.findViewById(R.id.bt_no);//重点看这行的Dialog
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                takePotho();
            }
        });
        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    //-------------------拍照返回，结果上传------------------
    private String fileName = "";
    private String imageName = "";

    /**
     * 获取原图片存储路径
     *
     * @return
     */
    private String getPhotopath() {
        String pathUrl = Environment.getExternalStorageDirectory() + "/iamge/";
        imageName = System.currentTimeMillis() + ".jpg";
        File file = new File(pathUrl);
        if (!file.exists()) {
            file.mkdirs();
        }
        fileName = pathUrl + imageName;
        log.e(fileName);
        return fileName;
    }

    /**
     * 拍照--跳转页面
     */
    private void takePotho() {
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        File out = new File(getPhotopath());
        Uri uri = Uri.fromFile(out);
        // 获取拍照后未压缩的原图片，并保存在uri路径中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000 && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = ImageUtils.getSmallBitmap(fileName);
            takePhotoSave(tiaoma, bitmap);
        } else {
            log.e("未拍照");
            ToastUtils.showLong("未拍照");
        }
    }

    /**
     * 根据旧条码查询是否  生成新条码  、 是否已经拍照
     *
     * @param ma
     */
    private void takePhotoSave(String ma, Bitmap bitmap) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String base = ImageUtils.bitmapToString(bitmap);
        String s = Constant.STRING + ma + "|" + base;
        params.put(Constant.CODE, WSOpraTypeCode.物品包拍照保存);//上传照片 7621
        params.put(Constant.PARAMETER, s);
        log.e("物品包拍照保存" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();//扫描之后跳转
                if (hsms != null) {
                    log.e("制包单审核制包-生成新条码--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        ToastUtils.showLong("图片成功上传");
                        map_image.clear();
                    } else {
                        ToastUtils.showLong("图片上传失败");
                    }
                } else {
                    log.e(Constant.HSMS);
                }
            }
        });
    }


    //~~~~~~~~~~~~~~~~~~~ 拍照或更改   结束  ~~~~~~~~~~~~~~~~


    ////-====================================清洗篮扫描


    private void beforeLan() {
        log.e(map_ma.size() + "扫蓝时包的数量");
        if (map_ma.size() <= 0) {
            ToastUtils.showLong("请先扫描物品包");
        } else if (map_ma.size() == 1) {   //一个包
            String newTiaoMa = getDanMap(map_ma);
            dialog.show();
            getQingXiLan("", newTiaoMa);
        } else if (map_ma.size() > 1) {  //至少一个包
            //首先查出批次对应的物品包 各个在哪些篮子里
            String pici_ma = "";
            Set<String> get = map_ma.keySet();
            for (String test : get) {
                String o = (String) map_ma.get(test);
                pici_ma += "'" + o + "',";
            }
            pici_ma=StringUtils.stringSubEnds(pici_ma);
            dialog.show();
            getPiciToLan(pici_ma);
        }
    }

    //一个包一个蓝/2个篮   先获取 条码对应的清洗篮
    private void getPiciToLan(final String picima) {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + "|(" + picima + ")";
        params.put(Constant.CODE, WSOpraTypeCode.获取清洗篮中物品包);
        params.put(Constant.PARAMETER, s);
        log.e("获取批次对应的篮子的清洗记录--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("获取批次对应的篮子的清洗记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        final List<QingXiJiLu> list_picilan = JSON.parseArray(result, QingXiJiLu.class);
                        map_ma_lan.clear();//每次先清空再赋值
                        QingXiJiLu qxjl = null;
                        if (!list_picilan.get(0).getID().equals("")) {
                            for (int i = 0; i < list_picilan.size(); i++) {
                                qxjl = list_picilan.get(i);
                                if (map_ma_lan.containsKey(qxjl.getWupinBzzjlID())) {
                                    map_ma_lan.put(qxjl.getWupinBzzjlID(), 2);
                                } else {
                                    map_ma_lan.put(qxjl.getWupinBzzjlID(), 1);
                                }
                            }
                        }
                        getQingXiLan(qixilan, "");
                    } else {
                        ToastUtils.showLong(Constant.NODATA);
                    }
                } else {
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    //    private List<QingXiJiLu> list_ma_lan = new ArrayList<>();//批次码各个对应所在的篮子
    private Map map_ma_lan = new HashMap<>();
    ;//批次码各个对应所在的篮子

    //一个包一个蓝/2个篮   先获取 条码对应的清洗篮
    private void getQingXiLan(final String qxlan, final String xintiaoma) {
        HashMap<String, Object> params = new HashMap<>();
        String s = "";
        if (qxlan.equals("")) { //操作单个物品包
            s = Constant.STRING + "" + "|('" + xintiaoma + "')";
        } else if (xintiaoma.equals("")) {//批量操作两个物品包以上
            s = Constant.STRING + qxlan + "|" + "";
        }
//                s = Constant.STRING + "" + "|" + xintiaoma;
        params.put(Constant.CODE, WSOpraTypeCode.获取清洗篮中物品包);
        params.put(Constant.PARAMETER, s);
        log.e("获取清洗篮中物品包--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("获取批次对应的篮子的清洗记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        final List<QingXiJiLu> list_chalan = JSON.parseArray(result, QingXiJiLu.class);

                        final String lan = list_chalan.get(0).getQingXiLan();//用于判断篮子里是否空
                        log.e(lan + "---" + qixilan);
                        //判断批量  是否已绑定该篮
                        if (map_ma.size() > 1) {
                            tag = 1;//表明是批量扫描过篮子
                            piScanLan(list_chalan, lan);
                        } else {    //单个物品保操作
                            tag = 2;//表明单个扫描过篮子
                            if (list_chalan.size() == 1) {  //数量绑定一个篮子
                                String bzzjlID = list_chalan.get(0).getWupinBzzjlID();
                                if (lan.equals("")) {  //空，第一次绑定
                                    bangDingLan(null);
                                } else {             //第二次绑定  ｛编号不一样 --提示已绑定的篮子编号，是否继续绑定｝
                                    dismissLoading();
                                    if (lan.equals(qixilan)) {//｛篮子编号一样--提示是否撤销绑定｝
                                        dialogCheXiao(lan, bzzjlID);
                                    } else {                   //｛篮子编号不一样--修改还是增加｝
                                        dialogUpOrAdd(list_chalan, ONE, list_chalan.get(0).getQingXiLan());
                                    }
                                }
                            } else if (list_chalan.size() >= 2) {
                                dismissLoading();
                                if (list_chalan.get(0).getQingXiLan().equals(qixilan)) {//当前扫描篮子与第一个一样
                                    dialogCheXiao(list_chalan.get(0).getQingXiLan(), list_chalan.get(0).getWupinBzzjlID());
                                } else if (list_chalan.get(1).getQingXiLan().equals(qixilan)) {//当前扫描篮子与第二个一样
                                    dialogCheXiao(list_chalan.get(1).getQingXiLan(), list_chalan.get(1).getWupinBzzjlID());
                                } else {
                                    dialogUpdata(list_chalan, list_chalan.get(0).getQingXiLan());
                                }
                            }
                        }
                    } else {
                        ToastUtils.showLong(Constant.NODATA);
                    }
                } else {
                    log.e(Constant.HSMS);
                }
            }
        });
    }


    private final static String ONE = "one";
    //针对批量
    private final static String PICI = "pici";

    private int tag = 0;// 1为批量，0 为单个  //2表明单个扫描过篮子（清空保存的单个数据，保证每次都是一个条码绑定篮子）

    private void piScanLan(List<QingXiJiLu> list_chalan, String lan) {

        if (lan.equals("")) {   // 篮子空的 3种情况
            // 1，批次中所有条码绑定篮子数<=1，直接绑定  2，批次中部分条码==2,过滤之后绑定   3，批次全都==2，全部过滤
            String no_ma = "";
            List<QingXiJiLu> list_last = new ArrayList<>();
            Set<String> get = map_ma.keySet();//批次
            for (String test : get) {
                String o = (String) map_ma.get(test);
                if (map_ma_lan.containsKey(o)) {  //批次条码其中有绑定 1/2 篮子
                    int in = (int) map_ma_lan.get(o);
                    if (in == 2) {   //条码已经对应两个篮子
                        no_ma += o + ",";
                    } else {
                        list_last.add(new QingXiJiLu(o, qixilan, "", ""));
                    }
                } else {
                    list_last.add(new QingXiJiLu(o, qixilan, "", ""));
                }
            }
            if (no_ma.equals("")) {  //批次中没有绑定两个篮子的条码---无论是绑定过一个还是首次绑定--全部继续绑定
                bangDingLan(list_last);
            } else {               //批次中不符合条件的提示弹窗--过滤掉不符合的(包括全部不符合的)
                dismissLoading();
                dialogGuolvGPici(list_last, no_ma);
            }
        } else {    //扫描篮子不为空   要么绑定要么撤销
            //判断篮子里的包（条码）和批次条码相同个数
            int num = 0;
            for (int i = 0; i < list_chalan.size(); i++) {
                if (map_ma.containsValue(list_chalan.get(i).getWupinBzzjlID())) {
                    num++;
                }
            }
            if (num < map_ma.size()) {      //相同数量小于批次数量
                //对比 批次中的条码是否已经绑定过两个篮子----过滤掉
                String no_ma = "";
                Set<String> set1 = new HashSet<String>(); //不满足条件--绑定2篮
                List<String> list2 = new ArrayList<>();
                List<QingXiJiLu> list_last = new ArrayList<>();

//                if (list_chalan.size() < map_ma.size()) {      //篮子 1  批次 2  挑出 1   -----绑定
                Set<String> get = map_ma.keySet();
                for (String test : get) {
                    String o = (String) map_ma.get(test);
                    if (map_ma_lan.containsKey(o)) {  //批次条码其中有绑定 1/2 篮子
                        int in = (int) map_ma_lan.get(o);
                        if (in == 2) {   //条码已经对应两个篮子
                            no_ma += o + ",";
                            set1.add(o);
                        } else {
                            list2.add(o);
                        }
                    } else {
                        list2.add(o);
                    }
                }
                //把篮子所有条码保存起来
                Set<String> set = new HashSet<String>();
                for (int i = 0; i < list_chalan.size(); i++) {
                    String tiaoma = list_chalan.get(i).getWupinBzzjlID();
                    set.add(tiaoma);
                }
                for (int i = 0; i < list2.size(); i++) {
                    if (!set.contains(list2.get(i))) {
                        list_last.add(new QingXiJiLu(list2.get(i), qixilan, "", ""));
                    }
                }
//                }
                if (no_ma.equals("")) {  //批次中没有绑定两个篮子的条码---无论是绑定过一个还是首次绑定--全部继续绑定
                    bangDingLan(list_last);
                } else {               //批次中不符合条件的提示弹窗--过滤掉不符合的
                    dismissLoading();
                    dialogGuolvGPici(list_last, no_ma);
                }
            } else if (num == map_ma.size()) {  //相同数量等于批次数量
                dismissLoading();
                dialogCheXiao(qixilan, "");
            }
        }

    }

    /**
     * 提示 已经绑定两个篮子的
     *
     * @param list
     * @param noma
     */

    private void dialogGuolvGPici(final List<QingXiJiLu> list, String noma) {
        if (noma.endsWith(",")) {
            noma = noma.substring(0, noma.length() - 1);
        }
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        if (list.size() == 0 || list == null) {
            dialog.getWindow().setContentView(R.layout.dialog_tishi);//重点看这获取弹出框内的视图view
            TextView content = (TextView) dialog.findViewById(R.id.content);//重点看这行的Dialog
            Button bt_know = (Button) dialog.findViewById(R.id.bt_know);//重点看这行的Dialog
            String text = noma + "全都不满足批次绑定，请去单个绑定";
            SpannableStringBuilder span = new SpannableStringBuilder(text);
//            span.setSpan(new ForegroundColorSpan(Color.RED), 0, span.length() - 16, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//            span.setSpan(new AbsoluteSizeSpan(35), 0, span.length() - 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setText(span);
            bt_know.setOnClickListener(new View.OnClickListener() { //绑定
                @Override

                public void onClick(View view) {
                    dismissLoading();
                    dialog.dismiss();
                }
            });
        } else {
            dialog.getWindow().setContentView(R.layout.dialog_del);//重点看这获取弹出框内的视图view
            TextView content = (TextView) dialog.findViewById(R.id.content);//重点看这行的Dialog
            Button bt_yes = (Button) dialog.findViewById(R.id.bt_yes);//重点看这行的Dialog
            Button bt_no = (Button) dialog.findViewById(R.id.bt_no);//重点看这行的Dialog
            content.setText(noma + "不满足绑定" + qixilan + ",是否过滤继续绑定？");

            bt_yes.setOnClickListener(new View.OnClickListener() { //撤销
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
//                    showLoading(qingqiu);
                    cachedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            bangDingLan(list);
                        }
                    });
                }
            });

            bt_no.setOnClickListener(new View.OnClickListener() { //绑定
                @Override
                public void onClick(View view) {
                    dismissLoading();
                    dialog.dismiss();
                }
            });
        }
    }

    //一个包绑定一个篮子 ====多个包绑定一个篮子
    private void bangDingLan(final List<QingXiJiLu> list_pici) {  //已扫描的篮子或
        HashMap<String, Object> params = new HashMap<>();
        List<QingXiJiLu> list = new ArrayList<>();
        String json_lan = "";
        if (list_pici != null) {
            json_lan = JSON.toJSONString(list_pici, SerializerFeature.WriteNullStringAsEmpty);
        } else {
            if (map_ma.size() > 1 || tag == 1) {   //批量操作
                Set<String> get = map_ma.keySet();
                for (String test : get) {
                    list.add(new QingXiJiLu((String) map_ma.get(test), qixilan, "", ""));
                    log.e("批量操作" + test + "---" + map_ma.get(test));
                }
            } else {                //单个操作
                String xintiaoma = getDanMap(map_ma);
                list.add(new QingXiJiLu(xintiaoma, qixilan, "", ""));
            }
            json_lan = JSON.toJSONString(list, SerializerFeature.WriteNullStringAsEmpty);
        }
        String s = Constant.DATATABLE + json_lan;
        params.put(Constant.CODE, WSOpraTypeCode.物品包绑定清洗篮);
        params.put(Constant.PARAMETER, s);
        log.e("物品包绑定清洗篮" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("物品包绑定清洗篮--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        ToastUtils.showLong("成功绑定" + qixilan + "号清洗篮");
                    } else {
                        ToastUtils.showLong(Constant.NODATA);
                    }
                } else {
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    /**
     * 取出单个的map值
     *
     * @param map
     * @return
     */
    private String getDanMap(Map map) {
        String xintiaoma = "";
        Set<String> get = map.keySet();
        for (String test : get) {
            xintiaoma = (String) map.get(test);
        }
        return xintiaoma;
    }

    //展示是否撤销的dialog
    private void dialogCheXiao(final String lan, final String bzzjlID) {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
//        dialog.setCanceledOnTouchOutside(false);//yuan
        dialog.setCanceledOnTouchOutside(true);//点击外部消失
        dialog.getWindow().setContentView(R.layout.dialog_del);//重点看这获取弹出框内的视图view
        TextView content = (TextView) dialog.findViewById(R.id.content);//重点看这行的Dialog
        content.setText("是否撤销 " + lan + " 号清洗篮的绑定?");
        Button bt_yes = (Button) dialog.findViewById(R.id.bt_yes);//重点看这行的Dialog
        Button bt_no = (Button) dialog.findViewById(R.id.bt_no);//重点看这行的Dialog

        bt_yes.setOnClickListener(new View.OnClickListener() { //撤销
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                cheXiaoLan(lan, bzzjlID);
            }
        });

        bt_no.setOnClickListener(new View.OnClickListener() { //绑定
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    //修改还是增加dialog
    private void dialogUpOrAdd(final List<QingXiJiLu> list, final String tag, final String lan) {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setContentView(R.layout.dialog_del);//重点看这获取弹出框内的视图view
        TextView content = (TextView) dialog.findViewById(R.id.content);//重点看这行的Dialog
        content.setText("已绑定" + lan + "号清洗篮,是修改还是继续绑定？");
        Button bt_yes = (Button) dialog.findViewById(R.id.bt_yes);//重点看这行的Dialog
        Button bt_no = (Button) dialog.findViewById(R.id.bt_no);//重点看这行的Dialog
        bt_yes.setText("修改");
        bt_no.setText("继续绑定");
        bt_yes.setOnClickListener(new View.OnClickListener() { //替换
            @Override
            public void onClick(View view) {
                dialog.dismiss();
//                showLoading(qingqiu);
                cachedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tag.equals(PICI)) {  //修改 清空以前的，保存当前的
                            piciUpOne(lan);
                        } else {
                            upDataQingXiLan(list, tag);
                        }
                    }
                });

            }
        });

        bt_no.setOnClickListener(new View.OnClickListener() { //继续绑定
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                cachedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        bangDingLan(null);
                    }
                });

            }
        });
    }

    //已绑定两个 是否替换上一次扫描的清洗篮dialog
    private void dialogUpdata(final List<QingXiJiLu> list, final String lan) {
//        String lan = list.get(1).getQingXiLan();
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setContentView(R.layout.dialog_del);//重点看这获取弹出框内的视图view
        TextView content = (TextView) dialog.findViewById(R.id.content);//重点看这行的Dialog
        content.setText("已绑定两个，是否从" + lan + "号清洗篮更改成" + qixilan + "号清洗篮？");
        Button bt_yes = (Button) dialog.findViewById(R.id.bt_yes);//重点看这行的Dialog
        Button bt_no = (Button) dialog.findViewById(R.id.bt_no);//重点看这行的Dialog

        bt_yes.setOnClickListener(new View.OnClickListener() {//替换
            @Override
            public void onClick(View view) {
                dialog.dismiss();
//                showLoading(qingqiu);
                cachedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (list == null) {  //批量--改第三次为第一个篮子
                            piciUpOne(lan);
                        } else {
                            upDataQingXiLan(list, ONE);
                        }
                    }
                });

            }
        });

        bt_no.setOnClickListener(new View.OnClickListener() { //继续绑定
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 按批次更改篮子==先查询篮子所有物品，在挑选出来要修改的
     *
     * @param lan
     */
    private void piciUpOne(final String lan) {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + lan + "|" + "";
        params.put(Constant.CODE, WSOpraTypeCode.获取清洗篮中物品包);
        params.put(Constant.PARAMETER, s);
        log.e("获取清洗篮中物品包--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("获取清洗篮中物品包--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<QingXiJiLu> list_chalan = JSON.parseArray(result, QingXiJiLu.class);

                        final List<QingXiJiLu> list_pici = new ArrayList<>();//挑拣出来所要操作的批次
                        for (int i = 0; i < list_chalan.size(); i++) {
                            Set<String> get = map_ma.keySet();
                            for (String test : get) {
                                String o = (String) map_ma.get(test);
                                if (list_chalan.get(i).getWupinBzzjlID().equals(o)) {
                                    list_pici.add(list_chalan.get(i));
                                }
                            }
                        }
//                        showLoading(qingqiu);
                        cachedThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                upDataQingXiLan(list_pici, PICI);
                            }
                        });
                    } else {
                        ToastUtils.showLong(Constant.NODATA);
                    }
                } else {
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    //修改清洗蓝绑定
    private void upDataQingXiLan(final List<QingXiJiLu> list, final String tag) {
        HashMap<String, Object> params = new HashMap<>();
        if (tag.equals(PICI)) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setQingXiLan(qixilan);
            }
        } else if (tag.equals(ONE)) {
            if (list.size() == 1) {
                list.get(0).setQingXiLan(qixilan);
            } else if (list.size() == 2) {
                list.get(1).setQingXiLan(qixilan);
            }
        }

        String json_lan = JSON.toJSONString(list, SerializerFeature.WriteNullStringAsEmpty);
        String s = Constant.DATATABLE + json_lan;
        params.put(Constant.CODE, WSOpraTypeCode.物品包修改清洗篮);
        params.put(Constant.PARAMETER, s);
        log.e("物品包修改清洗篮--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("获取清洗篮中物品包--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        ToastUtils.showLong("已成功修改为" + qixilan + "清洗篮");
                    } else {
                        ToastUtils.showLong(Constant.NODATA);
                    }
                } else {
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    //撤销清洗蓝绑定
    private void cheXiaoLan(final String lan, final String bzzjlID) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        List<QingXiJiLu> list = new ArrayList<>();

        if (bzzjlID.equals("")) {//为空  批量操作
            Set<String> get = map_ma.keySet();
            for (String test : get) {
                String o = (String) map_ma.get(test);
                list.add(new QingXiJiLu(o, lan, "", ""));
            }
        } else {
            list.add(new QingXiJiLu(bzzjlID, lan, "", ""));
        }

        String json_lan = JSON.toJSONString(list, SerializerFeature.WriteNullStringAsEmpty);
        String s = Constant.DATATABLE + json_lan;
        params.put(Constant.CODE, WSOpraTypeCode.物品包解绑清洗篮);
        params.put(Constant.PARAMETER, s);
        log.e("物品包解绑清洗篮--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("获取清洗篮中物品包--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        ToastUtils.showLong("成功撤销" + qixilan + "号清洗篮的绑定");
                    } else {
                        ToastUtils.showLong(Constant.NODATA);
                    }
                } else {
                    log.e(Constant.HSMS);
                }
            }
        });
    }
//========================================绑定清洗蓝结束


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
                scanChuLi(data);
            } else {
                log.e(action);
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
            tiaoma = split[1];
            piCi(tiaoma);//扫码前判断
        } else if (head.equals(Constant.QXL)) {//篮
            String[] split = data.split("@");
            qixilan = split[1];
            beforeLan();
        } else {
            ToastUtils.showLong("核对是否是物品包或清洗篮条码");
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


//-------------------扫码开始，结果上传------------------

    //扫码前判断
    private void piCi(String data) {
        if (tag == 1 || tag == 2) { //单个/批量对应的数据
            map_ma.clear();//扫描过篮子  单个/批量条码清空
            tag = 0;
        }
        saoma(data);
    }

    private void saoma(String id) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + "ID=" + id;
        params.put(Constant.CODE, WSOpraTypeCode.获取物品包追踪记录);
        params.put(Constant.PARAMETER, s);
        log.e("获取物品包追踪记录--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("获取物品包追踪记录--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<QXBean> list_saoma = JSON.parseArray(result, QXBean.class);
                        bean = list_saoma.get(0);
                        if (bean.getID().equals("")) {
                            dismissLoading();
                            ToastUtils.showLong(Constant.NOTIAOMA);
                            return;
                        }
                        saoMaChuLi(bean);
                        wupinbaoname = bean.getWuPinBMC();
//                        showLoading(qingqiu);
                        cachedThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                chaxun(bean.getWuPinBID());
//                                getAllWuPinBao();//获取所有物品包
                            }
                        });
                    } else {
                        ToastUtils.showLong(Constant.NODATA);
                    }
                } else {
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    private String wuPinBID;//每个物品包id
    private String huiShouDID;//提交审核状态  单个回收单

    private void saoMaChuLi(QXBean bean) {
        //对比科室，物品包id名称
        Hsd_Mx hsd_mx = null;
        for (int i = 0; i < list_hsd_mx.size(); i++) {
            hsd_mx = list_hsd_mx.get(i);
            //科室相同  物品包id相同
            log.e("科室id" + bean.getLingYongKS() + "==" + hsd_mx.getShenQingKSID() + "  物品包id" + bean.getWuPinBID() + "==" + hsd_mx.getWuPinBID());
            if (bean.getLingYongKS().equals(hsd_mx.getShenQingKSID()) && bean.getWuPinBID().equals(hsd_mx.getWuPinBID())) {
                wuPinBID = hsd_mx.getWuPinBID();
                huiShouDID = hsd_mx.getHuiShouDID();  // s780 2手术包  s780  2贵重包 .....
                //假如 3个科室  1 1个物品包   2   2相同物品包  3  3个物品包
                String zzjl = StringUtils.stringNull(hsd_mx.getLiShiZZJLID());                       //  "##"
                String newzzjl = StringUtils.stringNull(hsd_mx.getNewZZJLID());                       //  "1#2#3"
                int keShiSL = hsd_mx.getKeShiSL();
                if (zzjl.contains(tiaoma)) {    //重复扫描
                    ToastUtils.showLong("物品包已扫描");
                    shengChengTiaoMa(tiaoma, hsd_mx);//生成新条码  再提交
                    return;
                } else {
                    if (hsd_mx.getIsScan() != 1) {
                        if (keShiSL == 1) {
                            if (zzjl.equals("")) {
                                hsd_mx.setIsScan(1);
                                hsd_mx.setLiShiZZJLID(tiaoma);//添加旧条码
                                hsd_mx.setGYSSL(hsd_mx.getGYSSL() + 1);//扫码制包数量+1
                                hsd_mx.setZhiBaoSYL(hsd_mx.getZhiBaoSYL() + 1);
                                shengChengTiaoMa(tiaoma, hsd_mx);//生成新条码  再提交
                                return;
                            }
                        } else if (keShiSL > 1) {
                            if (zzjl.equals("")) {          //第一个物品包满足
                                hsd_mx.setLiShiZZJLID(tiaoma);//添加旧条码
                                hsd_mx.setGYSSL(hsd_mx.getGYSSL() + 1);//扫码制包数量+1
                                hsd_mx.setZhiBaoSYL(hsd_mx.getZhiBaoSYL() + 1);

                                shengChengTiaoMa(tiaoma, hsd_mx);//生成新条码  再提交
                                return;
                            } else if (!zzjl.contains(Constant.FUHAO) && !zzjl.equals("")) { //仅仅有一条数据｛1 是首次 0    2  正常 ｝
                                hsd_mx.setLiShiZZJLID(zzjl + Constant.FUHAO + tiaoma);//添加旧条码
                                hsd_mx.setGYSSL(hsd_mx.getGYSSL() + 1);//扫码制包数量+1
                                hsd_mx.setZhiBaoSYL(hsd_mx.getZhiBaoSYL() + 1);

                                shengChengTiaoMa(tiaoma, hsd_mx);//生成新条码  再提交
                                return;
                            } else if (zzjl.contains(Constant.FUHAO)) {   // //包含#  ｛1 含有首次 0#0  2 正常1#2#｝
                                String[] split = zzjl.split(Constant.FUHAO);
                                if (split.length < keShiSL) {   //数量  3   1 首次 0#0   2 正常 1#2
                                    if (split.length + 1 == keShiSL) {
                                        hsd_mx.setIsScan(1);
                                    }
                                    hsd_mx.setGYSSL(hsd_mx.getGYSSL() + 1);//扫码制包数量+1
                                    hsd_mx.setZhiBaoSYL(hsd_mx.getZhiBaoSYL() + 1);
                                    hsd_mx.setLiShiZZJLID(zzjl + Constant.FUHAO + tiaoma);//添加旧条码
                                    shengChengTiaoMa(tiaoma, hsd_mx);// 生成条码  再提交提交
                                    return;
                                }
                            }
                        }
                    }//重复判断
                }//回收单是否审核掉 iscan
            }//根据科室和物品包id判断
        }//循环所有回收单明细
    }

    /**
     * 生成新条码
     *
     * @param ma
     * @param hsd
     */
    private void shengChengTiaoMa(String ma, final Hsd_Mx hsd) {
        dialog.show();
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + ma + "|" + userMsg.getID() + "||" + hsd.getHuiShouDID();//空的物品包照片
        params.put(Constant.CODE, WSOpraTypeCode.制包单审核制包);
        params.put(Constant.PARAMETER, s);
        log.e("制包单审核制包-生成新条码" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("制包单审核制包-生成新条码--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<QXBean> list_saoma = JSON.parseArray(result, QXBean.class);
                        if (!list_saoma.get(0).getID().equals("")) {
                            String newma = list_saoma.get(0).getID();
                            String image = StringUtils.stringNull(list_saoma.get(0).getWupinBaoImage());
                            addMaImage(image, newma, tiaoma);//添加
                            tvSerach.setText(showAllTiao(map_ma));


                            if(hsd.getIsScan()!=1){
                                String newzzjl = StringUtils.stringNull(hsd.getNewZZJLID());
                                log.e("--" + newzzjl);
                                if (newzzjl.equals("")) {
                                    hsd.setNewZZJLID(newma);
                                    log.e(newma + "--" + newzzjl);
                                } else {
                                    hsd.setNewZZJLID(newzzjl + Constant.FUHAO + newma);
                                }
                                isCommit();//提交
                            }
                        } else {
                            dismissLoading();
                            ToastUtils.showLong("未生成新条码");
                        }
                    } else {
                        dismissLoading();
                        ToastUtils.showLong(Constant.NODATA);
                    }
                } else {
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    private void isCommit() {
        int wpbidTotalNum = 0;//所有相同回收单数量｛s780 2手术包  s780  2贵重包｝
        int wpbidNum = 0;    //满足的数量  s780 2手术包 已扫完
        for (int j = 0; j < list_hsd_mx.size(); j++) {
            Hsd_Mx hsd = null;
            if (list_hsd_mx.get(j).getHuiShouDID().equals(huiShouDID)) {
                wpbidTotalNum++;
                hsd = list_hsd_mx.get(j);
                if (hsd.getIsScan() == 1) {
                    wpbidNum++;
                }
            }
        }
        if (wpbidTotalNum == wpbidNum && wpbidNum != 0) {
            QingDian qingDian = null;
            for (int k = 0; k < list_huisd.size(); k++) {
                qingDian = list_huisd.get(k);
                if (qingDian.getHuiShouDID().equals(huiShouDID)) {
                    qingDian.setShenHeZT(1);
                    qingDian.setShenHeKS(userMsg.getKeShiMC());
                    qingDian.setShenHeKSID(userMsg.getKeShiID());
                    qingDian.setShenHeRen(userMsg.getYongHuXM());
                    qingDian.setShenHeRenID(userMsg.getID());
                    qingDian.setShenHeSJ(dateUtil.getYear_Day());

                    saveState(list_hsd_mx, list_huisd);
                    log.e("更新");
                    return;
                }
            }
        } else {
            saveState(list_hsd_mx, list_huisd);
        }
    }

    /**
     * 提交每个物品包扫描成功的状态，以及所对应的回收单
     *
     * @param list_wupb
     * @param list_huisd
     */
    private void saveState(List<Hsd_Mx> list_wupb, List<QingDian> list_huisd) {
        HashMap<String, Object> params = new HashMap<>();
        //                QingDian qd = null;
//                for (int i = 0; i < list_huisd.size(); i++) {
//                    qd = list_huisd.get(i);
//
//                    qd.setShenHeZT(0);
//                    qd.setShenHeKS("");
//                    qd.setShenHeKSID("");
//                    qd.setShenHeRen("");
//                    qd.setShenHeRenID("");
//                    qd.setShenHeSJ("");
//                }
//
//                for (int i = 0; i < list_wupb.size(); i++) {
//                    list_wupb.get(i).setLiShiZZJLID("");
//                    list_wupb.get(i).setIsScan(0);
//                }
        //---------
        String json_huisd = JSON.toJSONString(list_huisd, SerializerFeature.WriteNullStringAsEmpty);
        String json_wupb = JSON.toJSONString(list_wupb, SerializerFeature.WriteNullStringAsEmpty);

        String s = Constant.DATASET + json_huisd + "§" + json_wupb; //另包433    贵重 361
        params.put(Constant.CODE, WSOpraTypeCode.更新回收单和明细);
        params.put(Constant.PARAMETER, s);
        log.e("更新回收单和明细--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("更新回收单和明细--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        log.e("更新成功");
                    } else {
                        ToastUtils.showLong(Constant.NODATA);
                    }
                } else {
                    log.e(Constant.HSMS);
                }
            }
        });
    }

    private QXBean bean = null;//追踪条码实体类

    private void setAdapter(List<WuPinBean> list_qxmx) {
        total = 0;
        flShow.setVisibility(View.VISIBLE);
        for (int i = 0; i < list_qxmx.size(); i++) {
            total += list_qxmx.get(i).getShuLiang();
        }
        tvType.setText(wupinbaoname);//物品包名称
        tvNumber.setText("共" + total + "件");//物品包里面

        rcyWuDetail.setLayoutManager(new LinearLayoutManager(this));
        rcyWuDetail.setAdapter(new CommonAdapter<WuPinBean>(this, R.layout.item_wupinbao, list_qxmx) {
            @Override
            protected void convert(ViewHolder holder, final WuPinBean bean, final int position) {
                holder.setText(R.id.tv_item_name, bean.getQiXieMC());
                holder.setText(R.id.tv_item_num, bean.getShuLiang() + "");
                holder.setText(R.id.tv_wpbmx_dw, getDanWei(bean.getDanWei()));
            }
        });
    }

//    private void getAllWuPinBao() {
//        HashMap<String, Object> params = new HashMap<>();
//        String s = "string¤ShenQingSJ>='" + dateUtil.getDate() + " 00:00:00'";
////        String s = "string¤ShenQingSJ>='" + "2018-3-12 00:00:00'";
//        params.put(Constant.CODE, WSOpraTypeCode.获取回收单对应明细);
//        params.put(Constant.PARAMETER, s);
//        log.e("获取回收单对应明细--" + s);
//        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
//            @Override
//            public void callBack(Hsms hsms) {
//                dismissLoading();
//                if (hsms != null) {
//                    log.e("获取清洗篮中物品包--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
//                    if (hsms.getReturnCode() == 0) {
//                        String result = hsms.getReturnJson();
//                        list_all_wpb = JSON.parseArray(result, Hsd_Mx.class);
//                        Hsd_Mx hsd_mx = null;
//
//                        int state = 0; //判断是否属于当前所有物品包
//                        for (int i = 0; i < list_all_wpb.size(); i++) {
//                            hsd_mx = list_all_wpb.get(i);
//                            //科室相同  物品包id相同
//                            if (bean.getLingYongKS().equals(hsd_mx.getShenQingKSID()) && bean.getWuPinBID().equals(hsd_mx.getWuPinBID())) {
//                                state = 1;
//                                //假如 3个科室  1 1个物品包   2   2相同物品包  3  3个物品包
//                                String zzjl = hsd_mx.getLiShiZZJLID();
//                                int keShiSL = hsd_mx.getKeShiSL();
////
////                                String[] split = zzjl.split(Constant.FUHAO);
////                                if(keShiSL==split.length){
////                                    continue;
////                                }
//
//                                if (keShiSL > 1) {  //相同科室相同物品包 数量大于1
//                                    if (zzjl.equals("")) {          //第一个物品包满足
//                                        hsd_mx.setPostTiaoMa(tiaoma);
//                                        EventBus.getDefault().post(hsd_mx);//返回之前页面交互
////                                        getAllWuPinBao();//获取所有物品包
//                                        return;
//                                    } else {
//                                        if (zzjl.contains(Constant.FUHAO)) {  //至少 两个以上
//                                            String[] split = zzjl.split(Constant.FUHAO);
//
//                                            for (int j = 0; j < split.length; j++) {
//                                                if (bean.getID().equals(split[j])) {//条码已扫描
//                                                    ToastUtils.showLong("物品包已扫描");
//                                                    return;
//                                                }
//                                            }
//                                            if (keShiSL == split.length) { //全部扫描完，进入下次循环
//                                                continue;
//                                            } else if (keShiSL > split.length) { //科室数量大于 物品包数量---物品包未扫描
//                                                hsd_mx.setPostTiaoMa(tiaoma);
//                                                EventBus.getDefault().post(hsd_mx);//返回之前页面交互
////                                                getAllWuPinBao();//获取所有物品包
//                                                return;
//                                            }
//                                        } else {                   //两个以上物品包满足
//                                            if (keShiSL == 2) {
//                                                hsd_mx.setPostTiaoMa(tiaoma);
//                                                EventBus.getDefault().post(hsd_mx);//返回之前页面交互
////                                                getAllWuPinBao();//获取所有物品包
//                                                return;
//                                            }
//
//                                            if (bean.getID().equals(zzjl)) {
//                                                ToastUtils.showLong("物品包已扫描");
//                                                return;
//                                            } else {
//                                                hsd_mx.setPostTiaoMa(tiaoma);
//                                                EventBus.getDefault().post(hsd_mx);//返回之前页面交互
////                                                getAllWuPinBao();//获取所有物品包
//                                                return;
//                                            }
//                                        }
//                                    }
//                                } else if (keShiSL == 1) {   //  ==1
//                                    if (zzjl.equals(bean.getID())) {  //扫描的是同一个物品包
////                                        startIntent();
//                                        ToastUtils.showLong("物品包已扫描");
//                                        return;
//                                    } else if (zzjl.equals("")) {
//                                        hsd_mx.setPostTiaoMa(tiaoma);
//                                        EventBus.getDefault().post(hsd_mx);//返回之前页面交互
////                                        getAllWuPinBao();//获取所有物品包
//                                        return;
//                                    } else {
//                                        continue;
//                                    }
//                                }
//                            }
//                        }
//                        if (state == 0) {
//                            ToastUtils.showLong("物品包不属于当前批次");
//                        }
//                    } else {
//                        ToastUtils.showLong(Constant.NODATA);
//                    }
//                } else {
//                    log.e(Constant.HSMS);
//                }
//            }
//        });
//    }
}
