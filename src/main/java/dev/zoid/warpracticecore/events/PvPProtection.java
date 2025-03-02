package dev.zoid.warpracticecore.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.world.WorldLoadEvent;

public class PvPProtection implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if("spawn".equals(event.getEntity().getWorld().getName())) event.setCancelled(true);
        }
    }
}