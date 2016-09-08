package com.eeontheway.android.applocker.share;

/**
 * 分享基类
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public abstract class ShareBase implements IShare {
    private boolean toFriend;
    private IShare.OnFinishListener listener;

    /**
     * 是否分享到朋友
     * @return true 分享到朋友; false 否
     */
    public boolean isToFriend () {
        return toFriend;
    }

    /**
     * 设置接受的对像为朋友
     * @param toFriend
     */
    public void setToFriend (boolean toFriend) {
        this.toFriend = toFriend;
    }

    /**
     * 设置分享结束时的监听器
     * @param listener 监听器
     */
    public void setListener(IShare.OnFinishListener listener) {
        this.listener = listener;
    }

    /**
     * 获取分享结束时，应当调用的监听器
     * @return 监听器
     */
    public IShare.OnFinishListener getListener() {
        return listener;
    }
}
