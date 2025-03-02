package dev.zoid.warpracticecore.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class CreatureSummon implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == org.bukkit.entity.EntityType.RABBIT) {
            event.setCancelled(true);
        }
    }
}

