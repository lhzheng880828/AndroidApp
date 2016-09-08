package com.eeontheway.android.applocker.main;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.app.AppInfo;

import java.util.List;

/**
 * App选择列表的适配器
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
class AppSelectAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<AppSelectInfo> userInfoList;
    private List<AppSelectInfo> systemInfoList;

    public AppSelectAdapter(Context context, List<AppSelectInfo> userInfoList,
                            List<AppSelectInfo> systemInfoList) {
        this.context = context;
        this.userInfoList = userInfoList;
        this.systemInfoList = systemInfoList;
    }

    @Override
    public int getGroupCount() {
        return 2;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return (groupPosition == 0) ? userInfoList.size() : systemInfoList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return (groupPosition == 0) ? userInfoList : systemInfoList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<AppSelectInfo> listInfo = (List<AppSelectInfo>)getGroup(groupPosition);
        return listInfo.get(childPosition);
    }

    public String getGroupName (int groupPosition) {
        if (groupPosition == 0) {
            return context.getString(R.string.user_software);
        } else {
            return context.getString(R.string.system_software);
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    public long getChildSelectedCount (int groupPosition) {
        long count = 0;

        List<AppSelectInfo> listInfo = (List<AppSelectInfo>)getGroup(groupPosition);
        for (AppSelectInfo info : listInfo) {
            if (info.isSelected()) {
                count++;
            }
        }

        return count;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        class ViewHolder {
            TextView tv_name;
            TextView tv_childcount;
        }
        // 重刷UI
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group_applocker_list, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);
            viewHolder.tv_childcount = (TextView)convertView.findViewById(R.id.tv_childcount);
            convertView.findViewById(R.id.cb_lock).setVisibility(View.GONE);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 界面重新设置
        viewHolder.tv_name.setText(getGroupName(groupPosition));
        viewHolder.tv_childcount.setText(context.getString(R.string.child_selected_count,
                getChildSelectedCount(groupPosition), getChildrenCount(groupPosition)));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        class ViewHolder {
            ImageView iv_icon;
            CheckBox cb_lock;
            TextView tv_name;
        }

        // 重刷UI
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_applocker_list, null);
            viewHolder = new ViewHolder();
            viewHolder.cb_lock = (CheckBox)convertView.findViewById(R.id.cb_lock);
            viewHolder.iv_icon = (ImageView)convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 界面重新设置
        AppSelectInfo lockerInfo = (AppSelectInfo)getChild(groupPosition, childPosition);
        AppInfo appInfo = lockerInfo.getAppInfo();
        viewHolder.cb_lock.setChecked(lockerInfo.isSelected());
        viewHolder.iv_icon.setImageDrawable(appInfo.getIcon());
        viewHolder.tv_name.setText(appInfo.getName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
