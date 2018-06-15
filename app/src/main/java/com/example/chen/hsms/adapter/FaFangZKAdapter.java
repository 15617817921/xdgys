package com.example.chen.hsms.adapter;

import android.content.Context;

import com.example.chen.hsms.R;
import com.example.chen.hsms.base.CommonAdapter;
import com.example.chen.hsms.base.ViewHolder;
import com.example.chen.hsms.bean.local.QXBeanWP;

import java.util.List;

/**
 * Created by Administrator on 2017/9/4 0004.
 */

public class FaFangZKAdapter extends CommonAdapter<QXBeanWP> {
    public FaFangZKAdapter(Context context, int layoutId, List<QXBeanWP> mDatas) {
        super(context, layoutId, mDatas);
    }

    @Override
    public void convert(ViewHolder holder, List<QXBeanWP> t, int position, String tag) {
//        holder.setText(R.id.tv_ordernum, t.get(position).getWp_id());
        holder.setText(R.id.name, t.get(position).getWp_name());
        holder.setText(R.id.tv_item_num, t.get(position).getList().size() + "");
    }

}

