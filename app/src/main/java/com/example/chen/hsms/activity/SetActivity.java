package com.example.chen.hsms.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.hsms.R;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.utils.MyLogger;
import com.example.chen.hsms.utils.NetUtils;

import com.example.chen.hsms.utils.SaveUtils;
import com.example.chen.hsms.utils.ToastUtils;


import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.log.Logger;

public class SetActivity extends BaseActivity {

    @BindView(R.id.et_lianjie)
    EditText etLianjie;
    @BindView(R.id.bt_test)
    TextView btTest;
    @BindView(R.id.cb_moreset)
    CheckBox cbMoreset;
    @BindView(R.id.et_hos_name)
    EditText etHosName;
    @BindView(R.id.et_clientid)
    EditText etClientid;
    @BindView(R.id.et_jiqi)
    EditText etJiqi;
    @BindView(R.id.cb_start)
    CheckBox cbStart;
    @BindView(R.id.tv_sure)
    TextView tvSure;
    @BindView(R.id.iv_set_back)
    ImageView ivSetBack;

    @Override
    public int setLayoutId() {
        return R.layout.activity_set;
    }

    @Override
    protected void initView() {
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.state), 0);
//        initToolBar("设置",  ContextCompat.getColor(mContext,R.color.white));
    }

    @Override
    public void initDatas() {
        etLianjie.setSelection(etLianjie.length());
    }

    @Override
    public void initListeners() {
        if (SaveUtils.contains(mContext, "url")) {
            etLianjie.setText((String) SaveUtils.get(mContext, "url", ""));
            etLianjie.setSelection(etLianjie.length());
        }
    }


    @OnClick({R.id.bt_test, R.id.cb_moreset, R.id.cb_start, R.id.tv_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_test:
                hitKeyboard(view);
                if (NetUtils.isNetAvailable(mContext)) {
                    showLoading("请求中...");
                    singleThread.execute(new Runnable() {
                        @Override
                        public void run() {
                            testUrl();
                        }
                    });
                } else {
                    ToastUtils.showLong("网络似乎开小差了");
                }
                break;
            case R.id.cb_moreset:
                if (cbMoreset.isChecked()) {
                    ToastUtils.showLong("更多设置按钮开启");
                } else {
                    ToastUtils.showLong("更多设置按钮关闭");
                }
                break;
            case R.id.cb_start:
                if (cbStart.isChecked()) {
                    ToastUtils.showLong("启动按钮开启");
                } else {
                    ToastUtils.showLong("启动按钮关闭");
                }
                break;
            case R.id.tv_sure:
                ToastUtils.showLong("确定");
                break;
            default:
                break;
        }
    }

    //测试网络连接
    public void testUrl() {
        try {
            dismissLoading();
            String path = etLianjie.getText().toString().trim();
            // 新建一对象
            URL url = null;
            url = new URL(path);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接超时时间
            urlConn.setConnectTimeout(5 * 1000);
            // 开始连接
            urlConn.connect();
            // 判断请求是否成功
            log.e("请求码--"+urlConn.getResponseCode());
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
//            byte[] data = readStream(urlConn.getInputStream());
//            Log.i(TAG_GET, "Get方式请求成功，返回数据如下：");
//            Log.i(TAG_GET, new String(data, "UTF-8"));
                if (SaveUtils.contains(mContext, "url")) {
                    SaveUtils.remove(mContext, "url");
                }
                SaveUtils.put(mContext, "url", path);
                toast("测试成功");
                finish();
            } else if(urlConn.getResponseCode() == 404) {
                toast("您访问的页面不存在");
            }else {
                toast("网址不通请核对是否正确");
            }
            log.e(path);
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            log.e(e.getMessage());
            toast("请检查链接是否正确");
        }
    }

    private void toast(final String content) {
        Handler handler=  new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showLong(content);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.iv_set_back)
    public void onViewClicked() {
        finish();
    }
}
