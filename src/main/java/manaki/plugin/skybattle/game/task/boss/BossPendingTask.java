package manaki.plugin.skybattle.game.task.boss;

import io.lumine.xikage.mythicmobs.MythicMobs;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.task.a.APendingTask;
import manaki.plugin.skybattle.game.util.Games;
import org.bukkit.entity.LivingEntity;

public class BossPendingTask extends APendingTask {

    public BossPendingTask(GameState state, long period, String message) {
        super(state, "bossPending", period, message);
    }

    @Override
    public Runnable getStarter() {
        return () -> {
            var state = this.getState();
            var bm = Games.battleFromState(state);
            var mm = Games.mapFromState(state);
            var l = mm.getLocation(mm.getCenterLocation()).toCenterLocation(state.getWorldState().toWorld());
            var am = MythicMobs.inst().getMobManager().spawnMob(bm.getBossId(), l);

            var le = (LivingEntity) am.getEntity().getBukkitEntity();
            le.setRemoveWhenFarAway(false);
            le.setGlowing(true);

            state.setBoss(le);
        };
    }

}
