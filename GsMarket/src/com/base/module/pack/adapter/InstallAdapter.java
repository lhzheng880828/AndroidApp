/****************************************************************************
 *
 * FILENAME:        com.base.module.adapter.InstallAdapter.java
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
package com.base.module.pack.adapter;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.*;

import com.base.module.pack.R;
import com.base.module.pack.bean.Pack;
import com.base.module.pack.common.Utils;
import com.base.module.pack.inter.PackDaoInter;
import com.base.module.pack.method.PackMethod;
public class InstallAdapter extends BaseAdapter {
    protected static final String TAG = "InstallAdapter";
    static final String DOWN_ACTION = "com.base.module.pack.intent.action.DOWN_ACTION";
    private Context mContext;
    private List<Pack> mPacks;
    ViewHolder holder = null;
    //private PackDaoInter mPackDao;
    AnimationDrawable update_anim;
    //Bitmap contactPhoto;
    //PackMethod mPackMethod;
    private SharedPreferences mSharedPreferences;
    public InstallAdapter(Context context, List<Pack> packs, PackDaoInter packDao) {
        super();
        this.mPacks = packs;
        this.mContext = context;
        mSharedPreferences = context.getSharedPreferences("com.moudle.base.pack.updateapp", 0);
    }

    public int getCount() {
        return mPacks.size();
    }

    public Object getItem(int position) {
        return mPacks.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        holder = null;
        final Pack pack = mPacks.get(position);
        if (convertView == null || (holder = (ViewHolder) convertView.getTag()) == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_install, null);
            holder.name = (TextView) convertView.findViewById(R.id.install_item_name);
            holder.size = (TextView) convertView.findViewById(R.id.install_item_size);
            holder.uninstall = (Button) convertView.findViewById(R.id.install_item_uninstall);
            holder.version = (TextView) convertView.findViewById(R.id.install_item_version);
            holder.update = (Button) convertView.findViewById(R.id.install_item_update);
            holder.icon = (ImageView) convertView.findViewById(R.id.install_item_icon);
            holder.newVersion =  (TextView) convertView.findViewById(R.id.install_item_new_version);
            convertView.setTag(holder);
        }
        holder.name.setText(pack.getPackName());
        /*while is not update or updated */
        if (pack.getPackState() < Pack.STATE_UPDATE && (!pack.getPackUpdate().equals(Pack.IN_UPDATE))) {
            holder.update.setVisibility(View.GONE);
            holder.newVersion.setText("");
        } else {
            holder.update.setVisibility(View.VISIBLE);
            String newVersion = "";
            if(mSharedPreferences!=null){
                newVersion = mContext.getResources().getString(R.string.new_version)+"："+mSharedPreferences.getString(pack.getPackLName(), "");
            }
            holder.newVersion.setText(newVersion);
            if (pack.getPackUpdate().equals(Pack.IN_UPDATE)) {
                holder.uninstall.setEnabled(false);
                /*in updating*/
            } else { /*update or er_update*/
                holder.update.setBackgroundResource(R.drawable.list_btn_uninstall_pressed);
                holder.update.setId(position);
                holder.update.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Pack pack = mPacks.get(v.getId());
                            update(pack.getPackLName(),
                                    pack.getPackDownRoute());
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
        }
        holder.size.setText(mContext.getString(R.string.storage) + ":"
                + PackMethod.ShowSize(pack.getPackSize()));
        holder.version.setText(mContext.getResources().getString(R.string.current_version) + ":" + pack.getPackVersion());

        Drawable dr=holder.icon.getDrawable();
        if(dr!=null){			   
            Bitmap bt=((BitmapDrawable) dr).getBitmap();
            if(bt!=null){
                holder.icon.setImageBitmap(null);
                bt.recycle();  
            }  
        }
        if (!Utils.isEmpty(pack.getIconUrl())) {
            /*InputStream input = new ByteArrayInputStream(pack.getPackIcon());
			contactPhoto = BitmapFactory.decodeStream(input);
			holder.icon.setImageBitmap(contactPhoto);*/
            holder.icon.setImageURI(Uri.parse(pack.getIconUrl()));
        } else{
            holder.icon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.app));
        }
        holder.icon.setId(position);
        holder.icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openApllication(mPacks.get(v.getId()).getPackLName());
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
        holder.uninstall.setId(position);
        holder.uninstall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uninstall(mPacks.get(v.getId()).getPackLName());
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if(mSharedPreferences!=null){
                    mSharedPreferences.edit().remove(pack.getPackLName()).commit();
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView name;
        TextView version;
        TextView newVersion;
        TextView size;
        Button uninstall;
        Button update;
    }

    private void update(String l_name, String url) {
        PackMethod.update(mContext, l_name, url,null);	
    }

    private void uninstall(String l_name) {
        PackMethod.uninstall(mContext, l_name);
    }

    private void openApllication(String packLname) {
        PackMethod.openApllication(mContext, packLname);
    }
}