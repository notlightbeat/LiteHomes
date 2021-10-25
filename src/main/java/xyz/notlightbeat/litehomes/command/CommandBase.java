package xyz.notlightbeat.litehomes.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.notlightbeat.litehomes.manager.ConfigManager;
import xyz.notlightbeat.litehomes.util.StringUtils;

/**
 * Base class for the plugin's commands
 */
abstract public class CommandBase implements CommandExecutor {

    protected final int minArgs;
    protected final boolean requiresPermission;
    protected final String permission;
    protected final boolean playerOnly;
    protected final boolean opOnly;
    protected final String commandUsage;

    public CommandBase(int minArgs, boolean requiresPermission, String permission, boolean playerOnly, boolean opOnly, String commandUsage) {
        this.minArgs = minArgs;
        this.requiresPermission = requiresPermission;
        this.permission = permission;
        this.playerOnly = playerOnly;
        this.opOnly = opOnly;
        this.commandUsage = commandUsage;
    }

    /**
     * Method to execute when the command is called.
     *
     * @param sender  the CommandSender that sent the command
     * @param command the Command that got sent
     * @param label   the label of the command used, will be different when using an alias
     * @param args    the arguments that got sent alongside the command
     * @return        true if the command was executed successfully - false otherwise
     */
    abstract protected boolean execute(CommandSender sender, Command command, String label, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandCondition condition = checkCondition(sender, args);
        if (condition == CommandCondition.CORRECT) {
            return execute(sender, command, label, args);
        } else {
            sendErrorMessage(sender, condition);
            return true;
        }
    }

    private CommandCondition checkCondition(CommandSender sender, String[] args) {
        if (args.length < minArgs) {
            return CommandCondition.NOT_ENOUGH_ARGS;
        }
        if (requiresPermission && !sender.hasPermission(permission)) {
            return CommandCondition.PERMISSION_MISSING;
        }
        if (playerOnly && !(sender instanceof Player)) {
            return CommandCondition.NOT_PLAYER;
        }
        if (opOnly && !sender.isOp()) {
            return CommandCondition.NOT_OP;
        }
        return CommandCondition.CORRECT;
    }

    private void sendErrorMessage(CommandSender sender, CommandCondition condition) {
        String message;
        switch (condition) {
            case NOT_ENOUGH_ARGS:
                message = ConfigManager.notEnoughArgsErrorMessage;
                break;
            case PERMISSION_MISSING:
                message = ConfigManager.permissionMissingErrorMessage;
                break;
            case NOT_PLAYER:
                message = ConfigManager.playerOnlyCommandErrorMessage;
                break;
            case NOT_OP:
                message = ConfigManager.opOnlyCommandErrorMessage;
                break;
            default:
                message = "";
        }
        sender.sendMessage(StringUtils.formatPrefixMessage(message));
        if (condition == CommandCondition.NOT_ENOUGH_ARGS && ConfigManager.sendCommandUsageOnNotEnoughArgs) {
            sender.sendMessage(StringUtils.formatPrefixMessage("&9Usage: " + commandUsage));
        }
    }

    public enum CommandCondition {
        NOT_ENOUGH_ARGS,
        PERMISSION_MISSING,
        NOT_PLAYER,
        NOT_OP,
        CORRECT
    }

}
