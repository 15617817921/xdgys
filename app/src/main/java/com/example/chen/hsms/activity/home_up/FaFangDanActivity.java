package com.example.chen.hsms.activity.home_up;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chen.hsms.R;
import com.example.chen.hsms.adapter.FraPagerAdapter;
import com.example.chen.hsms.base.BaseActivity;
import com.example.chen.hsms.utils.BarUtils;
import com.example.chen.hsms.view.NoPreloadViewPager;


import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 请领发放订单
 * 两个fragment
 */
public class FaFangDanActivity extends BaseActivity {

    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
//    NoPreloadViewPager viewpager;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_srkuang)
    LinearLayout llSrkuang;


    @Override
    public int setLayoutId() {
        return R.layout.activity_fa_fang_dan;
    }

    @Override
    protected void initView() {
        BarUtils.setColorForSwipeBack(this, ContextCompat.getColor(mContext, R.color.state), 0);

//        initToolBar("请领发放单", ContextCompat.getColor(mContext, R.color.white));
    }

    @Override
    public void initDatas() {
        llSrkuang.setVisibility(View.GONE);
        tvTitle.setText(getResources().getText(R.string.qinglingffd));

        initTabLayout();
        tablayout.post(new Runnable() {
            @Override
            public void run() {
                setIndicator(tablayout, 30, 30);
            }
        });
    }

    @Override
    public void initListeners() {
    }

    private void initTabLayout() {
        FraPagerAdapter adapter = new FraPagerAdapter(getSupportFragmentManager(), this);
//        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
//        TabLayout tablayout = (TabLayout) findViewById(R.id.tablayout);
        viewpager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewpager);
        tablayout.setTabMode(TabLayout.MODE_FIXED);
    }

    public void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
