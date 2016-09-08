package com.eeontheway.android.applocker.lock;

/**
 * App在数据库中的信息
 * 主要用于在数据中缓存App的id，便于插入App锁定时直接从该对像中获得数据的id
 * 由于实际应用中数据里该结构数据量很少，加之在数据库中自动维护了一个引用计数用于自动删除该对像
 * 所以，在应用中的缓存就不存储引用计数，以避免自己去处理何时将该结构从数据库中删除。
 * 为了与数据库同步，每次添加或删除时app时，简单重新同步一下，少量开销.
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class AppNameInfo {
    private long id;
    private String packageName;

    public AppNameInfo(long id, String packageName) {
        this.id = id;
        this.packageName = packageName;
    }

    /**
     * 获取其在数据库的id
     *
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * 设置其在数据库中的id
     *
     * @param id 数据库id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * 获取包名
     *
     * @return 包名
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * 设置包名
     *
     * @param packageName 包名
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
