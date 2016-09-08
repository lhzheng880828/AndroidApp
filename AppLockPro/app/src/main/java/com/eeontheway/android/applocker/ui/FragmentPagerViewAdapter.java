package com.eeontheway.android.applocker.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

/**
 * Created by lishutong on 16-1-13.
 */
public class FragmentPagerViewAdapter extends FragmentPagerAdapter {
    private Context context;
    private FragmentPagerViewInfo[] infos;
    private FragmentManager fm;

    public FragmentPagerViewAdapter (Context context, FragmentManager fm,
                                                FragmentPagerViewInfo[] infos) {
        super(fm);

        this.context = context;
        this.fm = fm;
        this.infos = infos;
    }

    public int getCount() {
        return infos.length;
    }

    @Override
    public Fragment getItem(int position) {
        return infos[position].getFragment();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = context.getResources().getString(infos[position].getNameResId());
        return title;
    }
}