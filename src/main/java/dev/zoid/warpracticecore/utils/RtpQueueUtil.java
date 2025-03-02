package dev.zoid.warpracticecore.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import dev.zoid.warpracticecore.WarPracticeCore;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RtpQueueUtil implements Listener {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final Set<Player> queue = new HashSet<>();
    private static final Map<String, List<Location>> CACHED_LOCATIONS = new ConcurrentHashMap<>();
    private static final int INITIAL_CACHE_SIZE = 5;
    private static final Random random = new Random();

    public static void addToQueue(Player player) {
        if (queue.contains(player)) {
            leaveQueue(player);
            return;
        }
        queue.add(player);
        sendActionBar(player, miniMessage.deserialize("<#e3b3bd>You're now in rtp queue"));
        broadcastMessage(miniMessage.deserialize("<#1e90ff>" + player.getName() + " <#e3b3bd>is looking for a duel (/rtpqueue)"));
        if (queue.size() >= 2) {
            Iterator<Player> it = queue.iterator();
            Player p1 = it.next();
            Player p2 = it.next();
            queue.remove(p1);
            queue.remove(p2);
            findRandomLocation("desert").thenAccept(location -> {
                if (location != null) {
                    Location loc1 = location.clone().add(10, 10, 0);
                    Location loc2 = location.clone().add(-10, 10, 0);
                    loc1.setDirection(new Vector(-1, 0, 0));
                    loc2.setDirection(new Vector(1, 0, 0));
                    p1.teleportAsync(loc1);
                    p2.teleportAsync(loc2);
                    applySlowFalling(p1);
                    applySlowFalling(p2);
                    Component m1 = miniMessage.deserialize("<#e3b3bd>You're now fighting <#1e90ff>" + p2.getName());
                    Component m2 = miniMessage.deserialize("<#e3b3bd>You're now fighting <#1e90ff>" + p1.getName());
                    sendActionBar(p1, m1);
                    sendActionBar(p2, m2);
                    sendMessage(p1, m1);
                    sendMessage(p2, m2);
                }
            });
        } else {
            sendActionBar(player, miniMessage.deserialize("<#e3b3bd>Searching for an opponent..."));
        }
    }

    public static void leaveQueue(Player player) {
        if (queue.remove(player)) {
            Component m = miniMessage.deserialize("<red>You have left the queue");
            sendActionBar(player, m);
            sendMessage(player, m);
        }
    }

    public static void preloadRandomLocations() {
        for (World world : Bukkit.getWorlds()) {
            String worldName = world.getName();
            CACHED_LOCATIONS.putIfAbsent(worldName, new CopyOnWriteArrayList<>());
            for (int i = 0; i < INITIAL_CACHE_SIZE; i++) {
                generateRandomLocationAsync(world).thenAccept(loc -> CACHED_LOCATIONS.get(worldName).add(loc));
            }
        }
    }

    private static CompletableFuture<Location> findRandomLocation(String worldName) {
        List<Location> cache = CACHED_LOCATIONS.get(worldName);
        if (cache != null && !cache.isEmpty()) {
            Location loc = cache.remove(0);
            Bukkit.getScheduler().runTaskAsynchronously(WarPracticeCore.plugin(), () -> {
                World w = Bukkit.getWorld(worldName);
                if (w != null) {
                    generateRandomLocationAsync(w).thenAccept(newLoc -> CACHED_LOCATIONS.get(worldName).add(newLoc));
                }
            });
            return CompletableFuture.completedFuture(loc);
        }
        World w = Bukkit.getWorld(worldName);
        return w != null ? generateRandomLocationAsync(w) : CompletableFuture.completedFuture(null);
    }

    private static CompletableFuture<Location> generateRandomLocationAsync(World world) {
        CompletableFuture<Location> future = new CompletableFuture<>();
        WorldBorder border = world.getWorldBorder();
        double size = border.getSize() / 2;
        double centerX = border.getCenter().getX();
        double centerZ = border.getCenter().getZ();
        double x = centerX + (random.nextDouble() * size * 2 - size);
        double z = centerZ + (random.nextDouble() * size * 2 - size);
        int chunkX = ((int) x) >> 4;
        int chunkZ = ((int) z) >> 4;
        world.getChunkAtAsync(chunkX, chunkZ).thenAccept(chunk -> {
            int y = world.getHighestBlockYAt((int) x, (int) z);
            future.complete(new Location(world, x, y, z));
        }).exceptionally(ex -> {
            future.completeExceptionally(ex);
            return null;
        });
        return future;
    }

    private static void applySlowFalling(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 60, 1));
    }

    private static void sendActionBar(Player player, Component message) {
        player.sendActionBar(message);
    }

    private static void sendMessage(Player player, Component message) {
        player.sendMessage(message);
    }

    private static void broadcastMessage(Component message) {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        leaveQueue(event.getPlayer());
    }
}
