package manaki.plugin.skybattle.game.task.a;

import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.state.GameState;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class ATask extends BukkitRunnable {

    private final GameState state;

    public ATask(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return state;
    }

    public void selfDestroy() {
        this.cancel();
        state.removeTask(this);
    }

    public ATask start() {
        state.addTask(this);
        this.runTaskTimerAsynchronously(SkyBattle.get(), 0, 10);
        return this;
    }

}
