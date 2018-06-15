package com.example.chen.hsms.adapter;

import android.content.Context;

import com.example.chen.hsms.R;
import com.example.chen.hsms.base.CommonAdapter;
import com.example.chen.hsms.base.ViewHolder;
import com.example.chen.hsms.bean.local.HomeBean;

import java.util.List;

/**
 * Created by admin on 2017/7/6.
 */

public class HomeAdapter extends CommonAdapter<HomeBean> {
    public HomeAdapter(Context context, List<HomeBean> mDatas, int layoutId) {
        super(context, mDatas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, List<HomeBean> t, int position, String tag) {
        HomeBean bean = t.get(position);
        holder.setImageDrawable(R.id.iv_icon, bean.getDrawable());
        holder.setText(R.id.tv_text, bean.getName());
    }

}
