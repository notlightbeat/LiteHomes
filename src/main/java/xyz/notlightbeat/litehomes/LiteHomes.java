package xyz.notlightbeat.litehomes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.notlightbeat.litehomes.command.*;
import xyz.notlightbeat.litehomes.database.Database;
import xyz.notlightbeat.litehomes.manager.DatabaseManager;
import xyz.notlightbeat.litehomes.database.SQL;
import xyz.notlightbeat.litehomes.listener.InventoryListener;
import xyz.notlightbeat.litehomes.listener.TeleportCancelListener;
import xyz.notlightbeat.litehomes.manager.ConfigManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Calendar;

public final class LiteHomes extends JavaPlugin {

    public final String prefix = ChatColor.translateAlternateColorCodes('&', "[&b&lLite&9&lHomes&r]");

    public void onEnable() {
        init();
    }

    public void onDisable() {
        disable();
    }

    private void init() {
        saveDefaultConfig();
        ConfigManager.updateConfig();

        //bStats initialization
        Metrics metrics = new Metrics(this, 11859);

        //Initialize database, events and commands
        try {
            initializeDatabase();
        } catch (Exception e) {
            getLogger().severe("LiteHomes could not initialize the database. Printing stack trace and disabling plugin...");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        registerEvents();
        registerCommands();

        getLogger().info(ChatColor.AQUA + "Plugin enabled");
    }

    private void disable() {
        HandlerList.unregisterAll(this);
        try {
            DatabaseManager.db.closeConnection();
            if (ConfigManager.backupDatabase && DatabaseManager.db.system == Database.System.SQLITE) {
                backupHomes();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getLogger().info(ChatColor.AQUA + "Plugin disabled");
    }

    private void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new TeleportCancelListener(), this);
    }

    private void registerCommands() {
        getCommand("home").setExecutor(new HomeCommand());
        getCommand("homes").setExecutor(new HomesCommand());
        getCommand("sethome").setExecutor(new SetHomeCommand());
        getCommand("delhome").setExecutor(new DelHomeCommand());
        getCommand("lhreload").setExecutor(new ReloadCommand());
    }

    public void initializeDatabase() throws SQLException, IOException {
        //Initialize database manager
        DatabaseManager.db = new SQL(getDataFolder().getCanonicalPath() + "/litehomes.db");

        DatabaseManager.db.create("home", new String[]{
                "id int PRIMARY KEY",
                "homename text NOT NULL",
                "player text NOT NULL",
                "world text NOT NULL",
                "worldtype int NOT NULL",
                "x int NOT NULL",
                "y int NOT NULL",
                "z int NOT NULL",
        });
    }

    public void reloadConfiguration() {
        reloadConfig();
        ConfigManager.updateConfig();
    }

    public void backupHomes() {
        Calendar calendar = Calendar.getInstance();
        String dateString = calendar.get(Calendar.MONTH) + "-" +
                            calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                            calendar.get(Calendar.YEAR) + "-" +
                            calendar.get(Calendar.HOUR_OF_DAY) + "-" +
                            calendar.get(Calendar.MINUTE) + "-" +
                            calendar.get(Calendar.SECOND);
        new Thread(() -> {
            try {
                new File(getDataFolder().getCanonicalPath() + "/backups/").mkdir();
                Files.copy(new File(getDataFolder().getCanonicalPath() + "/litehomes.db").toPath(),
                        new File(getDataFolder().getCanonicalPath() + "/backups/litehomes-" + dateString + ".db.bak").toPath());
            } catch (Throwable e) {
                getLogger().severe("The database file could not be backed up");
                e.printStackTrace();
            }
        }, "Database Backup Thread").start();
    }

    public static LiteHomes getPlugin() {
        return getPlugin(LiteHomes.class);
    }

}
