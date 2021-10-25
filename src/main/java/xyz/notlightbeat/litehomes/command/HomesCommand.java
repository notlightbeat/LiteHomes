package xyz.notlightbeat.litehomes.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.notlightbeat.litehomes.gui.inventorygui.HomeGui;
import xyz.notlightbeat.litehomes.util.HomeUtils;
import xyz.notlightbeat.litehomes.util.StringUtils;

public class HomesCommand extends CommandBase {

    public HomesCommand() {
        super(0, true, "litehomes.command.homes", false, false, "/homes");
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    OfflinePlayer objectivePlayer = Bukkit.getOfflinePlayer(args[0]);
                    if (objectivePlayer != null) {
                        openHomesGui(player, objectivePlayer);
                    } else {
                        sender.sendMessage(StringUtils.formatMessage("&cPlayer not found"));
                    }
                } else {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    if (player != null) {
                        String[] homes = HomeUtils.getHomeNames(player);
                        sender.sendMessage(StringUtils.formatMessage(args[0] + "'s homes (" + homes.length + ")"));
                        for (String home : homes)
                            sender.sendMessage(home);
                    } else {
                        sender.sendMessage(StringUtils.formatMessage("&cPlayer not found"));
                    }
                }
            } else {
                if (sender instanceof Player)
                    openHomesGui((Player) sender);
                else
                    sender.sendMessage(StringUtils.formatMessage("&cPlease specify a player"));
            }
        } else {
            if (sender instanceof Player)
                openHomesGui((Player) sender, (OfflinePlayer) sender);
        }

        return true;
    }

    private static void openHomesGui(Player player) {
        HomeGui gui = new HomeGui(player, 0);
        player.openInventory(gui.getInventory());
    }

    private static void openHomesGui(Player player, OfflinePlayer objectivePlayer) {
        HomeGui gui = new HomeGui(player, objectivePlayer, 0);
        player.openInventory(gui.getInventory());
    }

}

