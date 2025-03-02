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

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final Set<Player> QUEUE = ConcurrentHashMap.newKeySet();
    private static final Map<String, List<Location>> CACHED_LOCATIONS = new ConcurrentHashMap<>();
    private static final int INITIAL_CACHE_SIZE = 5;
    private static final Random RANDOM = new Random();
    private static final int SLOW_FALLING_DURATION = 60;
    private static final int SLOW_FALLING_AMPLIFIER = 1;

    public static void addToQueue(Player player) {
        if (QUEUE.contains(player)) {
            leaveQueue(player);
            return;
        }

        QUEUE.add(player);
        sendActionBar(player, MINI_MESSAGE.deserialize("<#e3b3bd>You're now in rtp queue"));

        if (QUEUE.size() >= 2) {
            matchPlayers();
        } else {
            broadcastMessage(MINI_MESSAGE.deserialize("<#1e90ff>" + player.getName() + " <#e3b3bd>is looking for a duel (/rtpqueue)"));
            sendActionBar(player, MINI_MESSAGE.deserialize("<#e3b3bd>Searching for an opponent..."));
        }
    }

    private static void matchPlayers() {
        Iterator<Player> iterator = QUEUE.iterator();
        Player player1 = iterator.next();
        Player player2 = iterator.next();
        QUEUE.remove(player1);
        QUEUE.remove(player2);

        findRandomLocation("desert").thenAccept(location -> {
            if (location != null) {
                teleportPlayers(player1, player2, location);
            }
        });
    }

    private static void teleportPlayers(Player player1, Player player2, Location location) {
        Location loc1 = location.clone().add(10, 10, 0);
        Location loc2 = location.clone().add(-10, 10, 0);
        loc1.setDirection(new Vector(-1, 0, 0));
        loc2.setDirection(new Vector(1, 0, 0));

        player1.teleportAsync(loc1);
        player2.teleportAsync(loc2);
        applySlowFalling(player1);
        applySlowFalling(player2);

        Component message1 = MINI_MESSAGE.deserialize("<#e3b3bd>You're now fighting <#1e90ff>" + player2.getName());
        Component message2 = MINI_MESSAGE.deserialize("<#e3b3bd>You're now fighting <#1e90ff>" + player1.getName());

        sendActionBar(player1, message1);
        sendActionBar(player2, message2);
        sendMessage(player1, message1);
        sendMessage(player2, message2);
    }

    public static void leaveQueue(Player player) {
        if (QUEUE.remove(player)) {
            Component message = MINI_MESSAGE.deserialize("<red>You have left the queue");
            sendActionBar(player, message);
            sendMessage(player, message);
        }
    }

    public static void preloadRandomLocations() {
        Bukkit.getWorlds().forEach(world -> {
            String worldName = world.getName();
            CACHED_LOCATIONS.putIfAbsent(worldName, new CopyOnWriteArrayList<>());
            for (int i = 0; i < INITIAL_CACHE_SIZE; i++) {
                generateRandomLocationAsync(world).thenAccept(loc -> CACHED_LOCATIONS.get(worldName).add(loc));
            }
        });
    }

    private static CompletableFuture<Location> findRandomLocation(String worldName) {
        List<Location> cache = CACHED_LOCATIONS.get(worldName);
        if (cache != null && !cache.isEmpty()) {
            Location location = cache.remove(0);
            Bukkit.getScheduler().runTaskAsynchronously(WarPracticeCore.plugin(), () -> {
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    generateRandomLocationAsync(world).thenAccept(newLocation -> CACHED_LOCATIONS.get(worldName).add(newLocation));
                }
            });
            return CompletableFuture.completedFuture(location);
        }
        World world = Bukkit.getWorld(worldName);
        return world != null ? generateRandomLocationAsync(world) : CompletableFuture.completedFuture(null);
    }

    private static CompletableFuture<Location> generateRandomLocationAsync(World world) {
        CompletableFuture<Location> future = new CompletableFuture<>();
        WorldBorder border = world.getWorldBorder();
        double size = border.getSize() / 2;
        double centerX = border.getCenter().getX();
        double centerZ = border.getCenter().getZ();
        double x = centerX + (RANDOM.nextDouble() * size * 2 - size);
        double z = centerZ + (RANDOM.nextDouble() * size * 2 - size);
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, SLOW_FALLING_DURATION, SLOW_FALLING_AMPLIFIER));
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