package dev.zoid.warpracticecore.design;

import dev.zoid.warpracticecore.WarPracticeCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;

public class Chat implements Listener {
    private final LuckPerms luckPerms;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public Chat(Plugin plugin) {
        this.luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        CachedMetaData metaData = luckPerms.getPlayerAdapter(Player.class).getMetaData(player);
        String prefix = metaData.getPrefix() != null ? metaData.getPrefix() : "";
        Component prefixComponent = mm.deserialize(prefix + player.getName() + " ");
        Component formattedMessage = Component.text()
                .append(prefixComponent)
                .append(Component.text("->").color(net.kyori.adventure.text.format.NamedTextColor.GRAY))
                .append(Component.text(" " + event.getMessage()).color(net.kyori.adventure.text.format.NamedTextColor.WHITE))
                .build();
        String legacyFormat = LegacyComponentSerializer.legacySection().serialize(formattedMessage);
        event.setFormat(legacyFormat);
    }
}