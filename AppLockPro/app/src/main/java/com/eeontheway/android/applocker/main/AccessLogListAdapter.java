package com.eeontheway.android.applocker.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.lock.AccessLog;
import com.eeontheway.android.applocker.lock.LockConfigManager;

import java.io.IOException;

/**
 * 反馈列表RecyleView的适配器
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
class AccessLogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    boolean showTimeGroup;
    private LockConfigManager lockConfigManager;
    private RecyleViewItemSelectedListener listener;

    /**
     * Adapter的构造函数
     * @param context 上下文
     * @param manager 锁定配置管理器
     */
    public AccessLogListAdapter(Context context, LockConfigManager manager, boolean showTimeGroup) {
        this.context = context;
        this.showTimeGroup = showTimeGroup;
        lockConfigManager = manager;
    }

    /**
     * 加载拍摄的照片
     * @param logInfo 日志信息
     * @return 拍摄的照片
     */
    public Bitmap loadPhoto (AccessLog logInfo) {
        Bitmap bitmap = null;

        if (logInfo.getPhotoPath() != null) {
            if (logInfo.isPhotoInInternal()) {
                // 加载内部图像文件
                bitmap = BitmapFactory.decodeFile(logInfo.getPhotoPath());
                return bitmap;
            } else {
                // 加载相册中的文件
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                            Uri.parse(logInfo.getPhotoPath()));
                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 如果相册中的图像已经被删除，或者没有保存照片，则加载缺省的图像
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.icon_person);
        }
        return bitmap;
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_app_lock_log_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AccessLog logInfo = lockConfigManager.getAccessLog(position);
        ItemViewHolder viewHolder = (ItemViewHolder)holder;

        viewHolder.tv_appName.setText(logInfo.getAppName());
        viewHolder.cb_selected.setChecked(logInfo.isSelected());
        viewHolder.iv_photo.setImageBitmap(loadPhoto(logInfo));
        viewHolder.tv_appName.setText(logInfo.getAppName());
        viewHolder.tv_time.setText(logInfo.getTime());
        String location = logInfo.getLocation();
        if (location == null) {
            location = context.getString(R.string.unknwon_location);
        }
        viewHolder.tv_location.setText(context.getString(R.string.location_args, location));
        viewHolder.tv_error_count.setText(context.getString(R.string.password_err_counter,
                                                    logInfo.getPasswordErrorCount()));

    }

    @Override
    public int getItemCount() {
        return lockConfigManager.getAcessLogsCount();
    }

    /**
     * 加载更多
     */
    public void loadMoreItem (int count) {
        lockConfigManager.loadAccessLogsMore(count);
    }

    /**
     * 列表项的ViewHolder
     */
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_photo;
        private TextView tv_appName;
        private TextView tv_time;
        private TextView tv_location;
        private TextView tv_error_count;
        private View ll_item;
        private CheckBox cb_selected;

        public ItemViewHolder(final View itemView) {
            super(itemView);

            iv_photo = (ImageView) itemView.findViewById(R.id.iv_photo);
            tv_appName = (TextView) itemView.findViewById(R.id.tv_appName);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            tv_error_count = (TextView) itemView.findViewById(R.id.tv_error_count);
            ll_item = itemView.findViewById(R.id.ll_item);
            cb_selected = (CheckBox)itemView.findViewById(R.id.cb_selected);

            // 选中按钮点击事件
            cb_selected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    boolean isChecked = cb_selected.isChecked();
                    AccessLog log = lockConfigManager.getAccessLog(pos);
                    log.setSelected(isChecked);

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
                    if (listener != null) {
                        listener.onItemClicked(getAdapterPosition());
                    }
                }
            });
        }
    }
}