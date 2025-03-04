package dev.zoid.warpracticecore.design;

import dev.zoid.warpracticecore.WarPracticeCore;
import dev.zoid.warpracticecore.utils.TierUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Chat implements Listener {

    private final LuckPerms luckPerms;
    private final MiniMessage miniMessage;
    private final TierUtil tierUtil;
    private final Set<UUID> recentMessages = new HashSet<>();

    public Chat(Plugin plugin) {
        this.luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        this.tierUtil = WarPracticeCore.plugin().getTierUtil();
        this.miniMessage = MiniMessage.builder().build();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatEarly(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onChat(AsyncPlayerChatEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        if (recentMessages.contains(playerUUID)) {
            return;
        }

        recentMessages.add(playerUUID);
        Bukkit.getScheduler().runTaskLaterAsynchronously(WarPracticeCore.plugin(),
                () -> recentMessages.remove(playerUUID), 2L);

        Player player = event.getPlayer();
        String playerName = player.getName();
        String playerTier = tierUtil.getTier(playerName);

        if (playerTier == null) {
            playerTier = "<gray>N/A</gray>";
        }

        CachedMetaData metaData = luckPerms.getPlayerAdapter(Player.class).getMetaData(player);
        String prefix = metaData.getPrefix() != null ? metaData.getPrefix() : "";

        Component prefixComponent = miniMessage.deserialize(prefix, TagResolver.standard());
        Component playerNameComponent = Component.text(playerName);
        Component tierComponent = miniMessage.deserialize(playerTier, TagResolver.standard());
        Component arrow = Component.text(" -> ").color(NamedTextColor.GRAY);
        Component messageComponent = Component.text(event.getMessage()).color(NamedTextColor.WHITE);

        Component chatComponent = Component.empty()
                .append(prefixComponent);

        if (prefixComponent.color() != null) {
            playerNameComponent = playerNameComponent.color(prefixComponent.color());
        }

        chatComponent = chatComponent
                .append(playerNameComponent)
                .append(Component.text(" "))
                .append(Component.text("[").color(NamedTextColor.GRAY))
                .append(tierComponent)
                .append(Component.text("]").color(NamedTextColor.GRAY))
                .append(arrow)
                .append(messageComponent);

        Audience.audience(Bukkit.getOnlinePlayers()).sendMessage(chatComponent);
        Bukkit.getConsoleSender().sendMessage(chatComponent);
    }
}