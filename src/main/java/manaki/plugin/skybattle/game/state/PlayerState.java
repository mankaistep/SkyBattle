package manaki.plugin.skybattle.game.state;

import org.bukkit.entity.Player;

public class PlayerState {

    private final Player player;
    private boolean isDead;

    public PlayerState(Player player, boolean isDead) {
        this.player = player;
        this.isDead = isDead;
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
}
