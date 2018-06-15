package com.example.chen.hsms.adapter;

import android.content.Context;


import com.example.chen.hsms.R;


import com.example.chen.hsms.base.CommonAdapter;
import com.example.chen.hsms.base.ViewHolder;
import com.example.chen.hsms.bean.data.QXBean;



import java.util.List;

/**
 * Created by Administrator on 2017/9/5 0005.
 */

public class MeiJunAdapter extends CommonAdapter<QXBean> {
    public MeiJunAdapter(Context context, int layoutId, List<QXBean> mDatas) {
        super(context, layoutId, mDatas);
    }

    @Override
    public void convert(ViewHolder holder, List<QXBean> t, int position, String tag) {
        QXBean bean = t.get(position);
        holder.setText(R.id.tv_ordernum, bean.getID());
        holder.setText(R.id.tv_item_title, bean.getWuPinBMC());
    }
}