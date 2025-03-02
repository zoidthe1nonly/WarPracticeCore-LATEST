package dev.zoid.warpracticecore.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import dev.zoid.warpracticecore.utils.TextStyle;
import org.bukkit.inventory.ItemStack;

public class Discord {
    private static final ItemStack DISCORD_ITEM = RtpMenu.createItem(Material.AMETHYST_CLUSTER, TextStyle.parse("<#e3b3bd><!i>Discord"));

    public static void open(Player player) {
        Inventory dc = Bukkit.createInventory(null, 27, "Discord");
        dc.setItem(13, DISCORD_ITEM);
        dc.setItem(26, RtpMenu.CLOSE_ITEM);
        player.openInventory(dc);
    }
}