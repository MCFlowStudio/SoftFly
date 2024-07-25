package com.softhub.softfly.database;

import com.softhub.softfly.FlyData;
import com.softhub.softframework.database.cache.CacheStorage;

import java.util.UUID;

public class CachedDataService {

    public static FlyData get(UUID userId) {
        FlyData flyData = new FlyData(userId, false, 0.1f, 0);
        if (CacheStorage.get("flydata", userId.toString()) instanceof FlyData) {
            flyData = (FlyData) CacheStorage.get("flydata", userId.toString());
        }
        return flyData;
    }

    public static void set(UUID userId, FlyData flyData) {
        CacheStorage.set("flydata", userId.toString(), flyData);
    }

    public static void remove(UUID userId) {
        CacheStorage.remove("flydata", userId.toString());
    }

}
