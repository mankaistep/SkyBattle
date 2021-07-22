package manaki.plugin.skybattle.game.state;

import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

import java.util.Set;

public class PlayerState {

    private final Player player;
    private boolean isDead;

    private final Set<String> damaged;
    private int kill;
    private int assist;

    public PlayerState(Player player, boolean isDead) {
        this.player = player;
        this.isDead = isDead;
        this.damaged = Sets.newHashSet();
        this.kill = 0;
        this.assist = 0;
    }

    public Player getPlayer() {
        return player;
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
        return kill;
    }

    public int getAssist() {
        return assist;
    }

    public void addDamaged(String name) {
        this.damaged.add(name);
    }

    public void addKill(int value) {
        this.kill += value;
    }

    public void addAssist(int value) {
        this.assist += value;
    }

}
