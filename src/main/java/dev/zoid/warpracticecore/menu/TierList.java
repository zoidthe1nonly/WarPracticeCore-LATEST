package dev.zoid.warpracticecore.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import dev.zoid.warpracticecore.utils.TextStyle;
import org.bukkit.inventory.ItemStack;

public class TierList {
    private static final ItemStack TIERLIST_ITEM = RtpMenu.createItem(Material.END_CRYSTAL, TextStyle.parse("<#e3b3bd><!i>Tierlist"));

    public static void open(Player p) {
        Inventory tierList = Bukkit.createInventory(null, 27, "Tierlist");
        tierList.setItem(13, TIERLIST_ITEM);
        tierList.setItem(26, RtpMenu.CLOSE_ITEM);
        p.openInventory(tierList);
    }
}