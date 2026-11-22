package org.offhand.core;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TorchEquipPlugin extends JavaPlugin implements Listener {

    private static final List<Material> LIGHT_ITEMS = List.of(
            Material.TORCH,
            Material.SOUL_TORCH,
            Material.REDSTONE_TORCH,
            Material.COPPER_TORCH,
            Material.LANTERN,
            Material.SOUL_LANTERN,
            Material.COPPER_LANTERN
    );

    @Override
    public void onEnable() {
        generateGeyserMcpack();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private boolean isTorchOrLantern(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        return LIGHT_ITEMS.contains(item.getType());
    }

    private void generateGeyserMcpack() {
        try {
            File pluginsDir = getDataFolder().getParentFile();
            if (pluginsDir == null) return;

            File geyserDir = new File(pluginsDir, "Geyser-Spigot");
            if (geyserDir.exists() && geyserDir.isDirectory()) {
                File geyserPacks = new File(geyserDir, "packs");
                if (!geyserPacks.exists()) {
                    geyserPacks.mkdirs();
                }
                File targetPack = new File(geyserPacks, "TorchEquip.mcpack");
                writeMcpack(targetPack);
                getLogger().info("Generated Bedrock pack at: plugins/Geyser-Spigot/packs/TorchEquip.mcpack");
            }
        } catch (Exception e) {
            getLogger().warning("Failed to generate TorchEquip.mcpack: " + e.getMessage());
        }
    }

    private void writeMcpack(File targetFile) throws Exception {
        String manifestJson = """
                {
                  "format_version": 2,
                  "header": {
                    "name": "TorchEquip Off-Hand",
                    "description": "Enables equipping all torches and lanterns in the off-hand slot.",
                    "uuid": "8f3a1c20-7b4e-4d9a-9c12-45e8a1b2c3d4",
                    "version":,
                    "min_engine_version": [1, 21, 0]
                  },
                  "modules": [
                    {
                      "type": "data",
                      "uuid": "3e9b7d10-2a6f-4c8b-8e31-99a0b1c2d3e4",
                      "version": [1, 0, 0]
                    }
                  ]
                }
                """;

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(targetFile))) {
            zos.putNextEntry(new ZipEntry("manifest.json"));
            zos.write(manifestJson.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            for (Material mat : LIGHT_ITEMS) {
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

    // 1. Tecla F (Java & Bedrock)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        if (isTorchOrLantern(event.getOffHandItem()) || isTorchOrLantern(event.getMainHandItem())) {
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

        if (isTorchOrLantern(cursor)) {
            event.setCancelled(true);
            player.getInventory().setItemInOffHand(cursor.clone());
            player.setItemOnCursor(offhand.clone());
            player.updateInventory();
            return;
        }

        if ((cursor == null || cursor.getType() == Material.AIR) && isTorchOrLantern(offhand)) {
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

        if (event.getRawSlots().contains(45) && isTorchOrLantern(event.getOldCursor())) {
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
