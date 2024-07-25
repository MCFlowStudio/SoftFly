package com.softhub.softfly.task;

import com.softhub.softfly.BukkitInitializer;
import com.softhub.softfly.FlyData;
import com.softhub.softfly.database.CachedDataService;
import com.softhub.softframework.config.convert.MessageComponent;
import com.softhub.softframework.task.SimpleTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FlyTask {

    @Getter
    private static UUID taskId;

    public static void init() {
        taskId = UUID.randomUUID();
        SimpleTask.asyncRepeating(taskId.toString(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                FlyData flyData = CachedDataService.get(player.getUniqueId());
                if (flyData.getEnabled() && player.isFlying()) {
                    int remainingTime = flyData.getRemainingTime();
                    String formatTime = MessageComponent.formatTime(remainingTime);
                    player.sendActionBar(MessageComponent.formatMessage(BukkitInitializer.getInstance().getConfig(), "action_bar", formatTime));
                    if (remainingTime > 0) {
                        remainingTime--;
                        flyData.setRemainingTime(remainingTime);
                        CachedDataService.set(player.getUniqueId(), flyData);
                        if (remainingTime == 0) {
                            player.sendMessage(MessageComponent.formatMessage(BukkitInitializer.getInstance().getConfig(), "expired_fly_time"));
                            flyData.setEnabled(false);
                            player.setAllowFlight(false);
                            player.setFlying(false);
                        }
                    }
                }
            }
        }, 0L, 20L);
    }

    public static void end() {
        SimpleTask.cancelTask(taskId.toString());
    }

}
