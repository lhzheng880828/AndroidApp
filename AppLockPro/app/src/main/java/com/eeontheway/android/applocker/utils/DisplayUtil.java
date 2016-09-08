package com.eeontheway.android.applocker.utils;

import android.content.Context;

/**
 * DP/SP和PX之间的转换
 */
public class DisplayUtil {
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param context 上下文
     * @param pxValue 像素值
     * @return DP值
     */
    public static int px2dip (Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param context 上下文
     * @param dipValue DP值
     * @return 像素值
     */
    public static int dip2px (Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue 像素值
     * @return SP值
     */
    public static int px2sp (Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue SP值
     * @return 像素值
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕旋转方向
     * @param context 上下文
     * @return 屏幕方向 ORIENTATION_LANDSCAPE, ORIENTATION_PORTRAIT.
     */
    public static int getDisplayOrient (Context context) {
        return context.getResources().getConfiguration().orientation;
    }
}