package com.eeontheway.android.applocker.app;

/**
 * 进程信息类
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class ProcessInfo extends BaseAppInfo {
    /**
     * 进程的UID
     */
    private int uid;

    /**
     * 获得UID
     *
     * @return UID
     */
    public int getUid() {
        return uid;
    }

    /**
     * 设置UID
     *
     * @param uid UID信息
     */
    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     * 进程PID
     */
    private int pid;

    /**
     * 获得进程PID
     *
     * @return 进程PID
     */
    public int getPid() {
        return pid;
    }

    /**
     * 设置进程PID
     *
     * @param pid 进程PID
     */
    public void setPid(int pid) {
        this.pid = pid;
    }
}
