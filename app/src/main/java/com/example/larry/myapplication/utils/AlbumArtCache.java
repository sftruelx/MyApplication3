/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.larry.myapplication.utils;

import android.util.LruCache;

import com.example.larry.myapplication.entity.Classify;

import java.util.ArrayList;


/**
 * Implements a basic cache of album arts, with async loading support.
 */
public final class AlbumArtCache {
    private static final String TAG = LogHelper.makeLogTag(AlbumArtCache.class);




    private static final int MAX_ALBUM_ART_CACHE_SIZE = 12*1024*1024;  // 12 MB
    private static final int MAX_ART_WIDTH = 800;  // pixels
    private static final int MAX_ART_HEIGHT = 480;  // pixels

    private static final int MAX_ART_WIDTH_ICON = 128;  // pixels
    private static final int MAX_ART_HEIGHT_ICON = 128;  // pixels

    private static final int BIG_BITMAP_INDEX = 0;
    private static final int ICON_BITMAP_INDEX = 1;

    private final LruCache<String, ArrayList<Classify>> mCache;

    private static final AlbumArtCache Instance = new AlbumArtCache();

    public static AlbumArtCache getInstance() {
        return Instance;
    }

    private AlbumArtCache() {
        LogHelper.i(TAG,"new AlbumArtCache");
        // Holds no more than MAX_ALBUM_ART_CACHE_SIZE bytes, bounded by maxmemory/4 and
        // Integer.MAX_VALUE:
        int maxSize = Math.min(MAX_ALBUM_ART_CACHE_SIZE,
            (int) (Math.min(Integer.MAX_VALUE, Runtime.getRuntime().maxMemory()/4)));
        mCache = new LruCache<String,  ArrayList<Classify>>(maxSize) {
            @Override
            protected int sizeOf(String key, ArrayList<Classify> value) {
                return value.size()*1280;
            }
        };
    }

    public ArrayList<Classify> getClassifies(String artUrl) {
        ArrayList<Classify> result = mCache.get(artUrl);
        return result == null ? null : result;
    }

    public void putClassify(String artUrl,  ArrayList<Classify> classifies){
        mCache.put(artUrl, classifies);
    }


}
