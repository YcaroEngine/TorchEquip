package org.offhand.core;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TorchEquipPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private boolean isLightSource(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        return switch (item.getType()) {
            case TORCH, SOUL_TORCH, REDSTONE_TORCH, COPPER_TORCH,
                 LANTERN, SOUL_LANTERN, COPPER_LANTERN -> true;
            default -> false;
        };
    }

    @EventHandler
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        if (isLightSource(event.getOffHandItem())) {
            Player player = event.getPlayer();
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getClickedInventory() != null
                && event.getClickedInventory().getType() == InventoryType.PLAYER
                && event.getSlot() == 40) {

            ItemStack cursor = event.getCursor();
            ItemStack offhand = player.getInventory().getItemInOffHand();

            if (isLightSource(cursor)) {
                event.setCancelled(true);
                player.getInventory().setItemInOffHand(cursor.clone());
                player.setItemOnCursor(offhand.clone());
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
                return;
            }

            if ((cursor == null || cursor.getType() == Material.AIR) && isLightSource(offhand)) {
                event.setCancelled(true);
                player.setItemOnCursor(offhand.clone());
                player.getInventory().setItemInOffHand(null);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 0.9f);
            }
        }
    }
}

