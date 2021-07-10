package manaki.plugin.skybattle.game.task.game;

import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.util.Games;

public class GameManagerTask extends ATask {

    public GameManagerTask(GameState state) {
        super(state);
    }

    @Override
    public void run() {
        checkEnd();
    }

    public void checkEnd() {
        var state = this.getState();
        if (state.canFinish()) {
            this.selfDestroy();
            Games.managerFromState(state).finish(false);
        }
    }

}
