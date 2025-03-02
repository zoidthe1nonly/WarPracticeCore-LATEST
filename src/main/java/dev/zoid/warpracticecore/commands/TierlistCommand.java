package dev.zoid.warpracticecore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import dev.zoid.warpracticecore.menu.TierList;

public class TierlistCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        } else {
            Player p = (Player) sender;
            TierList.open(p);
            return true;
        }
    }
}
