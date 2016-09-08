
package com.base.module.pack.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.base.module.pack.R;
import com.base.module.pack.bean.ApkInfo;
import com.base.module.pack.bean.AppSoftwareInfo;
import com.base.module.pack.bean.Pack;
import com.base.module.pack.common.ImageLoader;
import com.base.module.pack.common.Utils;
import com.base.module.pack.listener.CustomWebViewDownLoadListener;
import com.base.module.pack.task.TaskPool;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * @author yyzhu
 *@CopyRight grandstream
 */
public class AppGridviewAdapter extends BaseAdapter{
    private Context mContext;
    private List<AppSoftwareInfo> mApplist;
    private LayoutInflater mLayoutInflater;
    private Map<String, Pack> mPacks = new HashMap<String, Pack>();
    private CustomWebViewDownLoadListener mDownListener;
    private ImageLoader mImageLoader;
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor;
    private String instaillPack;
    public AppGridviewAdapter(Context context,CustomWebViewDownLoadListener downListener,ImageLoader imageLoader){
        mContext = context;
        sharedPreferences = context.getSharedPreferences("com.moudle.base.pack.updateapp", 0);
        editor = sharedPreferences.edit();
        mApplist = new ArrayList<AppSoftwareInfo>();
        mLayoutInflater = LayoutInflater.from(context);
        mDownListener = downListener;
        mImageLoader= imageLoader;
    }


    @Override
    public int getCount() {
        return mApplist.size();
    }

    @Override
    public AppSoftwareInfo getItem(int position) {
        return mApplist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getSysid();
    }

    public void notifyDataSetChanged(List<AppSoftwareInfo> applist) {
        mApplist.clear();
        if(applist != null){
            mApplist.addAll(applist);
        }
        super.notifyDataSetChanged();
    }
    public void setDataPackSetChanged(Map<String, Pack> packs) {
        mPacks.clear();
        if(packs != null){
            mPacks.putAll(packs);
        }
        super.notifyDataSetChanged();
    }
    public void setInstallPack(String appPackageName) {
        instaillPack = appPackageName;
    }
    ViewHelper helper =null;
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        helper =null;
        if(view == null){
            view = mLayoutInflater.inflate(R.layout.appitem, null);
        }
        helper = new ViewHelper(view);
        String packageName = "";
       final AppSoftwareInfo info = getItem(position);
        String latestVersion = info.getLatestVersion();
        if(info != null){

            final List<ApkInfo> versionInfo = info.getApkInfo();

            if(versionInfo != null && versionInfo.size()>0){
                packageName =  versionInfo.get(0).getPackageName();
                String size = versionInfo.get(0).getSize();
                helper.sizeView.setText(size + " M");
            }else{
                helper.sizeView.setText("");
            }
            Pack pck = mPacks.get(packageName);
            int state =0;
            if(pck!=null && pck.getPackName().equals(info.getAppname())){
                state = pck.getPackState();
            }
            String downBtnText= State(state);
            helper.downText.setText(downBtnText);
            mImageLoader.displayImage(info.getIconurl(), helper.appIcon);
           // helper.appIcon.setImageResource(R.drawable.icon_default);//AppIconHelper.getIconRes118(packageName)

            helper.appNameView.setText(info.getAppname());
            Integer charge = info.getIscharge() ;
            helper.chargeView.setText((charge !=null &&charge.intValue() == 1) ?R.string.charge:R.string.free);

            helper.ratingBarView.setRating(info.getGradenum());
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.btn_icon_download);
            final boolean isExit = Utils.isPackageExit(mContext, packageName);
            helper.downImage.setVisibility(View.VISIBLE);
            if(isExit){
                helper.downLayout.setBackgroundResource(R.drawable.app_down_button_green);
                helper.downText.setText(R.string.to_open);
                drawable = mContext.getResources().getDrawable(R.drawable.btn_icon_open);
               boolean isHasNew = Utils.isHasNewVesion(mContext, packageName, latestVersion);
                if(isHasNew){
                    editor.putString(packageName, latestVersion);
                    editor.commit();
                    helper.downText.setText(R.string.to_update);
                    drawable = mContext.getResources().getDrawable(R.drawable.btn_icon_update);
                }
            }else{
                //helper.downButton.setText(R.string.to_install);//to_download
                helper.downLayout.setBackgroundResource(R.drawable.app_down_button_blue);
            }
            if (Pack.STATE_IN_DOWN == state || Pack.STATE_WAIT == state){
                helper.downText.setText(State(state));
                helper.downImage.setVisibility(View.GONE);
                helper.downLayout.setBackgroundResource(R.drawable.app_down_button_blue);
            }else{
                helper.downImage.setImageDrawable(drawable);
            }

            if(packageName.equals(instaillPack)){
                helper.downText.setText(R.string.installing);
            }
            helper.downLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if(versionInfo != null && versionInfo.size()>0){
                        String  packageName =  versionInfo.get(0).getPackageName();
                        if(!TextUtils.isEmpty(packageName)){
                          boolean hasNewVersion = false;
                            if(sharedPreferences!=null){
                             String newVersion = sharedPreferences.getString(packageName, "");
                             if(!TextUtils.isEmpty(newVersion)){
                                 hasNewVersion = true;
                             }
                            }
                            if(isExit && !hasNewVersion){
                                Utils.openApplication(mContext.getApplicationContext(), packageName);
                            }else{
                                long fileSize = 0;
                                try {
                                    fileSize = (long) (versionInfo.get(0).getSize()!=null?(Float.parseFloat(versionInfo.get(0).getSize())*1024.0f*1024.0f):0);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                mDownListener.downApp(versionInfo.get(0).getDownloadUrl(),info.getIconurl(), mContext,fileSize);
                            }
                        }
                    }
                }
            });
        }
        view.setTag(packageName);
        return view;
    }
    private class ViewHelper{
        LinearLayout downLayout;
        ImageView appIcon;
        //ImageView operatorIcon; 
        TextView appNameView;
        TextView chargeView;
        TextView sizeView;
        RatingBar ratingBarView;
        TextView downText ;
        ImageView downImage;
        private ViewHelper(View view){
            appIcon = (ImageView) view.findViewById(R.id.icon);
            appNameView = (TextView) view.findViewById(R.id.appname);
            chargeView = (TextView) view.findViewById(R.id.charge);
            sizeView =  (TextView) view.findViewById(R.id.size);
            ratingBarView = (RatingBar) view.findViewById(R.id.grade);
            downText = (TextView) view.findViewById(R.id.downButton);
            downImage = (ImageView) view.findViewById(R.id.downBtnimage);
            downLayout = (LinearLayout)view.findViewById(R.id.downLayout);
            //operatorIcon = (ImageView) view.findViewById(R.id.operator_icon);
        }
    }

    private String State(int state) {
        switch (state) {
        case 0:
            return mContext.getResources().getString(R.string.to_install);
        case Pack.STATE_INSTALL:
            return mContext.getResources().getString(R.string.to_open);
        case Pack.STATE_IN_DOWN:
            return mContext.getResources().getString(R.string.downloading);
        case Pack.STATE_IN_INSTALL:
            return mContext.getResources().getString(R.string.installing);
        case Pack.STATE_WAIT:
            return mContext.getResources().getString(R.string.wating);
        default:
            return mContext.getResources().getString(R.string.to_install);
        }
    }
}