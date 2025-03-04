package dev.zoid.warpracticecore.topic_events.utils.events;

import dev.zoid.warpracticecore.topic_events.utils.EventManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase("events") && !player.hasPermission("warpractice.event.admin")) {
            event.setCancelled(true);
            player.sendMessage("You cannot use commands in the events world.");
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (EventManager.isEventActive() && EventManager.getEventPlayers().contains(player)
                && EventManager.getCounter() > 0 && !player.hasPermission("warpractice.event.admin")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (EventManager.isEventActive() && EventManager.getEventPlayers().contains(player)
                && EventManager.getCounter() > 0 && !player.hasPermission("warpractice.event.admin")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            if (EventManager.isEventActive() && EventManager.getEventPlayers().contains(damaged)
                    && EventManager.getEventPlayers().contains(damager) && EventManager.getCounter() > 0) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (EventManager.shouldRestrictPlayerMovement(player)) {
            Location from = event.getFrom();
            Location to = event.getTo();

            if (to != null && (from.getX() != to.getX() || from.getZ() != to.getZ())) {
                Location originalLocation = EventManager.getPlayerLocation(player);
                if (originalLocation != null) {
                    event.setCancelled(true);
                    player.teleportAsync(originalLocation);
                }
            }
        }
    }
}
