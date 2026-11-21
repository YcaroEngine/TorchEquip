package com.oftalafe.offhand;

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

public class OffhandPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    // 1. Equipa apertando F (Java) ou troca de mão (Bedrock/Geyser)
    @EventHandler
    public void aoTrocarDeMao(PlayerSwapHandItemsEvent event) {
        ItemStack item = event.getOffHandItem();
        if (item != null && item.getType() != Material.AIR) {
            Player player = event.getPlayer();
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
        }
    }

    // 2. Força a troca no slot do escudo (Slot 40) pelo menu no Mobile (Bedrock/Geyser) e Java
    @EventHandler(priority = EventPriority.HIGHEST)
    public void aoMoverNoInventario(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getClickedInventory() != null
                && event.getClickedInventory().getType() == InventoryType.PLAYER
                && event.getSlot() == 40) {

            ItemStack noCursor = event.getCursor();
            ItemStack noEscudo = player.getInventory().getItemInOffHand();

            // Se o jogador tocou no slot do escudo segurando um item no menu (Mobile/Java)
            if (noCursor != null && noCursor.getType() != Material.AIR) {
                event.setCancelled(true);
                player.getInventory().setItemInOffHand(noCursor.clone());
                player.setItemOnCursor(noEscudo.clone());
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
            }
        }
    }
}

