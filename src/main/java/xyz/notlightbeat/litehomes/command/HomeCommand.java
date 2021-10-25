package xyz.notlightbeat.litehomes.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.notlightbeat.litehomes.LiteHomes;
import xyz.notlightbeat.litehomes.listener.TeleportCancelListener;
import xyz.notlightbeat.litehomes.manager.ConfigManager;
import xyz.notlightbeat.litehomes.util.HomeUtils;
import xyz.notlightbeat.litehomes.util.StringUtils;

public class HomeCommand extends CommandBase {

    public HomeCommand() {
        super(1, true, "litehomes.command.home", true, false, "/home <home>");
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (HomeUtils.homeExists(player, args[0])) {
            int[] coordinates = HomeUtils.getHomeCoords(player, args[0]);
            if (ConfigManager.teleportDelay == 0 || player.hasPermission("litehomes.tpdelayoverride")) {
                player.teleport(new Location(HomeUtils.getHomeWorld(player, args[0]), coordinates[0], coordinates[1], coordinates[2]));
            } else {
                if (TeleportCancelListener.teleports.contains(player.getUniqueId())) {
                    player.sendMessage(StringUtils.setPluginPrefix(ConfigManager.teleportRequestPendingMessage));
                } else {
                    TeleportCancelListener.teleports.add(player.getUniqueId());
                    Bukkit.getServer().getScheduler().runTaskLater(LiteHomes.getPlugin(), () -> {
                        if (TeleportCancelListener.teleports.contains(player.getUniqueId())) {
                            player.teleport(new Location(HomeUtils.getHomeWorld(player, args[0]), coordinates[0], coordinates[1], coordinates[2]));
                            TeleportCancelListener.teleports.remove(player.getUniqueId());
                        }
                    }, (long) ConfigManager.teleportDelay * 20L);
                    player.sendMessage(StringUtils.setPluginPrefix(ConfigManager.teleportDelayMessage.replaceAll("%seconds%", String.valueOf(ConfigManager.teleportDelay))));
                }
            }
        } else {
            player.sendMessage(StringUtils.setPluginPrefix(ConfigManager.homeNotExistsMessage));
        }
        return true;
    }

}
