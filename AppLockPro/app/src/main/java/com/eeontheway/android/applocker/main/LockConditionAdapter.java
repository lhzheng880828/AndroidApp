package com.eeontheway.android.applocker.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.locate.Position;
import com.eeontheway.android.applocker.lock.BaseLockCondition;
import com.eeontheway.android.applocker.lock.LockConfigManager;
import com.eeontheway.android.applocker.lock.PositionLockCondition;
import com.eeontheway.android.applocker.lock.TimeLockCondition;

/**
 * 反馈列表RecyleView的适配器
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
class LockConditionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_TIME = 0;
    private static final int ITEM_TYPE_GPS = 1;
    private static final int ITEM_TYPE_UNKNOWN = 2;

    private Context context;
    private LockConfigManager lockConfigManager;
    private RecyleViewItemSelectedListener listener;

    /**
     * Adapter的构造函数
     * @param context 上下文
     * @param manager 锁定配置管理器
     */
    public LockConditionAdapter(Context context, LockConfigManager manager) {
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
        return lockConfigManager.selectedLockCondition() > 0;
    }

    /**
     * 移除任何不锁定的APP
     * @return 移除的数量
     */
    public int removeSelectedLockConfig () {
        return lockConfigManager.deleteSelectedLockCondition();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_TIME) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_time_lock_config, parent, false);
            return new TimeConditionViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_postion_lock_config, parent, false);
            return new PositionConditionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseConditionViewHolder)holder).setConfig(lockConfigManager.getLockCondition(position));
    }

    @Override
    public int getItemCount() {
        return lockConfigManager.getLockConditionCount();
    }

    @Override
    public int getItemViewType(int position) {
        BaseLockCondition config = lockConfigManager.getLockCondition(position);
        if (config instanceof TimeLockCondition) {
            return ITEM_TYPE_TIME;
        } else if (config instanceof PositionLockCondition){
            return ITEM_TYPE_GPS;
        }

        return ITEM_TYPE_UNKNOWN;
    }

    /**
     * 基础的ItemViewHolder
     */
    abstract class BaseConditionViewHolder extends RecyclerView.ViewHolder {
        public View ll_item;
        public CheckBox cb_enable;
        public CheckBox cb_selected;

        public BaseConditionViewHolder(View itemView) {
            super(itemView);

            cb_enable = (CheckBox) itemView.findViewById(R.id.cb_enable);
            ll_item = itemView.findViewById(R.id.ll_item);
            cb_selected = (CheckBox) itemView.findViewById(R.id.cb_selected);

            // 选中按钮点击事件
            cb_selected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    boolean isChecked = cb_selected.isChecked();
                    BaseLockCondition config = lockConfigManager.getLockCondition(pos);
                    config.setSelected(isChecked);

                    if (listener != null) {
                        listener.onItemSelected(pos, isChecked);
                    }
                }
            });

            // 普通点击事件，切换锁定使能
            ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 切换使能状态
                    int pos = getAdapterPosition();
                    BaseLockCondition oldConfig = lockConfigManager.getLockCondition(pos);
                    BaseLockCondition newConfig = (BaseLockCondition) oldConfig.clone();
                    newConfig.setEnable(!oldConfig.isEnable());
                    lockConfigManager.updateLockCondition(newConfig);
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

        abstract public void setConfig(BaseLockCondition config);
    }

    /**
     * 位置信息的ViewHolder
     */
    class PositionConditionViewHolder extends BaseConditionViewHolder {
        public TextView tv_address;
        public TextView tv_latitude;
        public TextView tv_longitude;
        public TextView tv_radius;

        public PositionConditionViewHolder(View itemView) {
            super(itemView);

            tv_latitude = (TextView) itemView.findViewById(R.id.tv_latitude);
            tv_longitude = (TextView) itemView.findViewById(R.id.tv_longitude);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            tv_radius = (TextView) itemView.findViewById(R.id.tv_radius);
        }

        public void setConfig (BaseLockCondition config) {
            PositionLockCondition positionLockCondition = (PositionLockCondition)config;

            Position pos = positionLockCondition.getPosition();
            tv_address.setText(pos.getAddress());
            tv_latitude.setText(context.getString(R.string.latitude, pos.getLatitude()));
            tv_longitude.setText(context.getString(R.string.longitude, pos.getLongitude()));
            tv_radius.setText(context.getString(R.string.radius, pos.getRadius()));
            cb_enable.setChecked(positionLockCondition.isEnable());
            cb_selected.setChecked(positionLockCondition.isSelected());
        }
    }

    /**
     * 时间配置的ViewHolder
     */
    class TimeConditionViewHolder extends BaseConditionViewHolder {
        public TextView tv_start_time;
        public TextView tv_end_time;
        public TextView tv_day;

        public TimeConditionViewHolder(final View itemView) {
            super(itemView);

            tv_start_time = (TextView)itemView.findViewById(R.id.tv_start_time);
            tv_end_time = (TextView)itemView.findViewById(R.id.tv_end_time);
            tv_day = (TextView)itemView.findViewById(R.id.tv_day);
        }

        public void setConfig (BaseLockCondition config) {
            TimeLockCondition timeLockCondition = (TimeLockCondition)config;

            tv_start_time.setText(context.getString(R.string.start_time, timeLockCondition.getStartTime()));
            tv_end_time.setText(context.getString(R.string.end_time, timeLockCondition.getEndTime()));
            tv_day.setText(timeLockCondition.getDayString(context));
            cb_enable.setChecked(timeLockCondition.isEnable());
            cb_selected.setChecked(timeLockCondition.isSelected());
        }
    }
}