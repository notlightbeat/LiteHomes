package xyz.notlightbeat.litehomes.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.notlightbeat.litehomes.manager.ConfigManager;
import xyz.notlightbeat.litehomes.manager.DatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils class for wrapping database interaction in the context of home handling.
 */
public class HomeUtils {

    /**
     * Checks if a certain player's home exists.
     *
     * @param player   the player to check
     * @param homeName the name of the home to check
     * @return         true if the home already exists; false otherwise
     */
    public static boolean homeExists(OfflinePlayer player, String homeName) {
        try {
            ResultSet homes = DatabaseManager.db.select("homename, player", "home", "WHERE homename = ? AND player = ?", new String[]{homeName, getPlayerString(player)});
            if (homes != null) {
                //Returns true if there is at least 1 element in the row
                return homes.next();
            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the amount of homes for a given player
     *
     * @param player the player to apply the count onto
     * @return       the amount of homes for the given player
     */
    public static int getHomeCount(OfflinePlayer player) {
        try (ResultSet homes = DatabaseManager.db.select("player", "home", "WHERE player = ?", new String[]{getPlayerString(player)})) {
            if (homes != null) {
                int count = 0;
                while (homes.next()) {
                    ++count;
                }
                return count;
            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Sets a home for a given online player
     *
     * @param player   the player to set the home to
     * @param homeName the name of the home
     */
    public static void setHome(Player player, String homeName) {
        final Location location = player.getLocation();
        final int worldType;
        switch (location.getWorld().getEnvironment()) {
            case NORMAL:
                worldType = 0;
                break;
            case NETHER:
                worldType = 1;
                break;
            case THE_END:
                worldType = 2;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + location.getWorld().getEnvironment());
        }
        try {
            DatabaseManager.db.insert("home",
                    new String[]{"homename", "player", "world", "worldtype", "x", "y", "z"},
                    new String[]{homeName, getPlayerString(player), location.getWorld().getName(), Integer.toString(worldType), Integer.toString(location.getBlockX()), Integer.toString(location.getBlockY()), Integer.toString(location.getBlockZ())});
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get an array of home names for a given player, in order.
     *
     * @param player the player to list homes from
     * @return       the array of home names
     */
    public static String[] getHomeNames(OfflinePlayer player) {
        List<String> homeList = new ArrayList<>();
        try (ResultSet homes = DatabaseManager.db.select("homename", "home", "WHERE player = ?", new String[]{getPlayerString(player)})) {
            while (homes.next()) {
                homeList.add(homes.getString("homename"));
            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        String[] result = new String[homeList.size()];
        result = homeList.toArray(result);
        return result;
    }

    /**
     * Get a coordinate array for the home of the given player.
     * The array has the form <code>{x, y, z}</code> and will always have a length of 3.
     *
     * @param player   the player to get the home from
     * @param homeName the name of the home
     * @return         the coordinate array for the given home and player
     */
    public static int[] getHomeCoords(OfflinePlayer player, String homeName) {
        int[] coords = new int[3];
        try {
            ResultSet homes = DatabaseManager.db.select("x, y, z", "home", "WHERE homename = ? AND player = ?", new String[]{homeName, getPlayerString(player)});
            if (homes.next()) {
                coords[0] = homes.getInt("x");
                coords[1] = homes.getInt("y");
                coords[2] = homes.getInt("z");
            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return coords;
    }

    /**
     * Gets the coordinate array for all homes of the given player.
     * The coordinates will be in the same order as the <code>String[] getHomeNames(OfflinePlayer player)</code> method.
     * The array has the form
     * <code>
     *     {x, y, z
     *      x, y, z
     *      x, y, z}
     * </code>
     * and will always have a length of 3 times the amount of homes for the given player.
     *
     * @param player the player to get the coordinates from
     * @return       the coordinate array for all the player's homes
     */
    public static int[] getAllPlayerCoords(OfflinePlayer player) {
        List<Integer> coords = new ArrayList<>();
        try {
            ResultSet homes = DatabaseManager.db.select("x, y, z", "home", "WHERE player = ?", new String[]{getPlayerString(player)});
            while (homes.next()) {
                coords.add(homes.getInt("x"));
                coords.add(homes.getInt("y"));
                coords.add(homes.getInt("z"));
            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return coords.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Get the <code>World</code> for the given home and player.
     *
     * @param player   the player to check from
     * @param homeName the name of the home
     * @return         the World object for the given home; null if the World is not found
     */
    public static World getHomeWorld(OfflinePlayer player, String homeName) {
        try {
            ResultSet homes = DatabaseManager.db.select("world", "home", "WHERE homename = ? AND player = ?", new String[]{homeName, getPlayerString(player)});
            if (homes.next()) {
                return Bukkit.getWorld(homes.getString("world"));
            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deletes the home of the given player.
     *
     * @param player   the player to delete the home from
     * @param homeName the name of the home to delete
     */
    public static void delHome(OfflinePlayer player, String homeName) {
        try {
            DatabaseManager.db.delete("home", "WHERE homename = ? AND player = ?", new String[]{homeName, getPlayerString(player)});
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the home dimension for the given home and player.
     * 0 for Overworld, 1 for the Nether, 2 for The End
     *
     * @param player   the player to get the home from
     * @param homeName the name of the home
     * @return         the dimension the home is in
     */
    public static int getHomeDimension(OfflinePlayer player, String homeName) {
        try {
            ResultSet homes = DatabaseManager.db.select("worldtype", "home", "WHERE homename = ? AND player = ?", new String[]{homeName, getPlayerString(player)});
            if (homes.next()) {
                return homes.getInt("worldtype");
            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets the maximum amount of homes for the given online player.
     *
     * @param player the player to check
     * @return       the maximum amount of homes
     */
    public static int getMaxHomes(Player player) {
        int maxHomeAmount = 0;
        for (String permission : ConfigManager.homePermissions.keySet())
            if (player.hasPermission(permission)) {
                int max = ConfigManager.homePermissions.get(permission);
                if (max > maxHomeAmount)
                    maxHomeAmount = max;
            }
        return maxHomeAmount;
    }

    private static String getPlayerString(OfflinePlayer player) {
        return player.getUniqueId().toString();
    }

}
