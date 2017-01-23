package com.ing_kevin_barrientos.marvelpocketuniverse;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MarvelPocketApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheSize(2 * 1024 * 1024) // 2MB
                .diskCacheSize(50 * 1024 * 1024) // 50MB
                .diskCacheFileCount(100) // 100 images only
                .writeDebugLogs()
                .defaultDisplayImageOptions(
                        new DisplayImageOptions.Builder()
                                .showImageOnLoading(R.drawable.progress) // resource or drawable
                                .showImageForEmptyUri(R.drawable.placeholder_grey) // resource or drawable
                                .showImageOnFail(R.drawable.error_orange) // resource or drawable
                                .cacheInMemory(true)
                                .cacheOnDisk(true)
                                .build()
                )
                .build();
        ImageLoader.getInstance().init(config);
    }
}
