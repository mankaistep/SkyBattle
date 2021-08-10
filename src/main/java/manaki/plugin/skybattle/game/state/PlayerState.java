package manaki.plugin.skybattle.game.state;

import com.google.common.collect.Sets;
import manaki.plugin.skybattle.game.state.result.PlayerResult;
import org.bukkit.entity.Player;

import java.util.Set;

public class PlayerState {

    private boolean isDead;

    private final Set<String> damaged;
    private final PlayerResult result;

    public PlayerState(String playerName, GameState state, boolean isDead) {
        this.isDead = isDead;
        this.damaged = Sets.newHashSet();
        this.result = new PlayerResult(playerName, state.getType().getPlayersInTeam(), state.isRanked());
    }

    public PlayerState(GameState state, Player player, boolean isDead) {
        this.isDead = isDead;
        this.damaged = Sets.newHashSet();
        this.result = new PlayerResult(player.getName(), state.getType().getPlayersInTeam(), state.isRanked());
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public Set<String> getDamaged() {
        return damaged;
    }


    public int getKill() {
        return this.result.getStatistic().getKill();
    }

    public int getAssist() {
        return this.result.getStatistic().getAssist();
    }

    public void addDamaged(String name) {
        this.damaged.add(name);
    }

    public void addKill(int value) {
        this.result.getStatistic().addKill(value);
    }

    public void addAssist(int value) {
        this.result.getStatistic().addAssist(value);
    }

    public PlayerResult getResult() {
        return result;
    }
}
