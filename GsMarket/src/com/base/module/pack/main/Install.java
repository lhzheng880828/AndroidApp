/****************************************************************************
 *
 * FILENAME:        com.base.module.main.Install.java
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
import java.util.List;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;
import com.base.module.language.GuiDisplay;
import com.base.module.pack.R;
import com.base.module.pack.adapter.InstallAdapter;
import com.base.module.pack.bean.Pack;
import com.base.module.pack.common.Log;
import com.base.module.pack.common.Utils;
import com.base.module.pack.dao.PackDao;
import com.base.module.pack.inter.PackDaoInter;
import com.base.module.pack.method.PackMethod;
import com.base.module.pack.service.DownService;
import com.base.module.pack.task.TaskPool;
import com.base.module.pack.update.Update;
import com.base.module.pack.update.UpdateContect;
public class Install extends Fragment {
    protected static final String TAG = "Install";
    static final String UPDATE_URI = ApkListFragment.MARKET_SERVERURL+ Utils.getDeviceType().getNewestVersionURL();
    PackDaoInter mPackDao;
    private List<Pack> mPacks = new ArrayList<Pack>();
    private MyBroadcastReceiver mBroadcastReceiver = null;
    private InstallAdapter mInstallAdapter;
    private ListView mInstallLv;
    private IntentFilter mFilter;
    private TextView mTitle;
    private int mRegisterTag = 0;
    private GuiDisplay mGuidisplay;

    private Update UpdateService;
    private long mUpdateCount = 0;
    //private TaskPool mTaskPool;
    static boolean mThreadTag = false;
    //private PackMethod mPackMethod;
    /**
     * Safe to hold on to this.
     */
    public static final int MENU_CONTEXT_UNINSTALL = Menu.FIRST;
    public static final int MENU_CONTEXT_DELETE = Menu.FIRST + 1;
    public static final int MENU_CONTEXT_OPEN = Menu.FIRST + 2;
    public static final int MENU_CONTEXT_UPDATE = Menu.FIRST + 3;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.install, container, false);
        mGuidisplay = GuiDisplay.instance();
        mTitle = (TextView)inflate.findViewById(R.id.install_title);
        mInstallLv = (ListView) inflate.findViewById(R.id.install_lv);

        mPackDao = PackDao.getInstance(this.getActivity());
        mBroadcastReceiver = new MyBroadcastReceiver();

        //mPackMethod=PackMethod.getInstance();
        mFilter = new IntentFilter();
        mFilter.addAction(PackMethod.REFRESH_ACTION);
        //mTaskPool = TaskPool.getInstance();
        UpdateService = Update.getInstance();
        setAdapterForThis();
        inniteFail();
        return inflate;
    }

    private synchronized void setAdapterForThis() {
        initInstalls();
        mInstallAdapter = new InstallAdapter(this.getActivity(), mPacks, mPackDao);
        mInstallLv.setAdapter(mInstallAdapter);
    }

    private synchronized void initInstalls() {
        try {
            mPacks.clear();
            if (!mPackDao.isOpen()) {
                mPackDao.open();
            }
            mPacks = mPackDao.findInstall();
            mUpdateCount = mPackDao.getUpdateCount();
            mTitle.setText(mGuidisplay.getValue(this.getActivity(), 3344) + " " + mPacks.size() + " "
                    + mGuidisplay.getValue(this.getActivity(), 3345) + ". " + mUpdateCount
                    + " " + mGuidisplay.getValue(this.getActivity(), 3346) + ".");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context mContext, Intent intent) {
            Log.d("MyBroadcastReceiver onReceive");
            if (intent.getAction().equals(PackMethod.REFRESH_ACTION)) {
                setAdapterForThis();
            }
        }
    }

    private void registerBroader() {
        if (mRegisterTag == 0) {
            try {
                this.getActivity().registerReceiver(mBroadcastReceiver, mFilter);
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
    public void onDestroy() {
        mPackDao.close();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mPackDao.isOpen()) {
            mPackDao.open();
        }
        initUpdate();
        registerBroader();
        setAdapterForThis();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mPackDao.isOpen()) {
            mPackDao.open();
        }
        registerBroader();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPackDao.close();
    }


    void initUpdate() {
        Log.d("update_url "+UPDATE_URI);
        if (!mThreadTag) {
            mThreadTag = true;
            new Thread() {
                @Override
                public void run() {
                    if (!PackMethod.isValid(UPDATE_URI,true)) {
                        Log.d(TAG, "update uri is wrong or link time out --");
                    } else{
                        UpdateService.updatePacks(Install.this.getActivity(),
                                UpdateContect.uploadFile(UpdateService.reput(Install.this.getActivity()), UPDATE_URI));
                    }
                }
            }.start();
            mThreadTag = false;
        }
    }

    private void inniteFail() {
        int state;
        String l_name;
        for (int i = 0; i < mPacks.size(); i++) {
            Pack pack = mPacks.get(i);
            state = pack.getPackState();
            l_name = pack.getPackLName();
            TaskPool mTaskPool = TaskPool.getInstance();
            if (state == Pack.STATE_IN_UPDATE && !mTaskPool.isInPool(l_name)) {
                if (DownService.mEndHandler != null) {
                    Message msg = DownService.mEndHandler.obtainMessage(Pack.STATE_ER_DOWN);
                    msg.arg1 = -1;
                    msg.obj = pack;
                    DownService.mEndHandler.sendMessage(msg);
                } else
                    PackMethod.packFail(this.getActivity(), l_name);
            }
        }
    }

    @SuppressWarnings("unused")
    private void testDeleteAll() {
        for (int i = 0; i < mPacks.size(); i++) {
            PackMethod.uninstall(this.getActivity(),mPacks.get(i).getPackLName());
        }
    }
}