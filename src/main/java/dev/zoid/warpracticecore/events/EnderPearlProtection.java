package dev.zoid.warpracticecore.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EnderPearlProtection implements Listener {
    @EventHandler
    public void onEnderPearlTeleport(PlayerTeleportEvent event) {
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && "spawn".equals(event.getFrom().getWorld().getName()))
            event.setCancelled(true);
    }
}