package manaki.plugin.skybattle.game.task.game;

import manaki.plugin.skybattle.game.Games;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.task.a.ATask;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class GameManagerTask extends ATask {

    public GameManagerTask(GameState state) {
        super(state, 5);
    }

    @Override
    public void run() {
        damageLowYPlayers(this.getState());
        checkEnd();
    }

    public void damageLowYPlayers(GameState state) {
        var bm = Games.battleFromState(state);
        for (Player p : state.getPlayers()) {
            var ps = state.getPlayerState(p.getName());
            if (ps.isDead()) continue;

            int lowY = bm.getSetting().get("low-y", Integer.class);
            if (p.getLocation().getY() < lowY) {
                var maxhealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                var d = maxhealth / 50;
                p.damage(d);
                p.sendActionBar(new TextComponent("§c§lBạn bị sát thương khi dưới y < " + lowY));
            }
        }
    }


    public void checkEnd() {
        var state = this.getState();
        if (!state.isEnded() && state.canFinish()) {
            this.selfDestroy();
            Games.managerFromState(state).finish(false);
        }
    }

}
