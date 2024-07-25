package com.softhub.softfly.manager;

import com.softhub.softfly.FlyData;
import com.softhub.softfly.database.CachedDataService;
import com.softhub.softfly.database.StorageDataService;
import com.softhub.softframework.task.SimpleAsync;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FlyManager {

    public static CompletableFuture<Integer> getTime(String userName) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        SimpleAsync.async(() -> {
            Player player = Bukkit.getPlayer(userName);
            if (player != null) {
                future.complete(CachedDataService.get(player.getUniqueId()).getRemainingTime());
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(userName);
                if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
                    future.completeExceptionally(new RuntimeException("대상 유저를 찾을 수 없음"));
                }
                StorageDataService.get(offlinePlayer.getUniqueId()).thenAccept(flyData -> {
                    if (flyData != null)
                        future.complete(flyData.getRemainingTime());
                    future.completeExceptionally(new RuntimeException("데이터가 존재하지 않습니다."));
                });
            }
        });
        return future;
    }

    public static CompletableFuture<Integer> getTime(UUID userId) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        SimpleAsync.async(() -> {
            Player player = Bukkit.getPlayer(userId);
            if (player != null) {
                future.complete(CachedDataService.get(userId).getRemainingTime());
            } else {
                if (Bukkit.getOfflinePlayer(userId) == null || !Bukkit.getOfflinePlayer(userId).hasPlayedBefore()) {
                    future.completeExceptionally(new RuntimeException("대상 유저를 찾을 수 없음"));
                }
                StorageDataService.get(userId).thenAccept(flyData -> {
                    if (flyData != null)
                        future.complete(flyData.getRemainingTime());
                });
            }
        });
        return future;
    }

    public static CompletableFuture<Float> setSpeed(String userName, Float value) {
        CompletableFuture<Float> future = new CompletableFuture<>();
        SimpleAsync.async(() -> {
            Player player = Bukkit.getPlayer(userName);
            if (player != null) {
                SimpleAsync.sync(() -> player.setFlySpeed(value));
                FlyData flyData = CachedDataService.get(player.getUniqueId());
                flyData.setFlySpeed(value);
                future.complete(flyData.getFlySpeed());
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(userName);
                if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
                    future.completeExceptionally(new RuntimeException("대상 유저를 찾을 수 없음"));
                    return;
                }
                StorageDataService.get(offlinePlayer.getUniqueId()).thenAccept(flyData -> {
                    if (flyData != null) {
                        flyData.setFlySpeed(value);
                        StorageDataService.set(flyData).thenRun(() -> future.complete(flyData.getFlySpeed()));
                    } else {
                        future.completeExceptionally(new RuntimeException("FlyData를 찾을 수 없음"));
                    }
                }).exceptionally(ex -> {
                    future.completeExceptionally(ex);
                    return null;
                });
            }
        });
        return future;
    }

    public static void setTime(String userName, Integer value) {
        SimpleAsync.async(() -> {
            Player player = Bukkit.getPlayer(userName);
            if (player != null) {
                FlyData flyData = CachedDataService.get(player.getUniqueId());
                flyData.setRemainingTime(value);
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(userName);
                if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
                    return;
                }
                StorageDataService.get(offlinePlayer.getUniqueId()).thenAccept(flyData -> {
                    flyData.setRemainingTime(value);
                    StorageDataService.set(flyData);
                });
            }
        });
    }

    public static void setTime(UUID userId, Integer value) {
        SimpleAsync.async(() -> {
            Player player = Bukkit.getPlayer(userId);
            if (player != null) {
                FlyData flyData = CachedDataService.get(userId);
                flyData.setRemainingTime(value);
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(userId);
                if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
                    return;
                }
                StorageDataService.get(userId).thenAccept(flyData -> {
                    flyData.setRemainingTime(value);
                    StorageDataService.set(flyData);
                });
            }
        });
    }

    public static CompletableFuture<Integer> addTime(String userName, Integer value) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        SimpleAsync.async(() -> {
            Player player = Bukkit.getPlayer(userName);
            if (player != null) {
                FlyData flyData = CachedDataService.get(player.getUniqueId());
                flyData.setRemainingTime(flyData.getRemainingTime() + value);
                future.complete(flyData.getRemainingTime());
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(userName);
                if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
                    future.completeExceptionally(new RuntimeException("대상 유저를 찾을 수 없음"));
                    return;
                }
                StorageDataService.get(offlinePlayer.getUniqueId()).thenAccept(flyData -> {
                    if (flyData != null) {
                        flyData.setRemainingTime(flyData.getRemainingTime() + value);
                        StorageDataService.set(flyData).thenRun(() -> future.complete(flyData.getRemainingTime()));
                    } else {
                        future.completeExceptionally(new RuntimeException("FlyData를 찾을 수 없음"));
                    }
                }).exceptionally(ex -> {
                    future.completeExceptionally(ex);
                    return null;
                });
            }
        });
        return future;
    }

    public static CompletableFuture<Integer> addTime(UUID userId, Integer value) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        SimpleAsync.async(() -> {
            Player player = Bukkit.getPlayer(userId);
            if (player != null) {
                FlyData flyData = CachedDataService.get(userId);
                flyData.setRemainingTime(flyData.getRemainingTime() + value);
                future.complete(flyData.getRemainingTime());
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(userId);
                if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
                    future.completeExceptionally(new RuntimeException("대상 유저를 찾을 수 없음"));
                    return;
                }
                StorageDataService.get(userId).thenAccept(flyData -> {
                    if (flyData != null) {
                        flyData.setRemainingTime(flyData.getRemainingTime() + value);
                        StorageDataService.set(flyData).thenRun(() -> future.complete(flyData.getRemainingTime()));
                    } else {
                        future.completeExceptionally(new RuntimeException("FlyData를 찾을 수 없음"));
                    }
                }).exceptionally(ex -> {
                    future.completeExceptionally(ex);
                    return null;
                });
            }
        });
        return future;
    }

    public static CompletableFuture<Integer> removeTime(String userName, Integer value) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        SimpleAsync.async(() -> {
            Player player = Bukkit.getPlayer(userName);
            if (player != null) {
                FlyData flyData = CachedDataService.get(player.getUniqueId());
                flyData.setRemainingTime(flyData.getRemainingTime() - value);
                future.complete(flyData.getRemainingTime());
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(userName);
                if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
                    future.completeExceptionally(new RuntimeException("대상 유저를 찾을 수 없음"));
                    return;
                }
                StorageDataService.get(offlinePlayer.getUniqueId()).thenAccept(flyData -> {
                    if (flyData != null) {
                        flyData.setRemainingTime(flyData.getRemainingTime() - value);
                        StorageDataService.set(flyData).thenRun(() -> future.complete(flyData.getRemainingTime()));
                    } else {
                        future.completeExceptionally(new RuntimeException("FlyData를 찾을 수 없음"));
                    }
                }).exceptionally(ex -> {
                    future.completeExceptionally(ex);
                    return null;
                });
            }
        });
        return future;
    }

    public static CompletableFuture<Integer> removeTime(UUID userId, Integer value) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        SimpleAsync.async(() -> {
            Player player = Bukkit.getPlayer(userId);
            if (player != null) {
                FlyData flyData = CachedDataService.get(userId);
                flyData.setRemainingTime(flyData.getRemainingTime() - value);
                future.complete(flyData.getRemainingTime());
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(userId);
                if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
                    future.completeExceptionally(new RuntimeException("대상 유저를 찾을 수 없음"));
                    return;
                }
                StorageDataService.get(userId).thenAccept(flyData -> {
                    if (flyData != null) {
                        flyData.setRemainingTime(flyData.getRemainingTime() - value);
                        StorageDataService.set(flyData).thenRun(() -> future.complete(flyData.getRemainingTime()));
                    } else {
                        future.completeExceptionally(new RuntimeException("FlyData를 찾을 수 없음"));
                    }
                }).exceptionally(ex -> {
                    future.completeExceptionally(ex);
                    return null;
                });
            }
        });
        return future;
    }


}
