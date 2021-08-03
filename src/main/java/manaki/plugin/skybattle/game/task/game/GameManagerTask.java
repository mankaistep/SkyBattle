package manaki.plugin.skybattle.game.task.game;

import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.state.PlayerState;
import manaki.plugin.skybattle.game.state.result.PlayerResult;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.Games;
import manaki.plugin.skybattle.team.BattleTeam;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Map;

public class GameManagerTask extends ATask {

    public GameManagerTask(GameState state) {
        super(state, 5);
    }

    @Override
    public void run() {
        damageLowYPlayers(this.getState());
        saveTop(this.getState());
        checkValidPlayer();
        checkEnd();
    }

    public static void damageLowYPlayers(GameState state) {
        var bm = Games.battleFromState(state);
        for (Player p : state.getPlayers()) {
            if (p.getLocation().getY() < bm.getSetting().get("low-y", Integer.class)) {
                var maxhealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                var d = maxhealth / 50;
                p.damage(d);
                p.sendActionBar(new TextComponent("§c§lBạn bị sát thương khi dưới y < 150"));
            }
        }
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
        }
    }

}
