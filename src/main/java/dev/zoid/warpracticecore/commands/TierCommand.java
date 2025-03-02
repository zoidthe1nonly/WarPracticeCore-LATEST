package dev.zoid.warpracticecore.commands;

import dev.zoid.warpracticecore.WarPracticeCore;
import dev.zoid.warpracticecore.utils.TierUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class TierCommand implements CommandExecutor {
    private final TierUtil tierUtil;

    public TierCommand(JavaPlugin plugin) {
        this.tierUtil = ((WarPracticeCore) plugin).getTierUtil();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("warpractice.tier")) {
            sender.sendRichMessage("<red>You don't have permission to use this command.");
            return true;
        }
        if (args.length < 2) {
            sender.sendRichMessage("<red>Usage: /tier set/get/remove <player> [tier]");
            return true;
        }
        String subCommand = args[0].toLowerCase();
        String player = args[1];
        if (subCommand.equals("set")) {
            if (args.length != 3) {
                sender.sendRichMessage("<red>Usage: /tier set <player> <tier>");
                return true;
            }
            String tier = args[2];
            try {
                tierUtil.setTier(player, tier);
                sender.sendRichMessage("<green>Set " + player + "'s tier to " + tier);
            } catch (IllegalArgumentException e) {
                sender.sendRichMessage("<red>Invalid tier: " + tier + ". Must be HT1, LT1, HT2, LT2, HT3, LT3, HT4, LT4, or LT5.");
            }
        } else if (subCommand.equals("get")) {
            String tier = tierUtil.getTier(player);
            sender.sendRichMessage(player + "'s tier: " + (tier != null ? tier : "<#D3D3D3>N/A"));
        } else if (subCommand.equals("remove")) {
            if (args.length != 2) {
                sender.sendRichMessage("<red>Usage: /tier remove <player>");
                return true;
            }
            tierUtil.removeTier(player);
            sender.sendRichMessage("<green>Removed tier for " + player);
        } else {
            sender.sendRichMessage("<red>Usage: /tier set/get/remove <player> [tier]");
        }
        return true;
    }
}
