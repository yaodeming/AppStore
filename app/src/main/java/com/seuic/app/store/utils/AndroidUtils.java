package com.seuic.app.store.utils;

import android.graphics.Color;
import android.os.Build;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.seuic.app.store.AppStoreApplication;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

/**
 * Created on 2017/9/16.
 *
 * @author dpuntu
 */

public class AndroidUtils {
    /**
     * 判断主线程
     *
     * @return 是否为主线程
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /**
     * 获取系统时间戳
     *
     * @return 时间戳
     */
    public static String systemTime() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 获取设备序列号
     *
     * @return 序列号
     */
    public static String getSerial() {
        String serialNum = "";
        try {
            FileInputStream fin = new FileInputStream("/mnt/shell/emulated/briefsettingsn.txt");
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            serialNum = new String(buffer, "UTF-8");
            SpUtils.getInstance().putStr(SpUtils.SP_DEVICE_SN, serialNum);
            fin.close();
        } catch (Exception e) {
            Logger.e("SN号获取失败, 正在使用备份数据");
            e.printStackTrace();
            serialNum = SpUtils.getInstance().getStr(SpUtils.SP_DEVICE_SN, "0F81CE33-0733-1246-DC1D-E2881D7392FE");
        }
        return serialNum;
    }

    /**
     * 获得系統客户名称
     *
     * @return String
     */
    public static String getCustomer() {
        try {
            Class cl = Class.forName("android.os.SystemProperties");
            Object invoker = cl.newInstance();
            Method m = cl.getMethod("get", String.class, String.class);
            Object result = m.invoke(invoker, "persist.sys.seuic.customer", "seuic");
            return result.toString();
        } catch (Exception e) {
            Logger.e(android.util.Log.getStackTraceString(e));
            return "seuic";
        }
    }

    /**
     * 设置Activity中的标题栏和底部透明
     *
     * @param compatActivity
     *         Activity
     *
     * @return
     */
    public static void initStatusBar(AppCompatActivity compatActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                compatActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                compatActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            } else {
                Window window = compatActivity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                                          | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);
            }
        }
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     *         （DisplayMetrics类中属性density）
     *
     * @return
     */
    public static int px2dip(float pxValue) {
        final float scale = AppStoreApplication.getApp().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     *         （DisplayMetrics类中属性density）
     *
     * @return
     */
    public static int dip2px(float dipValue) {
        final float scale = AppStoreApplication.getApp().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     *         （DisplayMetrics类中属性scaledDensity）
     *
     * @return
     */
    public static int px2sp(float pxValue) {
        final float fontScale = AppStoreApplication.getApp().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     *         （DisplayMetrics类中属性scaledDensity）
     *
     * @return
     */
    public static int sp2px(float spValue) {
        final float fontScale = AppStoreApplication.getApp().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 长度计算
     *
     * @param dataSize
     *         原长度
     *
     * @return
     */
    public static String formatDataSize(long dataSize) {
        String unit = "B";
        long MAX = 1024 * 1024 * 1024;
        float dataSizeF = dataSize * 1F;
        if (dataSize >= MAX) {
            dataSizeF = dataSize / (MAX * 1F);
            unit = "G";
        } else if (dataSize >= MAX / 1024) {
            dataSizeF = dataSize / 1024F / 1024F;
            unit = "M";
        } else if (dataSize >= 1024) {
            dataSizeF = dataSize / 1024F;
            unit = "K";
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String size = decimalFormat.format(dataSizeF);
        return size + unit;
    }

    /**
     * 速度计算
     *
     * @param speed
     *         原长度
     *
     * @return
     */
    public static String formatSpeed(float speed) {
        String unit = "b/s";
        DecimalFormat decimalFormat;
        if (speed >= 1024 * 1024) {
            speed = speed / 1024F / 1024F;
            unit = "mb/s";
        } else if (speed >= 1024) {
            speed = speed / 1024F;
            unit = "kb/s";
        }
        decimalFormat = new DecimalFormat("0.00");
        String speedNew = decimalFormat.format(speed);
        return speedNew + unit;
    }

    public static void sleepTime(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final int MIN_CLICK_DELAY_TIME = 2000;

    /**
     * 防止多次点击,2秒点一次
     *
     * @param lastClickTime
     *         最后的点击时间
     *
     * @return 是否可以点击了
     */
    public static boolean isCanClick(long lastClickTime) {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        return flag;
    }

    public static String getStringById(int resId) {
        return AppStoreApplication.getApp().getString(resId);
    }
}
