/****************************************************************************
 *
 * FILENAME:        com.base.module.bean.Pack.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: 2012-2-2
 *
 * DESCRIPTION:     The class encapsulates the music ring tone operations.
 *
 * vi: set ts=4:
 *
 * Copyright (c) 2009-2011 by Grandstream Networks, Inc.
 * All rights reserved.
 *
 * This material is proprietary to Grandstream Networks, Inc. and,
 * in addition to the above mentioned Copyright, may be
 * subject to protection under other intellectual property
 * regimes, including patents, trade secrets, designs and/or
 * trademarks.
 *
 * Any use of this material for any purpose, except with an
 * express license from Grandstream Networks, Inc. is strictly
 * prohibited.
 *
 ***************************************************************************/
package com.base.module.pack.bean;

import com.base.module.pack.annotation.JSONObj;
import com.base.module.pack.annotation.JSONValue;

import java.io.Serializable;

@JSONObj
public class Pack implements Serializable {

    private static final long serialVersionUID = 1L;
    static final public int DELETED = -1;
    static final public int UN_DELETED = 1;

    static final public String DOWN_TYPE_ICON = "-1";
    static final public String DOWN_TYPE_NORMAL = "0";


    static final public String IN_UPDATE = "-1";

    static final public int STATE_UPDATE = 5;
    static final public int STATE_INSTALL = 4;
    static final public int STATE_UNINSTALL = -4;

    static final public int STATE_IN_INSTALL = 3;
    static final public int STATE_IN_DOWN = 2;
    static final public int STATE_WAIT = 1;

    static final public int STATE_ER_DOWN = -2;
    static final public int STATE_ER_INSTALL = -3;

    static final public int STATE_IN_UPDATE = 6;   //not used ,for use update equals IN_UPDATE
    static final public int STATE_ER_UPDATE = 7;

    private int press = 0; // download press

    private int packId;

    private String packName; // appName show name

    @JSONValue(name="packagename")
    private String packLName; // pack name ;long name
    private long packSize;
    private String packVersion;
    private Integer packVersionCode;

    //private byte[] packIcon;
    private String iconUrl;

    private String packSerial; // appcode

    @JSONValue(name="state")
    private int packState; // the every state

    private int recordDelete; // the state of record is deleted

    private String packDownload; // if it download ,it is from small icon ,it -1
    private String packDownRoute; // uri
    private String packNationRoute; // nation route
    private String packLoadTime;

    private String packInstall; // 0
    private String packInstallTime; // 0
    private String packInstallRoute; // 0
    private String packInstallInfor; // class name to iconinstall

    private int packDelete; // the state of pack is deleted ??? //0
    private String packUpdate; // if is update ,it -1 when install
    private String packUpdateTime; // the last down load insert time

    private Boolean isFind = false;

    public int getPress() {
        return press;
    }

    public void setPress(int press) {
        this.press = press;
    }

    public long getPackSize() {
        return packSize;
    }

    public void setPackSize(long packSize) {
        this.packSize = packSize;
    }

    public String getPackLName() {
        return packLName;
    }

    public void setPackLName(String packLName) {
        this.packLName = packLName;
    }

    public String getPackVersion() {
        return packVersion;
    }

    public void setPackVersion(String packVersion) {
        this.packVersion = packVersion;
    }

    public Integer getPackVersionCode() {
        return packVersionCode;
    }

    public void setPackVersionCode(Integer packVersionCode) {
        this.packVersionCode = packVersionCode;
    }

    public String getPackDownload() {
        return packDownload;
    }

    public void setPackDownload(String packDownload) {
        this.packDownload = packDownload;
    }

    public int getRecordDelete() {
        return recordDelete;
    }

    public void setRecordDelete(int recordDelete) {
        this.recordDelete = recordDelete;
    }

    public Boolean getIsFind() {
        return isFind;
    }

    public void setIsFind(Boolean isFind) {
        this.isFind = isFind;
    }

    public int getPackId() {
        return packId;
    }

    public void setPackId(int packId) {
        this.packId = packId;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getPackDownRoute() {
        return packDownRoute;
    }

    public void setPackDownRoute(String packDownRoute) {
        this.packDownRoute = packDownRoute;
    }

    public String getPackNationRoute() {
        return packNationRoute;
    }

    public void setPackNationRoute(String packNationRoute) {
        this.packNationRoute = packNationRoute;
    }

    public String getPackLoadTime() {
        return packLoadTime;
    }

    public void setPackLoadTime(String packLoadTime) {
        this.packLoadTime = packLoadTime;
    }

    public String getPackInstall() {
        return packInstall;
    }

    public void setPackInstall(String packInstall) {
        this.packInstall = packInstall;
    }

    public String getPackInstallTime() {
        return packInstallTime;
    }

    public void setPackInstallTime(String packInstallTime) {
        this.packInstallTime = packInstallTime;
    }

    public String getPackInstallRoute() {
        return packInstallRoute;
    }

    public void setPackInstallRoute(String packInstallRoute) {
        this.packInstallRoute = packInstallRoute;
    }

    public String getPackInstallInfor() {
        return packInstallInfor;
    }

    public void setPackInstallInfor(String packInstallInfor) {
        this.packInstallInfor = packInstallInfor;
    }

    public int getPackDelete() {
        return packDelete;
    }

    public void setPackDelete(int packDelete) {
        this.packDelete = packDelete;
    }

    public String getPackUpdate() {
        return packUpdate;
    }

    public void setPackUpdate(String packUpdate) {
        this.packUpdate = packUpdate;
    }

    public String getPackUpdateTime() {
        return packUpdateTime;
    }

    public void setPackUpdateTime(String packUpdateTime) {
        this.packUpdateTime = packUpdateTime;
    }

    /*public byte[] getPackIcon() {
		return packIcon;
	}

	public void setPackIcon(byte[] packIcon) {
		this.packIcon = packIcon;
	}*/



    public String getPackSerial() {
        return packSerial;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setPackSerial(String packSerial) {
        this.packSerial = packSerial;
    }

    public int getPackState() {
        return packState;
    }

    public void setPackState(int packState) {
        this.packState = packState;
    }

    public String toString() {	
        return "packCode:" + this.packSerial + "  packLName:" + packLName
                + "  appName:" + packName + "  packState:" + packState
                + "  url:" + this.packDownRoute + "  press:" + press;

    }
}