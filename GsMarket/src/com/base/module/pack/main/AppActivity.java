/**
 * Author:yyzhu
 * Copyright:GrandStream
 */
package com.base.module.pack.main;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.base.module.pack.R;
import com.base.module.pack.common.ImageLoader;
import com.base.module.pack.dao.PackDao;
import com.base.module.pack.method.PackMethod;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class AppActivity extends FragmentActivity{
    public static final String EXPANDFLAG = "expandflag";
    public static final String GMIINTERFACE = "gmiinterface";
    public static final int DOWNFLAG = 0;
    private static final int INSTALLFLAG = 1;
    public final static int DATACHANGE = 1;
    public static ViewPager mViewPager;
    private  EditText mSearchWord;
    LayoutInflater inflater;
    private ApkListFragment mCurrentFragment;
    public static final int TAB_INDEX_HOT = 0;
    public static final int TAB_INDEX_APP= 1;
    public static final int TAB_INDEX_GAME = 2;
    public static final int TAB_INDEX_TOOLS = 3;
    public static final int TAB_INDEX_COUNT = 4;

    public static final String HOT = "hot";
    public static final String APP = "app";
    public static final String GAME = "game";
    public static final String TOOLS = "tools";
    private ActionBar mActionBar;
    private boolean misSearchMode;
    private String webUrl = ApkListFragment.MARKET_SERVERURL+ApkListFragment.REQUEST_LIST_ACTION+"&apptypecode=";
    private int[] mTitle = new int[] {R.string.hot_label, R.string.app_label,
            R.string.game_label,R.string.tools_label};
    private ApkListFragment[] mFragmentList = new ApkListFragment[4];
    private String[] types = new String[] {HOT, APP, GAME, TOOLS};
    private InputMethodManager imm;
    private int tabPosition=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        //mImageLoader=new ImageLoader(getApplicationContext());
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBar = getActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        for (int title : mTitle) {
            // add tab
            ActionBar.Tab  newTab = mActionBar.newTab();
            mActionBar.addTab(newTab.setText(title).setTabListener(mTabListener));
        }
        initView();
    }

    private void initView(){

        mCurrentFragment = mFragmentList[0];
        mViewPager = (ViewPager) this.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                mActionBar.selectTab(mActionBar.getTabAt(i));
                if(i<mFragmentList.length){
                    mCurrentFragment = mFragmentList[i];
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter
    {

        public ViewPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String url="";
            if(position<types.length && !TextUtils.isEmpty(types[position])){
               url = webUrl + types[position];
               return ApkListFragment.instance(types[position], url);
            }
            url = webUrl + HOT;
            return ApkListFragment.instance(HOT, url);
        }

        @Override
        public int getCount() {
            return TAB_INDEX_COUNT;
        }
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        // TODO Auto-generated method stub
        super.onAttachFragment(fragment);
        
        if (fragment == null) {
            return;
        }
        String tag = fragment.getArguments().getString("tag");
        if (HOT.equals(tag)) {
            mFragmentList[0] = (ApkListFragment) fragment;
        } else if (GAME.equals(tag)) {
            mFragmentList[2] =  (ApkListFragment) fragment;
        }else if (APP.equals(tag)) {
            mFragmentList[1] =  (ApkListFragment) fragment;
        }else if (TOOLS.equals(tag)) {
            mFragmentList[3] =  (ApkListFragment) fragment;
        }
        if(mCurrentFragment==null){
            mCurrentFragment = mFragmentList[tabPosition];
        }
    }


    private final ActionBar.TabListener mTabListener = new ActionBar.TabListener(){

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction arg1) {

        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction arg1) {
            if(mFragmentList!=null && tab!=null && tab.getPosition() < mFragmentList.length){
                tabPosition = tab.getPosition();
                mCurrentFragment = mFragmentList[tab.getPosition()];
            }
            if (mViewPager!=null && mViewPager.getCurrentItem() != tab.getPosition()){
                mViewPager.setCurrentItem(tab.getPosition(), false);
                invalidateOptionsMenu();
            }
        }

        @Override
        public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
        }
    };

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_search:
            misSearchMode = true;
            resetActionBarForSearch();
            break; 
        }
        return super.onMenuItemSelected(featureId, item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (misSearchMode) {
            menu.findItem(R.id.menu_exp).setVisible(false);
            menu.findItem(R.id.menu_search).setVisible(false);
        }else{
            menu.findItem(R.id.menu_exp).setVisible(true);
            menu.findItem(R.id.menu_exp).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_actionbutton, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void resetActionBarForSearch(){
        invalidateOptionsMenu();
        mActionBar.setNavigationMode(ActionBar.DISPLAY_SHOW_CUSTOM);
        View searchView = inflater.inflate(R.layout.search, null);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setCustomView(searchView);
        if(searchView!=null){
            mSearchWord = (EditText) searchView.findViewById(R.id.search_word);
            imm = (InputMethodManager)mSearchWord.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mSearchWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH && mCurrentFragment!=null && mCurrentFragment.isAdded()) {
                        mCurrentFragment.search(mSearchWord.getText().toString().trim());
                    }
                    return false;
                }
            });

            searchView.findViewById(R.id.search_cancle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mSearchWord.setText("");
                }
            });
            searchView.findViewById(R.id.search_sure).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mCurrentFragment.initApplistData(getUrl(mSearchWord.getText().toString().trim()));
                    String searchKey = mSearchWord.getText().toString().trim();
                    if(mCurrentFragment!=null && mCurrentFragment.isAdded()){
                        mCurrentFragment.search(searchKey);
                    }
                }
            });
        }
        mActionBar.setCustomView(searchView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
        case R.id.history:
            Intent intent = new Intent(this, ExpandActivity.class);
            intent.putExtra(EXPANDFLAG, DOWNFLAG);
            startActivity(intent);
            overridePendingTransition(0, 0);
            break;
        case R.id.installed:
            Intent intentInstaill = new Intent(this, ExpandActivity.class);
            intentInstaill.putExtra(EXPANDFLAG, INSTALLFLAG);
            startActivity(intentInstaill);
            overridePendingTransition(0, 0);
            break;
        case android.R.id.home:
            onBackPressed();
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if(misSearchMode){
            misSearchMode = false;
            if(mSearchWord!=null){
                mSearchWord.setText("");
            }
            if(imm!=null && getCurrentFocus()!=null){
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            mActionBar.setCustomView(null);
            mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            mCurrentFragment.refershGridVIew();
            invalidateOptionsMenu();
        }else{
            super.onBackPressed();
        }
    }
}