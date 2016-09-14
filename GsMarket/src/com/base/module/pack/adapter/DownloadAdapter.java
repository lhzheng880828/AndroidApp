/****************************************************************************
 *
 * FILENAME:        com.base.module.adapter.DownloadAdapter.java
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.base.module.pack.common.ImageLoader;
import com.base.module.pack.common.Utils;
import com.base.module.pack.inter.PackDaoInter;
import com.base.module.pack.method.PackMethod;
import com.base.module.pack.method.MyTime;
import com.base.module.pack.service.DownService.DownloaderTask;
import com.base.module.pack.task.TaskPool;
public class DownloadAdapter extends BaseAdapter {
    protected static final String TAG = "DownloadAdapter";
    static final String DOWN_ACTION = "com.base.module.pack.intent.action.DOWN_ACTION";
    private Context mContext;
    private Map<String, Pack> mPacks = new HashMap<String, Pack>();
    private List<String> mPackNames;
    private MyTime mTime = new MyTime();
    private TaskPool mTaskPool;
    final String cancel_text;
    AnimationDrawable frameAnimation;
    private SharedPreferences sh = null;
    private SharedPreferences.Editor editor = null;
    ViewHolder holder;
    private ImageLoader mImageLoader = null;
    public DownloadAdapter(Context context, Map<String, Pack> packs, List<String> packNames, PackDaoInter packDao) {
        super();
        this.mContext = context;
        this.mPacks = packs;
        this.mPackNames = packNames;
        mTaskPool = TaskPool.getInstance();
        cancel_text = mContext.getString(R.string.cancel);
        mImageLoader =new ImageLoader(mContext);
        sh = context.getSharedPreferences("com.moudle.base.pack.downloadproess", 0);
        editor = sh.edit();
    }

    public void setmPacks(Map<String, Pack> mPacks) {
        this.mPacks = mPacks;
    }

    public void setmPackNames(List<String> mPackNames) {
        this.mPackNames = mPackNames;
    }

    public int getCount() {
        return mPackNames.size();
    }

    public Object getItem(int position) {
        return mPackNames.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        final String packLname=mPackNames.get(position);
        final Pack pack = mPacks.get(packLname);
        final DownloaderTask task=mTaskPool.get(packLname);
        int state = pack.getPackState();
        if ((convertView != null && (holder = ((ViewHolder) convertView.getTag(R.id.holder))) != null)) {
            /* ImageView iv=(ImageView) convertView.findViewById(R.id.download_item_icon);
            Drawable dr= iv.getDrawable();
            if(dr!=null){
                Bitmap bt=((BitmapDrawable) dr).getBitmap();
                if(bt!=null){
                    iv.setImageBitmap(null);
                    bt.recycle();
                }
            }*/
        }else{

            holder = new ViewHolder();
        }
        holder.flag = state;
        //Log.i(TAG, String.valueOf(state));

        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_download_pb, null);
        holder.pb = (ProgressBar) convertView.findViewById(R.id.download_item_pb);
        holder.percent = (TextView) convertView.findViewById(R.id.download_item_percent);
        holder.close = (ImageView) convertView.findViewById(R.id.download_item_close);
        holder.size = (TextView) convertView.findViewById(R.id.download_item_size);

        holder.name = (TextView) convertView.findViewById(R.id.download_item_name);
        holder.state = (TextView) convertView.findViewById(R.id.download_item_state);

        holder.icon = (ImageView) convertView.findViewById(R.id.download_item_icon);

        convertView.setTag(R.id.holder,holder);
        holder.name.setText(pack.getPackName());
        holder.state.setText(State(state));

        if (!Utils.isEmpty(pack.getIconUrl())) {
            mImageLoader.displayImage(pack.getIconUrl(),holder.icon);
        } else {
            holder.icon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.app));
        }

        if (state == Pack.STATE_IN_INSTALL) {
            if (holder.pb != null) {
                holder.pb.setIndeterminate(true);
                holder.close.setVisibility(View.INVISIBLE);
                editor.remove(packLname);
                editor.commit();
            }
        } else {
            int press = 0;
            if(state == Pack.STATE_IN_DOWN || state == Pack.STATE_WAIT){
                try {
                    //press = mTaskPool.getPress(mContext, pack.getPackLName());
                    if(task!=null && task.getLength()!=0){
                        press = (int) ((task.getCount() / (float) task.getLength()) * 100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                editor.putInt(packLname, press);
                editor.commit();
            }else{
                press = sh.getInt(packLname, 0);
            }
            if(state==Pack.STATE_UNINSTALL && press>0){
                press=0;
            }
            holder.pb.setProgress(press);
            float f = (float)press/100;
            long downSize = (long) (pack.getPackSize()*f);
            holder.size.setText(PackMethod.ShowSize(downSize)+"/"+PackMethod.ShowSize(pack.getPackSize()));
            holder.state.setText(State(state));
            holder.percent.setText(press + "%  ");
            holder.close.setVisibility(View.VISIBLE);
            holder.close.setId(position);

            if (Pack.STATE_ER_DOWN == state || Pack.STATE_ER_INSTALL == state || Pack.STATE_ER_UPDATE == state || Pack.STATE_UNINSTALL ==state) {
                holder.state.setTextColor(Color.parseColor("#ff0000"));
                holder.close.setImageResource(R.drawable.btn_redownload);
                holder.close.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Market", pack.getPackLName() + "  downroute "  + pack.getPackDownRoute());

                        PackMethod.sendDownloadBroader(mContext,pack.getPackDownRoute(),pack.getIconUrl(),null,pack.getPackSize());
                    }
                });

            }else{
                holder.close.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("Market"," close click");
                        mTaskPool.interrupt(mContext,mPackNames.get(v.getId()));

                    }
                });
            }
        }

        return convertView;
    }

    public static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView version;
        TextView size;
        TextView state;
        public TextView percent;
        //extView close_text;
        public ProgressBar pb;
        ImageView close;
        //Bitmap contactPhoto;
        //Button redown;
        //TextView redown_text;
        int flag;
    }

    private String State(int state) {
        if (state == 0)
            return "";
        if (state == Pack.STATE_UPDATE)
            return mContext.getString(R.string.can_update);
        else if (state == Pack.STATE_ER_UPDATE)
            return mContext.getString(R.string.update_fail);
        else if (state == Pack.STATE_INSTALL)
            return "";
        else if (state == Pack.STATE_UNINSTALL)
            return mContext.getString(R.string.uninstalled);
        else if (state == Pack.STATE_WAIT)
            return mContext.getString(R.string.waiting);
        else if (state == Pack.STATE_IN_DOWN) {
            return mContext.getString(R.string.downloading);
        } else if (state == Pack.STATE_IN_INSTALL)
            return mContext.getString(R.string.installing);
        else if (state == Pack.STATE_ER_DOWN)
            return mContext.getString(R.string.download_fail);
        else if (state == Pack.STATE_ER_INSTALL)
            return mContext.getString(R.string.install_fail);
        else
            return mContext.getString(R.string.no_load);
    }
}