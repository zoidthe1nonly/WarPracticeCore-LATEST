package dev.zoid.warpracticecore.utils;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TierUtil {
    private final Sqlite sqlite;
    private final JavaPlugin plugin;
    private static final Set<String> VALID_TIERS = new HashSet<>(Arrays.asList(
            "<#FF0000>HT1", "<#FFA500>HT2", "<#808080>HT3", "<#D3D3D3>HT4", "<#D3D3D3>HT5",
            "<#D3D3D3>LT1", "<white>LT2", "<#EEE8AA>LT3", "<#90EE90>LT4", "<#D3D3D3>LT5"
    ));

    public TierUtil(Sqlite sqlite, JavaPlugin plugin) {
        this.sqlite = sqlite;
        this.plugin = plugin;
    }

    public void setTier(String player, String tier) {
        if (!VALID_TIERS.contains(tier)) {
            throw new IllegalArgumentException("Invalid tier");
        }
        sqlite.setTierAsync(player, tier);
    }

    public String getTier(String player) {
        return sqlite.getTier(player);
    }

    public List<String> getPlayerSuggestions(String partial) {
        return sqlite.getPlayersLike(partial);
    }

    public void removeTier(String player) {
        sqlite.removeTierAsync(player);
    }
}
