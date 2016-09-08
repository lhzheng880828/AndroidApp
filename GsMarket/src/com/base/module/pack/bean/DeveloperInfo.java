/**
 * author:yyzhu
 * CopyRight@:grandstream
 */

package com.base.module.pack.bean;

import java.io.Serializable;


public class DeveloperInfo implements Serializable{

    private static final long serialVersionUID = 1L;

    private Integer sysid;

    private String username;

    private String passwd;

    private String realname;

    private String phone;

    private String mobiletelephone;

    private String email;

    private String createtime;

    private String creator;

    private String updatetime;

    private String updator;

    private String status;

    public Integer getSysid() {
        return sysid;
    }

    public void setSysid(Integer sysid) {
        this.sysid = sysid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobiletelephone() {
        return mobiletelephone;
    }

    public void setMobiletelephone(String mobiletelephone) {
        this.mobiletelephone = mobiletelephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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