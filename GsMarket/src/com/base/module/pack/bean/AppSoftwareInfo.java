/**
 *
 * Copyright (c) 2011, grandstream.
 */
package com.base.module.pack.bean;


import java.io.Serializable;
import java.util.List;

/**
 * ClassName:Biz_appSoftwareInfo
 * 
 * @Description:
 * @author WangZhangXing
 * @version 1.0 2011-12-26
 * @since 1.0
 * @see
 */
public class AppSoftwareInfo implements Serializable {
    /**
     * @Description
     */
    private static final long serialVersionUID = 1L;
    /**
     * @Description: 主键id
     */
    private Integer sysid;
    /**
     * @Description: 开发者id
     */
    private Integer developerid;
    /**
     * @Description: 应用名称
     */
    private String appname;
    /**
     * @Description: 图片路径
     */
    private String iconurl;
    /**
     * @Description: 缩略图路径
     */
    private String thumbnailurl;
    /**
     * @Description: 应用类别
     */
    private String apptypecode;
    /**
     * @Description: 描述
     */
    private String description;
    /**
     * @Description: 是否收费
     */
    private Integer ischarge;
    /**
     * @Description: 创建时间 格式为yyyy_MM_dd hh:mm:ss
     */
    private String createtime;
    /**
     * @Description: 创建者
     */
    private String creator;
    /**
     * @Description: 修改时间 格式为yyyy_MM_dd hh:mm:ss
     */
    private String updatetime;
    /**
     * @Description: 更新者
     */
    private String updator;
    /**
     * @Description: 状态(0：删除\r\n 1：可用\r\n 2：停用)
     */
    private String status;
    /**
     * @Description: 开发者信息
     */
    private DeveloperInfo developerInfo;
    /**
     * @Description: 分类信息
     */
    private AppType appType;


    /**
     * @Description: 版本信息
     */
    private List<ApkInfo> ApkInfo;
    /**
     * @Description: 下载信息
     */
    private AppDownloadInfo appDownloadInfo;

    /**
     * @Description: 评分信息
     */
    private List<GradeInfo> gradeInfo;

    /**
     * @Description: 下载总数
     */
    private int downloadCount;

    /**
     * @Description: 平均分数
     */
    private float gradenum;
    private String latestVersion;
    /**
     * @Description: 搜索关键字
     */
    private String searchWord;

    private String appcode;

    public String getAppcode() {
        return appcode;
    }

    public void setAppcode(String appcode) {
        this.appcode = appcode;
    }

    public Integer getSysid() {
        return sysid;
    }

    public void setSysid(Integer sysid) {
        this.sysid = sysid;
    }

    public Integer getDeveloperid() {
        return developerid;
    }

    public void setDeveloperid(Integer developerid) {
        this.developerid = developerid;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getThumbnailurl() {
        return thumbnailurl;
    }

    public void setThumbnailurl(String thumbnailurl) {
        this.thumbnailurl = thumbnailurl;
    }

    public String getApptypecode() {
        return apptypecode;
    }

    public void setApptypecode(String apptypecode) {
        this.apptypecode = apptypecode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIscharge() {
        return ischarge;
    }

    public void setIscharge(Integer ischarge) {
        this.ischarge = ischarge;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DeveloperInfo getDeveloperInfo() {
        return developerInfo;
    }

    public void setDeveloperInfo(DeveloperInfo developerInfo) {
        this.developerInfo = developerInfo;
    }

    public AppType getAppType() {
        return appType;
    }

    public void setAppType(AppType appType) {
        this.appType = appType;
    }

    public List<GradeInfo> getGradeInfo() {
        return gradeInfo;
    }

    public void setGradeInfo(List<GradeInfo> gradeInfo) {
        this.gradeInfo = gradeInfo;
    }

    public AppDownloadInfo getAppDownloadInfo() {
        return appDownloadInfo;
    }

    public void setAppDownloadInfo(AppDownloadInfo appDownloadInfo) {
        this.appDownloadInfo = appDownloadInfo;
    }

    public List<ApkInfo> getApkInfo() {
        return ApkInfo;
    }

    public void setApkInfo(List<ApkInfo> apkInfo) {
        ApkInfo = apkInfo;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public String getSearchWord() {
        return searchWord;
    }

    public float getGradenum() {
        return gradenum;
    }

    public void setGradenum(float gradenum) {
        this.gradenum = gradenum;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }
    
}