package xyz.notlightbeat.litehomes.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface InventoryGui extends InventoryHolder {

    /**
     * Method to call whenever a gui object is instantiated.
     * Should be called from the object's constructor.
     */
    void initializeItems();

    /**
     * This method opens the inventory to the respective HumanEntity.
     *
     * @param humanEntity human entity (player) to open the inventory to
     * @see   HumanEntity
     */
    void openInventory(HumanEntity humanEntity);

    /**
     * Method that will be called whenever the player clicks an item in the gui inventory.
     * Should be called from an InventoryClickEvent listener method.
     *
     * @param player    the player that made the action
     * @param itemIndex the clicked item's index
     * @param itemStack the ItemStack for the clicked item
     * @see   Player
     * @see   ItemStack
     */
    void onGUIClick(Player player, int itemIndex, ItemStack itemStack);

}
