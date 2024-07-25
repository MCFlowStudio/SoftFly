package com.softhub.softfly.listener;

import com.softhub.softfly.FlyData;
import com.softhub.softfly.database.CachedDataService;
import com.softhub.softfly.database.StorageDataService;
import com.softhub.softframework.task.SimpleAsync;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SimpleAsync.syncLater(() -> {
            StorageDataService.get(player.getUniqueId()).thenAccept(data -> {
                if (data == null) return;
                CachedDataService.set(player.getUniqueId(), data);
            });
        }, 5L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FlyData flyData = CachedDataService.get(player.getUniqueId());
        StorageDataService.set(flyData).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        }).thenRun(() -> CachedDataService.remove(player.getUniqueId()));
    }

}
