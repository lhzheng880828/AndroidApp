package com.base.module.pack.update;
/**
 * Copyright (c) 2009-2011 by Grandstream Networks
 */
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import android.content.Context;
import android.util.Log;
import com.base.module.pack.bean.Pack;
import com.base.module.pack.dao.PackDao;
import com.base.module.pack.inter.PackDaoInter;
import com.base.module.pack.method.PackMethod;
public class Update {
    private static final String TAG = "Update";
    private PackParser parser;
    private List<Pack> packs;
    private PackDaoInter mPackDao;
    private static final Update m_instance = new Update();

    public static Update getInstance() {
        return m_instance;
    }

    public Update() {
        parser = new PackParser();
    }

    public void updatePacks(Context context, String str) {
        try {
            if (str != null) {
                Log.d(TAG, "result getNewVersionInfo " + str);
                InputStream is = new ByteArrayInputStream(str.getBytes());
                packs = parser.parse(is);
                if (packs != null) {
                    if (mPackDao == null) {
                        mPackDao = PackDao.getInstance(context);
                    }
                    if (!mPackDao.isOpen()) {
                        mPackDao.open();
                    }
                    for (Pack pack : packs) {
                        mPackDao.setUpdateByLname(pack.getPackLName(), pack.getPackDownRoute());
                    }
                    PackMethod.sendRefreshBroader(context);
                }
            } else {
                Log.d(TAG, "update return is null");
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    public String reput(Context context) {
        try {
            if (mPackDao == null) {
                mPackDao = PackDao.getInstance(context);
            }
            if (!mPackDao.isOpen()) {
                mPackDao.open();
            }
            packs = mPackDao.findInstallOutUpdate();
            return parser.serialize(packs);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public void reputOut(Context context) {
        try {
            if (mPackDao == null) {
                mPackDao = PackDao.getInstance(context);
            }
            if (!mPackDao.isOpen()) {
                mPackDao.open();
            }
            packs = mPackDao.findInstall();
            String xml = parser.serialize(packs);
            FileOutputStream fos = context.openFileOutput("reput_packs.xml", Context.MODE_PRIVATE);
            fos.write(xml.getBytes("UTF-8"));
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }
}