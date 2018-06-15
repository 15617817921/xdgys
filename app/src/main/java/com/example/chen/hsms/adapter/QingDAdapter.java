package com.example.chen.hsms.adapter;

import android.content.Context;

import com.example.chen.hsms.R;
import com.example.chen.hsms.base.CommonAdapter;
import com.example.chen.hsms.base.ViewHolder;
import com.example.chen.hsms.bean.local.QXBeanWP;
import com.example.chen.hsms.utils.Constant;
import com.example.chen.hsms.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/9/26.
 */

public class QingDAdapter extends CommonAdapter<QXBeanWP> {

    public QingDAdapter(Context context, int layoutId, List<QXBeanWP> mDatas) {
        super(context, layoutId, mDatas);
    }

    @Override
    public void convert(ViewHolder holder, List<QXBeanWP> t, int position, String tag) {
        QXBeanWP bean = t.get(position);
        int num = bean.getNum();
        String lszzjl = StringUtils.stringNull(bean.getLiShiZZJLID());
        if (lszzjl.equals("0")) {
            holder.setText(R.id.tv_item_title, bean.getWp_name() + Constant.SCB);
        } else if (lszzjl.contains(Constant.FUHAO)) {
            String[] split = lszzjl.split(Constant.FUHAO);
            boolean contains = Arrays.asList(split).contains("0");
            if (contains) {
                holder.setText(R.id.tv_item_title, bean.getWp_name() + Constant.SCB);
            } else {
                holder.setText(R.id.tv_item_title, bean.getWp_name());
            }
        }
        holder.setText(R.id.tv_item_num, num + "");
    }

}
