package dev.zoid.warpracticecore.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager {
    public static void register(JavaPlugin plugin, Object... args) {
        int i = 0;
        while (i < args.length) {
            String commandName = (String) args[i++];
            Class<? extends CommandExecutor> commandClass = (Class<? extends CommandExecutor>) args[i++];
            try {
                CommandExecutor executor;
                try {
                    executor = commandClass.getDeclaredConstructor(JavaPlugin.class).newInstance(plugin);
                } catch (NoSuchMethodException e) {
                    executor = commandClass.getDeclaredConstructor().newInstance();
                }
                PluginCommand command = plugin.getCommand(commandName);
                if (command == null) {
                    plugin.getLogger().warning("Command '" + commandName + "' not found in plugin.yml");
                    continue;
                }
                command.setExecutor(executor);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to register '" + commandName + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}