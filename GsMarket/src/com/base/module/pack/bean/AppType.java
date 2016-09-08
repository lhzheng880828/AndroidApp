/**
 *
 * Copyright (c) 2011,grandstream.
 */

package com.base.module.pack.bean;

import java.io.Serializable;


public class AppType implements Serializable{

	private static final long serialVersionUID = 1L;

	private String code;
	
	private String name_cn;//chinese name

	private String name_en;//english name

	private String createtime;//create time, format :yyyy_MM_dd hh:mm:ss

	private String creator;//creator

	private String updatetime;//update time, format :yyyy_MM_dd hh:mm:ss

	private String updator;//updator

	private String status;//down state(downloading/install...)

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName_cn() {
		return name_cn;
	}

	public void setName_cn(String name_cn) {
		this.name_cn = name_cn;
	}

	public String getName_en() {
		return name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
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

}