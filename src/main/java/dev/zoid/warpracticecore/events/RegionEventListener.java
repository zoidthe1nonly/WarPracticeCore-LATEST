package dev.zoid.warpracticecore.events;

import dev.zoid.warpracticecore.utils.Region;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class RegionEventListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Region.getAllRegions().forEach(region -> region.handleBlockBreak(event));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Region.getAllRegions().forEach(region -> region.handleBlockPlace(event));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Region.getAllRegions().forEach(region -> region.handleEntityDamage(event));
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Region.getAllRegions().forEach(region -> region.handleEntityExplode(event));
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        Region.getAllRegions().forEach(region -> region.handleBlockExplode(event));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Region.getAllRegions().forEach(region -> region.handleEntityDamage(event));
    }
}
