package dev.zoid.warpracticecore.placeholders;

import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import dev.zoid.warpracticecore.utils.TierUtil;

public class TierPlaceholder extends PlaceholderExpansion {
    private final TierUtil tierUtil;

    public TierPlaceholder(TierUtil tierUtil) {
        this.tierUtil = tierUtil;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "wartiers";
    }

    @Override
    public String getPlugin() {
        return "WarPracticeCore";
    }

    @Override
    public String getAuthor() {
        return "Zoid";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return "";
        if (identifier.equals("tier")) {
            String tier = tierUtil.getTier(player.getName());
            return tier != null ? tier : "";
        }
        return null;
    }
}
