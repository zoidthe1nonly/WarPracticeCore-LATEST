package dev.zoid.warpracticecore.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class PingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        MiniMessage mm = MiniMessage.miniMessage();
        if (args.length == 0) {
            Component message = mm.deserialize("<#e3b3bd>Your ping is <#227ef5>" + getPing(player) + "ms");
            player.sendMessage(message);
            player.sendActionBar(message);
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(mm.deserialize("<red>Player not found."));
                return true;
            }
            Component message = mm.deserialize("<#227ef5>" + target.getName() + "'s <#e3b3bd>ping is <#227ef5>" + getPing(target) + "ms");
            player.sendMessage(message);
            player.sendActionBar(message);
        }
        return true;
    }

    private int getPing(Player player) {
        return player.getPing();
    }
}