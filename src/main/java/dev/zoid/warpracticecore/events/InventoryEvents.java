package dev.zoid.warpracticecore.events;

import dev.zoid.warpracticecore.utils.RtpUtil;
import dev.zoid.warpracticecore.utils.TextStyle;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryEvents implements Listener {

    private static final Component SANDBOX_MESSAGE = TextStyle.parse("<#e3b3bd>You're now in sandbox!");
    private static final Component DISCORD_MESSAGE = TextStyle.parse(
            "<#e3b3bd>   Click the link\n<#e3b3bd>   to join the server\n  <click:OPEN_URL:https://discord.gg/warnetwork><aqua><u>discord.gg/warnetwork</u></aqua></click>"
    );
    private static final Component TIER_MESSAGE = TextStyle.parse(
            "<#e3b3bd>   Click the link\n<#e3b3bd>   to join the tierlist\n  <click:OPEN_URL:https://discord.gg/xdZ4wwwnfX><aqua><u>https://discord.gg/xdZ4wwwnfX</u></aqua></click>"
    );


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        String title = view.getTitle();
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) return;
        if ("Sandbox".equals(title)) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            Material type = currentItem.getType();
            if (type == Material.SAND) {
                RtpUtil.rtp("desert", player);
            } else if (type == Material.GRASS_BLOCK) {
                RtpUtil.rtp("plains", player);
            } else if (type == Material.ORANGE_TERRACOTTA) {
                RtpUtil.rtp("badlands", player);
            } else if (type == Material.BARRIER) {
                player.closeInventory();
                return;
            }
            player.sendMessage(SANDBOX_MESSAGE);
            player.sendActionBar(SANDBOX_MESSAGE);
            player.closeInventory();
        } else if ("Discord".equals(title)) {
            event.setCancelled(true);
            if (currentItem.getType() == Material.AMETHYST_CLUSTER) {
                Player player = (Player) event.getWhoClicked();
                player.sendMessage(DISCORD_MESSAGE);
                player.closeInventory();
            }
        } else if ("Tierlist".equals(title)) {
            event.setCancelled(true);
            if (currentItem.getType() == Material.END_CRYSTAL) {
                Player player = (Player) event.getWhoClicked();
                player.sendMessage(TIER_MESSAGE);
                player.closeInventory();
            }
        }
    }
}
