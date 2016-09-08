package com.eeontheway.android.applocker.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.feedback.FeedBackListActivity;
import com.eeontheway.android.applocker.locate.LocationService;
import com.eeontheway.android.applocker.locate.Position;
import com.eeontheway.android.applocker.lock.DataObservable;
import com.eeontheway.android.applocker.lock.LockConfigManager;
import com.eeontheway.android.applocker.lock.LockModeInfo;
import com.eeontheway.android.applocker.login.IUserManager;
import com.eeontheway.android.applocker.login.LoginOrRegisterActivity;
import com.eeontheway.android.applocker.login.UserInfo;
import com.eeontheway.android.applocker.login.UserManagerFactory;
import com.eeontheway.android.applocker.share.ShareActivity;
import com.eeontheway.android.applocker.share.ShareInfo;
import com.eeontheway.android.applocker.utils.Configuration;
import com.eeontheway.android.applocker.utils.SystemUtils;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * 主界面左侧滑动页面显示的内容
 * 包含主菜单和用户账户入口
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class MainLeftFragment extends Fragment {
    public static final int REQUEST_LOGIN = 2;

    // 顶层菜单项
    private static final int MODE_LIST_ROW = 0;
    private static final int SETTING_ROW = 1;
    private static final int ABOUT_ROW = 2;
    private static final MenuInfo [] topMenuInfos = {
            new MenuInfo(R.string.mode_list, R.drawable.ic_mode),                // 模式列表
            new MenuInfo(R.string.settings, R.drawable.ic_settings),                // 设置
            new MenuInfo(R.string.about, R.drawable.ic_about)                     // 关于
    };

    private Activity parentActivity;
    private LockConfigManager lockConfigManager;
    private IUserManager userManager;
    private LocationService locationService;
    private LocationService.PositionChangeListener positionChangeListener;

    private ExpandableListView ev_menu;
    private BaseExpandableListAdapter ev_adapter;
    private ImageView iv_head;
    private TextView tv_welcome;
    private TextView tv_current_time;
    private TextView tv_current_address;
    private Button bt_reg_login;
    private Button bt_logout;
    private Button bt_share;
    private Button bt_feedback;
    private Observer observer;

    /**
     * Fragment的onCreate回调
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = getActivity();
        ev_adapter = new TopMenuAdapter();
        lockConfigManager = LockConfigManager.getInstance(parentActivity);
        locationService = LocationService.getInstance(parentActivity);

        initUserManager();
        initObserver();
        initLocateService();
    }

    /**
     * Fragment的onCreateView回调
     * @param savedInstanceState
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_left, null);

        // 找到所有的View
        ev_menu = (ExpandableListView) view.findViewById(R.id.ev_menu);
        tv_welcome = (TextView)view.findViewById(R.id.tv_welcome);
        iv_head = (ImageView) view.findViewById(R.id.iv_head);
        tv_current_time = (TextView)view.findViewById(R.id.tv_current_time);
        tv_current_address = (TextView) view.findViewById(R.id.tv_current_address);
        bt_reg_login = (Button) view.findViewById(R.id.bt_reg_login);
        bt_logout = (Button) view.findViewById(R.id.bt_logout);
        bt_feedback = (Button)view.findViewById(R.id.bt_feedback);
        bt_share = (Button)view.findViewById(R.id.bt_share);

        // 初始化菜单
        initView();
        initMenus();
        initShareAndFeedback();

        return view;
    }

    /**
     * Activity的onDestory函数
     */
    @Override
    public void onDestroy() {
        locationService.removePositionListener(positionChangeListener);
        lockConfigManager.unregisterObserver(observer);
        lockConfigManager.freeInstance();
        userManager.unInit();

        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLoginState();
        updateHeaderView(new Date(), locationService.getLastPosition());
    }

    /**
     * 等待初始密码的设置结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_LOGIN:         // 登陆进去
                updateLoginState();
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(parentActivity, R.string.login_scuess, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 初始化位置监听服务
     */
    private void initLocateService () {
        positionChangeListener = new LocationService.PositionChangeListener() {
            @Override
            public void onLocateResult(int errorCode, String errorMsg) {
            }

            @Override
            public void onPositionChanged(Position oldPosition, Position newPosition) {
                updateHeaderView(new Date(), newPosition);
            }
        };

        locationService.addPositionListener(positionChangeListener);
    }

    /**
     * 初始化监听器
     */
    private void initObserver () {
        observer = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                // 通知Adapter数据发生变化
                DataObservable.ObserverInfo info = (DataObservable.ObserverInfo)data;
                if (info.dataType != DataObservable.DataType.MODE_LIST) {
                    return;
                } else {
                    ev_adapter.notifyDataSetChanged();
                }
            }
        };

        lockConfigManager.registerObserver(observer);
    }

    /**
     * 初始化菜单列表
     */
    private void initMenus () {
        ev_menu.setAdapter(ev_adapter);
        ev_menu.expandGroup(MODE_LIST_ROW);

        // 列表的组点击事件
        ev_menu.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                switch (groupPosition) {
                    case MODE_LIST_ROW:
                        // 修改修改选中状态，以便切换菜单的指示器
                        MenuInfo menuInfo = topMenuInfos[groupPosition];
                        menuInfo.setSelected(!menuInfo.isSelected());
                        ev_adapter.notifyDataSetChanged();
                        break;
                    case SETTING_ROW:
                        SettingsActivity.start(parentActivity);
                        break;
                    case ABOUT_ROW:
                        AboutActivity.start(parentActivity);
                        break;
                }
                return false;
            }
        });

        // 列表子项点击事件
        ev_menu.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                if (groupPosition == MODE_LIST_ROW) {
                    lockConfigManager.switchModeInfo(childPosition);
                }
                return false;
            }
        });

        // 列表子项长按事件
        ev_menu.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.getId() == R.id.ll_sub_menu) {
                    // 排除掉最后一个无效的菜单项
                    int lockModeCount = lockConfigManager.getLockModeCount();
                    if ((lockModeCount > 0) && (position <= lockModeCount)) {
                        showModeModifyDialog(view, lockConfigManager.getLockModeInfo(position - 1));
                        return true;
                    }

                }
                return false;
            }
        });
    }

    /**
     * 初始化各种UI的initShareAndFeedback
     */
    private void initShareAndFeedback() {
        // 启动反馈功能
        bt_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedBackListActivity.start(parentActivity);
            }
        });

        // 启动分享功能
        bt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShareApp();
            }
        });
    }

    /**
     * 初始化界面显示
     */
    private void initView () {
        // 登陆按钮点击事件
        bt_reg_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginOrRegisterActivity.startForResult(MainLeftFragment.this, true, REQUEST_LOGIN);
            }
        });

        // 登陆出去事件
        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userManager.logout();
                updateHeaderView(new Date(), locationService.getLastPosition());
                updateLoginState();

                Toast.makeText(parentActivity, R.string.you_have_login_out,
                                                                Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化用户管理器
     */
    private void initUserManager() {
        userManager = UserManagerFactory.create(parentActivity);
        userManager.init(parentActivity);
    }

    /**
     * 更新用户登陆状态显示
     */
    private void updateLoginState () {
        UserInfo userInfo = userManager.getMyUserInfo();
        if (userInfo == null) {
            // 未登陆
            bt_reg_login.setVisibility(View.VISIBLE);
            bt_logout.setVisibility(View.GONE);
            tv_welcome.setText(R.string.you_are_not_logined_yet);
            iv_head.setImageResource(R.drawable.ic_unknown_user);

        } else {
            bt_reg_login.setVisibility(View.GONE);
            bt_logout.setVisibility(View.VISIBLE);
            tv_welcome.setText(getString(R.string.welcome_to_use_app, userInfo.getUserName()));
            iv_head.setImageResource(R.drawable.ic_known_user);
        }
    }

    /**
     * 更新时间和地址等其它头部信息
     */
    public void updateHeaderView (Date date, Position position) {
        tv_current_time.setText(SystemUtils.formatDate(date, "yyyy年MM月dd日 EEEE"));
        if ((position == null) || (position.getAddress() == null)) {
            tv_current_address.setText(getString(R.string.current_location,
                                                getString(R.string.unknwon_location)));
        } else {
            tv_current_address.setText(getString(R.string.current_location,
                                                locationService.getLastPosition().getAddress()));
        }
    }

    /**
     * 启动App分享功能
     */
    private void startShareApp () {
        // 创建App分享信息
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.url = Configuration.webSiteUrl;
        shareInfo.title = Configuration.SHARE_APP_TITLE;
        shareInfo.content = Configuration.APP_DESCRIPTION;
        shareInfo.thumbBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_lanucher);

        // 启动分享界面
        ShareActivity.start(parentActivity, shareInfo);
    }

    /**
     * 显示添加模式窗口，让用户选择
     */
    private void showCreateModeDialog() {
        ModeRenameDialog dialog = new ModeRenameDialog(parentActivity, true);
        dialog.setListener(new ModeRenameDialog.ResultListener() {
            @Override
            public void rename(String newName) {
                LockModeInfo modeInfo = lockConfigManager.addModeInfo(newName);
                if (modeInfo != null) {
                    // 如果之前为空，则切换到该模式；否则，保留之前的配置
                    if (lockConfigManager.getLockModeCount() == 1) {
                        lockConfigManager.switchModeInfo(0);
                    }
                    Toast.makeText(parentActivity, getString(R.string.mode_add_ok, modeInfo.getName()),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(parentActivity, R.string.add_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancel() {}
        });
        dialog.show();
    }

    /**
     * 显示模式名称修改对话框
     * 等待重命名的模式
     */
    private void showRenameModeDialog (final LockModeInfo modeInfo) {
        ModeRenameDialog dialog = new ModeRenameDialog(parentActivity, false);
        dialog.setListener(new ModeRenameDialog.ResultListener() {
            @Override
            public void rename(String newName) {
                modeInfo.setName(newName);
                boolean ok = lockConfigManager.updateModeInfo(modeInfo);
                if (ok) {
                    Toast.makeText(parentActivity, R.string.mode_rename_ok, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(parentActivity, R.string.mode_rename_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancel() {}
        });
        dialog.show();
    }

    /**
     * 显示删除模式对话框
     * @param modeInfo 待删除的模式
     */
    private void showDeleteModeDialog (final LockModeInfo modeInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(parentActivity, R.layout.view_mode_delete, null);
        TextView tv_msg = (TextView) view.findViewById(R.id.tv_msg);
        tv_msg.setText(getString(R.string.confirmDelete_modeinfo, modeInfo.getName()));
        final Button bt_del = (Button) view.findViewById(R.id.bt_del);
        final Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        bt_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 最后一项不允许删除，通过不使能删除按钮来实现，所以此处不必检查
                lockConfigManager.deleteModeInfo(modeInfo);

                dialog.dismiss();
                Toast.makeText(parentActivity, R.string.deleteOk, Toast.LENGTH_SHORT).show();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 显示模式修改对话框
     */
    private void showModeModifyDialog (View view, final LockModeInfo modeInfo) {
        // 弹出对话框
        PopupMenu popupMenu = new PopupMenu(parentActivity, view, Gravity.BOTTOM);
        popupMenu.inflate(R.menu.menu_mode_modify);
        if (lockConfigManager.getLockModeCount() == 1) {
            // 不允许删除最后一项
            popupMenu.getMenu().findItem(R.id.action_delete).setVisible(false);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_rename:
                        showRenameModeDialog(modeInfo);
                        break;
                    case R.id.action_delete:
                        showDeleteModeDialog(modeInfo);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    /**
     * 菜单列表显示的Adpter
     */
    private class TopMenuAdapter extends BaseExpandableListAdapter {
        private static final int CHILD_TYPE_NORMAL = 0;
        private static final int CHILD_TYPE_ADD = 1;

        @Override
        public int getGroupCount() {
            return topMenuInfos.length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return topMenuInfos[groupPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent) {
            // 渲染UI，由于菜单列表很少，就不用考虑优化
            View view = View.inflate(parentActivity, R.layout.item_menu_main_left_top, null);
            ImageView iv_icon = (ImageView)view.findViewById(R.id.iv_icon);
            TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
            ViewStub vs_indicator = (ViewStub)view.findViewById(R.id.vs_indicator);

            // 配置显示
            MenuInfo menuInfo = topMenuInfos[groupPosition];
            if (menuInfo.getIconRes() != MenuInfo.ICON_INVALID) {
                iv_icon.setImageResource(menuInfo.getIconRes());
            }
            tv_title.setText(menuInfo.getTitleRes());

            // 仅在模式列表上显示指示器
            if (groupPosition == MODE_LIST_ROW) {
                View hidView = vs_indicator.inflate();
                CheckBox checkBox = (CheckBox) hidView.findViewById(R.id.iv_indicator);
                checkBox.setChecked(menuInfo.isSelected());

                // 添加模式按钮
                ImageButton btn_add = (ImageButton) hidView.findViewById(R.id.btn_add);
                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCreateModeDialog();
                    }
                });
            }

            return view;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            int cnt = 0;

            if (groupPosition == MODE_LIST_ROW) {
                cnt = lockConfigManager.getLockModeCount();
            }
            return cnt;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }


        @Override
        public int getChildTypeCount() {
            return 2;
        }

        public int getChildType(int groupPosition, int childPosition) {
            if (groupPosition == MODE_LIST_ROW) {
                if (childPosition == lockConfigManager.getLockModeCount()) {
                    return CHILD_TYPE_ADD;
                } else {
                    return CHILD_TYPE_NORMAL;
                }
            } else {
                return 1;
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            LockModeInfo lockModeInfo = null;

            if (groupPosition == MODE_LIST_ROW) {
                lockModeInfo = lockConfigManager.getLockModeInfo(childPosition);
            }
            return lockModeInfo;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            View view = View.inflate(parentActivity, R.layout.item_menu_main_left_sub, null);
            TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
            CheckBox cb_enable = (CheckBox) view.findViewById(R.id.cb_enable);

            if (groupPosition == MODE_LIST_ROW) {
                LockModeInfo lockModeInfo = lockConfigManager.getLockModeInfo(childPosition);
                tv_title.setText(lockModeInfo.getName());
                if (lockModeInfo.isEnabled()) {
                    tv_title.setTextColor(getResources().getColor(R.color.colorFirstTextColor));
                } else {
                    tv_title.setTextColor(getResources().getColor(R.color.colorSecondTextColor));
                }
                cb_enable.setChecked(lockModeInfo.isEnabled());
            }

            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
