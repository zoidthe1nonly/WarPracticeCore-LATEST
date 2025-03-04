package dev.zoid.warpracticecore.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R3.CraftServer;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class Managers {

    public final WorldManager WORLD = new WorldManager();
    public final ServerManager SERVER = new ServerManager();

    public static class WorldManager {
        public final World bukkitOverworld = Bukkit.getWorlds().get(0);
        public final World bukkitSpawn;
        public final ServerLevel nmsOverworld;
        public final ServerLevel nmsSpawn;

        private WorldManager() {
            this.bukkitSpawn = getSpawnWorld();
            this.nmsOverworld = ((CraftWorld) bukkitOverworld).getHandle();
            this.nmsSpawn = ((CraftWorld) bukkitSpawn).getHandle();
        }

        @NotNull
        private World getSpawnWorld() {
            World world = Bukkit.getWorld("spawn");
            if (world == null) {
                throw new IllegalStateException("Spawn world not found!");
            }
            return world;
        }
    }
    public static class ServerManager {
        public final MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
    }
}