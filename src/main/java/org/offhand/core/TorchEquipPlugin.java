package org.offhand.core;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TorchEquipPlugin extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {

    private final Set<Material> allowedItems = new HashSet<>();
    private boolean enabledMode;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        saveDefaultConfig();
        loadSettings();

        Bukkit.getPluginManager().registerEvents(this, this);

        var cmd = getCommand("torchequip");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
    }

    private void loadSettings() {
        reloadConfig();
        String mode = getConfig().getString("mode", "on").trim().toLowerCase();
        enabledMode = mode.equals("on") || mode.equals("true");

        allowedItems.clear();
        for (String name : getConfig().getStringList("items")) {
            addMaterial(name);
        }
        for (String name : getConfig().getStringList("extra-items")) {
            addMaterial(name);
        }
    }

    private void addMaterial(String name) {
        if (name == null || name.isBlank()) return;
        Material mat = Material.matchMaterial(name.trim().toUpperCase());
        if (mat != null) {
            allowedItems.add(mat);
        }
    }

    private boolean isAllowed(ItemStack item) {
        if (!enabledMode) return false;
        if (item == null || item.getType() == Material.AIR) return false;
        return allowedItems.contains(item.getType());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            saveDefaultConfig();
            loadSettings();
            sender.sendMessage("[TorchEquip] Configuration reloaded! (mode: " + (enabledMode ? "on" : "off") + ")");
            return true;
        }

        sender.sendMessage("[TorchEquip] Usage: torchequip reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof ConsoleCommandSender && args.length == 1 && "reload".startsWith(args[0].toLowerCase())) {
            return List.of("reload");
        }
        return Collections.emptyList();
    }

    // 1. Tecla F (Java) ou troca de mao (Bedrock/Geyser)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        if (!enabledMode) return;
        ItemStack goingToOffhand = event.getOffHandItem();
        ItemStack goingToMain = event.getMainHandItem();

        if (isAllowed(goingToOffhand) || isAllowed(goingToMain)) {
            event.setCancelled(false);
            Player player = event.getPlayer();
            Bukkit.getScheduler().runTaskLater(this, () -> {
                player.updateInventory();
                if (isAllowed(player.getInventory().getItemInOffHand())) {
                    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
                }
            }, 1L);
        }
    }

    // 2. Menu do Inventario no Sobrevivencia (Java & Bedrock/Mobile)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!enabledMode) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event instanceof InventoryCreativeEvent) return;
        if (event.getView().getTopInventory().getType() != InventoryType.CRAFTING) return;

        int rawSlot = event.getRawSlot();
        ItemStack cursor = event.getCursor();
        ItemStack clicked = event.getCurrentItem();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)
                && rawSlot != 45
                && isAllowed(clicked)
                && (offhand == null || offhand.getType() == Material.AIR)) {
            event.setCancelled(true);
            player.getInventory().setItemInOffHand(clicked.clone());
            event.setCurrentItem(null);
            player.updateInventory();
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
            return;
        }

        if (rawSlot == 45) {
            if (isAllowed(cursor)) {
                event.setCancelled(true);
                player.getInventory().setItemInOffHand(cursor.clone());
                player.setItemOnCursor(offhand.clone());
                player.updateInventory();
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
                return;
            }

            if ((cursor == null || cursor.getType() == Material.AIR) && isAllowed(offhand)) {
                event.setCancelled(true);
                player.setItemOnCursor(offhand.clone());
                player.getInventory().setItemInOffHand(null);
                player.updateInventory();
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 0.9f);
            }
        }
    }

    // 3. Arrastar no inventario (Java)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!enabledMode) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTopInventory().getType() != InventoryType.CRAFTING) return;

        if (event.getRawSlots().contains(45) && isAllowed(event.getOldCursor())) {
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

    // 4. Menu do Inventario no Modo Criativo (Java & Bedrock/Mobile)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onCreativeClick(InventoryCreativeEvent event) {
        if (!enabledMode) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        int rawSlot = event.getRawSlot();
        ItemStack cursor = event.getCursor();
        ItemStack clicked = event.getCurrentItem();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)
                && rawSlot != 45
                && isAllowed(clicked)
                && (offhand == null || offhand.getType() == Material.AIR)) {
            event.setCancelled(true);
            player.getInventory().setItemInOffHand(clicked.clone());
            event.setCurrentItem(null);
            player.updateInventory();
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
            return;
        }

        if (rawSlot == 45) {
            if (isAllowed(cursor)) {
                event.setCancelled(true);
                player.getInventory().setItemInOffHand(cursor.clone());
                player.setItemOnCursor(offhand.clone());
                player.updateInventory();
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.1f);
                return;
            }

            if ((cursor == null || cursor.getType() == Material.AIR) && isAllowed(offhand)) {
                event.setCancelled(true);
                player.setItemOnCursor(offhand.clone());
                player.getInventory().setItemInOffHand(null);
                player.updateInventory();
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 0.9f);
            }
        }
    }
}
