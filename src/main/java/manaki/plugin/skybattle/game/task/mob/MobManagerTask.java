package manaki.plugin.skybattle.game.task.mob;

import io.lumine.xikage.mythicmobs.MythicMobs;
import manaki.plugin.skybattle.area.Areas;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.util.Utils;
import org.bukkit.entity.Player;

import java.util.Random;

public class MobManagerTask extends ATask {

    private final double RADIUS_CHECK = 10;

    public MobManagerTask(GameState state) {
        super(state);
    }

    @Override
    public void run() {
        // Check end
        if (this.getState().isEnded()) {
            this.selfDestroy();
            return;
        }

        // Check time
        var bm = Games.battleFromState(this.getState());
        if (this.getState().getTime() < bm.getMobSpawnAfter()) return;

        this.spawnMobs();
    }

    public void spawnMobs() {
        var state = this.getState();

        // No mob-spawning in last border
        if (state.isLastBorder()) {
            this.selfDestroy();
            return;
        }

        // Spawn
        var bm = Games.battleFromState(state);
        var mobm = bm.getMobModel();
        for (Player p : state.getPlayers()) {
            int cp = Utils.countPlayersAround(p, RADIUS_CHECK);
            int cm = Utils.countMythicMobsAround(p, RADIUS_CHECK);

            // Check amount
            if (cm >= mobm.getLimitPerPlayer() * (cp + 1)) continue;

            // Rate
            if (Utils.rate(mobm.getSpawnRate())) {
                // Get safe location
                var l = Utils.randomSafeLocation(p.getLocation(), Double.valueOf(RADIUS_CHECK).intValue());
                if (l == null) continue;

                // Random mob
                var area = Areas.check(l);
                var availMobs = mobm.getMobTypes().get(area);
                if (availMobs == null) continue;

                // Spawn
                var mid = availMobs.get(new Random().nextInt(availMobs.size()));
                MythicMobs.inst().getMobManager().spawnMob(mid, l);
            }
        }
    }
}
