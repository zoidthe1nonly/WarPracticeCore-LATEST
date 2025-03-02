package dev.zoid.warpracticecore.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BroadcastCommand implements CommandExecutor {
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final Sound sound = Sound.sound(org.bukkit.Sound.ENTITY_ELDER_GUARDIAN_CURSE, Sound.Source.MASTER, 1f, 1f);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!sender.hasPermission("warpractice.broadcast")) {
            sender.sendRichMessage("<red>You do not have the permission to run this command");
            return true;
        }
        if (strings.length == 0) {
            sender.sendRichMessage("<red>Usage: /broadcast <message>");
            return true;
        }

        String message = String.join(" ", strings);
        Component title = mm.deserialize("<aqua>ᴀɴɴᴏᴜɴᴄᴇᴍᴇɴᴛ");
        Component subtitle = mm.deserialize("<white>" + message);
        Title.Times times = Title.Times.times(Ticks.duration(10), Ticks.duration(70), Ticks.duration(20));
        Title broadcastTitle = Title.title(title,subtitle,times);

        Audience audience = Bukkit.getServer();

        audience.showTitle(broadcastTitle);
        audience.playSound(sound);

        return true;
    }
}