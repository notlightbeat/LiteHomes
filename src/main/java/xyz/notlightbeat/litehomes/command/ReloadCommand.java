package xyz.notlightbeat.litehomes.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.notlightbeat.litehomes.LiteHomes;
import xyz.notlightbeat.litehomes.util.StringUtils;

public class ReloadCommand extends CommandBase {

    public ReloadCommand() {
        super(0, true, "litehomes.command.reload", false, true, "/lhreload");
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        LiteHomes.getPlugin().reloadConfiguration();
        sender.sendMessage(StringUtils.formatPrefixMessage("&bConfiguration file reloaded!"));
        return true;
    }

}
