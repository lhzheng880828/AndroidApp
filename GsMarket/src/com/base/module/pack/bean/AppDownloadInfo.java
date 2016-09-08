/**
 * Copyright (c) grandstream
 */
package com.base.module.pack.bean;

import java.io.Serializable;


public class AppDownloadInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer sysid;

	private Integer appid;

	private Integer download;//download count

	private String downloaddate;//download times

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

	public Integer getDownload() {
		return download;
	}

	public void setDownload(Integer download) {
		this.download = download;
	}

	public String getDownloaddate() {
		return downloaddate;
	}

	public void setDownloaddate(String downloaddate) {
		this.downloaddate = downloaddate;
	}

}
