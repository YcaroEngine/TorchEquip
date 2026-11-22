package org.offhand.core;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
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

    // 1. Tecla F (Java) ou botão de troca de mão (Bedrock/Geyser) - Sobrevivência e Criativo
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        ItemStack goingToOffhand = event.getOffHandItem();
        ItemStack goingToMain = event.getMainHandItem();

        if (isLightSource(goingToOffhand) || isLightSource(goingToMain)) {
            event.setCancelled(false);
            Player player = event.getPlayer();
            Bukkit.getScheduler().runTaskLater(this, () -> {
                player.updateInventory();
                if (isLightSource(player.getInventory().getItemInOffHand())) {
                    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
                }
            }, 1L);
        }
    }

    // 2. Menu do Inventário no Sobrevivência (Java & Bedrock/Mobile)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event instanceof InventoryCreativeEvent) return;
        if (event.getView().getTopInventory().getType() != InventoryType.CRAFTING) return;

        int rawSlot = event.getRawSlot();
        ItemStack cursor = event.getCursor();
        ItemStack clicked = event.getCurrentItem();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        // Toque duplo / Shift-Click na tocha ou lampião para ir direto ao slot do escudo
        if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)
                && rawSlot != 45
                && isLightSource(clicked)
                && (offhand == null || offhand.getType() == Material.AIR)) {
            event.setCancelled(true);
            player.getInventory().setItemInOffHand(clicked.clone());
            event.setCurrentItem(null);
            player.updateInventory();
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
            return;
        }

        // Clique ou toque direto no slot do escudo (RawSlot 45)
        if (rawSlot == 45) {
            if (isLightSource(cursor)) {
                event.setCancelled(true);
                player.getInventory().setItemInOffHand(cursor.clone());
                player.setItemOnCursor(offhand.clone());
                player.updateInventory();
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
                return;
            }

            if ((cursor == null || cursor.getType() == Material.AIR) && isLightSource(offhand)) {
                event.setCancelled(true);
                player.setItemOnCursor(offhand.clone());
                player.getInventory().setItemInOffHand(null);
                player.updateInventory();
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 0.9f);
            }
        }
    }

    // 3. Arrastar no inventário (Java) para o slot do escudo
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTopInventory().getType() != InventoryType.CRAFTING) return;

        if (event.getRawSlots().contains(45) && isLightSource(event.getOldCursor())) {
            event.setCancelled(true);
            ItemStack offhand = player.getInventory().getItemInOffHand();
            player.getInventory().setItemInOffHand(event.getOldCursor().clone());
            Bukkit.getScheduler().runTaskLater(this, () -> {
                player.setItemOnCursor(offhand.clone());
                player.updateInventory();
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
            }, 1L);
        }
    }

    // 4. Menu do Inventário no Modo Criativo (Java & Bedrock/Mobile)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onCreativeClick(InventoryCreativeEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        int rawSlot = event.getRawSlot();
        ItemStack cursor = event.getCursor();
        ItemStack clicked = event.getCurrentItem();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        // Shift-Click / Toque duplo no Criativo
        if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)
                && rawSlot != 45
                && isLightSource(clicked)
                && (offhand == null || offhand.getType() == Material.AIR)) {
            event.setCancelled(true);
            player.getInventory().setItemInOffHand(clicked.clone());
            event.setCurrentItem(null);
            player.updateInventory();
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
            return;
        }

        // Clique ou toque direto no slot do escudo (RawSlot 45) no Criativo
        if (rawSlot == 45) {
            if (isLightSource(cursor)) {
                event.setCancelled(true);
                player.getInventory().setItemInOffHand(cursor.clone());
                player.setItemOnCursor(offhand.clone());
                player.updateInventory();
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
                return;
            }

            if ((cursor == null || cursor.getType() == Material.AIR) && isLightSource(offhand)) {
                event.setCancelled(true);
                player.setItemOnCursor(offhand.clone());
                player.getInventory().setItemInOffHand(null);
                player.updateInventory();
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 0.9f);
            }
        }
    }
}
