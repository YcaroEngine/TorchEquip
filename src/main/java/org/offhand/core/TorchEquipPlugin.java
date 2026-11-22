package org.offhand.core;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TorchEquipPlugin extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {

    private final Set<Material> allowedItems = new HashSet<>();
    private boolean autoGeneratePack;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        saveDefaultConfig();
        loadSettings();
        syncMcpack();

        Bukkit.getPluginManager().registerEvents(this, this);

        var cmd = getCommand("torchequip");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
    }

    private void loadSettings() {
        reloadConfig();
        autoGeneratePack = getConfig().getBoolean("auto-generate-geyser-pack", true);

        allowedItems.clear();
        for (String name : getConfig().getStringList("default-items")) {
            addMaterial(name);
        }

        // Lê qualquer linha abaixo de "extra-items:" contendo: New = NOME_ORIGINAL_MINECRAFT
        try {
            File configFile = new File(getDataFolder(), "config.yml");
            if (configFile.exists()) {
                List<String> lines = Files.readAllLines(configFile.toPath(), StandardCharsets.UTF_8);
                boolean inExtraSection = false;

                for (String rawLine : lines) {
                    String line = rawLine.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;

                    if (line.startsWith("extra-items:")) {
                        inExtraSection = true;
                        continue;
                    }

                    if (inExtraSection) {
                        int eqIndex = line.indexOf('=');
                        if (eqIndex != -1 && line.substring(0, eqIndex).trim().equalsIgnoreCase("New")) {
                            String itemName = line.substring(eqIndex + 1).trim();
                            addMaterial(itemName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            getLogger().warning("Error reading 'New =' entries under extra-items in config.yml: " + e.getMessage());
        }
    }

    private void addMaterial(String name) {
        if (name == null || name.isBlank()) return;
        String cleanName = name.trim();
        if (cleanName.toLowerCase().startsWith("minecraft:")) {
            cleanName = cleanName.substring("minecraft:".length());
        }
        Material mat = Material.matchMaterial(cleanName.toUpperCase());
        if (mat != null) {
            allowedItems.add(mat);
        }
    }

    private boolean isAllowed(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        return allowedItems.contains(item.getType());
    }

    // Se auto-generate-geyser-pack: true, apaga o .mcpack antigo e gera o novo atualizado
    private void syncMcpack() {
        if (!autoGeneratePack) return;

        try {
            File localPack = new File(getDataFolder(), "TorchEquip.mcpack");
            if (localPack.exists()) {
                localPack.delete();
            }
            writeMcpack(localPack);

            File pluginsDir = getDataFolder().getParentFile();
            if (pluginsDir == null) return;

            File geyserDir = new File(pluginsDir, "Geyser-Spigot");
            if (geyserDir.exists() && geyserDir.isDirectory()) {
                File geyserPacks = new File(geyserDir, "packs");
                if (!geyserPacks.exists()) {
                    geyserPacks.mkdirs();
                }
                File targetPack = new File(geyserPacks, "TorchEquip.mcpack");
                if (targetPack.exists()) {
                    targetPack.delete();
                }
                writeMcpack(targetPack);
                getLogger().info("Deleted old pack and generated updated TorchEquip.mcpack (" + allowedItems.size() + " items) in plugins/Geyser-Spigot/packs/");
            }
        } catch (Exception e) {
            getLogger().warning("Failed to regenerate TorchEquip.mcpack: " + e.getMessage());
        }
    }
    private void writeMcpack(File targetFile) throws Exception {
        String v1 = "[" + 1 + ", " + 0 + ", " + 0 + "]";
        String vMin = "[" + 1 + ", " + 21 + ", " + 0 + "]";

        String manifestJson = """
                {
                  "format_version": 2,
                  "header": {
                    "name": "TorchEquip Off-Hand",
                    "description": "Enables equipping default torches, lanterns, and extra items in the off-hand.",
                    "uuid": "8f3a1c20-7b4e-4d9a-9c12-45e8a1b2c3d4",
                    "version": %s,
                    "min_engine_version": %s
                  },
                  "modules": [
                    {
                      "type": "data",
                      "uuid": "3e9b7d10-2a6f-4c8b-8e31-99a0b1c2d3e4",
                      "version": %s
                    }
                  ]
                }
                """.formatted(v1, vMin, v1);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(targetFile))) {
            zos.putNextEntry(new ZipEntry("manifest.json"));
            zos.write(manifestJson.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            for (Material mat : allowedItems) {
                String id = mat.getKey().getKey();
                String itemJson = """
                        {
                          "format_version": "1.21.0",
                          "minecraft:item": {
                            "description": {
                              "identifier": "minecraft:%s"
                            },
                            "components": {
                              "minecraft:allow_off_hand": true
                            }
                          }
                        }
                        """.formatted(id);

                zos.putNextEntry(new ZipEntry("items/" + id + ".json"));
                zos.write(itemJson.getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
        }
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
            syncMcpack();
            sender.sendMessage("[TorchEquip] Reloaded! Allowed items: " + allowedItems.size() + " (auto-generate-geyser-pack: " + autoGeneratePack + ")");
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

    // 1. Tecla F (Java & Bedrock)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        if (isAllowed(event.getOffHandItem()) || isAllowed(event.getMainHandItem())) {
            event.setCancelled(false);
            Player player = event.getPlayer();
            Bukkit.getScheduler().runTaskLater(this, player::updateInventory, 1L);
        }
    }

    // 2. Menu do Inventário - Pegou e moveu para o slot do escudo (Java & Bedrock)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTopInventory().getType() != InventoryType.CRAFTING) return;
        if (event.getRawSlot() != 45) return;

        ItemStack cursor = event.getCursor();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        if (isAllowed(cursor)) {
            event.setCancelled(true);
            player.getInventory().setItemInOffHand(cursor.clone());
            player.setItemOnCursor(offhand.clone());
            player.updateInventory();
            return;
        }

        if ((cursor == null || cursor.getType() == Material.AIR) && isAllowed(offhand)) {
            event.setCancelled(true);
            player.setItemOnCursor(offhand.clone());
            player.getInventory().setItemInOffHand(null);
            player.updateInventory();
        }
    }

    // 3. Arrastar no menu do inventário até o slot do escudo
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTopInventory().getType() != InventoryType.CRAFTING) return;

        if (event.getRawSlots().contains(45) && isAllowed(event.getOldCursor())) {
            event.setCancelled(true);
            ItemStack offhand = player.getInventory().getItemInOffHand();
            player.getInventory().setItemInOffHand(event.getOldCursor().clone());
            Bukkit.getScheduler().runTaskLater(this, () -> {
                player.setItemOnCursor(offhand.clone());
                player.updateInventory();
            }, 1L);
        }
    }
}
