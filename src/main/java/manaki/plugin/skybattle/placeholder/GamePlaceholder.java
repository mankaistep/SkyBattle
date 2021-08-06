package manaki.plugin.skybattle.placeholder;

import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.Games;
import manaki.plugin.skybattle.util.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GamePlaceholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "skybattle";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Manaki";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player p, @NotNull String s) {
        try {
            var state = Games.getCurrentGame(p);
            if (state != null) {
                var ps = state.getPlayerState(p.getName());
                if (ps.isDead()) return "...";
                switch (s) {
                    case "homtiepte":
                        var remain = Games.getSupplyRemain(state) - 1;
                        if (remain == Integer.MAX_VALUE) return "Không còn";
                        return Utils.format(remain);
                    case "player_remain":
                        return state.getPlayers().size() + "";
                    case "player_max":
                        return state.getType().getPlayersInTeam() * 8 + "";
                    case "team_remain":
                        return state.getTeamAlive() + "";
                    case "team_max":
                        return 8 + "";
                    case "battle_name":
                        var bm = Games.battleFromState(state);
                        return bm.getName();
                    case "battle_type":
                        return state.getType().getName();
                    case "player_assists": {
                        return ps.getAssist() + "";
                    }
                    case "player_kills": {
                        return ps.getKill() + "";
                    }
                }
            }
        }
        catch (Exception e) {
            SkyBattle.get().getLogger().severe("Placeholder exception: " + e.getMessage());
            return "...";
        }

        return "...";
    }
}
