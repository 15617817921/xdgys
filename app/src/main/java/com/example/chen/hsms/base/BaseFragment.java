package com.example.chen.hsms.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.bigkoo.svprogresshud.SVProgressHUD;
import com.example.chen.hsms.R;
import com.example.chen.hsms.utils.MyLogger;

import butterknife.ButterKnife;


/**
 * Fragment基类
 * Created by qwp on 2016/5/6.
 */
public class BaseFragment extends Fragment {

    protected Context mContext;
    protected LayoutInflater mInflater;
    private ProgressDialog dialog;
    protected SVProgressHUD mSVProgressHUD;
    protected MyLogger log;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mSVProgressHUD = new SVProgressHUD(mContext);
        log = MyLogger.kLog();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    protected final <E extends View> E getView(View parent, int id) {
        try {
            return (E) parent.findViewById(id);
        } catch (ClassCastException ex) {

            throw ex;
        }
    }

    public void start_activity(Intent intent) {
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void gotoAtivity(Class clazz, Bundle bundle) {
        Intent it = new Intent(getActivity(), clazz);
        if (bundle != null) {
            it.putExtra("bundle", bundle);
        }
        startActivity(it);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void showLoading(String content) {
        if (dialog != null && dialog.isShowing()) return;
        dialog = new ProgressDialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(content);
        dialog.show();
    }

    public void dismissLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
