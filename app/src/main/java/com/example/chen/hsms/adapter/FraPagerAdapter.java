package com.example.chen.hsms.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.chen.hsms.fragment.QueryFragment;
import com.example.chen.hsms.fragment.TotalFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/7/17.
 */

public class FraPagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 2;
    private String[] tableTitle = new String[]{"发放汇总", "请领查询"};
    private Context mContext;
    private List<Fragment> mFragmentTab;
    private TotalFragment ffhzFragment;
    private QueryFragment qlcxFragment;

    public FraPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        initFragmentTab();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentTab.get(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tableTitle[position];
    }

    private void initFragmentTab() {
        ffhzFragment = new TotalFragment();
        qlcxFragment = new QueryFragment();
        mFragmentTab = new ArrayList<Fragment>();
        mFragmentTab.add(ffhzFragment);
        mFragmentTab.add(qlcxFragment);
    }
}

