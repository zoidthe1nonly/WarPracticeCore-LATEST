package dev.zoid.warpracticecore.commands;

import dev.zoid.warpracticecore.menu.RtpMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RtpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            RtpMenu.open(p);
            return true;
        }
        return true;
    }
}