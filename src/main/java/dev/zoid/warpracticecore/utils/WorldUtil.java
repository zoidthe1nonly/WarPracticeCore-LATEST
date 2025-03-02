package dev.zoid.warpracticecore.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.HashSet;
import java.util.Set;

public class WorldUtil {
    private static final Set<String> worldsWithGameRulesSet = new HashSet<>();

    public static void loadWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = new WorldCreator(worldName).createWorld();
            if (world == null) {
                Bukkit.getLogger().severe("Failed to create world: " + worldName);
                return;
            }
        }
        if (!worldsWithGameRulesSet.contains(worldName)) {
            setGameRules(world);
            worldsWithGameRulesSet.add(worldName);
        }
    }

    private static void setGameRules(World world) {
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
    }
}