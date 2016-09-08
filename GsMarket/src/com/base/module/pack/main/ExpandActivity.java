package com.base.module.pack.main;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.base.module.language.GuiDisplay;
import com.base.module.pack.R;
import com.base.module.pack.common.Log;

import java.util.ArrayList;

/**
 * User: czheng
 * Date: 4/9/13
 * Time: 10:33 AM
 * Copyright (c) 2009-2013 by Grandstream Networks, Inc.
 * All rights reserved. 
 */
public class ExpandActivity  extends FragmentActivity {

    private ExpandActivity.TabListener tabListener;
    private ExpandActivity.FragmentPagerAdapter fragmentPagerAdapter;
    private ArrayList<Fragment> fragmentArrayList;
    private ViewPager viewpager ;
    private GuiDisplay mGuidisplay;
    private ActionBar actionBar;

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.expand_actionbutton, menu);
        menu.findItem(R.id.expand_backhome).setOnMenuItemClickListener(new android.view.MenuItem.OnMenuItemClickListener() {

           @Override
           public boolean onMenuItemClick(MenuItem menuItem) {
               Intent intent = new Intent();
               intent.setClass(ExpandActivity.this, AppActivity.class);
               ExpandActivity.this.startActivity(intent);
               return true;
           }
       });
        return true;
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (android.os.Build.VERSION.SDK_INT < 5 /* ECLAIR */
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Intent intent = new Intent(this, AppActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        int flag = this.getIntent().getIntExtra(AppActivity.EXPANDFLAG, 0);
        actionBar.setSelectedNavigationItem(flag);
        super.onStart();
    }

    @Override
    protected void onResume() {

        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        Log.d("OnCreate-----");
        this.setContentView(R.layout.main);
        mGuidisplay = GuiDisplay.instance();

          actionBar = this.getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        fragmentArrayList = new ArrayList<Fragment>();
        fragmentArrayList.add(new Download());
        fragmentArrayList.add(new Install());
        viewpager = (ViewPager) this.findViewById(R.id.viewpager);
        fragmentPagerAdapter = new FragmentPagerAdapter(this.getFragmentManager());
        viewpager.setAdapter(fragmentPagerAdapter);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onPageSelected(int i) {
                //To change body of implemented methods use File | Settings | File Templates.
                actionBar.setSelectedNavigationItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        ActionBar.Tab tab = actionBar.newTab();
        tab.setText(mGuidisplay.getValue(this, 28));
        tabListener = new TabListener();
        tab.setTabListener(tabListener);
        actionBar.addTab(tab);
        tab = actionBar.newTab();
        tab.setText(mGuidisplay.getValue(this,3306));
        tab.setTabListener(tabListener);
        actionBar.addTab(tab);
    }

    public class FragmentPagerAdapter extends android.support.v13.app.FragmentPagerAdapter{

        public FragmentPagerAdapter(FragmentManager fm ) {
            super(fm);
        }



        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentArrayList.get(i);  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private class TabListener implements ActionBar.TabListener{

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            viewpager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}