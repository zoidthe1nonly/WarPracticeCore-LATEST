package dev.zoid.warpracticecore.commands;

import dev.zoid.warpracticecore.storage.SpawnData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (!sender.hasPermission("warpractice.setspawn")) return true;

        Player player = (Player) sender;
        SpawnData.save(
                player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ(),
                player.getLocation().getYaw(),
                player.getLocation().getPitch(),
                player.getLocation().getWorld().getName()
        );
        sender.sendMessage("Spawn set.");
        return true;
    }
}