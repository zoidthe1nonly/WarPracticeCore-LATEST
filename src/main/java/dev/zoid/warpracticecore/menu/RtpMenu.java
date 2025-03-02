package dev.zoid.warpracticecore.menu;

import dev.zoid.warpracticecore.utils.TextStyle;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class RtpMenu {

    public static final ItemStack CLOSE_ITEM = createItem(Material.BARRIER, TextStyle.parse("<red><!i>Close"));

    private static final ItemStack DESERT_ITEM;
    private static final ItemStack PLAINS_ITEM;
    private static final ItemStack BADLANDS_ITEM;

    static {
        DESERT_ITEM = createItem(Material.SAND, TextStyle.parse("<#e3b3bd><!i>Desert"));
        PLAINS_ITEM = createItem(Material.GRASS_BLOCK, TextStyle.parse("<#e3b3bd><!i>Plains"));
        BADLANDS_ITEM = createItem(Material.ORANGE_TERRACOTTA, TextStyle.parse("<#e3b3bd><!i>Badlands"));
    }

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "Sandbox");
        inv.setItem(11, DESERT_ITEM);
        inv.setItem(13, PLAINS_ITEM);
        inv.setItem(15, BADLANDS_ITEM);
        inv.setItem(26, CLOSE_ITEM);
        player.openInventory(inv);
    }

    public static ItemStack createItem(Material material, Component name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.displayName(name);
        item.setItemMeta(meta);
        return item;
    }
}