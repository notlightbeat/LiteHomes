package xyz.notlightbeat.litehomes.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.notlightbeat.litehomes.gui.inventorygui.DelHomeGui;
import xyz.notlightbeat.litehomes.manager.ConfigManager;
import xyz.notlightbeat.litehomes.util.HomeUtils;
import xyz.notlightbeat.litehomes.util.StringUtils;

public class DelHomeCommand extends CommandBase {

    public DelHomeCommand() {
        super(1, true, "litehomes.command.delhome", false, false, "/delhome <home>");
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    OfflinePlayer objectivePlayer = Bukkit.getPlayer(args[0]);
                    if (objectivePlayer != null) {
                        openDelHomeGui(player, objectivePlayer);
                    } else {
                        String[] homes = HomeUtils.getHomeNames(player);
                        boolean coincidence = false;
                        for (String home : homes)
                            if (home.equals(args[0])) {
                                coincidence = true;
                                break;
                            }
                        if (coincidence) {
                            HomeUtils.delHome(player, args[0]);
                            sender.sendMessage(StringUtils.setPluginPrefix(ConfigManager.delHomeSuccessMessage));
                        } else {
                            sender.sendMessage(StringUtils.setPluginPrefix(ConfigManager.homeNotExistsMessage));
                        }
                    }
                } else {
                    sender.sendMessage(StringUtils.formatPrefixMessage("&cCommand usage: /delhome <player> <home>"));
                }
            } else if (args.length == 2) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                if (player != null) {
                    String[] homes = HomeUtils.getHomeNames(player);
                    boolean coincidence = false;
                    for (String home : homes)
                        if (home.equals(args[1])) {
                            coincidence = true;
                            break;
                        }
                    if (coincidence) {
                        HomeUtils.delHome(player, args[1]);
                        sender.sendMessage(StringUtils.formatPrefixMessage("&bHome " + args[1] + " for player " + args[0] + " was deleted"));
                    }
                } else {
                    sender.sendMessage(StringUtils.formatPrefixMessage("&cPlayer not found"));
                }
            } else {
                if (sender instanceof Player)
                    openDelHomeGui((Player) sender, (Player) sender);
                else
                    sender.sendMessage(StringUtils.formatPrefixMessage("&cPlease specify a player"));
            }
        } else {
            if (sender instanceof Player)
                openDelHomeGui((Player) sender, (OfflinePlayer) sender);
        }

        return true;
    }

    private static void openDelHomeGui(Player player, OfflinePlayer objectivePlayer) {
        DelHomeGui gui = new DelHomeGui(player, objectivePlayer, 0);
        player.openInventory(gui.getInventory());
    }

}
