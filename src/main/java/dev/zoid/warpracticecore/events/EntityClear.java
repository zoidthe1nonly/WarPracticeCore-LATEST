package dev.zoid.warpracticecore.events;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityClear {
    private final Plugin plugin;
    private int taskId = -1;
    private static final long CLEAR_INTERVAL = 2 * 60 * 20;

    public EntityClear(Plugin plugin) {
        this.plugin = plugin;
    }

    public void startClearSchedule() {
        if (taskId != -1) {
            stopClearSchedule();
        }

        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                clearItems();
            }
        }.runTaskTimer(plugin, CLEAR_INTERVAL, CLEAR_INTERVAL).getTaskId();
    }

    public void stopClearSchedule() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    private void clearItems() {
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item) {
                    entity.remove();
                    count++;
                }
            }
        }
    }
}