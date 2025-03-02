package dev.zoid.warpracticecore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class Region {
    private final String name;
    private final Location point1;
    private final Location point2;

    private static final ConcurrentHashMap<String, Region> regions = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Player, Pair<Location, Location>> selectors = new ConcurrentHashMap<>();
    private static final String regionsFileName = "regions.dat";
    private static JavaPlugin plugin;

    public Region(String name, Location point1, Location point2) {
        this.name = name;
        this.point1 = point1;
        this.point2 = point2;
    }

    public static void initialize(JavaPlugin plugin) {
        Region.plugin = plugin;
    }

    public static Region getRegion(String name) {
        return regions.get(name);
    }

    public static void addRegion(Region region) {
        regions.put(region.name, region);
    }

    public static void removeRegion(String name) {
        regions.remove(name);
    }

    public static Pair<Location, Location> getSelector(Player player) {
        return selectors.get(player);
    }

    public static void setSelector(Player player, Location point1, Location point2) {
        selectors.put(player, new Pair<>(point1, point2));
    }

    public static void clearSelector(Player player) {
        selectors.remove(player);
    }

    public static Collection<Region> getAllRegions() {
        return regions.values();
    }

    public static void saveRegions() {
        File file = new File(plugin.getDataFolder(), regionsFileName);
        file.getParentFile().mkdirs();

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            for (Region region : regions.values()) {
                outputStream.writeObject(region.name);
                outputStream.writeObject(serializeLocation(region.point1));
                outputStream.writeObject(serializeLocation(region.point2));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadRegions() {
        File file = new File(plugin.getDataFolder(), regionsFileName);
        if (!file.exists()) return;

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    String name = (String) inputStream.readObject();
                    String point1Str = (String) inputStream.readObject();
                    String point2Str = (String) inputStream.readObject();
                    Location point1 = deserializeLocation(point1Str);
                    Location point2 = deserializeLocation(point2Str);
                    regions.put(name, new Region(name, point1, point2));
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String serializeLocation(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
    }

    private static Location deserializeLocation(String serialized) {
        String[] parts = serialized.split(",");
        String worldName = parts[0];
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);

        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    public boolean contains(Location location) {
        int minX = Math.min(point1.getBlockX(), point2.getBlockX());
        int maxX = Math.max(point1.getBlockX(), point2.getBlockX());
        int minY = Math.min(point1.getBlockY(), point2.getBlockY());
        int maxY = Math.max(point1.getBlockY(), point2.getBlockY());
        int minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());
        int maxZ = Math.max(point1.getBlockZ(), point2.getBlockZ());

        return location.getBlockX() >= minX && location.getBlockX() <= maxX &&
                location.getBlockY() >= minY && location.getBlockY() <= maxY &&
                location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ;
    }

    public void handleBlockBreak(BlockBreakEvent event) {
        if (contains(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    public void handleBlockPlace(BlockPlaceEvent event) {
        if (contains(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    public void handleEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && contains(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    public void handleEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> contains(block.getLocation()));
    }

    public void handleBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> contains(block.getLocation()));
    }

    public void handleEntityDamage(EntityDamageEvent event) {
        if (contains(event.getEntity().getLocation()) &&
                (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            event.setCancelled(true);
        }
    }

    public static class Pair<A, B> {
        private final A first;
        private final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        public A getFirst() {
            return first;
        }

        public B getSecond() {
            return second;
        }
    }
}
