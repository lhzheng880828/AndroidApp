package com.base.module.pack.devicesparm;
/**
 * User: czheng
 * Date: 3/6/13
 * Time: 7:11 PM
 * Copyright (c) 2009-2013 by Grandstream Networks, Inc.
 * All rights reserved. 
 */
public abstract class AbstractDeviceParm {

    abstract String deviceType();

    private final String DTYPE = "dType";

    public String getRequest_List_Action() {
        return "/getAppsToJsonData?"+DTYPE+"="+deviceType();
    }


    public  String getNewestVersionURL(){
        return "/getNewestVersion?"+DTYPE+"="+deviceType();
    }
}