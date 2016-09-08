package com.eeontheway.android.applocker.main;

/**
 * RecyleView的某个项被点击的事件
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-9
 */
interface RecyleViewItemSelectedListener {
    void onItemSelected (int pos, boolean selected);
    boolean onItemLongClicked (int pos);
    void onItemClicked (int pos);
}
