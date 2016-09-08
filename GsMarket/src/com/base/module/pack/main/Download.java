/****************************************************************************
 *
 * FILENAME:        com.base.module.main.Download.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: 2012-2-2
 *
 * DESCRIPTION:     The class encapsulates the music ring tone operations.
 *
 * vi: set ts=4:
 *
 * Copyright (c) 2009-2011 by Grandstream Networks, Inc.
 * All rights reserved.
 *
 * This material is proprietary to Grandstream Networks, Inc. and,
 * in addition to the above mentioned Copyright, may be
 * subject to protection under other intellectual property
 * regimes, including patents, trade secrets, designs and/or
 * trademarks.
 *
 * Any use of this material for any purpose, except with an
 * express license from Grandstream Networks, Inc. is strictly
 * prohibited.
 *
 ***************************************************************************/
package com.base.module.pack.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import com.base.module.language.GuiDisplay;
import com.base.module.pack.R;
import com.base.module.pack.adapter.DownloadAdapter;
import com.base.module.pack.bean.Pack;
import com.base.module.pack.common.Log;
import com.base.module.pack.dao.PackDao;
import com.base.module.pack.inter.PackDaoInter;
import com.base.module.pack.method.PackMethod;
import com.base.module.pack.task.TaskPool;
public class Download extends Fragment {
    protected static final String TAG = "Download";
    protected static final String HAS_TASK = "hasTask";
    protected static final String HAS_TASK_VALUE = "-1";
    protected static final String HAS_TASK_YES = "hasTask_yes";
    protected static final String HAS_TASK_NULL = "hasTask_null";
    protected static final int LIST_POS = -1;
    PackDaoInter mPackDao;
    private List<String> mPackNames;
    private Map<String, Pack> mPacks;
    private MyBroadcastReceiver mBroadcastReceiver = null;
    private DownloadAdapter mDownloadAdapter;
    private SharedPreferences mSharedPreferences;
    private ListView mDownsLv;
    private IntentFilter mFilter;
    private int mRegisterTag = 0;
    private GuiDisplay mGuidisplay;
    private SharedPreferences.Editor shEditor =null;
    /**
     * Safe to hold on to this.
     */
    public static final int MENU_CONTEXT_UNINSTALL = Menu.FIRST;
    public static final int MENU_CONTEXT_OPEN = Menu.FIRST + 2;
    public static final int MENU_CONTEXT_INSTALL = Menu.FIRST + 3;
    public static final int MENU_CONTEXT_RECORD_DELETE = Menu.FIRST + 4;
    public static final int MENU_CONTEXT_UPDATE = Menu.FIRST + 5;
    public FreshPressHandler mFreshPressHandler;
    private TaskPool mTaskPool;
    private int position = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.download, container, false);
        mGuidisplay = GuiDisplay.instance();
        mTaskPool = TaskPool.getInstance();
        shEditor = getActivity().getSharedPreferences("com.moudle.base.pack.downloadproess", 0).edit();
        mDownsLv = (ListView) inflate.findViewById(R.id.download_lv);
        mPackDao = PackDao.getInstance(this.getActivity());
        if (!mPackDao.isOpen()) {
            Log.w(TAG, "not not Open ");
        }
        mBroadcastReceiver = new MyBroadcastReceiver();
        mFilter = new IntentFilter();
        mFilter.addAction(PackMethod.REFRESH_ACTION);
        registerForContextMenu(mDownsLv);

        mFreshPressHandler = new FreshPressHandler();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        mPackNames=new ArrayList<String>();
        mPacks=new HashMap<String,Pack>();
        mDownloadAdapter=new DownloadAdapter(this.getActivity(),mPacks,mPackNames,mPackDao);
        mDownsLv.setAdapter(mDownloadAdapter);
        return inflate;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(R.layout.download);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        int position = ((AdapterContextMenuInfo) menuInfo).position;
        int state = mPacks.get(mPackNames.get(position)).getPackState();
        menu.setHeaderTitle(mGuidisplay.getValue(this.getActivity(), 46));
        if (state >= Pack.STATE_INSTALL) {
            menu.add(0, MENU_CONTEXT_OPEN, 1, mGuidisplay.getValue(this.getActivity(), 67));
            menu.add(0, MENU_CONTEXT_UNINSTALL, 2, mGuidisplay.getValue(this.getActivity(), 3311));
            if (state == Pack.STATE_UPDATE || state == Pack.STATE_ER_UPDATE)
                menu.add(0, MENU_CONTEXT_UPDATE, 5,
                        mGuidisplay.getValue(this.getActivity(), 71));
        }
        if ( state < Pack.STATE_WAIT) {
            menu.add(0, MENU_CONTEXT_INSTALL, 3, mGuidisplay.getValue(this.getActivity(), 3340));
            // menu.add(0, MENU_CONTEXT_DELETE, 4, "#delete");
            menu.add(0, MENU_CONTEXT_RECORD_DELETE, 6, mGuidisplay.getValue(this.getActivity(), 3342));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
        final Pack pack = mPacks.get(mPackNames.get(position));
        switch (item.getItemId()) {
        case MENU_CONTEXT_RECORD_DELETE:
            new Thread(){
                @Override
                public void run() {
                    deleteRecord(pack);
                }  
            }.start();
            shEditor.remove(mPackNames.get(position));
            mPacks.remove(mPackNames.get(position));
            mPackNames.remove(position);
            mFreshPressHandler.sendEmptyMessage(1);
            //freshAdapterForThis();
            break;
        case MENU_CONTEXT_OPEN:
            openApllication(pack.getPackLName());
            break;
        case MENU_CONTEXT_UPDATE: {
            update(pack.getPackLName(), pack.getPackDownRoute());
            break;
        }
        case MENU_CONTEXT_UNINSTALL: {
            uninstall(pack.getPackLName());
            // setAdapterForThis();
            break;
        }
        case MENU_CONTEXT_INSTALL: {
            sendDownloadBroader(pack.getPackDownRoute(),pack.getIconUrl());
            // setAdapterForThis();
            break;
        }
        }
        shEditor.commit();
        return super.onContextItemSelected(item);
    }

    boolean isPause=false;
    private class FreshPressHandler extends Handler {

        public void handleMessage(Message msg) {
            mDownloadAdapter.notifyDataSetChanged();
            if (!mSharedPreferences.getString(HAS_TASK, HAS_TASK_VALUE).equals(HAS_TASK_NULL)&&!isPause) {
                sleep(2000);
            }
            Message message = new Message();
            message.what = AppActivity.DATACHANGE;
            //MainActivity.dataChangeHandler.sendMessageDelayed(message,2000);
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };


    private Handler mFreshHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            mDownloadAdapter.setmPackNames(mPackNames);
            mDownloadAdapter.setmPacks(mPacks);
            mFreshPressHandler.sendEmptyMessage(1);
            if (position != LIST_POS) {
                mDownsLv.setSelection(position);
            }
        }

    };

    /**
     * view adapter
     */
    private void freshAdapterForThis() {
        new Thread(){
            @Override
            public void run() {
                try {
                    if (!mPackDao.isOpen()) {
                        mPackDao.open();
                    }
                    mPacks = mPackDao.findDownPackMaps();
                    mPackNames =new ArrayList<String>();
                    Set<String> keysSet = mPacks.keySet();
                    Iterator<String> iterator = keysSet.iterator();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        mPackNames.add(key);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }finally{
                    mFreshHandler.sendEmptyMessage(1);
                }
            }

        }.start();
    }


    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context mContext, Intent intent) {
            Log.d();
            if (intent.getAction().equals(PackMethod.REFRESH_ACTION)) {
                freshAdapterForThis();
            }
        }
    }

    private void registerBroader() {
        if (mRegisterTag == 0) {
            try {
                this.getActivity().
                registerReceiver(mBroadcastReceiver, mFilter);
                mRegisterTag = 1;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private void unregisterReceiver() {
        if (mRegisterTag == 1) {
            this.getActivity().unregisterReceiver(mBroadcastReceiver);
            mRegisterTag = 0;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        isPause=true;
        unregisterReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mPackDao.isOpen()) {
            mPackDao.open();
        }
        isPause=false;
        registerBroader();		
        mTaskPool.isDown(this.getActivity());
        freshAdapterForThis();
        Message msg = mFreshPressHandler.obtainMessage(Pack.STATE_IN_DOWN);
        mFreshPressHandler.sendMessage(msg)	;

    }

    @Override
    public void onStop() {
        super.onStop();
        mPackDao.close();
    }


    private void sendDownloadBroader(String url,String iconUrl) {
        PackMethod.sendDownloadBroader(this.getActivity(), url,iconUrl,null);
    }

    private void update(String l_name, String url) {
        PackMethod.update(this.getActivity(), l_name, url,null);
    }

    private void uninstall(String l_name) {
        PackMethod.uninstall(this.getActivity(), l_name);
    }

    private void deleteRecord(Pack pack) {
        PackMethod.deleteRecord(this.getActivity(), pack);
    }

    private void openApllication(String packLname) {
        PackMethod.openApllication(this.getActivity(), packLname);
    }


    /*@SuppressWarnings("unused")
    private void testDownAll() {
        String url;
        for (int i = 0; i < mPackNames.size(); i++) {
            String l_name = mPackNames.get(i);
            if (l_name.equals("com.base.module.asterisk")) {
                url = "http://market.ipvideotalk.com:9023/filestorage//resources/software/PIP/PIP_1.0.0.3.apk?packagename=com.base.module.asterisk&appname=PBX%20in%20Phone";
            } else if (l_name.equals("com.google.android.youtube")) {
                url = "http://market.ipvideotalk.com:9023/filestorage//resources/software/YouTube/YouTube_2.3.4.apk?packagename=com.google.android.youtube&appname=YouTube";
            } else {
                url = mPacks.get(l_name).getPackDownRoute();
            }
            PackMethod.sendDownloadBroader(this.getActivity(), url,null);
        }
    }*/

    @SuppressWarnings("unused")
    private void testDeleteAll() {
        for (int i = 0; i < mPackNames.size(); i++) {
            String l_name = mPackNames.get(i);
            deleteRecord(mPacks.get(l_name));
        }
    }
}