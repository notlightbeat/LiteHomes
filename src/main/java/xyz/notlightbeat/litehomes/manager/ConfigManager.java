package xyz.notlightbeat.litehomes.manager;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.notlightbeat.litehomes.LiteHomes;

import java.util.HashMap;
import java.util.Objects;

abstract public class ConfigManager {

    public static int teleportDelay;
    public static int numberOfRowsInGui;

    public static boolean backupDatabase;
    public static boolean showEmptyHomes;
    public static boolean setPrefixToMessages;
    public static boolean showCoordinatesInGui;
    public static boolean showHomesByDimension;
    public static boolean cancelTeleportOnMove;
    public static boolean cancelTeleportOnDamage;

    public static String setHomeAlertMessage;
    public static String maxHomesMessage;
    public static String homeScreenTitle;
    public static String delHomeScreenTitle;
    public static String setHomeErrorMessage;
    public static String teleportDelayMessage;
    public static String homeNotExistsMessage;
    public static String setHomeSuccessMessage;
    public static String delHomeSuccessMessage;
    public static String homeNotSpecifiedMessage;
    public static String teleportRequestPendingMessage;
    public static String teleportCancelledOnMoveMessage;
    public static String teleportCancelledOnDamageMessage;

    public static String homeBlock;
    public static String pageChangeBlock;
    public static String emptyHomesBlock;
    public static String deleteHomesBlock;

    public static String nextPageItemName;
    public static String emptyHomeItemName;
    public static String deleteHomesItemName;
    public static String previousPageItemName;

    public static boolean sendCommandUsageOnNotEnoughArgs;
    public static String notEnoughArgsErrorMessage;
    public static String permissionMissingErrorMessage;
    public static String playerOnlyCommandErrorMessage;
    public static String opOnlyCommandErrorMessage;

    public static HashMap<String, Integer> homePermissions;

    public static void updateConfig() {
        FileConfiguration config = LiteHomes.getPlugin().getConfig();
        config.get("teleport-delay");

        teleportDelay = config.getInt("teleport-delay");

        backupDatabase = config.getBoolean("backup-database-on-disable");
        cancelTeleportOnMove = config.getBoolean("cancel-teleport-on-move");
        cancelTeleportOnDamage = config.getBoolean("cancel-teleport-on-damage");

        setPrefixToMessages = config.getBoolean("messages.set-prefix");
        setHomeAlertMessage = colorFormat(config.getString("messages.set-home-alert"));
        teleportDelayMessage = colorFormat(config.getString("messages.teleport-delay"));
        setHomeSuccessMessage = colorFormat(config.getString("messages.set-home-success"));
        setHomeErrorMessage = colorFormat(config.getString("messages.set-home-error"));
        delHomeSuccessMessage = colorFormat(config.getString("messages.del-home-success"));
        maxHomesMessage = colorFormat(config.getString("messages.max-homes"));
        homeNotExistsMessage = colorFormat(config.getString("messages.home-not-exists"));
        homeNotSpecifiedMessage = colorFormat(config.getString("messages.home-not-specified"));
        teleportRequestPendingMessage = colorFormat(config.getString("messages.teleport-request-pending"));
        teleportCancelledOnMoveMessage = colorFormat(config.getString("messages.teleport-cancelled-on-move"));
        teleportCancelledOnDamageMessage = colorFormat(config.getString("messages.teleport-cancelled-on-damage"));

        sendCommandUsageOnNotEnoughArgs = config.getBoolean("messages.send-command-usage-on-missing-args");
        notEnoughArgsErrorMessage = colorFormat(config.getString("messages.not-enough-args-error-message"));
        permissionMissingErrorMessage = colorFormat(config.getString("messages.permission-missing-error-message"));
        playerOnlyCommandErrorMessage = colorFormat(config.getString("messages.player-only-command-error-message"));
        opOnlyCommandErrorMessage = colorFormat(config.getString("messages.op-only-command-error-message"));

        numberOfRowsInGui = config.getInt("gui.number-of-rows");
        showHomesByDimension = config.getBoolean("gui.show-homes-by-dimension");
        showEmptyHomes = config.getBoolean("gui.show-empty-homes");
        showCoordinatesInGui = config.getBoolean("gui.show-coordinates");
        homeScreenTitle = colorFormat(config.getString("gui.home-screen-title"));
        delHomeScreenTitle = colorFormat(config.getString("gui.del-home-screen-title"));
        emptyHomeItemName = colorFormat(config.getString("gui.empty-home-item-name"));
        deleteHomesItemName = colorFormat(config.getString("gui.delete-homes-item-name"));
        previousPageItemName = colorFormat(config.getString("gui.previous-page-item-name"));
        nextPageItemName = colorFormat(config.getString("gui.next-page-item-name"));
        homeBlock = config.getString("gui.home-block");
        emptyHomesBlock = config.getString("gui.empty-homes-block");
        deleteHomesBlock = config.getString("gui.delete-homes-block");
        pageChangeBlock = config.getString("gui.page-change-block");

        if (numberOfRowsInGui < 1)
            numberOfRowsInGui = 1;
        if (numberOfRowsInGui > 5)
            numberOfRowsInGui = 5;

        homePermissions = new HashMap<>();
        for (String key : Objects.requireNonNull(config.getConfigurationSection("max-homes")).getKeys(false)) {
            homePermissions.put("litehomes." + key, config.getInt("max-homes." + key));
        }
    }

    private static String colorFormat(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
