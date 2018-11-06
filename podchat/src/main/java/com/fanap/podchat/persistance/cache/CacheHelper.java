package com.fanap.podchat.persistance.cache;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.util.LruCache;

import java.util.ArrayList;
import java.util.HashMap;

public class CacheHelper<T> extends LruCache<Long, T> {
    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     * the maximum number of entries in the cache. For all other caches,
     * this is the maximum sum of the sizes of the entries in this cache.
     */

    private static HashMap<Class, CacheHelper> instances;
    private ArrayList<Long> keys;

    private CacheHelper(int maxSize) {
        super(maxSize);
        keys = new ArrayList<>();
    }

    public static <T> CacheHelper<T> getNewInstance(Context context, Class<T> tClass) {
        try {
            if (instances != null) {
                if (instances.get(tClass) != null) {
                    return instances.get(tClass);
                }
            } else {
                instances = new HashMap<>();
            }

            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            int availableMemoryInBytes = activityManager.getMemoryClass() * 1024 * 1024;
            int maxSize = availableMemoryInBytes / 16;
            instances.put(tClass, new CacheHelper<T>(maxSize));
            return instances.get(tClass);
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    protected int sizeOf(Long key, T value) {
        return value.toString().getBytes().length;
    }

    public void addToCache(Long key, T t) {
        put(key, t);
        if (!keys.contains(key)) {
            keys.add(key);
        }
    }

    public T getFromCache(Long id){
        return get(id);
    }

}
