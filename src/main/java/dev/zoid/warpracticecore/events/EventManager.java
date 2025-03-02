package dev.zoid.warpracticecore.events;

import dev.zoid.warpracticecore.WarPracticeCore;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class EventManager {

    @SafeVarargs
    public static void register(@NotNull Plugin plugin, Class<? extends Listener>... classes) {
        try {
            for (Class<? extends Listener> clazz : classes) {
                Listener listener;

                try {
                    listener = clazz.getDeclaredConstructor(Plugin.class).newInstance(plugin);
                } catch (NoSuchMethodException e) {
                    listener = clazz.getDeclaredConstructor().newInstance();
                }

                Bukkit.getPluginManager().registerEvents(listener, plugin);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to register event listener: " + e.getMessage(), e);
        }
    }
}

