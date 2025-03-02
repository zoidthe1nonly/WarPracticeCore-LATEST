package dev.zoid.warpracticecore.design;

import dev.zoid.warpracticecore.WarPracticeCore;
import dev.zoid.warpracticecore.utils.TierUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class Chat implements Listener {

    private final LuckPerms luckPerms;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final TierUtil tierUtil;
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacySection();

    public Chat(Plugin plugin) {
        this.luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        this.tierUtil = WarPracticeCore.plugin().getTierUtil();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        String playerName = player.getName();
        String playerTier = tierUtil.getTier(playerName);

        if (playerTier == null) {
            playerTier = "<gray>N/A</gray>";
        }

        CachedMetaData metaData = luckPerms.getPlayerAdapter(Player.class).getMetaData(player);
        String prefix = metaData.getPrefix() != null ? metaData.getPrefix() : "";

        Component prefixComponent = miniMessage.deserialize(prefix);
        Component playerNameComponent = miniMessage.deserialize(playerName);
        Component tierComponent = miniMessage.deserialize(playerTier);
        Component arrow = Component.text(" -> ").color(NamedTextColor.GRAY);
        Component messageComponent = Component.text(event.getMessage()).color(NamedTextColor.WHITE);

        Component chatComponent = Component.text()
                .append(prefixComponent)
                .append(Component.text("["))
                .append(playerNameComponent)
                .append(Component.text("] "))
                .append(Component.text("[").color(NamedTextColor.GRAY))
                .append(tierComponent)
                .append(Component.text("]").color(NamedTextColor.GRAY))
                .append(arrow)
                .append(messageComponent)
                .build();

        String legacyFormat = legacySerializer.serialize(chatComponent);
        Audience.audience(Bukkit.getOnlinePlayers()).sendMessage(miniMessage.deserialize(legacyFormat));
        Bukkit.getConsoleSender().sendMessage(miniMessage.deserialize(legacyFormat));
    }
}