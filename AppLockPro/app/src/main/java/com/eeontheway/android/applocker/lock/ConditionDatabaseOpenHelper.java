package com.eeontheway.android.applocker.lock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 应用锁定数据库打开器
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class ConditionDatabaseOpenHelper extends SQLiteOpenHelper {
    private static final int currentVersion = 27;
    private static final String dbName = "applocklist.db";

    private static final String app_list_tableName = "app_list";
    public static final String app_list_creator =           // App列表
            "CREATE TABLE app_list (\n" +
            "    id           INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "    package_name STRING  NOT NULL UNIQUE ON CONFLICT FAIL,\n" +
            "    ref          INTEGER DEFAULT (0) \n" +
            ");";

    private static final String mode_list_tableName = "mode_list";
    public static final String mode_list_creator =          // 模式列表
            "CREATE TABLE mode_list (" +
            "   id                  INTEGER PRIMARY KEY AUTOINCREMENT," +
            "   mode_name           STRING  NOT NULL" +
            ");";

    private static final String app_lock_config_tableName = "app_lock_config";
    private static final String app_lock_config_add_ref_trigger =
            "CREATE TRIGGER add_ref\n" +
            "      AFTER INSERT ON app_lock_config\n" +
            "      FOR EACH ROW\n" +
            "BEGIN\n" +
            "    UPDATE app_list\n" +
            "       SET ref = ref + 1\n" +
            "     WHERE id = new.app_id;\n" +
            "END;\n";
    private static final String app_lock_config_del_ref_trigger =
            "CREATE TRIGGER del_ref\n" +
            "      AFTER DELETE ON app_lock_config\n" +
            "      FOR EACH ROW\n" +
            "BEGIN\n" +
            "    UPDATE app_list\n" +
            "       SET ref = ref - 1\n" +
            "     WHERE id = old.app_id;\n" +
            "    DELETE FROM app_list\n" +
            "          WHERE ref = 0;\n" +
            "END;\n";
    public static final String app_lock_config_creator =   // App锁定配置
            "CREATE TABLE app_lock_config ( " +
            "   id                  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "   app_id              INTEGER REFERENCES app_list (id) ON DELETE CASCADE NOT NULL," +
            "   mode_id             INTEGER NOT NULL REFERENCES mode_list (id) ON DELETE CASCADE," +
            "   enable              BOOLEAN NOT NULL" +
            ")";

    private static final String app_log_list_tableName = "app_log_list";
    private static final String app_log_list_creator =      // 锁定日志配置
            "CREATE TABLE app_log_list (" +
                    "   id             INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "   appName        STRING  NOT NULL," +
                    "   packageName    STRING  NOT NULL," +
                    "   passErrorCount INTEGER NOT NULL," +
                    "   photoPath      STRING," +
                    "   time           STRING NOT NULL," +
                    "   location       STRING  NOT NULL" +
                    ");";

    public static final String time_lock_config_tableName = "time_lock_config";
    public static final String BASE_COND_FIELD_ID = "id";
    public static final String BASE_CONDFIELD_MODE_ID = "mode_id";
    public static final String BASE_COND_FIELD_ENABLE = "enable";
    public static final String TIME_FIELD_START_TIME = "start_time";
    public static final String TIME_FIELD_END_TIME = "end_time";
    public static final String TIME_FIELD_DAY = "day";
    public static final String time_lock_config_creator =   // 时间锁定配置
            "CREATE TABLE time_lock_config (" +
            "   id                 INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "   start_time         DATETIME NOT NULL," +
            "   end_time           DATETIME NOT NULL," +
            "   day                INTEGER  NOT NULL," +
            "   mode_id            INTEGER  REFERENCES mode_list (id) ON DELETE CASCADE, " +
            "   enable             BOOLEAN  NOT NULL" +
            ");";
    public static final String position_lock_config_tableName = "gps_lock_config";
    public static final String POSITION_FIELD_LATITUDE = "latitude";
    public static final String POSITION_FIELD_LONGITUDE = "longitude";
    public static final String POSITION_FIELD_RADIUS = "radius";
    public static final String POSITION_FIELD_ADDRESS = "address";
    public static final String position_lock_config_creator =    // gps锁定配置
            "CREATE TABLE gps_lock_config (\n" +
            "    id                 INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
            "    enable             BOOLEAN NOT NULL,\n" +
            "    mode_id            INTEGER REFERENCES mode_list (id) ON DELETE CASCADE,\n" +
            "    latitude           DOUBLE  NOT NULL,\n" +
            "    longitude          DOUBLE  NOT NULL,\n" +
            "    radius             DOUBLE  NOT NULL,\n" +
            "    address            STRING\n" +
            ");";



    /**
     * 构造函数
     * @param context 上下文
     */
    public ConditionDatabaseOpenHelper(Context context) {
        super(context, dbName, null, currentVersion);
    }

    /**
     * OnCreate回调
     * @param db 数据库
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(app_list_creator);
        db.execSQL(mode_list_creator);
        db.execSQL(app_lock_config_creator);
        db.execSQL(app_lock_config_add_ref_trigger);
        db.execSQL(app_lock_config_del_ref_trigger);
        db.execSQL(time_lock_config_creator);
        db.execSQL(position_lock_config_creator);
        db.execSQL(app_log_list_creator);
    }

    /**
     * onUpgrade回调
     * @param db 数据库
     * @param oldVersion 老版本
     * @param newVersion 新版本
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            default:
                db.execSQL("DROP TABLE if exists " + app_list_tableName + " ;");
                db.execSQL("DROP TABLE if exists " + mode_list_tableName + " ;");
                db.execSQL("DROP TABLE if exists " + app_lock_config_tableName + " ;");
                db.execSQL("DROP TABLE if exists " + time_lock_config_tableName + " ;");
                db.execSQL("DROP TABLE if exists " + position_lock_config_tableName + " ;");
                db.execSQL("DROP TABLE if exists " + app_log_list_tableName + " ;");
                onCreate(db);
                break;
        }
    }


}
