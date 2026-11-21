<p align="right">
  🌎 <strong>Language:</strong> <a href="README.md">🇦🇺 English</a> | <a href="README.pt-BR.md">🇧🇷 Português (BR)</a>
</p>

# TorchEquip

**A lightweight, zero-overhead crossplay plugin built specifically for Minecraft Java 26.3 servers running GeyserMC.**  
**Designed to bridge the off-hand gap between Java and Bedrock Edition, allowing PC, Mobile, and Console players to freely equip any torch into their shield slot.**  
**Works seamlessly out of the box with no commands, no permissions, and full support for both inventory menu equipping and instant quick-swap.**

[Releases](../../releases) • [GeyserMC](https://geysermc.org) • [Report an Issue](../../issues)

---

## What is TorchEquip?
**TorchEquip** is a focused, zero-overhead plugin designed for **Minecraft Java 26.3** servers running **GeyserMC**. 

By default, Minecraft Bedrock Edition restricts players from placing regular torches into the off-hand (shield) slot through the inventory interface. TorchEquip removes this restriction seamlessly, giving Mobile, Console, and Windows Bedrock players the exact same off-hand torch freedom as Java Edition players — without adding commands, permissions, or bloated configuration files.

---

## Currently Supporting

| Platform | Supported Versions | Status |
| :--- | :--- | :--- |
| **Minecraft: Java Edition** | `26.3` | ✅ Fully Supported |
| **Minecraft: Bedrock Edition** (via Geyser) | Latest Bedrock releases | ✅ Fully Supported |
| **Server Software** | Paper, Purpur, Folia `26.3` | ✅ Tested & Verified |

---

## Features & Mechanics
- **Native GeyserMC Integration:** Intercepts Bedrock inventory interactions cleanly so mobile and console players can equip torches just like a shield.
- **Inventory Menu Equipping (PC & Mobile):** Open your inventory, select any torch, and place or tap it directly into the off-hand slot next to your player model.
- **Instant Quick-Swap:** Hold a torch in your main hand and press `F` (Java Edition) or trigger your off-hand swap action (Bedrock/Geyser) to equip it immediately.
- **All Torch Variants Supported:** `TORCH`, `SOUL_TORCH`, `REDSTONE_TORCH`, and `COPPER_TORCH`.
- **Plug-and-Play Architecture:** No commands to memorize, no permissions to configure, and virtually zero impact on server tick performance (TPS).

---

## Installation & Setup
**1. Requirements:** Ensure your server is running **Paper**, **Purpur**, or **Folia** on **Minecraft 26.3** with **Java 25** or newer, alongside [GeyserMC](https://geysermc.org) for Bedrock crossplay.  
**2. Download:** Navigate to the [**Releases**](../../releases) section of this repository and download `TorchEquip.jar`.  
**3. Install:** Move `TorchEquip.jar` into your server's `/plugins/` directory.  
**4. Start:** Restart your server. The plugin activates automatically with no extra configuration needed.

---

## How to Use
**Through the Inventory Screen (Java & Bedrock/Mobile):**  
**1.** Open your player inventory.  
**2.** Pick up or tap a torch from your inventory.  
**3.** Click or tap directly on the **off-hand (shield) slot** to equip it.

**Through Quick-Swap:**  
**Java Edition:** Hold a torch in your main hand and press `F`.  
**Bedrock Edition:** Hold a torch and use your configured Geyser off-hand swap button/emote.

---

## Building from Source
If you prefer to compile `TorchEquip.jar` manually instead of using the pre-built GitHub Releases:

**1.** Clone this repository:
```bash
git clone https://github.com
cd TorchEquip
```
**2.** Compile with Maven (requires **JDK 25**):
```bash
mvn clean package
```
**3.** Locate the compiled plugin at `target/TorchEquip.jar`.

---

## Links & Credits
**Author:** Ycaro Rodrigues ([YcaroEngine](https://github.com))  
**Repository:** [YcaroEngine/TorchEquip](https://github.com/TorchEquip)  
**Crossplay Bridge:** [GeyserMC Official Website](https://geysermc.org)
