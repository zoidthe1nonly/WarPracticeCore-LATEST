package dev.zoid.warpracticecore.events;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveEvent implements Listener {

    private final MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.quitMessage(null);
        event.quitMessage(mm.deserialize("<gray>[<#ff0015>-<gray>] <white>" + event.getPlayer().getName()));
    }
}