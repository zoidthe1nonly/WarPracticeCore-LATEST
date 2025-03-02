package dev.zoid.warpracticecore.commands;

import dev.zoid.warpracticecore.utils.Region;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;
import java.util.List;

public class RegionCommand implements CommandExecutor, TabCompleter, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("warpractice.region")) {
            return true;
        }

        Player player = (Player) sender;
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "selector":
                    Region.setSelector(player, null, null);
                    player.sendMessage("Region selector enabled. Right-click and left-click to set points.");
                    return true;
                case "create":
                    if (args.length < 2) {
                        return false;
                    }
                    String name = args[1];
                    Region.Pair<Location, Location> selector = Region.getSelector(player);
                    if (selector == null || selector.getFirst() == null || selector.getSecond() == null) {
                        player.sendMessage("You need to set both points first.");
                        return true;
                    }

                    Region.addRegion(new Region(name, selector.getFirst(), selector.getSecond()));
                    Region.clearSelector(player);
                    player.sendMessage("Region '" + name + "' created successfully.");
                    return true;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("selector", "create");
        }
        return Collections.emptyList();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Region.Pair<Location, Location> selector = Region.getSelector(player);
        if (selector == null) {
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getClickedBlock() != null) {
            Region.setSelector(player, selector.getFirst(), event.getClickedBlock().getLocation());
            player.sendMessage("Second point set at " + event.getClickedBlock().getLocation().toVector());
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            Region.setSelector(player, event.getClickedBlock().getLocation(), selector.getSecond());
            player.sendMessage("First point set at " + event.getClickedBlock().getLocation().toVector());
        }
    }
}
