package com.example.chen.hsms.adapter;

import android.content.Context;

import com.example.chen.hsms.R;
import com.example.chen.hsms.base.CommonAdapter;
import com.example.chen.hsms.base.ViewHolder;
import com.example.chen.hsms.bean.local.QXBeanWP;

import java.util.List;

/**
 * Created by Administrator on 2018/3/28.
 */

public class QingDSciAdapter extends CommonAdapter<QXBeanWP> {

    public QingDSciAdapter(Context context, int layoutId, List<QXBeanWP> mDatas) {
        super(context, layoutId, mDatas);
    }

    @Override
    public void convert(ViewHolder holder, List<QXBeanWP> t, int position, String tag) {
        QXBeanWP bean = t.get(position);
        int num = bean.getNum();
        holder.setText(R.id.tv_item_title, bean.getWp_name());
        holder.setText(R.id.tv_item_num, num + "");
    }

//    @Override
//    public void convert(ViewHolder holder, QXBeanWP t, int position, String tag) {
//        Hsd_Mx bean = t.get(position);
////        int num = bean.getNum();
//        holder.setText(R.id.tv_item_title, bean.getWuPinBMC());
////        holder.setText(R.id.tv_item_num, bean. + "");
//    }

}

//每一个包的信息  相同两个包并列显示（不合并）
//public class QingDSciAdapter extends CommonAdapter<Hsd_Mx> {
//
//    public QingDSciAdapter(Context context, int layoutId, List<Hsd_Mx> mDatas) {
//        super(context, layoutId, mDatas);
//    }
//
//    @Override
//    public void convert(ViewHolder holder, List<Hsd_Mx> t, int position, String tag) {
//        Hsd_Mx bean = t.get(position);
////        int num = bean.getNum();
//        holder.setText(R.id.tv_item_title, bean.getWuPinBMC());
////        holder.setText(R.id.tv_item_num, bean. + "");
//    }
//
//}
