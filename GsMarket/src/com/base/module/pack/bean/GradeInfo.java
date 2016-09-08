/**
 * Copyright (c) 2015, grandstream.
 * author:yyzhu
 */

package com.base.module.pack.bean;

import java.io.Serializable;

public class GradeInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer sysid;
    private Integer appid;
    private float gradenum;
    private String comment;
    private String gradetime;

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


    public float getGradenum() {
        return gradenum;
    }

    public void setGradenum(float gradenum) {
        this.gradenum = gradenum;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public String getGradetime() {
        return gradetime;
    }

    public void setGradetime(String gradetime) {
        this.gradetime = gradetime;
    }
}