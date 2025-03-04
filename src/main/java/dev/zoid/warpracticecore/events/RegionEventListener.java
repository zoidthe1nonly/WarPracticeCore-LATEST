package dev.zoid.warpracticecore.events;

import dev.zoid.warpracticecore.utils.Region;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

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

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        Region.getAllRegions().forEach(region -> region.handleBlockBurn(event));
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        Region.getAllRegions().forEach(region -> region.handleBlockIgnite(event));
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Region.getAllRegions().forEach(region -> region.handlePlayerBucketEmpty(event));
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Region.getAllRegions().forEach(region -> region.handlePlayerBucketFill(event));
    }
}
