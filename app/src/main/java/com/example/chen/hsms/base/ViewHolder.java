package com.example.chen.hsms.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
	private SparseArray<View> array;
	private int mPosition;
	private View mConvertView;
	private Context context;

	private ViewHolder(Context context, ViewGroup parent, int layoutId,
					   int position) {
		this.context = context;
		mPosition = position;
		this.array = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		mConvertView.setTag(this);
	}

	public static ViewHolder getHolder(Context context, View convertView,
									   ViewGroup parent, int layoutId, int position) {
		if (convertView == null) {
			return new ViewHolder(context, parent, layoutId, position);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.mPosition = position;
		return holder;
	}

	public <T extends View> T getView(int viewId) {
		View view = array.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			array.put(viewId, view);
		}
		return (T) view;
	}

	public View getConvertView() {
		return mConvertView;
	}

	public int getPosition() {
		return mPosition;
	}

	public ViewHolder setText(int viewId, String text) {
		TextView textView = getView(viewId);
		textView.setText(text);
		return this;
	}


	public void setTextAndImage(int viewId, int imageId, String text) {
		TextView textView = getView(viewId);
		textView.setText(text);
		Drawable drawable = context.getResources().getDrawable(imageId);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		textView.setCompoundDrawables(drawable, null, null, null);
		
	}

	public ViewHolder setImageSrc(int viewId, int drawableId) {
		ImageView imageView = getView(viewId);
		imageView.setImageResource(drawableId);
		return this;
	}
	public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
		ImageView imageView = getView(viewId);
		imageView.setImageDrawable(drawable);
		return this;
	}
}
