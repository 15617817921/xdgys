package com.example.chen.hsms.base;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {
	protected String tag;
	protected Context mContext;
	protected List<T> mDatas;
	protected int layoutId;
	protected LayoutInflater mInflater;
	public CommonAdapter(Context context, List<T> mDatas, int layoutId,String tag) {
		this.tag= tag;
		this.mContext= context;
		this.mDatas = mDatas;
		this.layoutId = layoutId;
		this.mInflater= LayoutInflater.from(context);
	}
	public CommonAdapter(Context context, int layoutId , List<T> mDatas ) {
		this.mContext= context;
		this.mDatas =  mDatas;
		this.layoutId = layoutId;
		this.mInflater= LayoutInflater.from(context);
	}
	public CommonAdapter(Context context, List<T> mDatas ,int layoutId) {
		this.mContext= context;
		this.mDatas =  mDatas;
		this.layoutId = layoutId;
		this.mInflater= LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		Log.i("Tag", mDatas.size() + "");
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {

		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	public List<T> getmDatas() {
		return mDatas;
	}
	public void setData(List<T> mDatas){

		this.mDatas=mDatas;
		notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.getHolder(mContext, convertView, parent,layoutId, position);
		convert(holder, mDatas,position,tag);
		return holder.getConvertView();
	}

	public abstract void convert(ViewHolder holder, List<T> t, int position, String tag);
}