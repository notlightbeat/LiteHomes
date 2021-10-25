package xyz.notlightbeat.litehomes.gui.inventorygui;

import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.notlightbeat.litehomes.gui.InventoryGui;
import xyz.notlightbeat.litehomes.manager.ConfigManager;
import xyz.notlightbeat.litehomes.util.HomeUtils;
import xyz.notlightbeat.litehomes.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DelHomeGui implements InventoryGui {

    private final Inventory inv;
    private final OfflinePlayer objectivePlayer;
    private final int pageIndex;

    public DelHomeGui(Player player, OfflinePlayer objectivePlayer, int pageIndex) {
        this.objectivePlayer = objectivePlayer;
        this.pageIndex = pageIndex;
        if (player.getName().equals(objectivePlayer.getName()))
            inv = Bukkit.createInventory(this, (ConfigManager.numberOfRowsInGui + 1) * 9, ChatColor.translateAlternateColorCodes('&', ConfigManager.delHomeScreenTitle));
        else
            inv = Bukkit.createInventory(this, (ConfigManager.numberOfRowsInGui + 1) * 9, ChatColor.translateAlternateColorCodes('&', "&9Delete &1" + objectivePlayer.getName() + "'s &9homes"));

        initializeItems();
    }

    public Inventory getInventory() {
        return inv;
    }

    public void initializeItems() {
        String[] playerHomes = HomeUtils.getHomeNames(objectivePlayer);
        int[] homeCoords = HomeUtils.getAllPlayerCoords(objectivePlayer);
        boolean lastPage = Math.ceil(playerHomes.length / (ConfigManager.numberOfRowsInGui * 9.0)) <= pageIndex + 1;

        for (int i = ConfigManager.numberOfRowsInGui * 9 * pageIndex; i < Math.min(playerHomes.length, ConfigManager.numberOfRowsInGui * 9 + ConfigManager.numberOfRowsInGui * 9 * pageIndex); i++) {
            inv.addItem(createHomeItem(HomeUtils.getHomeDimension(objectivePlayer, playerHomes[i]), playerHomes[i], new int[]{homeCoords[i * 3], homeCoords[i * 3 + 1], homeCoords[i * 3 + 2]}));
        }

        if (pageIndex != 0)
            inv.setItem((ConfigManager.numberOfRowsInGui + 1) * 9 - 9, createHomeItem(5, ConfigManager.previousPageItemName, new int[]{0, 0, 0}));
        if (!lastPage)
            inv.setItem((ConfigManager.numberOfRowsInGui + 1) * 9 - 1, createHomeItem(5, ConfigManager.nextPageItemName, new int[]{0, 0, 0}));
    }

    private ItemStack createHomeItem(int worldType, String name, int[] coords) {
        Material material;

        switch (worldType) {
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
        if (itemIndex == (ConfigManager.numberOfRowsInGui + 1) * 9 - 9) {
            DelHomeGui gui = new DelHomeGui(player, objectivePlayer, pageIndex - 1);
            player.openInventory(gui.getInventory());
        } else if (itemIndex == (ConfigManager.numberOfRowsInGui + 1) * 9 - 1) {
            DelHomeGui gui = new DelHomeGui(player, objectivePlayer, pageIndex + 1);
            player.openInventory(gui.getInventory());
        } else {
            HomeUtils.delHome(objectivePlayer, itemStack.getItemMeta().getDisplayName());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', StringUtils.setPluginPrefix(ConfigManager.delHomeSuccessMessage)));
            DelHomeGui gui;
            if (pageIndex == 0 || Math.ceil(HomeUtils.getHomeCount(objectivePlayer) / (ConfigManager.numberOfRowsInGui * 9.0)) == pageIndex + 1) {
                gui = new DelHomeGui(player, objectivePlayer, pageIndex);
            } else {
                gui = new DelHomeGui(player, objectivePlayer, pageIndex - 1);
            }
            player.openInventory(gui.getInventory());
        }
    }

}