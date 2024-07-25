package com.softhub.softfly.command;

import com.softhub.softfly.BukkitInitializer;
import com.softhub.softfly.FlyData;
import com.softhub.softfly.config.ConfigManager;
import com.softhub.softfly.database.CachedDataService;
import com.softhub.softfly.manager.FlyManager;
import com.softhub.softframework.command.Command;
import com.softhub.softframework.command.CommandExecutor;
import com.softhub.softframework.command.CommandHelp;
import com.softhub.softframework.command.CommandParameter;
import com.softhub.softframework.config.convert.MessageComponent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@Command(name = "플라이", aliases = "fly", description = "플라이 명령어입니다.", permission = "softfly.command.fly")
public class FlyCommand {

    @CommandHelp
    public boolean onToggle(Player sender) {
        FlyData flyData = CachedDataService.get(sender.getUniqueId());
        int remainingTime = flyData.getRemainingTime();

        if (remainingTime > 0) {
            boolean newState = !flyData.getEnabled();
            flyData.setEnabled(newState);

            sender.setAllowFlight(newState);
            if (!newState) {
                sender.setFlying(false);
            }

            String formatTime = MessageComponent.formatTime(remainingTime);
            Component message = newState ?
                    MessageComponent.formatMessage(BukkitInitializer.getInstance().getConfig(), "fly_toggle_on", formatTime) :
                    MessageComponent.formatMessage(BukkitInitializer.getInstance().getConfig(), "fly_toggle_off", formatTime);
            sender.sendMessage(message);
            CachedDataService.set(sender.getUniqueId(), flyData);
        } else {
            sender.sendMessage(MessageComponent.formatMessage(BukkitInitializer.getInstance().getConfig(), "not_enough_time"));
        }
        return true;
    }



    @CommandExecutor(label = "확인", description = "타인의 플라이 시간을 확인합니다.", permission = "softfly.command.fly.timeget")
    public boolean onTimeGet(Player sender, @CommandParameter(name = "대상 플레이어", type = CommandParameter.ParamType.STRING, index = 1) String targetName) {
        FlyManager.getTime(targetName).thenAccept(flyTime -> {
            String formatTime = MessageComponent.formatTime(flyTime);
            sender.sendMessage(MessageComponent.formatMessage(BukkitInitializer.getInstance().getConfig(), "command_get_time", targetName, formatTime));
        }).exceptionally(ex -> {
            sender.sendMessage(MessageComponent.formatMessage(BukkitInitializer.getInstance().getConfig(), "command_error", ex.getLocalizedMessage()));
            return null;
        });
        return true;
    }

    @CommandExecutor(label = "설정", description = "타인의 플라이 시간을 설정합니다.", permission = "softfly.command.fly.timeset")
    public boolean onTimeSet(Player sender, 
                             @CommandParameter(name = "대상 플레이어", type = CommandParameter.ParamType.STRING, index = 1) String targetName,
                             @CommandParameter(name = "시간", type = CommandParameter.ParamType.INTEGER, index = 2) Integer second) {
        FlyManager.setTime(targetName, second);
        String formatTime = MessageComponent.formatTime(second);
        sender.sendMessage(MessageComponent.formatMessage(BukkitInitializer.getInstance().getConfig(), "command_set_time", targetName, formatTime));
        return true;
    }

    @CommandExecutor(label = "지급", description = "타인의 플라이 시간을 추가합니다.", permission = "softfly.command.fly.timeadd")
    public boolean onTimeAdd(Player sender, 
                             @CommandParameter(name = "대상 플레이어", type = CommandParameter.ParamType.STRING, index = 1) String targetName,
                             @CommandParameter(name = "시간", type = CommandParameter.ParamType.INTEGER, index = 2) Integer second) {
        FlyManager.addTime(targetName, second).thenAccept(flyTime -> {
            String formatTime = MessageComponent.formatTime(flyTime);
            sender.sendMessage(MessageComponent.formatMessage(BukkitInitializer.getInstance().getConfig(), "command_add_time", targetName, second, formatTime));
        }).exceptionally(ex -> {
            sender.sendMessage(MessageComponent.formatMessage(BukkitInitializer.getInstance().getConfig(), "command_error", ex.getLocalizedMessage()));
            return null;
        });
        return true;
    }

    @CommandExecutor(label = "차감", description = "타인의 플라이 시간을 차감합니다.", permission = "softfly.command.fly.timeremove")
    public boolean onTimeRemove(Player sender,
                                @CommandParameter(name = "대상 플레이어", type = CommandParameter.ParamType.STRING, index = 1) String targetName,
                                @CommandParameter(name = "시간", type = CommandParameter.ParamType.INTEGER, index = 2) Integer second) {
        FlyManager.removeTime(targetName, second).thenAccept(flyTime -> {
            String formatTime = MessageComponent.formatTime(flyTime);
            sender.sendMessage(MessageComponent.formatMessage(BukkitInitializer.getInstance().getConfig(), "command_remove_time", targetName, second, formatTime));
        }).exceptionally(ex -> {
            sender.sendMessage(MessageComponent.formatMessage(BukkitInitializer.getInstance().getConfig(), "command_error", ex.getLocalizedMessage()));
            return null;
        });
        return true;
    }

    @CommandExecutor(label = "리로드", description = "플러그인 설정을 다시 불러옵니다.", permission = "softfly.command.fly.reload")
    public boolean onReload(Player sender) {
        ConfigManager.init();
        sender.sendMessage("플러그인 설정을 다시 불러왔습니다.");
        return true;
    }

}
