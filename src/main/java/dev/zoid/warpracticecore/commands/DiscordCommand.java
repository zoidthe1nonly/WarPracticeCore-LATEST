package dev.zoid.warpracticecore.commands;

import dev.zoid.warpracticecore.menu.Discord;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class DiscordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        } else if (sender instanceof Player) {
            Player p = (Player) sender;
            Discord.open(p);
            return true;
        }
        return true;
    }
}
