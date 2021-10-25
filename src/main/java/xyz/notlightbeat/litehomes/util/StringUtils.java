package xyz.notlightbeat.litehomes.util;

import org.bukkit.ChatColor;
import xyz.notlightbeat.litehomes.LiteHomes;
import xyz.notlightbeat.litehomes.manager.ConfigManager;

/**
 * Class containing useful String methods commonly used in plugins
 */
public class StringUtils {

    /**
     * Prefixes the given string, if applicable.
     * If the prefix is disabled, the method returns the same String.
     *
     * @param  message string to apply the prefix to
     * @return         the prefixed message, if applicable
     */
    public static String setPluginPrefix(String message) {
        if (ConfigManager.setPrefixToMessages)
            return LiteHomes.getPlugin().prefix + " " + message;
        else
            return message;
    }

    /**
     * Formats the given string using Minecraft's color formatting codes.
     *
     * @param  message string to format
     * @return         the formatted message
     */
    public static String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Same as <code>formatMessage(String message)</code> but prefixing
     * the message with the plugin's prefix, if applicable.
     *
     * @param  message string to format and apply the string to
     * @return         the formatted message
     * @see            #formatMessage(java.lang.String)
     */
    public static String formatPrefixMessage(String message) {
            return ChatColor.translateAlternateColorCodes('&', setPluginPrefix(message));
    }

    /**
     * Counts the amount of characters that appear in the given string.
     *
     * @param string    the String to count characters from
     * @param character the character to count
     * @return          the occurrences of the character in the string
     */
    public static long countCharOccurrences(String string, char character) {
        return string.chars().filter(ch -> ch == character).count();
    }

}
