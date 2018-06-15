package com.example.chen.hsms.adapter;

import android.content.Context;

import com.example.chen.hsms.R;
import com.example.chen.hsms.base.CommonAdapter;
import com.example.chen.hsms.base.ViewHolder;
import com.example.chen.hsms.bean.local.Bean;

import java.util.List;

/**
 * Created by admin on 2017/7/17.
 */

public class ListViewAdapter extends CommonAdapter<Bean.SBean> {


    public ListViewAdapter(Context context, List<Bean.SBean> mDatas, int layoutId) {
        super(context, mDatas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, List<Bean.SBean> t, int position, String tag) {
        Bean.SBean bean = t.get(position);
        holder.setText(R.id.tv_item_num, bean.getnum());
        holder.setText(R.id.name, bean.getmz());
    }

}
