package com.example.chen.hsms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chen.hsms.R;
import com.example.chen.hsms.bean.local.Bean;
import com.example.chen.hsms.view.CoustomListView;

import java.util.List;

/**
 * Created by admin on 2017/7/17.
 */

public class FaFangTotalAdapter extends RecyclerView.Adapter<FaFangTotalAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Bean> list;
    private ViewHolder viewHolder;
    private Context mContext;
    private View view;
    private ListViewAdapter adapter;

    public FaFangTotalAdapter(Context context, List<Bean> datats) {
        mLayoutInflater = LayoutInflater.from(context);
        list = datats;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = mLayoutInflater.inflate(R.layout.item_fafang_total_rcy, parent, false);
        viewHolder = new ViewHolder(view);
        viewHolder.tv_keshi = (TextView) view.findViewById(R.id.tv_keshi);
        viewHolder.lv_fafang = (CoustomListView) view.findViewById(R.id.lv_fafang);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Bean bean = list.get(position);
        holder.tv_keshi.setText(bean.gettitle());
        adapter = new ListViewAdapter(mContext, bean.getS(), R.layout.rcy_item_lv);
        holder.lv_fafang.setAdapter(adapter);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }
        TextView tv_keshi;
        ListView lv_fafang;
    }
}
