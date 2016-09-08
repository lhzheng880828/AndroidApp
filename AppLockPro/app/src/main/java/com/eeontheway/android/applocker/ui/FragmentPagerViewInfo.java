package com.eeontheway.android.applocker.ui;

import android.app.Fragment;

/**
 * Created by lishutong on 16-1-13.
 */
// Fragment信息类
public class FragmentPagerViewInfo {
    private Fragment fragment;
    private int nameResId;
    private int iconSelectedResId;
    private int iconUnSelectedResId;

    public FragmentPagerViewInfo(Fragment fragment, int nameResId,
                                 int iconSelectedResId, int iconUnSelectedResId) {
        this.fragment = fragment;
        this.nameResId = nameResId;
        this.iconSelectedResId = iconSelectedResId;
        this.iconUnSelectedResId = iconUnSelectedResId;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public int getNameResId() {
        return nameResId;
    }

    public int getIconSelectedResId() {
        return iconSelectedResId;
    }

    public int getIconUnSelectedResId() {
        return iconUnSelectedResId;
    }
}