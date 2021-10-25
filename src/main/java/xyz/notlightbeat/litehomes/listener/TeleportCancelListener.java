package xyz.notlightbeat.litehomes.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.notlightbeat.litehomes.manager.ConfigManager;
import xyz.notlightbeat.litehomes.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeleportCancelListener implements Listener {

    public static List<UUID> teleports;

    public TeleportCancelListener() {
        teleports = new ArrayList<>();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (teleports.contains(player.getUniqueId()) && ConfigManager.cancelTeleportOnMove && event.getFrom().distance(event.getTo()) > 0.1) {
            teleports.remove(player.getUniqueId());
            player.sendMessage(StringUtils.setPluginPrefix(ConfigManager.teleportCancelledOnMoveMessage));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && ConfigManager.cancelTeleportOnDamage) {
            Player player = (Player) event.getEntity();
            if (teleports.contains(player.getUniqueId())) {
                teleports.remove(player.getUniqueId());
                player.sendMessage(StringUtils.setPluginPrefix(ConfigManager.teleportCancelledOnDamageMessage));
            }
        }
    }

}
