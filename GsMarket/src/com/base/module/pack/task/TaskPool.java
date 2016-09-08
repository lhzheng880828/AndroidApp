package com.base.module.pack.task;
/**
 * Copyright (c) 2009-2011 by Grandstream Networks
 */
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.base.module.pack.bean.Pack;
import com.base.module.pack.common.Log;
import com.base.module.pack.dao.PackDao;
import com.base.module.pack.inter.PackDaoInter;
import com.base.module.pack.method.PackMethod;
import com.base.module.pack.service.DownService;
import com.base.module.pack.service.DownService.DownloaderTask;
public class TaskPool extends ThreadGroup {
    protected static final String TAG = "TaskPool";
    public static final String HAS_TASK = "hasTask";
    protected static final String HAS_TASK_YES = "hasTask_yes";
    public static final String HAS_TASK_NULL = "hasTask_null";
    public static final String HAS_TASK_VALUE = "-1";
    public static final String DOWNLOAD_TASK_CHANGE_ACTION = "com.base.module.pack.downloadtaskchange";
    //private final static String DOWNLOAD_PATH = "/data/data/com.base.module.pack/package";
    private boolean isClosed = false; // Thread pool is closed
    private static Map<String, DownloaderTask> mTasks=new LinkedHashMap<String, DownloaderTask>();;
    private static int taskPoolID = 1;
    private static final TaskPool m_instance = new TaskPool();
    private PackDaoInter mPackDao;
    //private PackMethod mPackMethod;
    private Queue<Pack> mWaitQueue = new LinkedList<Pack>();
    public static TaskPool getInstance() {
        return m_instance;
    }

    private TaskPool() {
        super(taskPoolID + "");
        setDaemon(true);
        //mTasks = 
        //mPackMethod = PackMethod.getInstance();
    }

    public synchronized int getSize() {
        return mTasks.size();
    }

    public synchronized DownloaderTask getPrevTask() {
        if (mTasks == null || mTasks.isEmpty() || mTasks.size() == 0)
            return null;
        Set<String> keysSet = mTasks.keySet();
        Iterator<String> iterator = keysSet.iterator();
        if (iterator.hasNext()) {
            String key = (String) iterator.next();
            return mTasks.get(key);
        } else
            return null;
    }

    public int getPress(Context context, String l_name) {
        if (mTasks != null && !mTasks.isEmpty() && mTasks.size() > 0) {
            if (mTasks.get(l_name) != null) {
                long count = mTasks.get(l_name).getCount();
                long length = mTasks.get(l_name).getLength();
                if (count != 0 && length != 0)
                    return (int) ((count / (float) length) * 100);
            }
        }
        return 0;
    }

    /** put a new task */
    public synchronized void put(Context context, String l_name, DownloaderTask task) {
        if (isClosed) {
            throw new IllegalStateException();
        }
        if (task != null) {
            PackMethod.setPreference(context, HAS_TASK, HAS_TASK_YES);
            mTasks.put(l_name, task);
            // notify();
        }
    }

    public  synchronized DownloaderTask get(String l_name) {
        return mTasks.get(l_name);
    }

    /** remove task */
    public synchronized void remove(Context context, String l_name) {
        if (isClosed) {
            throw new IllegalStateException();
        }
        if (mTasks == null || mTasks.isEmpty() || mTasks.size() == 0)
            return;
        DownloaderTask task = mTasks.get(l_name);
        if (task != null) {
            mTasks.remove(l_name);
            // notify();
        }
        if (mTasks == null || mTasks.isEmpty() || mTasks.size() == 0)
            PackMethod.setPreference(context, HAS_TASK, HAS_TASK_NULL);
    }

    /** put a new task */
    public synchronized  void isDown(Context context) {
        if (isClosed) {
            throw new IllegalStateException();
        }
        if (mTasks == null || mTasks.isEmpty()) {
            PackMethod.setPreference(context, HAS_TASK, HAS_TASK_NULL);			
        } else{
            PackMethod.setPreference(context, HAS_TASK, HAS_TASK_YES);

        }

    }

