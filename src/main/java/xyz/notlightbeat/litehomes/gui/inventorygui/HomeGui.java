package xyz.notlightbeat.litehomes.gui.inventorygui;

import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.notlightbeat.litehomes.LiteHomes;
import xyz.notlightbeat.litehomes.gui.InventoryGui;
import xyz.notlightbeat.litehomes.listener.TeleportCancelListener;
import xyz.notlightbeat.litehomes.manager.ConfigManager;
import xyz.notlightbeat.litehomes.util.HomeUtils;
import xyz.notlightbeat.litehomes.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeGui implements InventoryGui {

    private final Inventory inv;
    private final Player player;
    private final OfflinePlayer objectivePlayer;
    private final int pageIndex;

    public HomeGui(Player player, int pageIndex) {
        this.player = player;
        this.objectivePlayer = player;
        this.pageIndex = pageIndex;
        this.inv = Bukkit.createInventory(this, (ConfigManager.numberOfRowsInGui + 1) * 9, ConfigManager.homeScreenTitle);
        initializeItems();
    }

    public HomeGui(Player player, OfflinePlayer objectivePlayer, int pageIndex) {
        this.player = player;
        this.objectivePlayer = objectivePlayer;
        this.pageIndex = pageIndex;
        if (player.getName().equals(objectivePlayer.getName())) {
            this.inv = Bukkit.createInventory(this, (ConfigManager.numberOfRowsInGui + 1) * 9, ConfigManager.homeScreenTitle);
        } else {
            this.inv = Bukkit.createInventory(this, (ConfigManager.numberOfRowsInGui + 1) * 9, ChatColor.translateAlternateColorCodes('&', "&1" + objectivePlayer.getName() + "'s &9homes"));
        }
        initializeItems();
    }

    public Inventory getInventory() {
        return inv;
    }

    public void initializeItems() {
        String[] playerHomes = HomeUtils.getHomeNames(objectivePlayer);
        int[] homeCoords = HomeUtils.getAllPlayerCoords(objectivePlayer);
        int placedHomes = 0;
        boolean lastPage = Math.ceil(playerHomes.length / (ConfigManager.numberOfRowsInGui * 9.0)) <= pageIndex + 1;

        for (int i = ConfigManager.numberOfRowsInGui * 9 * pageIndex; i < Math.min(playerHomes.length, ConfigManager.numberOfRowsInGui * 9 + ConfigManager.numberOfRowsInGui * 9 * pageIndex); i++) {
            inv.addItem(createItem(HomeUtils.getHomeDimension(objectivePlayer, playerHomes[i]), playerHomes[i], new int[]{homeCoords[i * 3], homeCoords[i * 3 + 1], homeCoords[i * 3 + 2]}));
            ++placedHomes;
        }

        if (player.getName().equals(objectivePlayer.getName()) && ConfigManager.showEmptyHomes && lastPage) {

            int maxHomes = HomeUtils.getMaxHomes(player);
            for (int i = 0; i < Math.min(maxHomes - playerHomes.length, ConfigManager.numberOfRowsInGui * 9 - placedHomes); i++)
                inv.addItem(createItem(3, ConfigManager.emptyHomeItemName.replaceAll("%index%", "" + i), new int[]{0, 0, 0}));

        }

        inv.setItem((ConfigManager.numberOfRowsInGui + 1) * 9 - 5, createItem(4, ConfigManager.deleteHomesItemName, new int[]{0, 0, 0}));
        if (pageIndex != 0)
            inv.setItem((ConfigManager.numberOfRowsInGui + 1) * 9 - 9, createItem(5, ConfigManager.previousPageItemName, new int[]{0, 0, 0}));
        if (!lastPage)
            inv.setItem((ConfigManager.numberOfRowsInGui + 1) * 9 - 1, createItem(5, ConfigManager.nextPageItemName, new int[]{0, 0, 0}));
    }

    private ItemStack createItem(int worldType, String name, int[] coords) {
        Material material;

        switch(worldType) {
            case 0:
                material = ConfigManager.showHomesByDimension ? Material.GRASS : Material.getMaterial(ConfigManager.homeBlock);
                break;
            case 1:
                material = ConfigManager.showHomesByDimension ? Material.NETHERRACK : Material.getMaterial(ConfigManager.homeBlock);
                break;
            case 2:
                material = ConfigManager.showHomesByDimension ? Material.ENDER_STONE : Material.getMaterial(ConfigManager.homeBlock);
                break;
            case 3:
                material = Material.getMaterial(ConfigManager.emptyHomesBlock);
                break;
            case 4:
                material = Material.getMaterial(ConfigManager.deleteHomesBlock);
                break;
            case 5:
                material = Material.getMaterial(ConfigManager.pageChangeBlock);
                break;
            default:
                material = Material.AIR;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();

        if (worldType <= 2 && ConfigManager.showCoordinatesInGui) {
            lore.add("Coordinates:");
            lore.add(coords[0] + ", " + coords[1] + ", " + coords[2]);
            meta.setLore(lore);
        }

        item.setItemMeta(meta);
        return item;
    }

    public void openInventory(HumanEntity humanEntity) {
        humanEntity.openInventory(inv);
    }

    public void onGUIClick(Player player, int itemIndex, ItemStack itemStack) {
        if (itemStack.getType() == Material.getMaterial(ConfigManager.emptyHomesBlock)) {
            player.sendMessage(StringUtils.setPluginPrefix(ConfigManager.setHomeAlertMessage));
            player.closeInventory();
        } else if (itemIndex == (ConfigManager.numberOfRowsInGui + 1) * 9 - 5) {
            DelHomeGui gui = new DelHomeGui(player, objectivePlayer, pageIndex);
            player.closeInventory();
            player.openInventory(gui.getInventory());
        } else if (itemIndex == (ConfigManager.numberOfRowsInGui + 1) * 9 - 9) {
            HomeGui gui = new HomeGui(player, objectivePlayer, pageIndex - 1);
            player.openInventory(gui.getInventory());
        } else if (itemIndex == (ConfigManager.numberOfRowsInGui + 1) * 9 - 1) {
            HomeGui gui = new HomeGui(player, objectivePlayer, pageIndex + 1);
            player.openInventory(gui.getInventory());
        } else {
            int[] coordinates = HomeUtils.getHomeCoords(objectivePlayer, itemStack.getItemMeta().getDisplayName());

            if (ConfigManager.teleportDelay == 0 || player.hasPermission("litehomes.tpdelayoverride")) {
                player.teleport(new Location(HomeUtils.getHomeWorld(objectivePlayer, itemStack.getItemMeta().getDisplayName()), coordinates[0], coordinates[1], coordinates[2]));
            } else {
                if (TeleportCancelListener.teleports.contains(player.getUniqueId())) {
                    player.sendMessage(StringUtils.setPluginPrefix(ConfigManager.teleportRequestPendingMessage));
                } else {
                    TeleportCancelListener.teleports.add(player.getUniqueId());
                    Bukkit.getServer().getScheduler().runTaskLater(LiteHomes.getPlugin(), () -> {
                        if (TeleportCancelListener.teleports.contains(player.getUniqueId())) {
                            player.teleport(new Location(HomeUtils.getHomeWorld(objectivePlayer, itemStack.getItemMeta().getDisplayName()), coordinates[0], coordinates[1], coordinates[2]));
                            TeleportCancelListener.teleports.remove(player.getUniqueId());
                        }
                    }, (long)ConfigManager.teleportDelay * 20L);
                    player.sendMessage(StringUtils.setPluginPrefix(ConfigManager.teleportDelayMessage.replaceAll("%seconds%", String.valueOf(ConfigManager.teleportDelay))));
                }
            }
            player.closeInventory();
        }
    }

}