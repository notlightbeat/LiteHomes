package xyz.notlightbeat.litehomes.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.notlightbeat.litehomes.manager.ConfigManager;
import xyz.notlightbeat.litehomes.util.HomeUtils;
import xyz.notlightbeat.litehomes.util.StringUtils;

public class SetHomeCommand extends CommandBase {

    public SetHomeCommand() {
        super(1, true, "litehomes.command.sethome", true, false, "/sethome <home>");
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (!HomeUtils.homeExists(player, args[0])) {
            if (HomeUtils.getHomeCount(player) < HomeUtils.getMaxHomes(player)) {
                HomeUtils.setHome(player, args[0]);
                player.sendMessage(StringUtils.setPluginPrefix(ConfigManager.setHomeSuccessMessage));
            } else {
                player.sendMessage(StringUtils.setPluginPrefix(ConfigManager.maxHomesMessage));
            }
        } else {
            player.sendMessage(StringUtils.setPluginPrefix(ConfigManager.setHomeErrorMessage));
        }

        return true;
    }

}
