package com.example.chen.hsms.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.chen.hsms.R;


/**
 * Created by Administrator on 2018/3/5.
 */

public class ShowDialog {
    private ShowDialog() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void setShowErro(Context context, String data) {
        /**
         * 设置弹出对话框
         * */
        new AlertDialog.Builder(context)
                .setTitle("错误提示")
                .setMessage(data)
                .show();
    }
}
