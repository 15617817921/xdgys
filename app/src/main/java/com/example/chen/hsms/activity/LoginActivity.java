package com.example.chen.hsms.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.chen.hsms.R;
import com.example.chen.hsms.application.MyApplication;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.bean.data.UserMsg;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.NetUtils;
import com.example.chen.hsms.utils.SaveUtils;
import com.example.chen.hsms.utils.ToastUtils;
import com.example.chen.hsms.webserviceutils.WebServiceUtils;
import com.example.chen.hsms.ws.Hsms;
import com.example.chen.hsms.ws.WSOpraTypeCode;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.bt_login)
    Button btLogin;
    @BindView(R.id.tv_set)
    TextView tvSet;
    @BindView(R.id.ll_username)
    LinearLayout llUsername;
    @BindView(R.id.ll_pas)
    LinearLayout llPas;

    MyApplication app;
    @BindView(R.id.ll)
    LinearLayout ll;
    private String uesrname;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        app = new MyApplication();

    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
//        getRealSize();
        llUsername.getBackground().setAlpha(80);//0~255透明度值
        llPas.getBackground().setAlpha(80);//0~255透明度值
    }


    public void getRealSize() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        log.e("尺寸大小" + point.toString());
    }

    @Override
    public void initDatas() {
        if (SaveUtils.contains(mContext, Constant.UESRNAME)) {
            uesrname = (String) SaveUtils.get(mContext, Constant.UESRNAME, "");
            password = (String) SaveUtils.get(mContext, Constant.PASSWORD, "");
            etUsername.setText(uesrname);
            etPassword.setText(password);
            etUsername.setSelection(etUsername.length());
            etPassword.setSelection(etPassword.length());
        }
    }

    @Override
    public void initListeners() {
    }

    @OnClick({R.id.bt_login, R.id.tv_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                uesrname = etUsername.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(uesrname) || TextUtils.isEmpty(password)) {
                    mSVProgressHUD.showInfoWithStatus(Constant.USERNULL);
                } else {
                    dialog.setMessage(Constant.LOGIN);
                    dialog.show();
                    cachedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            dlrequest();
                        }
                    });
                }
                break;
            case R.id.tv_set:
                gotoAtivity(SetActivity.class, null);
                break;
        }
    }

    /**
     * 请求登录
     */
    public void dlrequest() {
        if (!SaveUtils.contains(mContext, "url")) {
            dismissLoading();
            return;
        }
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + uesrname + "|" + password;
        params.put(Constant.CODE, WSOpraTypeCode.验证用户);
        params.put(Constant.PARAMETER, s);
        log.e("验证用户--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                if (hsms != null) {
                    log.e("验证用户--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        SaveUtils.put(mContext, Constant.UESRNAME, uesrname);
                        SaveUtils.put(mContext, Constant.PASSWORD, password);
//                        showLoading("获取用户信息");
                        getUserMsg();
                    } else {
                        dismissLoading();
                        ToastUtils.showLong("没有该用户或密码错误");
                        log.e(Constant.IP);
                    }
                } else {
                    log.e(Constant.HSMS);
                }
            }
        });
    }


    private void getUserMsg() {
        HashMap<String, Object> params = new HashMap<>();
        String s = Constant.STRING + uesrname;
        params.put(Constant.CODE, WSOpraTypeCode.获取用户信息);
        params.put(Constant.PARAMETER, s);
        log.e("获取用户信息--" + s);
        WebServiceUtils.callWebService(dialog, NetUtils.isNetAvailable(mContext), params, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(Hsms hsms) {
                dismissLoading();
                if (hsms != null) {
                    log.e("获取用户信息--" + hsms.getResturnMsg() + "--" + hsms.getReturnID() + "--" + hsms.getReturnCode() + "--" + hsms.getReturnJson());
                    if (hsms.getReturnCode() == 0) {
                        String result = hsms.getReturnJson();
                        List<UserMsg> userMsgList = JSON.parseArray(result, UserMsg.class);
                        if (userMsgList.size() == 1) {
                            MyApplication.getInstance().setUserMsg(userMsgList.get(0));
                            ToastUtils.showShort(Constant.SUCCSE);
                            gotoAtivity(HomeActivity.class, null);
                            finish();
                        } else {
                            chooseDialog(userMsgList);
                        }
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
     * 多个科室对话框
     *
     * @param userMsgList
     */
    private void chooseDialog(final List<UserMsg> userMsgList) {
        final String[] names = new String[userMsgList.size()];
        int i = 0;
        for (UserMsg x : userMsgList) {
            names[i] = x.getKeShiMC();
            i++;
        }

        new AlertDialog.Builder(LoginActivity.this).setTitle("选择科室")

                .setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.getInstance().setUserMsg(userMsgList.get(which));

                        ToastUtils.showShort("登录成功");
//                        Bundle b = new Bundle();
//                        b.putString("GongNengQX", gongNengQX);
//                        log.e(gongNengQX);
                        gotoAtivity(HomeActivity.class, null);
                        finish();
                    }
                }).setNegativeButton("取消", null).show();
    }
}
