package com.eeontheway.android.applocker.lock;

/**
 * 模式信息
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class LockModeInfo {
    private int id;
    private String name;
    private boolean enabled;

    /**
     * 获取模式ID
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * 设置模式ID
     * @param id 模式ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取模式名称
     * @return 模式名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置模式名
     * @param name 模式名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 当前是否使能
     * @return true/false
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置当前是否使能
     * @param enabled true/false
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
