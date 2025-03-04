package dev.zoid.warpracticecore.topic_events.utils.commands;

import dev.zoid.warpracticecore.topic_events.utils.EventManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventCancelAnnounceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("warpractice.event.cancelannounce")) {
            sender.sendMessage("No permission.");
            return true;
        }
        if (!EventManager.isEventAnnouncementActive()) {
            sender.sendMessage("There is no event announcement to cancel.");
            return true;
        }
        for(Player player : Bukkit.getOnlinePlayers()){
            player.showTitle(Title.title(
                    Component.text("ᴇᴠᴇɴᴛ-ᴄᴀɴᴄᴇʟ").color(TextColor.color(0xFF0000)),
                    Component.text("Event has been postponed").color(TextColor.color(0xFFFFFF))
            ));
        }
        EventManager.stopEventAnnouncement();
        return true;
    }
}