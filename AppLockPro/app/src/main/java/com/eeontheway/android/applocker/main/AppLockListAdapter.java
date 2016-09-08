package com.eeontheway.android.applocker.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.app.AppInfo;
import com.eeontheway.android.applocker.lock.AppLockInfo;
import com.eeontheway.android.applocker.lock.LockConfigManager;

import static com.eeontheway.android.applocker.R.*;

/**
 * 反馈列表RecyleView的适配器
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
class AppLockListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LockConfigManager lockConfigManager;
    private RecyleViewItemSelectedListener listener;

    /**
     * Adapter的构造函数
     * @param context 上下文
     * @param manager 锁定配置管理器
     */
    public AppLockListAdapter(Context context, LockConfigManager manager) {
        this.context = context;
        lockConfigManager = manager;
    }

    /**
     * 设置点击的回调处理器
     * @param listener 回调处理器
     */
    public void setItemSelectedListener (RecyleViewItemSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * 检查是否有任何App被选中
     * @return true/false
     */
    public boolean isAnyItemSelected () {
        return lockConfigManager.selectedAppCount() > 0;
    }

    /**
     * 移除任何不锁定的APP
     * @return 移除的数量
     */
    public int removeSelectedApp () {
        return lockConfigManager.deleteSelectedAppInfo();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_app_lock_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AppLockInfo appLockInfo = lockConfigManager.getAppLockInfo(position);
        AppInfo appInfo = appLockInfo.getAppInfo();

        ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
        itemViewHolder.iv_icon.setImageDrawable(appInfo.getIcon());
        itemViewHolder.cb_lock.setChecked(appLockInfo.isEnable());
        itemViewHolder.tv_name.setText(appInfo.getName());
        itemViewHolder.cb_selected.setChecked(appLockInfo.isSelected());
    }

    @Override
    public int getItemCount() {
        return lockConfigManager.getAppListCount();
    }

    /**
     * 列表项的ViewHolder
     */
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_icon;
        private CheckBox cb_lock;
        private TextView tv_name;
        private View ll_item;
        private CheckBox cb_selected;

        public ItemViewHolder(final View itemView) {
            super(itemView);

            iv_icon = (ImageView) itemView.findViewById(id.iv_icon);
            cb_lock = (CheckBox) itemView.findViewById(id.cb_lock);
            tv_name = (TextView)itemView.findViewById(id.tv_name);
            ll_item = itemView.findViewById(id.ll_item);
            cb_selected = (CheckBox)itemView.findViewById(id.cb_selected);

            // 选中按钮点击事件
            cb_selected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    boolean isChecked = cb_selected.isChecked();
                    AppLockInfo appLockInfo = lockConfigManager.getAppLockInfo(pos);
                    appLockInfo.setSelected(isChecked);

                    // 调用回调函数
                    if (listener != null) {
                        listener.onItemSelected(pos, isChecked);
                    }
                }
            });

            // 普通点击事件，切换锁定使能
            ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 切换是否锁定状态
                    int pos = getAdapterPosition();
                    AppLockInfo appLockInfo = lockConfigManager.getAppLockInfo(pos);
                    lockConfigManager.setAppLockEnable(appLockInfo, !appLockInfo.isEnable());
                }
            });

            // 长点击事件
            ll_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        return listener.onItemLongClicked(getAdapterPosition());
                    }
                    return false;
                }
            });
        }
    }
}