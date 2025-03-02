package dev.zoid.warpracticecore.utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CommandRunner {
    private static final List<String> COMMANDS = List.of(
            "/world flat",
            "/pos1 0,92,0",
            "/pos2 0,92,0",
            "/schem load flat.schem",
            "/paste",
            "/world box",
            "/pos1 6,161,-1",
            "/pos2 6,161,-1",
            "/schem load box.schem",
            "/paste"
    );

    public static void init(JavaPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                executeCommands(plugin);
            }
        }.runTaskTimer(plugin, 0, 6000);
    }

    private static void executeCommands(JavaPlugin plugin) {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index >= COMMANDS.size()) {
                    cancel();
                    return;
                }
                String command = COMMANDS.get(index);
                Bukkit.getLogger().info("Executing: " + command);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                index++;
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}
