package com.softhub.softfly.database;

import com.softhub.softfly.FlyData;
import com.softhub.softframework.SoftFramework;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StorageDataService {

    public static void init() {
        SoftFramework.getDatabaseManager().createTable("fly_data", "uuid VARCHAR(36), enabled VARCHAR(5), time INTEGER");
    }

    public static CompletableFuture<Void> set(FlyData flyData) {
        String table = "fly_data";
        String uuid = flyData.getUserId().toString();
        String enabled = flyData.getEnabled() ? "true" : "false";
        Integer time = flyData.getRemainingTime();

        String selected = "uuid, enabled, time";
        Object[] values = {uuid, enabled, time};

        return SoftFramework.getDatabaseManager().set(selected, values, "uuid", "=", uuid, table);
    }

    public static void setSync(FlyData flyData) {
        String table = "fly_data";
        String uuid = flyData.getUserId().toString();
        String enabled = flyData.getEnabled() ? "true" : "false";
        Integer time = flyData.getRemainingTime();

        String selected = "uuid, enabled, time";
        Object[] values = {uuid, enabled, time};

        SoftFramework.getDatabaseManager().setSync(selected, values, "uuid", "=", uuid, table);
    }

    public static CompletableFuture<FlyData> get(UUID userId) {
        String table = "fly_data";
        String column = "uuid";
        String logicGate = "=";
        String data = userId.toString();

        String selectedColumns = "enabled, time";

        return SoftFramework.getDatabaseManager()
                .getMultipleColumnsList(selectedColumns, table, column, logicGate, data, rs -> {
                    Boolean enabled = Boolean.parseBoolean(rs.getString("enabled"));
                    Integer remainingTime = rs.getInt("time");
                    return new FlyData(userId, enabled, remainingTime);
                })
                .thenApply(resultList -> resultList.isEmpty() ? null : resultList.get(0));
    }

}
