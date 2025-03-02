package dev.zoid.warpracticecore.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RtpUtil {

    private static final Random random = new Random();
    private static final int MAX_ATTEMPTS = 3;
    private static final int CHUNK_LOAD_RADIUS = 1;
    private static final Map<String, List<Location>> CACHED_LOCATIONS = new ConcurrentHashMap<>();
    private static final int INITIAL_CACHE_SIZE = 5;
    private static JavaPlugin plugin;
    private static final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public static void initialize(JavaPlugin plugin) {
        RtpUtil.plugin = plugin;
        preloadRandomLocations();
    }

    public static void preloadRandomLocations() {
        for (World world : Bukkit.getWorlds()) {
            String worldName = world.getName();
            CACHED_LOCATIONS.putIfAbsent(worldName, new CopyOnWriteArrayList<>());
            for (int i = 0; i < INITIAL_CACHE_SIZE; i++) {
                generateRandomLocationAsync(world).thenAccept(loc -> {
                    CACHED_LOCATIONS.get(worldName).add(loc);
                });
            }
        }
    }

    public static void rtp(String worldName, Player player) {
        if (isCooldownActive(player, worldName)) {
            Component msg = MiniMessage.miniMessage().deserialize("<red>Woah, cool down bud");
            player.sendActionBar(msg);
            player.sendMessage(msg);
            return;
        }
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("World '" + worldName + "' does not exist.");
        }
        List<Location> locations = CACHED_LOCATIONS.get(worldName);
        if (locations != null && !locations.isEmpty()) {
            Location location = locations.remove(0);
            player.teleportAsync(location).thenAccept(success -> {
                if (success) {
                    setCooldown(player, worldName, 10000);
                    int chunkX = location.getBlockX() >> 4;
                    int chunkZ = location.getBlockZ() >> 4;
                    preloadSurroundingChunks(world, chunkX, chunkZ);
                }
                generateRandomLocationAsync(world).thenAccept(newLoc -> {
                    locations.add(newLoc);
                });
            });
            return;
        }
        attemptRtp(world, player, 0, worldName);
    }

    private static void attemptRtp(World world, Player player, int attempt, String worldName) {
        if (attempt >= MAX_ATTEMPTS) {
            return;
        }
        generateRandomLocationAsync(world).thenAccept(location -> {
            int chunkX = location.getBlockX() >> 4;
            int chunkZ = location.getBlockZ() >> 4;
            player.teleportAsync(location).thenAccept(success -> {
                if (success) {
                    setCooldown(player, worldName, 10000);
                    preloadSurroundingChunks(world, chunkX, chunkZ);
                } else {
                    attemptRtp(world, player, attempt + 1, worldName);
                }
            });
        });
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
            future.complete(new Location(world, x, y + 1, z));
        }).exceptionally(ex -> {
            future.completeExceptionally(ex);
            return null;
        });
        return future;
    }

    private static void preloadSurroundingChunks(World world, int chunkX, int chunkZ) {
        for (int x = -CHUNK_LOAD_RADIUS; x <= CHUNK_LOAD_RADIUS; x++) {
            for (int z = -CHUNK_LOAD_RADIUS; z <= CHUNK_LOAD_RADIUS; z++) {
                if (x == 0 && z == 0) continue;
                world.getChunkAtAsync(chunkX + x, chunkZ + z);
            }
        }
    }

    private static boolean isCooldownActive(Player player, String worldName) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) return false;
        Long cooldownTime = playerCooldowns.get(worldName);
        return cooldownTime != null && System.currentTimeMillis() < cooldownTime;
    }

    private static void setCooldown(Player player, String worldName, long millis) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>()).put(worldName, System.currentTimeMillis() + millis);
    }
}
