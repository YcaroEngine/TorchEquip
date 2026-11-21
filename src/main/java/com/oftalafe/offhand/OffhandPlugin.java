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

    private boolean ehTochaOuLampiao(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        return switch (item.getType()) {
            case TORCH, SOUL_TORCH, REDSTONE_TORCH, COPPER_TORCH,
                 LANTERN, SOUL_LANTERN, COPPER_LANTERN -> true;
            default -> false;
        };
    }

    // 1. Equipa apenas tochas e lampiões apertando F (Java) ou troca de mão (Bedrock/Geyser)
    @EventHandler
    public void aoTrocarDeMao(PlayerSwapHandItemsEvent event) {
        if (ehTochaOuLampiao(event.getOffHandItem())) {
            Player player = event.getPlayer();
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
        }
    }

    // 2. Permite equipar e desequipar tochas e lampiões pelo menu no Mobile (Bedrock/Geyser) e Java
    @EventHandler(priority = EventPriority.HIGHEST)
    public void aoMoverNoInventario(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getClickedInventory() != null
                && event.getClickedInventory().getType() == InventoryType.PLAYER
                && event.getSlot() == 40) {

            ItemStack noCursor = event.getCursor();
            ItemStack noEscudo = player.getInventory().getItemInOffHand();

            // Equipar tocha/lampião no slot vazio ou trocar por outro
            if (ehTochaOuLampiao(noCursor)) {
                event.setCancelled(true);
                player.getInventory().setItemInOffHand(noCursor.clone());
                player.setItemOnCursor(noEscudo.clone());
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
                return;
            }

            // Desequipar a tocha/lampião ao tocar no slot do escudo com o cursor vazio no Mobile
            if ((noCursor == null || noCursor.getType() == Material.AIR) && ehTochaOuLampiao(noEscudo)) {
                event.setCancelled(true);
                player.setItemOnCursor(noEscudo.clone());
                player.getInventory().setItemInOffHand(null);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 0.9f);
            }
        }
    }
}
