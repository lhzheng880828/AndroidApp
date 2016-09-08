package com.eeontheway.android.applocker.share;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 分享接口信息类
 * @author lishutong
 * @version v1.0
 * @Time 2016-12-15
 */
public class ShareInfo implements Parcelable {
    /**
     * 网页访问的URL
     */
    public String url;

    /**
     * 分享标题
     */
    public String title;

    /**
     * 分享内容
     */
    public String content;

    /**
     * 分享的缩略图
     */
    public Bitmap thumbBitmap;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(url);
        dest.writeValue(title);
        dest.writeValue(content);
        dest.writeValue(thumbBitmap);
    }

    public static final Parcelable.Creator<ShareInfo> CREATOR = new Creator<ShareInfo>() {
        public ShareInfo createFromParcel(Parcel source) {
            ShareInfo info = new ShareInfo();
            info.url = (String)source.readValue(String.class.getClassLoader());
            info.title = (String)source.readValue(String.class.getClassLoader());
            info.content = (String)source.readValue(String.class.getClassLoader());
            info.thumbBitmap = (Bitmap)source.readValue(Bitmap.class.getClassLoader());
            return info;
        }
        public ShareInfo [] newArray(int size) {
            return new ShareInfo[size];
        }
    };
}
