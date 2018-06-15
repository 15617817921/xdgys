package com.example.chen.hsms.application;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by admin on 2017/7/27.
 */

public class MyActivityManager {

    private static List<Activity> activityList = new ArrayList<Activity>();
    private static MyActivityManager instance;
    //提供栈的对象
    private Stack<Activity> activityStack = new Stack<>();

    public static MyActivityManager getInstance() {
        if (instance == null) {
            instance = new MyActivityManager();
        }
        return instance;
    }
    //单例模式：饿汉式
    private MyActivityManager(){

    }
    private static MyActivityManager activityManager = new MyActivityManager();



    //activity的添加
    public void add(Activity activity){
        if(activity != null){
            activityStack.add(activity);
        }
    }

    //插曲：[12,3,44,6,332,65,-56,1]
    //删除指定的activity
    public void remove(Activity activity){
        if(activity != null){
//            for(int i = 0; i < activityStack.size(); i++) {
//                Activity currentActivity = activityStack.get(i);
//                if(currentActivity.getClass().equals(activity.getClass())){
//                    currentActivity.finish();//销毁当前的activity
//                    activityStack.remove(i);//从栈空间移除
//                }
//            }

            for(int i = activityStack.size() - 1;i >= 0;i--){
                Activity currentActivity = activityStack.get(i);
                if(currentActivity.getClass().equals(activity.getClass())){
                    currentActivity.finish();//销毁当前的activity
                    activityStack.remove(i);//从栈空间移除
                }
            }
        }
    }

    //删除当前的activity
    public void removeCurrent(){
        //方式一：
//        Activity activity = activityStack.get(activityStack.size() - 1);
//        activity.finish();
//        activityStack.remove(activityStack.size() - 1);

        //方式二：
        Activity activity = activityStack.lastElement();
        activity.finish();
        activityStack.remove(activity);
    }

    //删除所有的activity
    public void removeAll(){
        for (int i = activityStack.size() - 1;i >= 0;i--){
            Activity activity = activityStack.get(i);
            activity.finish();
            activityStack.remove(activity);
        }
    }

    //返回栈大小
    public int size(){
        return activityStack.size();
    }
    /**
     * 添加 Activity 到列表
     *
     * @param activity activity
     */
    public static void addActivity(Activity activity) {
        if (activityList == null) {
            activityList = new ArrayList<Activity>();
        }
        activityList.add(activity);
    }

    /**
     * 获取界面数量
     *
     * @return activity size
     */
    public static int getActivitySize() {
        if (activityList != null) {
            return activityList.size();
        }
        return 0;
    }

    /**
     * 获取当前 Activity - 堆栈中最后一个压入的
     *
     * @return current Activity
     */
    public static Activity getCurrentActivity() {
        if (activityList != null && activityList.size() > 0) {
            Activity activity = activityList.get(activityList.size() - 1);
            return activity;
        }
        return null;
    }

    /**
     * 获取指定类名的 Activity
     *
     * @param cls 指定的类
     * @return Activity
     */
    public static Activity getActivity(Class<?> cls) {
        if (activityList == null) {
            return null;
        }
        for (Activity activity : activityList) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 结束指定的 Activity
     *
     * @param activity Activity
     */
    public static void removeActivity(Activity activity) {
        if (activity != null) {
            activityList.remove(activity);
        }
    }

    /**
     * 结束指定类名的 Activity
     *
     * @param cls 指定的类
     */
    public static void removeActivity(Class<?> cls) {
        if (activityList == null) {
            return;
        }
        for (Activity activity : activityList) {
            if (activity.getClass().equals(cls)) {
                activityList.remove(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        if (activityList == null) {
            return;
        }
        int size = activityList.size();
        for (int i = 0; i < size; i++) {
            if (null != activityList.get(i)) {
                activityList.get(i).finish();
            }
        }
        activityList.clear();
    }

    /**
     * 结束其他所有的Activity
     *
     * @param activity 不需要销毁的Activity
     */
    public static void finishOtherAllActivity(Activity activity) {
        if (activityList == null) {
            return;
        }
        for (int i = 0, size = activityList.size(); i < size; i++) {
            if (activity != activityList.get(i)) {
                activityList.get(i).finish();
                activityList.remove(i);
            }
        }
    }

}
