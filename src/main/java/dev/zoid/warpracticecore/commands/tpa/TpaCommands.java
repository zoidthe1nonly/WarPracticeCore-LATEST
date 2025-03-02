package dev.zoid.warpracticecore.commands.tpa;

import dev.zoid.warpracticecore.utils.TpaUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class TpaCommands implements CommandExecutor {
    private final Plugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public TpaCommands(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(miniMessage.deserialize("<red>Only players can use this command."));
            return true;
        }
        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase();
        switch (cmd) {
            case "tpa":
                if (args.length == 0) {
                    player.sendMessage(miniMessage.deserialize("<red>Usage: /tpa <player>"));
                    return true;
                }
                Player target = plugin.getServer().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(miniMessage.deserialize("<red>Player not found."));
                    return true;
                }
                TpaUtil.sendTPARequest(player, target, plugin);
                break;
            case "tpahere":
                if (args.length == 0) {
                    player.sendMessage(miniMessage.deserialize("<red>Usage: /tpahere <player>"));
                    return true;
                }
                target = plugin.getServer().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(miniMessage.deserialize("<red>Player not found."));
                    return true;
                }
                TpaUtil.sendTPAHereRequest(player, target, plugin);
                break;
            case "tpaccept":
                String senderName = args.length > 0 ? args[0] : null;
                TpaUtil.acceptTPARequest(player, senderName, plugin);
                break;
            case "tpdeny":
                if (args.length == 0) {
                    player.sendMessage(miniMessage.deserialize("<red>Usage: /tpdeny <player>"));
                    return true;
                }
                senderName = args[0];
                TpaUtil.denyTPARequest(player, senderName);
                break;
            case "tpacancel":
                TpaUtil.cancelTPARequest(player);
                break;
            case "back":
                TpaUtil.teleportToDeathLocation(player);
                break;
            default:
                player.sendMessage(miniMessage.deserialize("<red>Unknown command."));
                break;
        }
        return true;
    }
}
