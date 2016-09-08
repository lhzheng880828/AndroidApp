package com.base.module.pack.bean;
/**
 * Copyright:Grandstream
 */
import java.io.Serializable;
public class ApkInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String packageName;
    private String appname;
    private String appcode;
    private String version;
    private long filesize;
    private String downloadUrl;
    private Integer sysid;
    private Integer appid;
    private String publishdata;
    private String updatedetail;
    private Integer versioncode;
    private String size;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getAppcode() {
        return appcode;
    }

    public void setAppcode(String appcode) {
        this.appcode = appcode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public Integer getSysid() {
        return sysid;
    }

    public void setSysid(Integer sysid) {
        this.sysid = sysid;
    }

    public Integer getAppid() {
        return appid;
    }

    public void setAppid(Integer appid) {
        this.appid = appid;
    }

    public String getPublishdata() {
        return publishdata;
    }

    public void setPublishdata(String publishdata) {
        this.publishdata = publishdata;
    }

    public String getUpdatedetail() {
        return updatedetail;
    }

    public void setUpdatedetail(String updatedetail) {
        this.updatedetail = updatedetail;
    }

    public Integer getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(Integer versioncode) {
        this.versioncode = versioncode;
    }
}