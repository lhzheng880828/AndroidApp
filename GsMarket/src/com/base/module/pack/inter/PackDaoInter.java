/****************************************************************************
 *
 * FILENAME:        com.base.module.inter.PackManagerDaoInter.java
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
package com.base.module.pack.inter;
import java.util.List;
import java.util.Map;
import com.base.module.pack.bean.Pack;
public interface PackDaoInter {
    void close();

    boolean isOpen();

    void open();

    List<Pack> findPacks();

    long insertOrUpdate(Pack pack);

    List<Pack> findDownPacks();

    Pack findByLName(String packageName);

    long installByLname(String packageName);

    long uninstallByLname(String packageName);

    long deleteRecord(String packLName);

    List<Pack> findInstall();

    public String getAllAppStatus();

    public String getAppStatusByPackage(String package_name);

    long failInstallByLname(String l_name);

    Map<String, Pack> findDownPackMaps();

    long failDownloadByLname(String l_name);

    public void processDownloadTask();

    long setUpdateByLname(String packLName, String packNationRoute);

    long getUpdateCount();

    List<Pack> findInstallOutUpdate();

    long setInUpdate(String l_name);

    int getFailCount();
}