package com.example.chen.hsms.receiver;

import android.app.ActivityManager;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Parcelable;

import com.example.chen.hsms.utils.ConstantsUtil;
import com.example.chen.hsms.utils.MyLogger;
import com.example.chen.hsms.utils.ToastUtils;

import java.util.List;

/**
 * Created by admin on 2017/7/14.
 */

public class NetStateReceiver extends BroadcastReceiver {
    private static final String TAG = "NetStateReceiver";
    private Handler mHandler;

    public NetStateReceiver() {
    }

    public NetStateReceiver(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //wifi开关监听
//        isOpenWifi(intent, mHandler);
//        是否连接wifi
//        isConnectionWifi(intent, mHandler);
//        监听网络连接设置
        log = MyLogger.kLog();
        isConnection(intent, mHandler, context);
    }

    /**
     * 监听wifi打开与关闭
     * （与连接与否无关）
     *
     * @author hjy
     * created at 2016/12/12 17:33
     */
    private void isOpenWifi(Intent intent, Handler mHandler) {
        // 这个监听wifi的打开与关闭，与wifi的连接无关
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                //Wifi关闭
                case WifiManager.WIFI_STATE_DISABLED:
                    Log.e(TAG, "wifiState:wifi关闭！");
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    Log.e(TAG, "wifiState:wifi打开！");
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 连接有用的wifi（有效无线路由）
     * WifiManager.WIFI_STATE_DISABLING与WIFI_STATE_DISABLED的时候，根本不会接到这个广播
     *
     * @author hjy
     * created at 2016/12/13 9:47
     */
    private void isConnectionWifi(Intent intent, Handler mHandler) {
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State state = networkInfo.getState();
                boolean isConnected = state == NetworkInfo.State.CONNECTED;
                //wifi连接
                if (isConnected) {
                    mHandler.sendEmptyMessage(ConstantsUtil.NetWork.IS_WIFI);
                    Log.e(TAG, "isConnected:isWifi:true");
                    ToastUtils.showLong("请确保你已经打开WIFI网络");
                }
            }
        }
    }

    /**
     * 监听网络连接的设置，包括wifi和移动数据的打开和关闭。(推荐)
     * 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
     *
     * @author hjy
     * created at 2016/12/13 9:47
     */
    private void isConnection(Intent intent, Handler mHandler, Context context) {
        if (!isBackground(context)) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                ConnectivityManager manager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.isConnected()) {
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                            // connected to wifi
                            mHandler.sendEmptyMessage(ConstantsUtil.NetWork.IS_WIFI);
                            log.e("当前WiFi连接可用 ");
                            ToastUtils.showLong("已连接到wifi网络 ");
                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            // connected to the mobile provider's data plan
                            mHandler.sendEmptyMessage(ConstantsUtil.NetWork.IS_MOBILE);
                            log.e("当前移动网络连接可用 ");
                            ToastUtils.showLong("已连接到2G/3G/4G网络 ");
                        }
                    } else {
                        ToastUtils.showLong("请确保你已经打开WIFI网络");
                        Log.e(TAG, "当前没有网络连接，请确保你已经 打开网络 ");
                        mHandler.sendEmptyMessage(ConstantsUtil.NetWork.NO_CONNECTION);
                    }
                    log.e("TypeName：" + activeNetwork.getTypeName());
                    log.e("SubtypeName：" + activeNetwork.getSubtypeName());
                    log.e("State：" + activeNetwork.getState());
                    log.e("DetailedState：" + activeNetwork.getDetailedState().name());
                    log.e("ExtraInfo：" + activeNetwork.getExtraInfo());
                    log.e("Type：" + activeNetwork.getType());

                } else {   // not connected to the internet
//                ToastUtils.showLong("没网能干点啥，快去打开网络吧");
                    log.e("activeNetwork，请确保你已经打开网络 ");
                    ToastUtils.showLong("没网能干点啥，快去打开网络吧 ");
                    mHandler.sendEmptyMessage(ConstantsUtil.NetWork.NO_CONNECTION);
                }
            }
        }
    }

    private MyLogger log;

    private boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    log.e(context.getPackageName() + "后台"
                            + appProcess.processName);
                    return true;
                } else {
                    log.e(context.getPackageName() + "前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }
}

