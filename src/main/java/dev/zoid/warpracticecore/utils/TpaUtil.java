package dev.zoid.warpracticecore.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TpaUtil {
    private TpaUtil() {}
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final Map<UUID, UUID> tpaRequests = new HashMap<>();
    private static final Map<UUID, UUID> tpaHereRequests = new HashMap<>();
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final Map<UUID, Location> deathLocations = new HashMap<>();
    private static final long REQUEST_EXPIRE_TIME = 600L;

    public static void sendTPARequest(Player sender, Player target, Plugin plugin) {
        sendRequest(sender, target, plugin, tpaRequests,
                " \n <#e6b8aa>Sent <#A5D8FF>teleport <#e6b8aa>request to <#A5D8FF>" + target.getName() +
                        "\n <#e6b8aa>They have 30 seconds to accept\n     <#db0718><click:run_command:/tpacancel><hover:show_text:' '>[CANCEL]</hover></click>\n ",
                " \n <#e6b8aa>Recieved <#A5D8FF>teleport <#e6b8aa>request by <#A5D8FF>" + sender.getName() +
                        "\n <#e6b8aa>You have 30 seconds to accept\n     <#db0718><click:run_command:/tpdeny " + sender.getName() +
                        "><hover:show_text:' '>[DENY]</hover></click> <gray>- <#A5D8FF><click:run_command:/tpaccept " + sender.getName() +
                        "><hover:show_text:' '>[ACCEPT]</hover></click>\n ");
    }

    public static void sendTPAHereRequest(Player sender, Player target, Plugin plugin) {
        sendRequest(sender, target, plugin, tpaHereRequests,
                " \n <#e6b8aa>Sent <#A5D8FF>teleport here <#e6b8aa>request to <#A5D8FF>" + target.getName() +
                        "\n <#e6b8aa>They have 30 seconds to accept\n     <#db0718><click:run_command:/tpacancel><hover:show_text:' '>[CANCEL]</hover></click>\n ",
                " \n <#e6b8aa>Recieved <#A5D8FF>teleport here <#e6b8aa>request by <#A5D8FF>" + sender.getName() +
                        "\n <#e6b8aa>You have 30 seconds to accept\n     <#db0718><click:run_command:/tpdeny " + sender.getName() +
                        "><hover:show_text:' '>[DENY]</hover></click> <gray>- <#A5D8FF><click:run_command:/tpaccept " + sender.getName() +
                        "><hover:show_text:' '>[ACCEPT]</hover></click>\n ");
    }

    private static void sendRequest(Player sender, Player target, Plugin plugin, Map<UUID, UUID> requestMap, String senderMessage, String targetMessage) {
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            sender.sendMessage(miniMessage.deserialize("<red>You cannot send a teleport request to yourself."));
            return;
        }
        UUID senderId = sender.getUniqueId();
        UUID targetId = target.getUniqueId();
        if (tpaRequests.containsKey(targetId) || tpaHereRequests.containsKey(targetId)) {
            sender.sendMessage(miniMessage.deserialize("<red>" + target.getName() + " already has a pending TPA request."));
            return;
        }
        requestMap.put(targetId, senderId);
        cooldowns.put(senderId, System.currentTimeMillis());
        sender.sendMessage(miniMessage.deserialize(senderMessage));
        target.sendMessage(miniMessage.deserialize(targetMessage));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (requestMap.remove(targetId) != null) {
                sender.sendMessage(miniMessage.deserialize("<red>Your teleport request to " + target.getName() + " has timed out."));
            }
        }, REQUEST_EXPIRE_TIME);
    }

    public static void acceptTPARequest(Player target, String senderName, Plugin plugin) {
        UUID targetId = target.getUniqueId();
        UUID senderId = null;
        if (senderName != null) {
            Player sender = Bukkit.getPlayer(senderName);
            if (sender == null) {
                target.sendMessage(miniMessage.deserialize("<red>The player who sent the request is no longer online."));
                return;
            }
            senderId = sender.getUniqueId();
        } else {
            if (tpaRequests.containsKey(targetId)) {
                senderId = tpaRequests.get(targetId);
            } else if (tpaHereRequests.containsKey(targetId)) {
                senderId = tpaHereRequests.get(targetId);
            } else {
                target.sendMessage(miniMessage.deserialize("<red>You have no pending TPA requests."));
                return;
            }
        }
        Player sender = Bukkit.getPlayer(senderId);
        if (sender == null) {
            target.sendMessage(miniMessage.deserialize("<red>The player who sent the request is no longer online."));
            return;
        }
        if (tpaRequests.containsKey(targetId) && tpaRequests.get(targetId).equals(senderId)) {
            tpaRequests.remove(targetId);
            Bukkit.getScheduler().runTask(plugin, () -> {
                sender.teleport(target.getLocation());
                sender.sendMessage(miniMessage.deserialize("<#e6b8aa>You got teleported to <#A5D8FF>" + target.getName()));
                target.sendMessage(miniMessage.deserialize("<#e6b8aa>" + sender.getName() + " got teleported to you"));
                playBanjoSound(sender, plugin);
                playBanjoSound(target, plugin);
            });
        } else if (tpaHereRequests.containsKey(targetId) && tpaHereRequests.get(targetId).equals(senderId)) {
            tpaHereRequests.remove(targetId);
            Bukkit.getScheduler().runTask(plugin, () -> {
                target.teleport(sender.getLocation());
                target.sendMessage(miniMessage.deserialize("<#e6b8aa>You got teleported to <#A5D8FF>" + sender.getName()));
                sender.sendMessage(miniMessage.deserialize("<#e6b8aa>" + target.getName() + " got teleported to you"));
                playBanjoSound(sender, plugin);
                playBanjoSound(target, plugin);
            });
        } else {
            target.sendMessage(miniMessage.deserialize("<red>You have no pending TPA request from " + sender.getName() + "."));
        }
    }

    public static void denyTPARequest(Player target, String senderName) {
        UUID targetId = target.getUniqueId();
        Player sender = Bukkit.getPlayer(senderName);
        if (sender == null) {
            target.sendMessage(miniMessage.deserialize("<red>The player who sent the request is no longer online."));
            return;
        }
        UUID senderId = sender.getUniqueId();
        if (!senderId.equals(tpaRequests.get(targetId)) && !senderId.equals(tpaHereRequests.get(targetId))) {
            target.sendMessage(miniMessage.deserialize("<red>You have no pending TPA request from " + sender.getName() + "."));
            return;
        }
        tpaRequests.remove(targetId);
        tpaHereRequests.remove(targetId);
        sender.sendMessage(miniMessage.deserialize("<red>" + target.getName() + " has denied your TPA request."));
        target.sendMessage(miniMessage.deserialize("<red>You have denied " + sender.getName() + "'s TPA request."));
    }

    public static void cancelTPARequest(Player sender) {
        UUID senderId = sender.getUniqueId();
        UUID targetId = null;
        for (Map.Entry<UUID, UUID> entry : tpaRequests.entrySet()) {
            if (entry.getValue().equals(senderId)) {
                targetId = entry.getKey();
                break;
            }
        }
        if (targetId == null) {
            for (Map.Entry<UUID, UUID> entry : tpaHereRequests.entrySet()) {
                if (entry.getValue().equals(senderId)) {
                    targetId = entry.getKey();
                    break;
                }
            }
        }
        if (targetId == null) {
            sender.sendMessage(miniMessage.deserialize("<red>You have no pending TPA requests to cancel."));
            return;
        }
        tpaRequests.remove(targetId);
        tpaHereRequests.remove(targetId);
        Player target = Bukkit.getPlayer(targetId);
        sender.sendMessage(miniMessage.deserialize("<#e6b8aa>You cancelled your request to " + (target != null ? target.getName() : "null")));
        if (target != null) {
            target.sendMessage(miniMessage.deserialize("<red>" + sender.getName() + " has canceled their TPA request."));
        }
    }

    public static void setDeathLocation(Player player) {
        deathLocations.put(player.getUniqueId(), player.getLocation());
    }

    public static void teleportToDeathLocation(Player player) {
        Location location = deathLocations.get(player.getUniqueId());
        if (location == null) {
            player.sendMessage(miniMessage.deserialize("<red>No death location found."));
            return;
        }
        player.teleportAsync(location);
        player.sendMessage(miniMessage.deserialize("<#e6b8aa>You have been teleported to your death location."));
    }

    private static void playBanjoSound(Player player, Plugin plugin) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, 1.0f);
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.stopSound(Sound.BLOCK_NOTE_BLOCK_BANJO), 30L);
    }
}
