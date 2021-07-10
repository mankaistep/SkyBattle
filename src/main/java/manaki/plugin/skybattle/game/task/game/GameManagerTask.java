package manaki.plugin.skybattle.game.task.game;

import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.team.Team;
import org.bukkit.Bukkit;

public class GameManagerTask extends ATask {

    public GameManagerTask(GameState state) {
        super(state);
    }

    @Override
    public void run() {
        checkValidPlayer();
        checkEnd();
    }

    /*
    Invalid if:
        1. Not online
        2. Not in the right world
     */
    public void checkValidPlayer() {
        var state = this.getState();
        var gm = Games.managerFromState(state);
        for (Team team : state.getCurrentTeams()) {
            var iter = team.getPlayers().iterator();
            while (iter.hasNext()) {
                var pn = iter.next();
                var p = Bukkit.getPlayer(pn);
                if (p != null) {
                    if (Games.isByPassedInvalid(p)) continue;

                    // Invalid
                    if (p.getWorld() != state.getWorldState().toWorld()) {
                        iter.remove();
                        gm.playerDead(p);
                    }
                }

                // Invalid
                if (p == null) {
                    iter.remove();
                    gm.playerQuit(pn);
                }
            }
        }
    }

    public void checkEnd() {
        var state = this.getState();
        if (state.canFinish()) {
            this.selfDestroy();
            Games.managerFromState(state).finish(false);
        }
    }

}
