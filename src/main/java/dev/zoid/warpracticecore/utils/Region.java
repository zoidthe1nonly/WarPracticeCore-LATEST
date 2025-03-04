package dev.zoid.warpracticecore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class Region implements Serializable {
    private final String name;
    private transient Location point1; // Marked as transient
    private transient Location point2; // Marked as transient

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
            outputStream.writeObject(regions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadRegions() {
        File file = new File(plugin.getDataFolder(), regionsFileName);
        if (!file.exists()) return;

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            Object loadedRegions = inputStream.readObject();
            if (loadedRegions instanceof ConcurrentHashMap) {
                regions.putAll((ConcurrentHashMap<String, Region>) loadedRegions);
            }
        } catch (EOFException e) {
            System.err.println("EOFException caught during region loading. The file may be corrupted or empty.");
            e.printStackTrace();
            regions.clear();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    public void handleBlockBurn(BlockBurnEvent event) {
        if (contains(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    public void handleBlockIgnite(BlockIgniteEvent event) {
        if (contains(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    public void handlePlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (contains(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
        }
    }

    public void handlePlayerBucketFill(PlayerBucketFillEvent event) {
        if (contains(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
        }
    }


    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(point1.getWorld().getName());
        out.writeDouble(point1.getX());
        out.writeDouble(point1.getY());
        out.writeDouble(point1.getZ());
        out.writeFloat(point1.getYaw());
        out.writeFloat(point1.getPitch());

        out.writeObject(point2.getWorld().getName());
        out.writeDouble(point2.getX());
        out.writeDouble(point2.getY());
        out.writeDouble(point2.getZ());
        out.writeFloat(point2.getYaw());
        out.writeFloat(point2.getPitch());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        String worldName1 = (String) in.readObject();
        double x1 = in.readDouble();
        double y1 = in.readDouble();
        double z1 = in.readDouble();
        float yaw1 = in.readFloat();
        float pitch1 = in.readFloat();
        World world1 = Bukkit.getWorld(worldName1);
        Location point1 = new Location(world1, x1, y1, z1, yaw1, pitch1);

        String worldName2 = (String) in.readObject();
        double x2 = in.readDouble();
        double y2 = in.readDouble();
        double z2 = in.readDouble();
        float yaw2 = in.readFloat();
        float pitch2 = in.readFloat();
        World world2 = Bukkit.getWorld(worldName2);
        Location point2 = new Location(world2, x2, y2, z2, yaw2, pitch2);
        java.lang.reflect.Field point1Field;
        java.lang.reflect.Field point2Field;
        try {
            point1Field = Region.class.getDeclaredField("point1");
            point2Field = Region.class.getDeclaredField("point2");
            point1Field.setAccessible(true);
            point2Field.setAccessible(true);
            point1Field.set(this, point1);
            point2Field.set(this, point2);
        } catch (Exception e) {
            e.printStackTrace();
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