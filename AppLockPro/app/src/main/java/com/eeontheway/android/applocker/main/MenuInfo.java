package com.eeontheway.android.applocker.main;

/**
 * 菜单信息
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class MenuInfo {
    public static final int ICON_INVALID = 0;

    private int titleRes;                   // 菜单名称
    private int iconRes;                    // 菜单图标
    private boolean selected;               // 是否被选中

    /**
     * 构造函数
     * @param titleRes
     * @param iconRes
     */
    public MenuInfo(int titleRes, int iconRes) {
        this.titleRes = titleRes;
        this.iconRes = iconRes;
    }

    /**
     * 获取标题资源
     * @return 标题资源
     */
    public int getTitleRes() {
        return titleRes;
    }

    /**
     * 获取图标资源
     * @return 图标资源
     */
    public int getIconRes() {
        return iconRes;
    }

    /**
     * 项目是否被选中
     * @return true 选中; false 未选中
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 设置是否被选中
     * @param selected true 选中; false 未选中
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
