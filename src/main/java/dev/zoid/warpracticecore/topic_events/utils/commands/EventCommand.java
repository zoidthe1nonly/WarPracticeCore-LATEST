package dev.zoid.warpracticecore.topic_events.utils.commands;

import dev.zoid.warpracticecore.topic_events.utils.EventManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;

        if (!EventManager.isEventAnnouncementActive() || EventManager.isEventActive()) {
            player.sendMessage("No event is currently open to join.");
            return true;
        }

        EventManager.addPlayerToEvent(player);
        return true;
    }
}