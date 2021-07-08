package manaki.plugin.skybattle.game.task.boss;

import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.task.a.ATask;

public class BossManagerTask extends ATask {

    private boolean spawned;

    public BossManagerTask(GameState state) {
        super(state);
        this.spawned = false;
    }

    @Override
    public void run() {
        this.spawnBoss();
    }

    public void spawnBoss() {
        var state = this.getState();

        if (spawned) {
            this.selfDestroy();
            return;
        }

        // Only spawn in last border
        if (!state.isLastBorder()) {
            return;
        }

        // Spawn
        new BossPendingTask(state, 30000, "§6§lTriệu hồi trùm cuối").start();
        this.spawned = true;
    }
}
