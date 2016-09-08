package com.eeontheway.android.applocker.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.lock.DataObservable;
import com.eeontheway.android.applocker.lock.LockConfigManager;
import com.eeontheway.android.applocker.ui.ListHeaderView;
import com.eeontheway.android.applocker.ui.WaitingProgressDialog;

import java.util.Observable;
import java.util.Observer;

/**
 * 显示指定锁定模式下所有配置锁定App的应用列表
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class AppLockListFragment extends Fragment {
    private RecyclerView rcv_list;
    private TextView tv_empty;
    private Button bt_del;
    private FloatingActionButton fab_add;
    private ListHeaderView ll_header;

    private AppLockListAdapter lockListAdapter;
    private Activity parentActivity;
    private LockConfigManager lockConfigManager;
    private Observer observer;
    private WaitingProgressDialog progressDialog;

    /**
     * Fragment的OnCreate()回调
     * @param savedInstanceState 之前保存的状态
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = getActivity();
        lockConfigManager = LockConfigManager.getInstance(parentActivity);

        initProgressDialog();
        initAdapter();
        initDataObserver();
    }

    /**
     * Fragment的onDestroy()回调
     */
    @Override
    public void onDestroy() {
        lockConfigManager.unregisterObserver(observer);
        lockConfigManager.freeInstance();
        super.onDestroy();
    }

    /**
     * Fragment的onCreateView()回调
     * @param savedInstanceState 之前保存的状态
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_lock_list, container, false);

        ll_header = (ListHeaderView) view.findViewById(R.id.ll_header);
        bt_del = (Button) view.findViewById(R.id.bt_del);
        rcv_list = (RecyclerView) view.findViewById(R.id.rcv_list);
        tv_empty = (TextView) view.findViewById(R.id.tv_empty);
        fab_add = (FloatingActionButton) view.findViewById(R.id.fab_add);

        initHeader();
        initListView();
        initButtons();
        return view;
    }

    /**
     * 初始化头部
     */
    private void initHeader () {
        // 更改标题计数
        updateTotalCountShow();

        // 配置全选监听器
        ll_header.setListener(new ListHeaderView.ClickListener() {
            @Override
            public void onCheckAllSetListener(boolean isChecked) {
                if (lockConfigManager.getAppListCount() > 0) {
                    lockConfigManager.selectAllApp(isChecked);
                    showDeleteButton(isChecked);
                    updateTotalCountShow();
                }
            }

            @Override
            public void onDoubleClickedListener() {
                rcv_list.smoothScrollToPosition(0);
            }
        });
    }

    /**
     * 初始化ListView
     */
    private void initListView () {
        rcv_list.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcv_list.setLayoutManager(layoutManager);
        rcv_list.addItemDecoration(new LockListViewItemDecoration());
        rcv_list.setItemAnimator(new LockListViewItemAnimator());
        rcv_list.setAdapter(lockListAdapter);

        // 配置滚动事件
        rcv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ll_header.showReturnTopAlert(false);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                ll_header.showReturnTopAlert(dy < 0);
            }
        });

        // 初始化空白显示页
        if (lockListAdapter.getItemCount() == 0) {
            tv_empty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化进度对话框
     */
    private void initProgressDialog () {
        progressDialog = new WaitingProgressDialog(parentActivity);
        progressDialog.setMessage(getString(R.string.deleting));
    }

    /**
     * 显示等待进度对话框
     */
    private void showWaitingProgressDialog (boolean show) {
        progressDialog.show(show);
    }

    /**
     * 初始化Button
     */
    private void initButtons () {
        // 配置删除按钮
        bt_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 额外开辟线程去删除，以防止删除大批量数据卡死
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        showWaitingProgressDialog(true);

                        // Bug: 需要注册监听，否则后台线程会操作UI
                        lockConfigManager.setObserverEnable(DataObservable.DataType.APP_LIST, false);
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        int size = lockListAdapter.removeSelectedApp();
                        if (size < 0) {
                            Toast.makeText(parentActivity, R.string.delete_failed, Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        Toast.makeText(parentActivity, R.string.deleteOk, Toast.LENGTH_SHORT).show();
                        showDeleteButton(false);
                        showWaitingProgressDialog(false);

                        // 删除完毕后，恢复监听，通知数据发生变化
                        lockConfigManager.setObserverEnable(DataObservable.DataType.APP_LIST, true);
                    }
                }.execute();
            }
        });


        // 初始化浮动按钮
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSelectActivity.start(AppLockListFragment.this);
            }
        });
    }

    /**
     * 初始化适配器
     */
    private void initAdapter () {
        lockListAdapter = new AppLockListAdapter(parentActivity, lockConfigManager);
        lockListAdapter.setItemSelectedListener(new RecyleViewItemSelectedListener() {
            @Override
            public void onItemSelected(int pos, boolean selected) {
                showDeleteButton(selected);
                updateTotalCountShow();
            }

            @Override
            public boolean onItemLongClicked(final int pos) {
                // 创建一个弹出式的对话框，当作上下文菜单
                AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(parentActivity,
                        android.R.layout.simple_list_item_1);
                adapter.add(getString(R.string.delete));
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Delete
                                lockConfigManager.deleteAppInfo(pos);
                                break;
                        }
                    }
                });
                builder.create().show();
                return true;
            }

            @Override
            public void onItemClicked(int pos) {}
        });
    }

    /**
     * 注册数据变化监听器
     */
    private void initDataObserver () {
        observer = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                // 通知Adapter数据发生变化
                DataObservable.ObserverInfo info = (DataObservable.ObserverInfo)data;
                if (info.dataType != DataObservable.DataType.APP_LIST) {
                    return;
                } else {
                    switch (info.changeType) {
                        case UNKNOWN:
                            lockListAdapter.notifyDataSetChanged();
                            break;
                        case INSERT:
                            lockListAdapter.notifyItemRangeInserted(info.startPos, info.count);
                            break;
                        case UPDATE:
                            lockListAdapter.notifyItemRangeChanged(info.startPos, info.count);
                            break;
                        case REMOVE:
                            lockListAdapter.notifyDataSetChanged();
                            break;
                    }
                }

                // 是否显示空白view?
                if (lockListAdapter.getItemCount() > 0) {
                    tv_empty.setVisibility(View.GONE);
                } else {
                    tv_empty.setVisibility(View.VISIBLE);
                }

                // 更改标题计数
                updateTotalCountShow();
            }
        };

        lockConfigManager.registerObserver(observer);
    }

    /**
     * 更新标题中数量显示
     */
    private void updateTotalCountShow () {
        ll_header.setTitle(getString(R.string.selected_count,
                lockConfigManager.selectedAppCount(),
                lockConfigManager.getAppListCount()));
    }

    /**
     * 显示删除按钮
     * @param show 是否显示删除按钮
     */
    private void showDeleteButton (boolean show) {
        if (show) {
            if (bt_del.getVisibility() != View.VISIBLE) {
                // 显示删除按钮
                Animation animation = AnimationUtils.loadAnimation(parentActivity,
                        R.anim.listview_cleanbutton_bottom_in);
                animation.setFillAfter(true);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        bt_del.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {}

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                bt_del.startAnimation(animation);
            }
        } else {
            // 有任意一项未选中，则取消全选
            ll_header.setCheckAll(false);

            // 如果没有任何项选中，则隐藏按钮
            if (!lockListAdapter.isAnyItemSelected()){
                if (bt_del.getVisibility() != View.GONE) {
                    // 隐藏删除按钮
                    Animation animation = AnimationUtils.loadAnimation(parentActivity,
                            R.anim.listview_cleanbutton_bottom_out);
                    animation.setFillAfter(true);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            bt_del.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                    bt_del.startAnimation(animation);
                }
            }
        }
    }
}
