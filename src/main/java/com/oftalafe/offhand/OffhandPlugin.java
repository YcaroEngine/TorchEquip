package com.oftalafe.offhand;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class OffhandPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void aoTrocarDeMao(PlayerSwapHandItemsEvent event) {
        ItemStack item = event.getOffHandItem();
        if (item != null && item.getType() != Material.AIR) {
            Player player = event.getPlayer();
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
        }
    }

    @EventHandler
    public void aoMoverNoInventario(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getClickedInventory() != null
                && event.getClickedInventory().getType() == InventoryType.PLAYER
                && event.getSlot() == 40) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                ItemStack offhand = player.getInventory().getItemInOffHand();
                if (offhand.getType() != Material.AIR) {
                    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
                }
            }, 1L);
        }
    }
}
