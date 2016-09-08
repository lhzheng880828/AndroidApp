package com.eeontheway.android.applocker.lock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.app.AppInfo;
import com.eeontheway.android.applocker.locate.Position;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 锁定配置管理器
 * 该配置器中的有些数据将被UI线程和后台锁监控线程同时访问，所以要加synchronized
 * 考虑到UI需要支持读和写，而后台监控线程只需要支持读，所以只需要对写操作加synchronized
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class LockConfigManager {
    private static final String lastModeIdKey = "lastModeIdKey";
    private static final String LOCK_CONFIG_FILE = "lock_config_pref";
    private static LockConfigManager instance;
    private static int instanceCount;
    private Context context;
    private ConditionDatabase conditionDatabase;
    private LockModeInfo currentLockModeInfo;
    private List<LockModeInfo> lockModeInfoList = new ArrayList<>();
    private List<AppLockInfo> appLockInfoList = new ArrayList<>();
    private List<BaseLockCondition> lockConditionList = new ArrayList<>();
    private List<AccessLog> accessLogList = new ArrayList<>();

    private DataObservable observable;
    private BroadcastReceiver packageRemoveReceiver;
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    private LockConfigManager(Context context) {
        this.context = context;

        conditionDatabase = new ConditionDatabase(context);
        observable = new DataObservable();
        conditionDatabase.open();
        loadModeList();
        registerPackageRemoveListener();
    }

    /**
     * 获取初始实例
     *
     * @param context 上下文
     */
    public static LockConfigManager getInstance(Context context) {
        if (instance == null) {
            instance = new LockConfigManager(context);
        }
        instanceCount++;
        return instance;
    }

    /**
     * 释放实例
     */
    public static void freeInstance() {
        if (instance != null) {
            if (--instanceCount == 0) {
                instanceCount = 0;
                instance.conditionDatabase.close();
                instance.observable.deleteObservers();
                instance.removePackageListener();
                instance = null;
            }
        }
    }

    /**
     * 注册数据变化的监听器
     *
     * @param observer
     */
    public void registerObserver(Observer observer) {
        observable.addObserver(observer);
    }

    /**
     * 取消数据变化监听器
     * @param observer 监听器
     */
    public void unregisterObserver (Observer observer) {
        observable.deleteObserver(observer);
    }

    /**
     * 配置是否使能监听器
     * @param dataType 指定的数据类型
     * @param enable 是否使能
     */
    public void setObserverEnable (DataObservable.DataType dataType, boolean enable) {
        observable.setEnable(enable);

        // 手动触发一次
        if (enable) {
            observable.notifyItemChanged(dataType);
        }
    }

    /**
     * 初始化模式列表
     */
    private void loadModeList() {
        // 获取所有的模式列表
        lockModeInfoList = conditionDatabase.queryModeList();
        if (lockModeInfoList.size() == 0) {
            // 如果列表为空，则创建一个缺省项，再切换过去
            currentLockModeInfo = createDefaultMode();
            switchModeInfo(0);
        } else {
            // 如果列表不为空，则检查上一次使能的模式id
            // 如果找到，则切换过去，否则，切换至第0个
            SharedPreferences sp = context.getSharedPreferences(LOCK_CONFIG_FILE, Context.MODE_PRIVATE);
            boolean exist = sp.contains(lastModeIdKey);
            if (exist) {
                // 找到相应id的模式，如果找到，则切换过去
                int id = sp.getInt(lastModeIdKey, 0);
                for (LockModeInfo modeInfo : lockModeInfoList) {
                    if (id == modeInfo.getId()) {
                        switchModeInfo(lockModeInfoList.indexOf(modeInfo));
                        return;
                    }
                }

            }

            // 找不到相应id的项，或者没有相应配置，则切换至模式0
            switchModeInfo(0);
        }
    }

    /**
     * 创建缺省的模式
     */
    private LockModeInfo createDefaultMode () {
        LockModeInfo modeInfo = addModeInfo(context.getString(R.string.default_mode));
        if (modeInfo == null) {
            throw new IllegalStateException("Add default mode failed");
        }

        // 缺省模式，默认使能
        modeInfo.setEnabled(true);
        return modeInfo;
    }

    /**
     * 获取模式信息列表
     *
     * @return 模式信息列表
     */
    public int getLockModeCount() {
        return lockModeInfoList.size();
    }

    /**
     * 获取指定位置的模式信息
     *
     * @param index 指定位置
     * @return 模式信息
     */
    public LockModeInfo getLockModeInfo(int index) {
        if (index < lockModeInfoList.size()) {
            return lockModeInfoList.get(index);
        }

        return null;
    }

    /**
     * 获取当前模式的名称
     * @return 当前模式名
     */
    public String getCurrentLockModeName () {
        return currentLockModeInfo.getName();
    }

    /**
     * 添加一个新模式
     *
     * @param modeName 新模式名
     * @return 新模式信息
     */
    public LockModeInfo addModeInfo(String modeName) {
        LockModeInfo modeInfo = conditionDatabase.addModeInfo(modeName);
        if (modeInfo != null) {
            modeInfo.setEnabled(false);
            int startPos = lockModeInfoList.size();
            lockModeInfoList.add(modeInfo);
            observable.notifyItemInserted(DataObservable.DataType.MODE_LIST, startPos, 1);
        }
        return modeInfo;
    }

    /**
     * 删除一个模式
     * @param modeInfo 待删除的模式
     * @return 是否删除成功
     */
    public boolean deleteModeInfo(LockModeInfo modeInfo) {
        if (lockModeInfoList.size() > 1) {
            int index = lockModeInfoList.indexOf(modeInfo);
            conditionDatabase.deleteModeInfo(modeInfo);
            lockModeInfoList.remove(index);
            observable.notifyItemRangeRemoved(DataObservable.DataType.MODE_LIST, index, 1);

            // 删除后，需要切换模式
            switchModeInfo(0);
            return true;
        }

        return false;
    }

    /**
     * 更新模式信息
     *
     * @param modeInfo 待更新的模式信息
     * @return 是否成功 true/false
     */
    public boolean updateModeInfo(LockModeInfo modeInfo) {
        int index = lockModeInfoList.indexOf(modeInfo);
        boolean ok = conditionDatabase.updateModeInfo(modeInfo);
        if (ok) {
            observable.notifyItemChanged(DataObservable.DataType.MODE_LIST, index);
        }
        return ok;
    }

    /**
     * 切换当前模式
     *
     * @param index 新模式的序号
     */
    public void switchModeInfo(int index) {
        // 如果是自己，则不必再切换
        // 否则，完成切换
        LockModeInfo nextMode = lockModeInfoList.get(index);
        if (nextMode == currentLockModeInfo) {
            return;
        }

        // 获取指定模式
        if (currentLockModeInfo != null) {
            currentLockModeInfo.setEnabled(false);
        }

        currentLockModeInfo = lockModeInfoList.get(index);
        currentLockModeInfo.setEnabled(true);

        // 重新加载各项配置
        loadAppInfoList();
        loadLockConditionList();

        // 保存当前模式
        SharedPreferences sp = context.getSharedPreferences(LOCK_CONFIG_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(lastModeIdKey, currentLockModeInfo.getId());
        editor.commit();

        // 通知所有的数据类型都发生变化
        observable.notifyItemChanged(DataObservable.DataType.MODE_LIST);
        observable.notifyItemChanged(DataObservable.DataType.APP_LIST);
        observable.notifyItemChanged(DataObservable.DataType.CONDITION_LIST);
        observable.notifyItemChanged(DataObservable.DataType.LOG_LIST);
    }

    /**
     * 获取当前模式下加入锁定配置的App数量
     *
     * @return App数量
     */
    public int getAppListCount() {
        return appLockInfoList.size();
    }

    /**
     * 检查被选中的App数量
     *
     * @return 被选中的APP数量
     */
    public int selectedAppCount() {
        int count = 0;

        for (AppLockInfo lockInfo : appLockInfoList) {
            if (lockInfo.isSelected()) {
                count++;
            }
        }

        return count;
    }

    /**
     * 选中所有的App
     * @param selected 是否全选所有的App
     */
    public void selectAllApp (boolean selected) {
        for (AppLockInfo lockInfo : appLockInfoList) {
            lockInfo.setSelected(selected);
        }

        observable.notifyItemChanged(DataObservable.DataType.APP_LIST);
    }

    /**
     * 获得指定位置的App锁定信息
     *
     * @param index 指定位置
     * @return App锁定信息
     */
    public AppLockInfo getAppLockInfo(int index) {
        return appLockInfoList.get(index);
    }

    /**
     * 加载所有的APp列表
     * @param modeInfo 指定的模式
     */
    synchronized public void loadAppInfoList (LockModeInfo modeInfo) {
        appLockInfoList = conditionDatabase.getAppInfoByMode(modeInfo);
        if (appLockInfoList.size() > 0) {
            observable.notifyItemInserted(DataObservable.DataType.APP_LIST, 0, appLockInfoList.size());
        }
    }

    public void loadAppInfoList() {
        loadAppInfoList(currentLockModeInfo);
    }

    /**
     * 将指定的App信息添加到指定模式下边
     *
     * @param appInfoList  指定的App锁定信息链表
     * @param lockModeInfo 指定的App信息
     * @return 是否添加成功; true/false
     */
    synchronized public boolean addAppInfoToMode(List<AppInfo> appInfoList, LockModeInfo lockModeInfo) {
        // 转换格式
        List<AppLockInfo> newLockerList = new ArrayList<>();
        for (AppInfo appInfo : appInfoList) {
            AppLockInfo appLockInfo = new AppLockInfo();
            appLockInfo.setAppInfo(appInfo);
            appLockInfo.setEnable(true);
            newLockerList.add(appLockInfo);
        }

        // 批量插入数据库中
        boolean ok = conditionDatabase.addAppInfoListToMode(newLockerList, lockModeInfo);
        if (ok) {
            // 加入缓存队列
            int startPos = appLockInfoList.size();
            appLockInfoList.addAll(newLockerList);
            observable.notifyItemInserted(DataObservable.DataType.APP_LIST,
                    startPos, newLockerList.size());
        }
        return ok;
    }

    public boolean addAppInfoToMode(List<AppInfo> appInfoList) {
        return addAppInfoToMode(appInfoList, currentLockModeInfo);
    }

    /**
     * 切换App的锁定状态
     *
     * @param appLockInfo 待设置的App信息
     * @param enable      是否锁定
     * @return 操作是否成功
     */
    synchronized public boolean setAppLockEnable(AppLockInfo appLockInfo, boolean enable) {
        boolean ok;

        ok = conditionDatabase.updateAppInfo(appLockInfo, enable);
        if (ok) {
            int index = appLockInfoList.indexOf(appLockInfo);
            appLockInfo.setEnable(enable);
            observable.notifyItemChanged(DataObservable.DataType.APP_LIST, index);
        }
        return ok;
    }

    /**
     * 判断指定包名的App是否在App锁定列表中
     *
     * @param packageName
     * @return true/false
     */
    public boolean isPackageInAppLockList(String packageName) {
        for (AppLockInfo info : appLockInfoList) {
            if (info.getPackageName().equals(packageName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 将App从锁定配置列表中移除
     *
     * @return 移除的数量
     */
    synchronized public int deleteSelectedAppInfo() {
        int count = 0;
        List<AppLockInfo> removeList = new ArrayList<>();

        // 扫描需要移除的App信息
        for (AppLockInfo lockInfo : appLockInfoList) {
            if (lockInfo.isSelected()) {
                removeList.add(lockInfo);
            }
        }

        // 使用事务处理批量快速批量删除
        boolean ok = conditionDatabase.deleteAppInfoList(removeList);
        if (ok) {
            observable.notifyItemChanged(DataObservable.DataType.APP_LIST);
        }

        // 开始移除操作
        count = removeList.size();
        for (AppLockInfo lockInfo : removeList) {
            appLockInfoList.remove(lockInfo);
        }
        return count;
    }

    /**
     * 删除指定的App信息
     *
     * @param positon 待删除的App信息序号
     * @return 成功/失败
     */
    synchronized public boolean deleteAppInfo (int positon) {
        List<AppLockInfo> removeList = new ArrayList<>();

        AppLockInfo appLockInfo = appLockInfoList.get(positon);
        removeList.add(appLockInfo);
        boolean ok = conditionDatabase.deleteAppInfoList(removeList);
        if (ok) {
            appLockInfoList.remove(positon);
            observable.notifyItemRangeRemoved(DataObservable.DataType.APP_LIST, positon, 1);
        }

        return ok;
    }

    /**
     * 删除指定的App信息
     * @param packageName 待删除的App包名
     * @return 成功/失败
     */
    synchronized public boolean deleteAppInfo(String packageName) {
        boolean ok = conditionDatabase.deleteAppInfo(packageName);
        if (ok) {
            for (int i = 0; i < appLockInfoList.size(); i++) {
                AppLockInfo appLockInfo = appLockInfoList.get(i);
                if (appLockInfo.getPackageName().equals(packageName)) {
                    appLockInfoList.remove(i);
                    observable.notifyItemChanged(DataObservable.DataType.APP_LIST);
                    break;
                }
            }
        }

        return ok;
    }

    /**
     * 加载锁定时机信息列表
     */
    public void loadLockConditionList() {
        loadLockConditionList(currentLockModeInfo);
    }

    synchronized public void loadLockConditionList(LockModeInfo modeInfo) {
        lockConditionList = conditionDatabase.queryLockCondition(modeInfo);
        if (lockConditionList.size() > 0) {
            observable.notifyItemInserted(DataObservable.DataType.CONDITION_LIST, 0, lockConditionList.size());
        }
    }

    /**
     * 删除指定的锁定条件
     * @param positon 待删除的锁定条件序号
     * @return 成功/失败
     */
    synchronized public boolean deleteLockCondition (int positon) {
        BaseLockCondition condition = lockConditionList.get(positon);
        boolean ok = conditionDatabase.deleteLockCondition(condition);
        if (ok) {
            lockConditionList.remove(condition);
            observable.notifyItemRangeRemoved(DataObservable.DataType.CONDITION_LIST, positon, 1);
        }

        return ok;
    }

    /**
     * 删除选中的锁定配置
     * @return 移除的数量
     */
    synchronized public int deleteSelectedLockCondition() {
        int count = 0;
        List<BaseLockCondition> removeList = new ArrayList<>();

        try {
            conditionDatabase.beginTransaction();

            // 扫描需要移除的App信息
            for (BaseLockCondition config : lockConditionList) {
                if (config.isSelected()) {
                    removeList.add(config);
                }
            }

            // 开始移除操作
            count = removeList.size();
            for (BaseLockCondition config : removeList) {
                // 从数据库中移除, 再从缓存队列中移除
                conditionDatabase.deleteLockCondition(config);
            }

            conditionDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            conditionDatabase.endTransaction();
        }

        // 操作成功后，将配置从缓存中移除
        for (BaseLockCondition condition : removeList) {
            lockConditionList.remove(condition);
        }

        // 通知外界数据发生变化
        observable.notifyItemChanged(DataObservable.DataType.CONDITION_LIST);
        return count;
    }

    /**
     * 检查被选中的App数量
     *
     * @return 被选中的APP数量
     */
    public int selectedLockCondition() {
        int count = 0;

        for (BaseLockCondition config : lockConditionList) {
            if (config.isSelected()) {
                count++;
            }
        }

        return count;
    }

    /**
     * 选中所有的条件
     */
    public void selectAllCondition (boolean selected) {
        for (BaseLockCondition condition : lockConditionList) {
            condition.setSelected(selected);
        }

        observable.notifyItemChanged(DataObservable.DataType.CONDITION_LIST);
    }

    /**
     * 获取指定位置的锁定配置
     *
     * @param index 指定位置
     * @return 锁定配置信息
     */
    public BaseLockCondition getLockCondition(int index) {
        return lockConditionList.get(index);
    }

    /**
     * 获取当前模式下的锁定配置的数量
     *
     * @return 锁定配置的数量
     */
    public int getLockConditionCount() {
        return lockConditionList.size();
    }

    /**
     * 更新锁定时机配置
     * @param config 待更新的配置
     * @return 是否成功 true/false
     */
    synchronized public boolean updateLockCondition(BaseLockCondition config) {
        boolean ok = conditionDatabase.updateLockCondition(config);
        if (ok) {
            // 如果数据库写成功了，则更新缓存
            int index = 0;
            for (int i = 0; i < lockConditionList.size(); i++) {
                BaseLockCondition condition = lockConditionList.get(i);
                // 注意 ，不能只检查id，还要检查类型(isMatch()中检查)
                if (condition.isMatch(config)) {
                    condition.copy(config);
                    index = i;
                    break;
                }
            }

            observable.notifyItemChanged(DataObservable.DataType.CONDITION_LIST, index);
        }
        return ok;
    }

    /**
     * 将指定的锁定配置添加到指定模式下边
     *
     * @param config  指定的App锁定信息
     * @param lockModeInfo 指定的App信息
     * @return 是否添加成功; true/false
     */
    synchronized public boolean addLockConditionIntoMode(BaseLockCondition config, LockModeInfo lockModeInfo) {
        boolean ok = conditionDatabase.addLockConditionIntoMode(config, lockModeInfo);
        if (ok) {
            int startPos = lockConditionList.size();
            lockConditionList.add(config);
            observable.notifyItemInserted(DataObservable.DataType.CONDITION_LIST, startPos, 1);
        }
        return ok;
    }

    public boolean addLockConditionIntoMode(BaseLockCondition config) {
        return addLockConditionIntoMode(config, currentLockModeInfo);
    }

    /**
     * 获取日志条目数
     * @return 日志条目数
     */
    public int getAcessLogsCount () {
        return accessLogList.size();
    }

    /**
     * 获取指定位置的日志信息
     * @param index 指定位置
     * @return 日志信息
     */
    public AccessLog getAccessLog (int index) {
        if (index < accessLogList.size()) {
            return accessLogList.get(index);
        }

        return null;
    }

    /**
     * 获取指定包名的最新锁定日志
     * @param packageName 查找是使用的包名
     * @return 日志信息
     */
    public AccessLog getAccessLog (String packageName) {
        for (AccessLog accessLog : accessLogList) {
            if (accessLog.getPackageName().equals(packageName)) {
                return accessLog;
            }
        }

        return null;
    }

    /**
     * 检查被选中的日志条目数量
     *
     * @return 被选中的日志条目数
     */
    public int selectedAccessLogs () {
        int count = 0;

        for (AccessLog log : accessLogList) {
            if (log.isSelected()) {
                count++;
            }
        }

        return count;
    }

    /**
     * 选中的所有日志条目数量
     */
    public void selectAllAccessLogs (boolean selecte) {
        for (AccessLog log : accessLogList) {
            log.setSelected(selecte);
        }

        if (accessLogList.size() > 0) {
            observable.notifyItemChanged(DataObservable.DataType.LOG_LIST);
        }
    }

    /**
     * 将指定的日志信息添加到数据库中保存
     * @param accessLog  待保存的日志信息
     * @return 是否添加成功; true/false
     */
    synchronized public boolean addAccessLog (AccessLog accessLog) {
        boolean ok = conditionDatabase.addAccessInfo(accessLog);
        if (ok) {
            // 新生成的日志，添加到头部
            accessLogList.add(0, accessLog);
            observable.notifyItemInserted(DataObservable.DataType.LOG_LIST, 0, 1);
        }
        return ok;
    }

    /**
     * 更新日志信息
     * @param accessLog 更新日志信息
     * @return 是否成功 true/false
     */
    synchronized public boolean updateAccessLog (AccessLog accessLog) {
        boolean ok = conditionDatabase.updateAccessInfo(accessLog);
        if (ok) {
            // 如果数据库写成功了，则更新缓存
            int index = 0;
            for (int i = 0; i < accessLogList.size(); i++) {
                AccessLog log = accessLogList.get(i);
                if (log.getId() == accessLog.getId()) {
                    log.copy(accessLog);
                    index = i;
                    break;
                }
            }
            observable.notifyItemChanged(DataObservable.DataType.LOG_LIST, index);
        }
        return ok;
    }

    /**
     * 删除指定的日志信息
     * @param positon 待删除的App信息序号
     * @return 成功/失败
     */
    synchronized public boolean deleteAccessLog (int positon) {
        List<AccessLog> accessLogList = new ArrayList<>();

        AccessLog accessLog = accessLogList.get(positon);
        accessLogList.add(accessLog);
        boolean ok = conditionDatabase.deleteAccessLogList(accessLogList);
        if (ok) {
            accessLogList.remove(accessLog);
            observable.notifyItemRangeRemoved(DataObservable.DataType.LOG_LIST, positon, 1);
        }

        return ok;
    }

    /**
     * 删除选中的锁定配置
     * @return 移除的数量
     */
    synchronized public int deleteSelectedAccessLogs () {
        int count = 0;
        List<AccessLog> removeList = new ArrayList<>();

        // 扫描需要移除的App信息
        for (AccessLog log : accessLogList) {
            if (log.isSelected()) {
                removeList.add(log);
            }
        }

        boolean ok = conditionDatabase.deleteAccessLogList(removeList);
        if (ok) {
            // 开始移除操作
            count = removeList.size();
            for (AccessLog log : removeList) {
                // 只删除内部的照片，存储在相册中的不删除
                if (log.getPhotoPath() != null) {
                    if (log.isPhotoInInternal()) {
                        String path = log.getPhotoPath();
                        new File(path).delete();
                    }
                }
                // 再从缓存队列中移除
                accessLogList.remove(log);
            }

            // 通知外界数据发生变化
            observable.notifyItemChanged(DataObservable.DataType.LOG_LIST);
        }

        return count;
    }

    /**
     * 获取更多的日志信息，用于延迟加载
     * @param moreCount 期望获取的更多数量
     * @return 实际获取的数量
     */
    synchronized public int loadAccessLogsMore (int moreCount) {
        List<AccessLog> accessLogs = conditionDatabase.queryAccessLog(
                                                accessLogList.size(), moreCount);
        if (accessLogs.size() > 0) {
            int pos = accessLogList.size();
            accessLogList.addAll(accessLogs);
            observable.notifyItemInserted(DataObservable.DataType.LOG_LIST, pos, accessLogs.size());
        }
        return accessLogs.size();
    }

    /**
     * 检查指定的包是否需要锁定
     * @param packageName 待检查的包
     * @param calendar 当前日期
     * @param position 当前地址
     * @return true/false
     */
    public boolean isPackageNeedLock (String packageName, Calendar calendar,
                                                   Position position) {
        // 遍历App列表，查找其是否在其中
        for (AppLockInfo appLockInfo : appLockInfoList) {
            // 如果找到相应的App信息，则进一步判断是否符合锁定条件
            AppInfo appInfo = appLockInfo.getAppInfo();
            if (appInfo.getPackageName().equals(packageName)) {
                // 如果未使能，则不需要锁定
                if (!appLockInfo.isEnable()) {
                    return false;
                }

                // 没有锁定条件，即意味着无条件满足
                if (lockConditionList.size() == 0) {
                    return true;
                }

                // 遍历锁定条件队列，判断是否满足所有锁定条件
                for (BaseLockCondition condition : lockConditionList) {
                    // 未使能，略过
                    if (!condition.isEnable()) {
                        continue;
                    }

                    if (condition instanceof TimeLockCondition) {
                        // 判断时间上是否匹配
                        if (!((TimeLockCondition)condition).isMatch(calendar)) {
                            return false;
                        }
                    } else if (condition instanceof PositionLockCondition) {
                        // 判断地理位置是否符合
                        if (!((PositionLockCondition)condition).isMatch(position)) {
                            return false;
                        }
                    }
                }

                // 未满足任何条件，视为锁定
                return true;
            }
        }

        return false;
    }

    /**
     * 注册安装包移除的监听器
     * 该监听器的用处是当包移除时，同时删除数据库中的配置，避免异常
     */
    private void registerPackageRemoveListener() {
        packageRemoveReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String packageName = intent.getData().getSchemeSpecificPart();
                deleteAppInfo(packageName);
            }
        };

        // 注册监听器
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        context.registerReceiver(packageRemoveReceiver, intentFilter);
    }

    /**
     * 取消安装包移除的监听器
     */
    private void removePackageListener() {
        context.unregisterReceiver(packageRemoveReceiver);
    }
}
