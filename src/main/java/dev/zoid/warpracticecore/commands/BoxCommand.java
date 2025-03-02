package dev.zoid.warpracticecore.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BoxCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        World world = player.getServer().getWorld("box");
        if (world == null) return false;
        player.teleportAsync(new Location(world, 6, 161, -1));
        return true;
    }
}
