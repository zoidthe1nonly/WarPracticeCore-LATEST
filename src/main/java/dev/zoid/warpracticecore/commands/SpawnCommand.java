package dev.zoid.warpracticecore.commands;

import dev.zoid.warpracticecore.storage.SpawnData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        Object[] spawnData = SpawnData.load();
        if (spawnData == null) return true;

        double x = (double) spawnData[0];
        double y = (double) spawnData[1];
        double z = (double) spawnData[2];
        float yaw = (float) spawnData[3];
        float pitch = (float) spawnData[4];
        String worldName = (String) spawnData[5];

        org.bukkit.World world = org.bukkit.Bukkit.getWorld(worldName);

        player.teleportAsync(new org.bukkit.Location(world, x, y, z, yaw, pitch));
        return true;
    }
}