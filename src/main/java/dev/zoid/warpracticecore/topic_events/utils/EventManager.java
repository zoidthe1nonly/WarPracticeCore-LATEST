package dev.zoid.warpracticecore.topic_events.utils;

import dev.zoid.warpracticecore.WarPracticeCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {
    private static boolean eventAnnouncementActive = false;
    private static boolean eventActive = false;
    private static final List<Player> eventPlayers = new CopyOnWriteArrayList<>();
    private static final Map<Player, Location> playerLocations = new HashMap<>();
    private static BukkitTask announcementTask;
    private static BukkitTask countdownTask;
    private static World eventWorld;
    private static int counter;
    private static final String EVENT_WORLD_NAME = "events";
    private static final TextColor EVENT_COLOR = TextColor.color(0x0080ff);
    private static final TextColor WHITE_COLOR = TextColor.color(0xFFFFFF);
    private static final TextColor RED_COLOR = TextColor.color(0xFF0000);

    public static void setEventWorld(World world) {
        if (world != null) {
            eventWorld = world;
        }
    }

    public static boolean isEventAnnouncementActive() {
        return eventAnnouncementActive;
    }

    public static void stopEventAnnouncement() {
        if (announcementTask != null) {
            announcementTask.cancel();
            announcementTask = null;
        }
        eventAnnouncementActive = false;
    }

    public static boolean isEventActive() {
        return eventActive;
    }

    public static void startEventAnnouncement() {
        if (eventAnnouncementActive) return;
        eventAnnouncementActive = true;
        announcementTask = new BukkitRunnable() {
            @Override
            public void run() {
                Title title = Title.title(
                        Component.text("ᴘʀᴇ-ᴇᴠᴇɴᴛ").color(EVENT_COLOR),
                        Component.text("An event is about to occur, /event").color(WHITE_COLOR)
                );
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.showTitle(title);
                }
            }
        }.runTaskTimer(WarPracticeCore.plugin(), 0L, 20L * 60);
    }

    public static void startEvent() {
        stopEventAnnouncement();
        playerLocations.clear();

        if (eventWorld == null) {
            World world = Bukkit.getWorld(EVENT_WORLD_NAME);
            if (world != null) {
                eventWorld = world;
            } else {
                broadcastToEventPlayers("Error: Event world not found. Contact an administrator.");
                return;
            }
        }

        if (!eventWorld.getName().equalsIgnoreCase(EVENT_WORLD_NAME)) {
            World world = Bukkit.getWorld(EVENT_WORLD_NAME);
            if (world != null) {
                eventWorld = world;
            } else {
                broadcastToEventPlayers("Error: Event world not found. Contact an administrator.");
                return;
            }
        }

        eventActive = true;
        teleportPlayersToEvent();
    }

    public static void addPlayerToEvent(Player player) {
        if (player != null && !eventPlayers.contains(player)) {
            eventPlayers.add(player);
            String message = "<gray>" + player.getName() + " has joined the events queue (" + eventPlayers.size() + "/1000)</gray>";
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(message));
        }
    }

    private static void broadcastToEventPlayers(String message) {
        for (Player p : eventPlayers) {
            if (p != null && p.isOnline()) {
                p.sendMessage(message);
            }
        }
    }

    private static void teleportPlayersToEvent() {
        if (eventWorld == null) {
            broadcastToEventPlayers("Error with Event World");
            return;
        }

        for (Player p : eventPlayers) {
            if (p != null && p.isOnline()) {
                try {
                    Location loc = new Location(eventWorld, 0, 92, 0);
                    if (!eventWorld.isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                        eventWorld.loadChunk(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
                    }
                    p.teleport(loc);
                    p.setInvulnerable(true);
                    p.sendMessage("You are now in the event world.");
                } catch (Exception e) {
                    p.sendMessage("Error: Unable to teleport to event world. Try again or contact an administrator.");
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(WarPracticeCore.plugin(), EventManager::startCountdown, 20L);
    }

    private static void startCountdown() {
        counter = 60;
        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (counter <= 0) {
                    endCountdown();
                    cancel();
                    return;
                }

                if (eventPlayers.isEmpty()) {
                    cancel();
                    stopEvent();
                    return;
                }

                Title title = Title.title(
                        Component.text(String.valueOf(counter)).color(EVENT_COLOR),
                        Component.text("Event is starting, get ready").color(WHITE_COLOR)
                );

                for (Player p : eventPlayers) {
                    if (p != null && p.isOnline()) {
                        p.showTitle(title);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 100, 1, false, false, false));
                    }
                }

                if (counter == 60) {
                    distributePlayers();
                    for (Player p : eventPlayers) {
                        if (p != null && p.isOnline()) {
                            p.setCollidable(false);
                            p.setGravity(false);
                            p.setInvulnerable(true);
                            p.setAllowFlight(true);
                            p.setFlying(true);
                            p.setCanPickupItems(false);
                        }
                    }
                }

                if (counter == 15) {
                    for (Player p : eventPlayers) {
                        if (p != null && p.isOnline()) {
                            p.stopAllSounds();
                            p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 0.7f);
                        }
                    }
                }

                if (counter == 10) {
                    for (Player p : eventPlayers) {
                        if (p != null && p.isOnline()) {
                            p.stopAllSounds();
                            p.playSound(p, Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD, 1f, 0.5f);
                            p.playSound(p, Sound.ENTITY_WITHER_AMBIENT, 0.7f, 0.5f);
                        }
                    }
                }

                if (counter == 5) {
                    for (Player p : eventPlayers) {
                        if (p != null && p.isOnline()) {
                            p.stopAllSounds();
                            p.playSound(p, Sound.MUSIC_DISC_11, 1f, 0.6f);
                            p.playSound(p, Sound.ENTITY_SKELETON_HORSE_DEATH, 0.7f, 0.4f);
                        }
                    }
                }

                if (counter == 3) {
                    for (Player p : eventPlayers) {
                        if (p != null && p.isOnline()) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1, false, false, false));
                            p.playSound(p, Sound.ENTITY_GHAST_SCREAM, 0.6f, 0.5f);
                        }
                    }
                }

                if (counter == 1) {
                    for (Player p : eventPlayers) {
                        if (p != null && p.isOnline()) {
                            p.playSound(p, Sound.ENTITY_WITHER_DEATH, 0.7f, 0.5f);
                            p.playSound(p, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 0.4f);
                        }
                    }
                }

                counter--;
            }
        }.runTaskTimer(WarPracticeCore.plugin(), 0L, 20L);
    }

    private static void endCountdown() {
        Title startTitle = Title.title(
                Component.text("ᴇᴠᴇɴᴛ ѕᴛᴀʀᴛᴇᴅ").color(EVENT_COLOR),
                Component.text("Good luck!").color(WHITE_COLOR)
        );

        for (Player p : eventPlayers) {
            if (p != null && p.isOnline()) {
                p.setCollidable(true);
                p.setGravity(true);
                p.setInvulnerable(false);
                p.setAllowFlight(false);
                p.setFlying(false);
                p.removePotionEffect(PotionEffectType.DARKNESS);
                p.removePotionEffect(PotionEffectType.BLINDNESS);
                p.setCanPickupItems(true);
                p.stopAllSounds();
                p.showTitle(startTitle);
                p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.8f);
                p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 0.8f);
            }
        }
    }

    private static void distributePlayers() {
        if (eventPlayers.isEmpty() || eventWorld == null) return;

        List<Player> validPlayers = new ArrayList<>();
        for (Player p : eventPlayers) {
            if (p != null && p.isOnline()) {
                validPlayers.add(p);
            }
        }

        if (validPlayers.isEmpty()) return;

        WorldBorder border = eventWorld.getWorldBorder();
        double borderRadius = border.getSize() / 2;
        int playerCount = validPlayers.size();

        double spacingFactor;
        if (playerCount <= 10) {
            spacingFactor = 0.8;
        } else if (playerCount <= 25) {
            spacingFactor = 0.85;
        } else if (playerCount <= 50) {
            spacingFactor = 0.9;
        } else {
            spacingFactor = 0.95;
        }

        double radius = borderRadius * spacingFactor;
        double angleIncrement = 2 * Math.PI / playerCount;

        for (int i = 0; i < playerCount; i++) {
            Player player = validPlayers.get(i);
            if (player == null || !player.isOnline()) continue;

            double angle = i * angleIncrement;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Location location = new Location(eventWorld, x, 255, z);
            if (!eventWorld.isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                eventWorld.loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
            }

            Location groundLocation = getSafeGroundLocation(location);
            player.teleportAsync(groundLocation);
            playerLocations.put(player, groundLocation);
        }
    }

    private static Location getSafeGroundLocation(Location location) {
        if (location == null || location.getWorld() == null) return new Location(eventWorld, 0, 100, 0);

        Block highestBlock = location.getWorld().getHighestBlockAt(location);
        int y = highestBlock.getY() + 1;

        Material material = highestBlock.getType();
        if (material == Material.WATER || material == Material.LAVA ||
                material == Material.FIRE || material == Material.CACTUS || material == Material.BARRIER) {
            y += 3;
        }

        return new Location(location.getWorld(), location.getX(), y, location.getZ(),
                location.getYaw(), location.getPitch());
    }

    public static void stopEvent() {
        if (announcementTask != null) {
            announcementTask.cancel();
            announcementTask = null;
        }
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }

        Title endTitle = Title.title(
                Component.text("ᴇᴠᴇɴᴛ ᴇɴᴅ").color(RED_COLOR),
                Component.text("Hope you had a good experience").color(WHITE_COLOR)
        );

        World spawnWorld = Bukkit.getWorld("spawn");
        Location spawnLocation = new Location(spawnWorld, 0, 90, 0,180,0);

        for (Player p : eventPlayers) {
            if (p != null && p.isOnline()) {
                p.showTitle(endTitle);
                p.setCollidable(true);
                p.setGravity(true);
                p.setInvulnerable(false);
                p.setAllowFlight(false);
                p.setFlying(false);
                p.removePotionEffect(PotionEffectType.DARKNESS);
                p.removePotionEffect(PotionEffectType.BLINDNESS);
                p.setCanPickupItems(true);
                p.stopAllSounds();
                if (spawnWorld != null && spawnWorld.isChunkLoaded(spawnLocation.getChunk())){
                    p.teleport(spawnLocation);
                }else {
                    p.teleport(spawnLocation);
                }

            }
        }

        eventAnnouncementActive = false;
        eventActive = false;
        eventPlayers.clear();
        playerLocations.clear();
    }

    public static List<Player> getEventPlayers() {
        return new ArrayList<>(eventPlayers);
    }

    public static int getCounter() {
        return counter;
    }

    public static boolean shouldRestrictPlayerMovement(Player player) {
        return counter > 0 && eventActive && eventPlayers.contains(player);
    }

    public static Location getPlayerLocation(Player player) {
        return playerLocations.get(player);
    }
}