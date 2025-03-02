package dev.zoid.warpracticecore.commands;

import dev.zoid.warpracticecore.utils.TierUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TierTabCompleter implements TabCompleter {
    private final TierUtil tierUtil;
    private static final List<String> SUB_COMMANDS = Arrays.asList("set", "get", "remove");
    private static final List<String> TIERS = Arrays.asList(
            "<#FF0000>HT1", "<#FFA500>HT2", "<#808080>HT3", "<#D3D3D3>HT4", "<#D3D3D3>HT5",
            "<#D3D3D3>LT1",  "<white>LT2", "<#EEE8AA>LT3", "<#90EE90>LT4", "<#D3D3D3>LT5"
    );

    public TierTabCompleter(TierUtil tierUtil) {
        this.tierUtil = tierUtil;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return SUB_COMMANDS.stream()
                    .filter(sc -> sc.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return tierUtil.getPlayerSuggestions(args[1]);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            return TIERS.stream()
                    .filter(tier -> tier.startsWith(args[2]))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}