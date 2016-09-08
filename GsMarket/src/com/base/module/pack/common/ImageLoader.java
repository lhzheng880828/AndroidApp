
package com.base.module.pack.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import com.base.module.pack.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;


public class ImageLoader {

    private MemoryCache memoryCache ;
    private FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

    private final int stub_id = R.drawable.icon_default;

    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        memoryCache = new MemoryCache();
    }

    public void displayImage(String url, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
        }else {
            queuePhoto(url, imageView);
            imageView.setImageResource(stub_id);
        }
    }

    private void queuePhoto(String url, ImageView imageView)
    {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        ThreadManager.execute(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url)
    {
        File f = fileCache.getFile(url);


        Bitmap b = decodeFile(f);
        if (b == null){

            try {
                URL imageUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setConnectTimeout(60000);
                conn.setReadTimeout(60000);
                conn.setInstanceFollowRedirects(true);
                InputStream is = conn.getInputStream();
                OutputStream os = new FileOutputStream(f);
                Utils.saveFile(is, os);
                b = decodeFile(f);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return b;
    }


    private Bitmap decodeFile(File f) {
        try {

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);


            final int REQUIRED_SIZE = 100;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }


    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }


    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run()
        {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null){
                photoToLoad.imageView.setImageBitmap(bitmap);
            }else{
                photoToLoad.imageView.setImageResource(stub_id);
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    public class MemoryCache {
        private Map<String, SoftReference<Bitmap>> cache = Collections
                .synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());

        public Bitmap get(String id) {
            if (!cache.containsKey(id))
                return null;
            SoftReference<Bitmap> ref = cache.get(id);
            return ref.get();
        }

        public void put(String id, Bitmap bitmap) {
            cache.put(id, new SoftReference<Bitmap>(bitmap));
        }

        public void clear() {
            cache.clear();
        }
    }

    public class FileCache {

        private File cacheDir;

        public FileCache(Context context) {

            if (android.os.Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)){
                cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),
                        ".marketcache");
            }else{
                cacheDir = context.getCacheDir();
            }
            if (!cacheDir.exists())
                cacheDir.mkdirs();
        }

        public File getFile(String url) {

            String filename = String.valueOf(url.hashCode());
            File f = new File(cacheDir, filename);
            return f;

        }

        public void clear() {
            File[] files = cacheDir.listFiles();
            if (files == null)
                return;
            for (File f : files)
                f.delete();
        }

    }

}