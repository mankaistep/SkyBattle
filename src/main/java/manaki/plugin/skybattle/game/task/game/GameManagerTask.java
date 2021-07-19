package manaki.plugin.skybattle.game.task.game;

import com.google.common.collect.Lists;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.team.BattleTeam;
import org.bukkit.Bukkit;

public class GameManagerTask extends ATask {

    public GameManagerTask(GameState state) {
        super(state, 5);
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
        for (BattleTeam battleTeam : state.getCurrentTeams()) {
            var iter = battleTeam.getPlayers().iterator();
            while (iter.hasNext()) {
                var pn = iter.next();
                var p = Bukkit.getPlayer(pn);
                if (p != null) {
                    if (Games.isByPassedInvalid(p)) continue;

                    // Invalid
                    if (p.getWorld() != state.getWorldState().toWorld()) {
                        System.out.println("Before: " + state.getStartTeams().get(0).getPlayers().size() + " " + state.getStartTeams().get(1).getPlayers().size());
                        iter.remove();
                        gm.playerDead(p);
                        System.out.println("After: " + state.getStartTeams().get(0).getPlayers().size() + " " + state.getStartTeams().get(1).getPlayers().size());
                    }
                }

                // Invalid
                if (p == null) {
                    System.out.println("Before quit: " + state.getStartTeams().get(0).getPlayers().size() + " " + state.getStartTeams().get(1).getPlayers().size());
                    iter.remove();
                    gm.playerQuit(pn);
                    System.out.println("After quit: " + state.getStartTeams().get(0).getPlayers().size() + " " + state.getStartTeams().get(1).getPlayers().size());
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
