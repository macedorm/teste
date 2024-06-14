package br.com.jjconsulting.mobile.dansales.util;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class PicassoCustom{

    private static Picasso mInstance;
    private static OkHttpClient mOkHttpClient;
    private static Cache diskCache;
    private static LruCache lruCache;

    private static long mDiskCacheSize = 50*1024*1024;

    public static synchronized Picasso getSharedInstance(Context context)
    {
        if(mInstance == null) {
            if (context != null) {
                File cache = new File(context.getApplicationContext().getCacheDir(), "picasso_cache");

                if (!cache.exists()) {
                    cache.mkdirs();
                }

                diskCache = new Cache(cache, mDiskCacheSize);
                lruCache = new LruCache(context);
                mOkHttpClient = new OkHttpClient.Builder().cache(diskCache).connectTimeout(Integer.MAX_VALUE, TimeUnit.MILLISECONDS).build();
                mInstance = new Picasso.Builder(context).memoryCache(lruCache).downloader(new OkHttp3Downloader(mOkHttpClient)).indicatorsEnabled(false).build();

            }
        }

        try{
            Picasso.setSingletonInstance(mInstance);
        }catch (Exception ex){

        }

        return mInstance;
    }


    public static Picasso setImage(Context context, String url, ImageView imageView){
        Picasso customPicasso = PicassoCustom.getSharedInstance(context);

        if(TextUtils.isNullOrEmpty(url)){
            url = "http://";
        } else if (!url.contains("http://") && !url.contains("https://")){
           url = BuildConfig.URL_SITE + url;
        }

        LogUser.log(url);

        customPicasso.
                load(url)
                .error(context.getResources().getDrawable(R.drawable.ic_photo_black_24dp)).into(imageView);

        return customPicasso;
    }

    public static void clear(Context context){

        if(mInstance == null){
            mInstance =  PicassoCustom.getSharedInstance(context);
        }

        clearLRUCache();
        clearDiskCache();
    }

    private static void clearLRUCache()
    {
        if(lruCache!=null) {
            lruCache.clear();
        }

        lruCache = null;
    }

    private static void clearDiskCache(){
        try {
            if(diskCache!=null) {
                diskCache.evictAll();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        diskCache = null;
    }

    public static void deletePicassoInstance() {
        mInstance = null;
    }
}