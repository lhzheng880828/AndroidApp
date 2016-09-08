/****************************************************************************
 *
 * FILENAME:        com.base.module.dao.PackManagerDao.java
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
package com.base.module.pack.dao;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import com.base.module.language.GuiDisplay;
import com.base.module.pack.bean.Pack;
import com.base.module.pack.common.JSONUtil;
import com.base.module.pack.common.Log;
import com.base.module.pack.db.DateSource;
import com.base.module.pack.inter.PackDaoInter;
import com.base.module.pack.method.MyTime;
import com.base.module.pack.provider.PackProvider;
import com.base.module.pack.service.FailNotice;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
public class PackDao implements PackDaoInter {

    private String TAG = "DownService11";
    private SQLiteDatabase mPackManagerDB;
    private Context mContext;
    private GuiDisplay mGuidisplay;
    //private MyTime mTime = new MyTime();
    private final String SQL = "CREATE TABLE if not exists  [package] (" + "[pack_id] INTEGER  PRIMARY KEY AUTOINCREMENT  NULL," + "[pack_name] TEXT(80)  NULL,"
            + "[pack_l_name] TEXT(80)   UNIQUE NULL," + "[pack_vesion] TEXT(80)   NULL,"+"[pack_versioncode] INTEGER NULL," + "[pack_size] LONG   NULL," + "[pack_download] TEXT(80)   NULL,"
            + "[pack_downl_route] TEXT(80)   NULL," + "[pack_nation_route] TEXT(80)  NULL," + "[pack_load_time] TEXT(20)  NULL,"
            + "[pack_install] TEXT(8)  NULL," + "[pack_install_time] TEXT(8)  NULL," + "[pack_install_route] TEXT(80)  NULL,"
            + "[pack_install_infor] TEXT(8)  NULL," + "[pack_delete] INTEGER  NULL," + "[pack_update] TEXT(8)  NULL," + "[pack_update_time] TEXT(8)  NULL,"
            /*+ "[pack_icon] BLOG  NULL," */    + "[pack_icon_url] TEXT NULL," + "[pack_serial] TEXT(8)  NULL," + "[pack_state] INTEGER  NULL," + "[record_delete] INTEGER  NULL" + ")";
    public static final String DB_NAME = "Pack_Manager";
    public static final String TB_NAME = "package";
    public static final String Id = "pack_id";
    public static final String Name = "pack_name"; // appname show name which named by user
    public static final String LName = "pack_l_name"; // pack name
    public static final String Size = "pack_size";
    public static final String Version = "pack_vesion";
    //public static final String Icon = "pack_icon";
    public static final String VERSION_CODE = "pack_versioncode";
    public static final String IconUrl = "pack_icon_url";
    public static final String Serial = "pack_serial"; // appcode
    public static final String State = "pack_state";
    public static final String Download = "pack_download"; // if it download ,it is from small icon ,it -1
    public static final String DownRoute = "pack_downl_route";
    public static final String NationRoute = "pack_nation_route";
    public static final String LoadTime = "pack_load_time";
    public static final String Install = "pack_install";
    public static final String InstTime = "pack_install_time";
    public static final String InstRoute = "pack_install_route";
    public static final String InstInfor = "pack_install_infor";// class name
    public static final String PackDelete = "pack_delete";
    public static final String Update = "pack_update"; // if is update ,it -1 when install
    public static final String UpTime = "pack_update_time"; // the last down  load time
    public static final String RecordDelete = "record_delete";
    private FailNotice mFailNotice;
    public static Boolean isUpdate = Boolean.FALSE;

    /**
     * if don't hava the task,creat	 *
     */
    public PackDao() {

    }

    public PackDao(Context context) {
        Log.i(TAG, "PackManagerDao---open database");
        mContext = context;
        mGuidisplay = GuiDisplay.instance();
        try {
            mPackManagerDB = openDatabase();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (mPackManagerDB == null) {
            Toast.makeText(mContext, mGuidisplay.getValue(mContext, 3302), Toast.LENGTH_LONG).show();
            return;
        }

        mPackManagerDB.execSQL(SQL);
        if (!isUpdate) {
            if (!checkColumnExist(mPackManagerDB, TB_NAME, VERSION_CODE)) {
                mPackManagerDB.execSQL("alter table "+TB_NAME +" add column "+VERSION_CODE);
            }
        }
        mFailNotice = new FailNotice(mContext);
    }


    // query column if exist
    private boolean checkColumnExist(SQLiteDatabase db, String tableName
            , String columnName) {
        boolean result = false ;
        Cursor cursor = null ;
        try{
            //查询一行
            cursor = db.rawQuery( "SELECT * FROM " + tableName + " LIMIT 0", null );
            result = cursor != null && cursor.getColumnIndex(columnName) != -1 ;
        }catch (Exception e){
            Log.e(TAG,"checkColumnExists1..." + e.getMessage()) ;
        }finally{
            if(null != cursor && !cursor.isClosed()){
                cursor.close() ;
            }
        }
        return result ;
    }


    public static PackDaoInter getInstance(Context context){
        return new PackDao(context);
    }

    public SQLiteDatabase getPackManagerDB() {
        return mPackManagerDB;
    }

    public void setPackManagerDB(SQLiteDatabase mPackManagerDB) {
        this.mPackManagerDB = mPackManagerDB;
    }

    private SQLiteDatabase openDatabase() {
        return DateSource.getDateSource(mContext);
    }

    public void close() {
        mPackManagerDB.close();
    }

    public void open() {
        try {
            mPackManagerDB = openDatabase();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (mPackManagerDB == null) {
            Toast.makeText(mContext, mGuidisplay.getValue(mContext, 3302), Toast.LENGTH_LONG).show();
            return;
        }
    }

    public boolean isOpen() {
        return mPackManagerDB.isOpen();
    }

    // main method
    /**
     * return the count of the row of the msm's table
     */
    public long count() {
        SQLiteDatabase db = mPackManagerDB;
        long count = 0;
        Cursor c = null;
        try {
            c = db.query(TB_NAME, new String[] { "count(*)" }, null, null, null, null, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
            }
            return count;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            c.close();
        }
        return 0;
    }

    public long getUpdateCount() {
        SQLiteDatabase db = mPackManagerDB;
        long count = 0;
        Cursor c = null;
        try {
            c = db.query(TB_NAME, new String[] { "count(*)" }, State + " = '" + Pack.STATE_UPDATE + "' or " + State + " = '"
                    + Pack.STATE_ER_UPDATE + "'", null, null, null, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
            }
            return count;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            c.close();
        }
        return 0;
    }

    public int getFailCount() {
        SQLiteDatabase db = mPackManagerDB;
        int count = 0;
        Cursor c = null;
        try {
            c = db.query(TB_NAME, new String[] { "count(*)" }, State + " = '" + Pack.STATE_ER_UPDATE + "' or " + State + " = '"
                    + Pack.STATE_ER_INSTALL + "' or " + State + " = '" + Pack.STATE_ER_DOWN + "'  ", null, null, null, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
            }
            return count;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            c.close();
        }
        return 0;
    }

    // ===============find================
    public Pack findByLName(String l_name) {
        Pack s = new Pack();
        Cursor c = null;
        SQLiteDatabase db = mPackManagerDB;
        try {
            c = db.query(TB_NAME, new String[] { Id, Name, LName, Size, Version, VERSION_CODE, DownRoute, Download,
                    NationRoute, LoadTime, Install, InstTime, InstRoute, InstInfor, PackDelete,
                    Update, UpTime, IconUrl, Serial, State, RecordDelete }, LName + " = '" + l_name + "'",
                    null, null, null, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                s = getPack(c);
            }
            return s;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            c.close();
        }
        return s;
    }

    public Pack findById(int id) {
        Pack s = new Pack();
        Cursor c = null;
        SQLiteDatabase db = mPackManagerDB;
        try {
            c = db.query(TB_NAME, new String[] { Id, Name, LName, Size, Version,VERSION_CODE, DownRoute, Download,
                    NationRoute, LoadTime, Install, InstTime, InstRoute, InstInfor, PackDelete,
                    Update, UpTime,  IconUrl, Serial, State, RecordDelete }, Id + " = '" + id + "'", null,
                    null, null, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                s = getPack(c);
            }
            return s;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            c.close();
        }
        return s;
    }

    public List<Pack> findInstall() {
        Log.i(TAG, "PackManagerDao---findInstall====");
        List<Pack> resList = new ArrayList<Pack>();
        Cursor c = null;
        try {
            c = mPackManagerDB.query(TB_NAME, new String[] { Id, Name, DownRoute, NationRoute, LName, Size,
                    Version,VERSION_CODE, LoadTime, Download, Install, InstTime, InstRoute, InstInfor,
                    PackDelete, Update, UpTime, IconUrl, Serial, State, RecordDelete }, State
                    + " >= '" + Pack.STATE_INSTALL + "' or " + Update + " = '" + Pack.IN_UPDATE + "'", null, null, null, InstTime
                    + " desc");
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                resList.add(getPack(c));
                while (c.moveToNext()) {
                    resList.add(getPack(c));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            c.close();
        }
        return resList;
    }

    public List<Pack> findInstallOutUpdate() {
        Log.i(TAG, "PackManagerDao---findInstall====");
        List<Pack> resList = new ArrayList<Pack>();
        Cursor c = null;
        try {
            c = mPackManagerDB.query(TB_NAME, new String[] { Id, Name, DownRoute, NationRoute, LName, Size,
                    Version,VERSION_CODE, LoadTime, Download, Install, InstTime, InstRoute, InstInfor,
                    PackDelete, Update, UpTime, IconUrl, Serial, State, RecordDelete }, State + " = '"
                            + Pack.STATE_INSTALL + "'", null, null, null, InstTime + " desc");
            // Log.i(TAG,
            // "PackManagerDao---findByContact---c.count===="
            // + c.getCount());
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                resList.add(getPack(c));
                // Log.w(TAG, "getPack(c)---" + getPack(c));
                while (c.moveToNext()) {
                    resList.add(getPack(c));
                }
            }
            c.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            c.close();
        }
        return resList;
    }


    public String getAllAppStatus() {
        Log.d("come in getALLAppStatusStrByDB");
        List<Pack> packs = this.findPacks();
        Log.d(" packs " +packs.size());
        if (packs.isEmpty()) {
            return null;
        }
        String str = JSONUtil.ObjToJson(packs);
        Log.d("result "+ str);
        return str;
    }

    public String getAppStatusByPackage(String packagename) {
        Log.d("come in getAppStatusByPackage");
        Pack pack = this.findByLName(packagename);
        if (pack == null|| pack.getPackState() == 0) {
            return null;
        }
        String str = JSONUtil.ObjToJson(pack);
        Log.d("result "+ str);
        return str;
    }

    /**
     * find the sends which waittign for send
     * 
     * 
     * @return
     */
    public List<Pack> findPacks() {
        List<Pack> resList = new ArrayList<Pack>();
        SQLiteDatabase db = mPackManagerDB;
        Cursor c = null;
        try {
            c = db.query(TB_NAME, new String[] { Id, LName, Size, Version,VERSION_CODE, Name, Download, DownRoute,
                    NationRoute, LoadTime, Install, InstTime, InstRoute, InstInfor, PackDelete,
                    Update, UpTime, IconUrl,Serial, State, RecordDelete }, null, null, null, null,
                    LoadTime);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                resList.add(getPack(c));
                while (c.moveToNext()) {
                    resList.add(getPack(c));
                }
            }
            c.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            c.close();
        }
        return resList;
    }


    /**
     * find the sends which waittign for send
     * 
     * @return
     */
    public List<Pack> findDownPacks() {
        List<Pack> resList = new ArrayList<Pack>();
        SQLiteDatabase db = mPackManagerDB;
        Cursor c = null;
        try {
            c = db.query(TB_NAME, new String[] { Id, Name, LName, Size, Version,VERSION_CODE, Download, DownRoute,
                    NationRoute, LoadTime, Install, InstTime, InstRoute, InstInfor, PackDelete,
                    Update, UpTime, IconUrl, Serial, State, RecordDelete }, RecordDelete + "!= '"
                            + Pack.DELETED + "'", null, null, null, LoadTime + " desc");
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                resList.add(getPack(c));
                while (c.moveToNext()) {
                    resList.add(getPack(c));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            c.close();
        }
        return resList;
    }

    public synchronized Map<String, Pack> findDownPackMaps() {
        Map<String, Pack> resList = new LinkedHashMap<String, Pack>();
        SQLiteDatabase db = mPackManagerDB;
        Cursor c = null;
        try {
            c = db.query(TB_NAME, new String[] { Id, Name, LName, Size, Version,VERSION_CODE, Download, DownRoute,
                    NationRoute, LoadTime, Install, InstTime, InstRoute, InstInfor, PackDelete,
                    Update, UpTime, IconUrl ,Serial, State, RecordDelete },RecordDelete + "!= '"
                            + Pack.DELETED + "' and " + State +" < '"+Pack.STATE_INSTALL+"'", null, null, null, LoadTime + " desc");
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                resList.put(c.getString(c.getColumnIndex(LName)), getPack(c));
                while (c.moveToNext()) {
                    resList.put(c.getString(c.getColumnIndex(LName)), getPack(c));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            c.close();
        }
        return resList;
    }

    // =============== insert ===============
    /**
     * insert when send
     * 
     * mTime when is not has a mTime in ,that get present mTime
     */
    public long insertDownPack(Pack pack) {
        long row = -1;
        // long countspace = Integer.parseInt(mContext
        // .getString(R.string.sms_maxcount)) - count();
        SQLiteDatabase db = mPackManagerDB;
        ContentValues cv = new ContentValues();
        String ttime = MyTime.getdate();
        if (pack.getPackLoadTime() != null)
            ttime = pack.getPackLoadTime();
        cv.put(Name, pack.getPackName());
        cv.put(LName, pack.getPackLName());
        cv.put(Size, pack.getPackSize());
        cv.put(Version, pack.getPackVersion());
        cv.put(VERSION_CODE,pack.getPackVersionCode());
        cv.put(Download, pack.getPackDownload());
        cv.put(DownRoute, pack.getPackDownRoute());
        cv.put(NationRoute, pack.getPackNationRoute());
        cv.put(LoadTime, ttime);
        cv.put(Install, pack.getPackInstall());
        cv.put(InstTime, pack.getPackInstallTime());
        cv.put(InstRoute, pack.getPackInstallRoute());
        cv.put(InstInfor, pack.getPackInstallInfor());
        cv.put(PackDelete, Pack.UN_DELETED);
        cv.put(Update, "0");
        cv.put(UpTime, pack.getPackUpdateTime());
        cv.put(IconUrl, pack.getIconUrl());
        cv.put(Serial, pack.getPackSerial());
        cv.put(State, pack.getPackState());
        cv.put(RecordDelete, Pack.UN_DELETED);
        try {
            // insert
            row = db.insert(TB_NAME, null, cv);
            Log.i(TAG, "insert " + " ok");
        } catch (Exception E) {
            Log.e(TAG + " insertSend", E.toString());
        }
        return row;
    }

    // ========== update ===================
    public synchronized long insertOrUpdate(Pack pack) {
        Pack s = pack;
        long row = -1;
        s.setIsFind(false);
        Pack findPack = findByLName(s.getPackLName());
        SQLiteDatabase db = mPackManagerDB;
        String ttime = MyTime.getdate();
        ContentValues cv = new ContentValues();
        if (s.getPackName() != null)
            cv.put(Name, s.getPackName());
        if (s.getPackLName() != null)
            cv.put(LName, s.getPackLName());
        if (s.getPackDownload() != null)
            cv.put(Download, s.getPackDownload());
        if (s.getPackDownRoute() != null)
            cv.put(DownRoute, s.getPackDownRoute());
        if (s.getPackNationRoute() != null)
            cv.put(NationRoute, s.getPackNationRoute());
        if (s.getPackLoadTime() != null)
            cv.put(LoadTime, s.getPackLoadTime());
        else
            cv.put(LoadTime, ttime);
        if (s.getPackInstall() != null)
            cv.put(Install, s.getPackInstall());
        if (s.getPackInstallTime() != null)
            cv.put(InstTime, s.getPackInstallTime());
        if (s.getPackInstallRoute() != null)
            cv.put(InstRoute, s.getPackInstallRoute());
        if (s.getPackInstallInfor() != null)
            cv.put(InstInfor, s.getPackInstallInfor());
        if (s.getPackDelete() != 0)
            cv.put(PackDelete, s.getPackDelete());
        if (s.getPackUpdate() != null)
            cv.put(Update, s.getPackUpdate());
        else
            cv.put(Update, "0");
        if (s.getPackUpdate() == null || !s.getPackUpdate().equals(Pack.IN_UPDATE)) {
            if (s.getPackSize() != 0)
                cv.put(Size, s.getPackSize());
            if (s.getPackVersion() != null)
                cv.put(Version, s.getPackVersion());
            if (s.getPackVersionCode() != null)
                cv.put(VERSION_CODE, s.getPackVersionCode());
            if (s.getPackUpdateTime() != null)
                cv.put(UpTime, s.getPackUpdateTime());
            if (s.getIconUrl() != null)
                cv.put(IconUrl, s.getIconUrl());
        }
        if (s.getPackSerial() != null)
            cv.put(Serial, s.getPackSerial());
        if (s.getRecordDelete() != 0)
            cv.put(RecordDelete, s.getRecordDelete());
        else
            cv.put(RecordDelete, Pack.UN_DELETED);
        try {
            if (findPack.getIsFind()) {
                if (s.getPackState() != 0)
                    cv.put(State, s.getPackState());
                row = db.update(TB_NAME, cv, LName + " = '" + findPack.getPackLName() + "'", null);
                Log.d(TAG, "rUpdate " + s.getPackName() + " ok");
            } else {
                cv.put(PackDelete, Pack.UN_DELETED);
                cv.put(State, s.getPackState());
                row = db.insert(TB_NAME, null, cv);
                Log.d(TAG, "insert " + s.getPackName() + " ok");
            }
        } catch (Exception E) {
            Log.e(TAG, " insertOrUpdate" + E.toString());
            E.printStackTrace();
        }
        return row;
    }

    public long failDownloadByLname(String l_name) {
        long row = -1;
        Pack findPack = findByLName(l_name);
        if (findPack.getIsFind()) {
            SQLiteDatabase db = mPackManagerDB;
            ContentValues cv = new ContentValues();
            if (findPack.getPackUpdateTime() != null) {
                cv.put(LoadTime, findPack.getPackUpdateTime());
            }
            if (findPack.getPackUpdate().equals(Pack.IN_UPDATE)) {
                cv.put(State, Pack.STATE_ER_UPDATE);
                cv.put(Update, "0");
            } else
                cv.put(State, Pack.STATE_ER_DOWN);
            try {
                // update
                row = db.update(TB_NAME, cv, LName + " = '" + l_name + "'", null);
                Log.i(TAG, "failDownloadByLname " + l_name + " ok");
            } catch (Exception E) {
                Log.e(TAG, " failDownloadByLname" + E.toString());
                E.printStackTrace();
            }
        }
        mFailNotice.setFailNotice(getFailCount());
        return row;
    }



    // ============================
    /*
     * (non-Javadoc)
     * 
     * @see com.base.module.pack.inter.PackManagerDaoInter#delete(int)
     */
    public long failInstallByLname(String l_name) {
        long row = -1;
        Pack findPack = findByLName(l_name);
        if (findPack.getIsFind()) {
            SQLiteDatabase db = mPackManagerDB;
            ContentValues cv = new ContentValues();
            if (findPack.getPackUpdateTime() != null) {
                cv.put(LoadTime, findPack.getPackUpdateTime());
            }
            if (findPack.getPackUpdate().equals(Pack.IN_UPDATE)) {
                cv.put(State, Pack.STATE_ER_UPDATE);
                cv.put(Update, "0");
            } else
                cv.put(State, Pack.STATE_ER_INSTALL);
            try {
                // update
                row = db.update(TB_NAME, cv, LName + " = '" + l_name + "'", null);
                Log.i(TAG, "failInstallByLname " + l_name + " ok");
            } catch (Exception E) {
                Log.e(TAG, " failInstallByLname" + E.toString());
                E.printStackTrace();
            }
        }
        mFailNotice.setFailNotice(getFailCount());
        return row;
    }

    public void processDownloadTask(){
        SQLiteDatabase db = mPackManagerDB;
        ContentValues cv = new ContentValues();
        try{
            cv.put(State, Pack.STATE_ER_UPDATE);
            cv.put(Update, "0");
            db.update(TB_NAME, cv, State + "=?", new String[]{String.valueOf(Pack.IN_UPDATE)});

            cv.clear();
            cv.put(State, Pack.STATE_ER_INSTALL);
            db.update(TB_NAME, cv, State + "=?", new String[]{String.valueOf(Pack.STATE_IN_INSTALL)});

            cv.clear();
            cv.put(State, Pack.STATE_ER_DOWN);
            db.update(TB_NAME, cv, State + "=? or " + State + "=? ", new String[]{String.valueOf(Pack.STATE_IN_DOWN),String.valueOf(Pack.STATE_WAIT)});

        }catch (Exception e) {
            Log.e(TAG, " processDownloadTask" + e.toString());
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.base.module.pack.inter.PackManagerDaoInter#delete(int)
     */
    public long installByLname(String l_name) {
        long row = -1;
        Pack findPack = findByLName(l_name);
        if (findPack.getIsFind()) {
            SQLiteDatabase db = mPackManagerDB;
            ContentValues cv = new ContentValues();
            cv.put(State, Pack.STATE_INSTALL);
            cv.put(InstTime, MyTime.getdate());
            if (findPack.getPackUpdateTime() != null) {
                cv.put(LoadTime, findPack.getPackUpdateTime());
            }
            if (findPack.getPackUpdate() != null && findPack.getPackUpdate().equals(Pack.IN_UPDATE))
                cv.put(Update, "0");
            try {
                // update
                row = db.update(TB_NAME, cv, LName + " = '" + l_name + "'", null);
                Log.i(TAG, "deleteRecord " + l_name + " ok");
            } catch (Exception E) {
                Log.e(TAG, " deleteRecord" + E.toString());
                E.printStackTrace();
            }
        }
        return row;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.base.module.pack.inter.PackManagerDaoInter#delete(int)
     */
    public long uninstallByLname(String l_name) {
        long row = -1;
        Pack findPack = findByLName(l_name);
        if (findPack.getIsFind()) {
            if (findPack.getRecordDelete() == Pack.DELETED)
                delete(findPack.getPackLName());
            else {
                SQLiteDatabase db = mPackManagerDB;
                ContentValues cv = new ContentValues();
                cv.put(State, Pack.STATE_UNINSTALL);
                try {
                    // update
                    row = db.update(TB_NAME, cv, LName + " = '" + l_name + "'", null);
                    Log.i(TAG, "deleteRecord " + l_name + " ok");
                } catch (Exception E) {
                    Log.e(TAG, " deleteRecord" + E.toString());
                    E.printStackTrace();
                }
            }
        }
        return row;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.base.module.pack.inter.PackManagerDaoInter#delete(int)
     */
    public long setUpdateByLname(String l_name, String url) {
        long row = -1;
        Pack findPack = findByLName(l_name);
        if (findPack.getIsFind()) {
            Log.d(TAG, "###-------------------indPack.getPackDownRoute()" + findPack.getPackDownRoute());
            Log.d(TAG, "###-----------------------------url" + url);
            SQLiteDatabase db = mPackManagerDB;
            ContentValues cv = new ContentValues();
            if (findPack.getPackState() == Pack.STATE_INSTALL)
                cv.put(State, Pack.STATE_UPDATE);
            cv.put(DownRoute, url);
            try {
                // update
                row = db.update(TB_NAME, cv, LName + " = '" + l_name + "'", null);
                Log.i(TAG, "deleteRecord " + l_name + " ok");
            } catch (Exception E) {
                Log.e(TAG, " deleteRecord" + E.toString());
                E.printStackTrace();
            }
        }
        return row;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.base.module.pack.inter.PackManagerDaoInter#delete(int)
     */
    public long setInUpdate(String l_name) {
        long row = -1;
        Pack findPack = findByLName(l_name);
        if (findPack.getIsFind()) {
            SQLiteDatabase db = mPackManagerDB;
            ContentValues cv = new ContentValues();

            Log.i("++++++", String.valueOf(findPack.getPackState()));

            //if (findPack.getPackState() >= Pack.STATE_UPDATE)
            cv.put(Update, Pack.IN_UPDATE);
            try {
                // update
                row = db.update(TB_NAME, cv, LName + " = '" + l_name + "'", null);
                Log.i(TAG, "set in update " + l_name + " ok");
            } catch (Exception E) {
                Log.e(TAG, " set in update " + E.toString());
                E.printStackTrace();
            }

        }
        return row;
    }

    // ===========delete================
    /*
     * (non-Javadoc)
     * 
     * @see com.base.module.pack.inter.PackManagerDaoInter#delete(int)
     */
    public long deleteRecord(String l_name) {
        long row = -1;
        Pack findPack = findByLName(l_name);
        if (findPack.getIsFind()) {
            if (findPack.getPackState() < Pack.STATE_WAIT) {
                row = delete(l_name);
            } else if (findPack.getPackState() >= Pack.STATE_INSTALL) {
                SQLiteDatabase db = mPackManagerDB;
                ContentValues cv = new ContentValues();
                cv.put(RecordDelete, Pack.DELETED);
                try {
                    // update
                    row = db.update(TB_NAME, cv, LName + " = '" + l_name + "'", null);
                    Log.i(TAG, "deleteRecord " + l_name + " ok");
                } catch (Exception E) {
                    Log.e(TAG, " deleteRecord" + E.toString());
                    E.printStackTrace();
                }
            }
        }
        return row;
    }

    /**
     * re
     */
    public long deleteRecord(Pack pack) {
        long row = -1;
        Pack findPack = findByLName(pack.getPackLName());
        if (findPack.getIsFind()) {
            if (findPack.getPackState() < Pack.STATE_WAIT) {
                row = delete(pack.getPackLName());
            } else if (findPack.getPackState() >= Pack.STATE_INSTALL) {
                SQLiteDatabase db = mPackManagerDB;
                ContentValues cv = new ContentValues();
                cv.put(RecordDelete, Pack.DELETED);
                try {
                    row = db.update(TB_NAME, cv, Id + " = '" + pack.getPackId() + "'", null);
                    Log.i(TAG, "rUpdate " + pack.getPackId() + " ok");
                    Log.i(TAG, "deleteRecord " + pack + " ok");
                } catch (Exception E) {
                    Log.e(TAG, " deleteRecord" + E.toString());
                    E.printStackTrace();
                }
            }
        }
        return row;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.base.module.pack.inter.PackManagerDaoInter#delete(int)
     */
    public long delete(String l_name) {
        int row = -1;
        try {
            row = mPackManagerDB.delete(PackDao.TB_NAME, LName + "='" + l_name + "'", null);
            PackProvider.deleteIcon(mContext, l_name);
            mFailNotice.setFailNotice(0);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        Log.w(TAG, "delete " + l_name + " ok");
        return row;
    }

    /**
     * re
     */
    public long delete(Pack pack) {
        return delete(pack.getPackLName());
    }

    /**
     * clear
     */
    public long deleteAll() {
        int row = mPackManagerDB.delete(PackDao.TB_NAME, LName + "is not '" + null + "'", null);
        PackProvider.deleteAllIcon(mContext);
        Log.i(TAG, "deleteAll " + " ok");
        return row;
    }

    // ==========method===========
    /**
     * find the pack by cursor
     * 
     * @param c
     * @return
     */
    private Pack getPack(Cursor c) {

        Pack pack = new Pack();
        pack.setIsFind(true);
        pack.setPackId(c.getInt(c.getColumnIndex(Id)));
        pack.setPackName(c.getString(c.getColumnIndex(Name)));
        pack.setPackLName(c.getString(c.getColumnIndex(LName)));
        pack.setPackSize(c.getLong(c.getColumnIndex(Size)));
        pack.setPackVersion(c.getString(c.getColumnIndex(Version)));
        pack.setPackVersionCode(c.getInt(c.getColumnIndex(VERSION_CODE)));
        pack.setPackDownload(c.getString(c.getColumnIndex(Download)));
        pack.setPackDownRoute(c.getString(c.getColumnIndex(DownRoute)));
        pack.setPackNationRoute(c.getString(c.getColumnIndex(NationRoute)));
        pack.setPackLoadTime(c.getString(c.getColumnIndex(LoadTime)));
        pack.setPackInstall(c.getString(c.getColumnIndex(Install)));
        pack.setPackInstallTime(c.getString(c.getColumnIndex(InstTime)));
        pack.setPackInstallRoute(c.getString(c.getColumnIndex(InstRoute)));
        pack.setPackInstallInfor(c.getString(c.getColumnIndex(InstInfor)));
        pack.setPackDelete(c.getInt(c.getColumnIndex(PackDelete)));
        pack.setPackUpdate(c.getString(c.getColumnIndex(Update)));
        pack.setPackUpdateTime(c.getString(c.getColumnIndex(UpTime)));
        //pack.setPackIcon(c.getBlob(c.getColumnIndex(Icon)));
        pack.setIconUrl(c.getString(c.getColumnIndex(IconUrl)));
        pack.setPackSerial(c.getString(c.getColumnIndex(Serial)));
        pack.setPackState(c.getInt(c.getColumnIndex(State)));
        pack.setRecordDelete(c.getInt(c.getColumnIndex(RecordDelete)));
        return pack;
    }
}