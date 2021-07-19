package manaki.plugin.skybattle.game.task.a;

import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.state.GameState;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class ATask extends BukkitRunnable {

    private final GameState state;
    private final int tickLoop;

    public ATask(GameState state, int tickLoop) {
        this.state = state;
        this.tickLoop = tickLoop;
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
        this.runTaskTimer(SkyBattle.get(), 0, this.tickLoop);
        return this;
    }

}
