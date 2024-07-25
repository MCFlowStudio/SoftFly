package com.softhub.softfly;

import com.softhub.softfly.command.FlyCommand;
import com.softhub.softfly.config.ConfigManager;
import com.softhub.softfly.database.CachedDataService;
import com.softhub.softfly.database.StorageDataService;
import com.softhub.softfly.listener.PlayerListener;
import com.softhub.softfly.task.FlyTask;
import com.softhub.softframework.command.CommandRegister;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitInitializer extends JavaPlugin {

    @Getter
    private static BukkitInitializer instance;

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.init();
        StorageDataService.init();
        FlyTask.init();
        CommandRegister.registerCommands(new FlyCommand());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            StorageDataService.get(player.getUniqueId()).thenAccept(data -> {
                CachedDataService.set(player.getUniqueId(), data);
            });
        }

    }

    @Override
    public void onDisable() {
        FlyTask.end();
        for (Player player : Bukkit.getOnlinePlayers()) {
            FlyData flyData = CachedDataService.get(player.getUniqueId());
            if (flyData == null) continue;
            StorageDataService.setSync(flyData);
        }
    }
}
