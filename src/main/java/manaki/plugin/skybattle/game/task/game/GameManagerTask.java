package manaki.plugin.skybattle.game.task.game;

import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.state.PlayerState;
import manaki.plugin.skybattle.game.state.result.PlayerResult;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.Games;
import manaki.plugin.skybattle.team.BattleTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class GameManagerTask extends ATask {

    public GameManagerTask(GameState state) {
        super(state, 5);
    }

    @Override
    public void run() {
        saveTop(this.getState());
        checkValidPlayer();
        checkEnd();
    }

    public static void saveTop(GameState state) {
        for (Player p : state.getAlivePlayers()) {
            var ps = state.getPlayerState(p.getName());
            ps.getResult().setTop(state.getTeamAlive());

            // Send
            var pr = ps.getResult();

            // Send
            SkyBattle.get().getExecutor().sendResult(pr);
        }
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

            // Save winner
            for (Player p : getState().getPlayers()) {
                var ps = state.getPlayerState(p.getName());
                ps.getResult().setWinner(true);
                SkyBattle.get().getExecutor().sendResult(ps.getResult());
            }
        }
    }

}
