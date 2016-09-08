package com.eeontheway.android.applocker.feedback;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eeontheway.android.applocker.R;

import java.util.List;

/**
 * 反馈列表RecyleView的适配器
 */
class FeedBackListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE = 0;
    private static final int FOOT_TYPE = 1;

    private Context context;
    private ItemClickListener itemClickListener;
    private List<FeedBackTopic> feedBackInfoList;
    private boolean upLoadingMoreEnabled;

    public FeedBackListAdapter(Context context, List<FeedBackTopic> feedBackInfoList) {
        this.context = context;
        this.feedBackInfoList = feedBackInfoList;
    }

    /**
     * 设置是否上拉使能
     * @param enable
     */
    public void setUpLoadingMoreEnable (boolean enable) {
        boolean lastIsEnabled = upLoadingMoreEnabled;

        upLoadingMoreEnabled = enable;
        if (lastIsEnabled != enable) {
            notifyDataSetChanged();
        }
    }

    /**
     * 列表项的ViewHolder
     */
    class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView et_request_content;
        public TextView tv_request_time;
        public TextView tv_request_responsed;
        public CardView cardView;

        public ItemViewHolder(View itemView) {
            super(itemView);

            et_request_content = (TextView)itemView.findViewById(R.id.et_request_content);
            tv_request_time = (TextView)itemView.findViewById(R.id.tv_reqeust_time);
            tv_request_responsed = (TextView)itemView.findViewById(R.id.tv_reqeust_responsed);
            et_request_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClickListener(getAdapterPosition());
                }
            });
            cardView = (CardView)itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClickListener(getAdapterPosition());
                }
            });
        }
    }

    /**
     * 列表项底部加载更多的布局
     */
    class FooterViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar pb_progress;
        public TextView tv_loading;

        public FooterViewHolder(View itemView) {
            super(itemView);

            pb_progress = (ProgressBar)itemView.findViewById(R.id.pb_progress);
            tv_loading = (TextView)itemView.findViewById(R.id.tv_loading);
        }
    }

    /**
     * 设置项目点击回调
     */
    interface ItemClickListener {
        void onItemClickListener (int postion);
    }

    /**
     * 设置点击的回调处理器
     * @param itemClickListener 回调处理器
     */
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_feedback_reqeust_info, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == FOOT_TYPE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.foot_feedback_list, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
            FeedBackInfo info = feedBackInfoList.get(position).getTopic();
            itemViewHolder.et_request_content.setText(info.getContent());
            itemViewHolder.tv_request_time.setText(context.getString(R.string.feed_back_time,
                                                            info.getCreateTime()));
            if (info.isResponsed()) {
                itemViewHolder.tv_request_responsed.setText(context.getString(R.string.replyed));
                itemViewHolder.tv_request_responsed.setTextColor(
                                context.getResources().getColor(R.color.feedback_responsed));
            } else {
                itemViewHolder.tv_request_responsed.setText(context.getString(R.string.not_replyed));
                itemViewHolder.tv_request_responsed.setTextColor(
                                context.getResources().getColor(R.color.feedback_not_responsed));
            }
        }
    }

    @Override
    public int getItemCount() {
        int size = feedBackInfoList.size();
        if (upLoadingMoreEnabled) {
             size += 1;
        }
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        if (upLoadingMoreEnabled && ((position + 1)> feedBackInfoList.size())) {
            return FOOT_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }
}