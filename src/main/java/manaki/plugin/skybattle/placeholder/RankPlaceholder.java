package manaki.plugin.skybattle.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RankPlaceholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "skybattleclient";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MankaiStep";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String s) {
        try {
            if (s.contains("rank_display")) {
                return "§r§4§l⚔";
            }
        }
        catch (Exception e) {
            return "...";
        }

        return "Wrong!";
    }
}
