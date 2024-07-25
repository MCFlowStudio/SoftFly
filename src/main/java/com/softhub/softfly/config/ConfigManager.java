package com.softhub.softfly.config;

import com.softhub.softfly.BukkitInitializer;

public class ConfigManager {

    public static void init() {
        BukkitInitializer.getInstance().saveDefaultConfig();
    }

}
