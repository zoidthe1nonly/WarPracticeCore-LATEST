package dev.zoid.warpracticecore.topic_events.utils.commands;

import dev.zoid.warpracticecore.topic_events.utils.EventManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EventAnnounceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("warpractice.event.announce")) {
            sender.sendMessage("No permission.");
            return true;
        }

        if (EventManager.isEventActive()) {
            sender.sendMessage("An event is already active.");
            return true;
        }
        if (EventManager.isEventAnnouncementActive()) {
            sender.sendMessage("An event announcement is already active.");
            return true;
        }
        EventManager.startEventAnnouncement();
        return true;
    }
}