package manaki.plugin.skybattle.team;

import com.google.common.collect.Lists;
import org.bukkit.Color;

import java.util.List;

public class BattleTeam {

    private final Color color;
    private final List<String> players;

    public BattleTeam(Color color, List<String> list) {
        this.color = color;
        this.players = list;
    }

    public Color getColor() {
        return color;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void add(String player) {
        players.add(player);
    }

    public void remove(String player) {
        players.remove(player);
    }

    public BattleTeam cloneTeam() {
        return new BattleTeam(this.color, Lists.newArrayList(this.players));
    }
}