    /** put a new task */
    public synchronized void interrupt(Context context, String l_name) {
        Log.d("close interrupt ");
        if (isClosed) {
            throw new IllegalStateException();
        }

        synchronized(DownService.o){
            Log.d("close interrupt synchronize");
            Log.d("mTasks keyset " +mTasks.keySet().toString());
            if(mTasks != null && mTasks.get(l_name)==null){
                Log.d("this mTask " +mTasks.get(l_name));
                if (DownService.mHandler != null) {
                    Message msg = DownService.mHandler.obtainMessage(DownService.REMOVE_QUEUE);
                    msg.obj = l_name;
                    DownService.mHandler.sendMessage(msg);

                }
                packFail(context, l_name);
                return;
            }
        }

        Log.d("close interrupt synchronize end");
        if (mTasks == null || mTasks.isEmpty() || mTasks.size() == 0) {
            Log.d("close interrupt mTask");
            packFail(context, l_name);
            PackMethod.setPreference(context, HAS_TASK, HAS_TASK_NULL);
            return;
        }

        DownloaderTask task = mTasks.get(l_name);
        if (task != null) {
            Log.d("task cancel");
            task.cancel(true);
            //            task.setCanceld(true);
        }
    }

    /** remove task */
    public synchronized boolean isInPool(String l_name) {
        if (isClosed) {
            throw new IllegalStateException();
        }
        if (mTasks != null && !mTasks.isEmpty() && mTasks.size() > 0 && mTasks.get(l_name) != null)
            return true;
        return false;
    }

    /** close pool */
    public synchronized void closePool() {
        if (!isClosed) {
            waitFinish();
            isClosed = true;
            mTasks.clear();
            interrupt();
        }
    }

    /** finish */
    public void waitFinish() {
        synchronized (this) {
            isClosed = true;
            notifyAll(); // Wake up all still in getTask () method to wait in
            // the work of the task thread
        }
        Thread[] threads = new Thread[activeCount()]; // activeCount()
        // Return to this thread
        // group activities in
        // the estimate of thread.
        int count = enumerate(threads); // enumerate()Methods of inherited from
        // the ThreadGroup, according to the
        // estimate of thread activities get in
        // the current thread group all
        // activities of the thread
        for (int i = 0; i < count; i++) {
            try {
                threads[i].join(); // waiting for Threaded end work
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    void packFail(Context context, String l_name) {
        Log.d(TAG,"packFail");
        if (mPackDao == null)
            mPackDao = PackDao.getInstance(context);
        else if (!mPackDao.isOpen())
            mPackDao.open();
        mPackDao.failDownloadByLname(l_name);
        Log.d("packfail sendBroader start");
        PackMethod.sendRefreshBroader(context);
        //DownService.removeDown(l_name);
    }
    private void notifyDowloadtaskChange(Context context,String packageName){
        Intent intent = new Intent(DOWNLOAD_TASK_CHANGE_ACTION);
        intent.putExtra(PackMethod.PACKAGENAME, packageName);
        context.sendBroadcast(intent);
    }
    public boolean isWaitQueueEmpty(){
        return mWaitQueue.isEmpty();
    }

    public synchronized Pack pollFromWaitQueue(){
        return mWaitQueue.poll();
    }

    public synchronized void addToWaitQueue(Context context,Pack pack){
        notifyDowloadtaskChange(context,pack.getPackLName());
        mWaitQueue.add(pack);
    }

    public synchronized void removeWaitQueue(String l_name) {
        for (Pack pack : mWaitQueue) {
            if (pack.getPackLName().equals(l_name)) {
                mWaitQueue.remove(pack);
                break;
            }
        }
    }

    public synchronized boolean isInWaitQueue(String l_name) {
        for (Pack pack : mWaitQueue) {
            if (pack.getPackLName().equals(l_name)) {
                return true;
            }
        }
        return false;
    }
}