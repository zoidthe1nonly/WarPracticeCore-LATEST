package dev.zoid.warpracticecore.events;

import dev.zoid.warpracticecore.storage.SpawnData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnEvent implements Listener {

    @EventHandler
    public void respawnEvent(PlayerRespawnEvent e) {
        Object[] spawnData = SpawnData.load();
        if (spawnData == null) return;

        double x = (double) spawnData[0];
        double y = (double) spawnData[1];
        double z = (double) spawnData[2];
        float yaw = (float) spawnData[3];
        float pitch = (float) spawnData[4];
        String worldName = (String) spawnData[5];

        org.bukkit.World world = org.bukkit.Bukkit.getWorld(worldName);
        if (world == null) return;

        e.setRespawnLocation(new org.bukkit.Location(world, x, y, z, yaw, pitch));
    }
}
