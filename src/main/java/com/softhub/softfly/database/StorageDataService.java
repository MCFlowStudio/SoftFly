package com.softhub.softfly.database;

import com.softhub.softfly.FlyData;
import com.softhub.softframework.SoftFramework;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StorageDataService {

    public static void init() {
        SoftFramework.getDatabaseManager().createTable("fly_data", "user_id VARCHAR(36), enabled VARCHAR(5), flyspeed FLOAT, time INTEGER");
    }

    public static CompletableFuture<Void> set(FlyData flyData) {
        String table = "fly_data";
        String uuid = flyData.getUserId().toString();
        String enabled = flyData.getEnabled() ? "true" : "false";
        Float flySpeed = flyData.getFlySpeed();
        Integer time = flyData.getRemainingTime();

        String selected = "enabled, flyspeed, time";
        Object[] values = {enabled, flySpeed, time};

        return SoftFramework.getDatabaseManager().set(selected, values, "user_id", "=", uuid, table);
    }

    public static void setSync(FlyData flyData) {
        String table = "fly_data";
        String uuid = flyData.getUserId().toString();
        String enabled = flyData.getEnabled() ? "true" : "false";
        Float flySpeed = flyData.getFlySpeed();
        Integer time = flyData.getRemainingTime();

        String selected = "enabled, flyspeed, time";
        Object[] values = {enabled, flySpeed, time};

        SoftFramework.getDatabaseManager().setSync(selected, values, "user_id", "=", uuid, table);
    }

    public static CompletableFuture<FlyData> get(UUID userId) {
        String table = "fly_data";
        String column = "user_id";
        String logicGate = "=";
        String data = userId.toString();

        String selectedColumns = "enabled, flyspeed, time";

        return SoftFramework.getDatabaseManager()
                .getMultipleColumnsList(selectedColumns, table, column, logicGate, data, rs -> {
                    Boolean enabled = Boolean.parseBoolean(rs.getString("enabled"));
                    Float flySpeed = rs.getFloat("flyspeed");
                    Integer remainingTime = rs.getInt("time");
                    return new FlyData(userId, enabled, flySpeed, remainingTime);
                })
                .thenApply(resultList -> resultList.isEmpty() ? null : resultList.get(0));
    }

}
