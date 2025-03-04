package dev.zoid.warpracticecore.topic_events.utils.commands;

import dev.zoid.warpracticecore.WarPracticeCore;
import dev.zoid.warpracticecore.topic_events.utils.EventManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EventStopCommand implements CommandExecutor {
    private final WarPracticeCore plugin;

    public EventStopCommand(WarPracticeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("warpractice.event.stop")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        EventManager.stopEvent();
        setupEventWorldBorderAndSchematic();
        return true;
    }

    public void setupEventWorldBorderAndSchematic() {
        World eventWorld = Bukkit.getWorld("events");
        if (eventWorld == null) {
            Bukkit.getLogger().warning("Event world not found!");
            return;
        }

        WorldBorder border = eventWorld.getWorldBorder();
        Location center = new Location(eventWorld, 0, 92, 0);
        border.setCenter(center);
        border.setSize(153);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            String[] commands = {
                    "/world events",
                    "/pos1 0,92,0",
                    "/pos2 0,92,0",
                    "/schem load eventsgoat.schem",
                    "/paste"
            };

            for (String cmd : commands) {
                Bukkit.dispatchCommand(console, cmd);
            }
        }, 1L);

    }
}