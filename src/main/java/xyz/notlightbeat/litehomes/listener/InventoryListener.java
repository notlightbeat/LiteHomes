package xyz.notlightbeat.litehomes.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import xyz.notlightbeat.litehomes.gui.InventoryGui;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof InventoryGui) {
            event.setCancelled(true);
            InventoryGui gui = (InventoryGui)event.getInventory().getHolder();
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            gui.onGUIClick((Player)event.getWhoClicked(), event.getRawSlot(), event.getCurrentItem());
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof InventoryGui) {
            e.setCancelled(true);
        }

    }

}
