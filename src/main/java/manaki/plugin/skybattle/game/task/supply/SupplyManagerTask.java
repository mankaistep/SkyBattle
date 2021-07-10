package manaki.plugin.skybattle.game.task.supply;

import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.state.SupplyState;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.util.Games;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Chest;

import java.util.ArrayList;
import java.util.List;

public class SupplyManagerTask extends ATask {

    private final List<Integer> supplySpawned;

    public SupplyManagerTask(GameState state) {
        super(state);
        this.supplySpawned = new ArrayList<>();
    }

    @Override
    public void run() {
        this.spawnSupplyEffect();
        this.spawnPendingSupply();
    }

    public void spawnSupplyEffect() {
        var state = this.getState();
        for (SupplyState ss : state.getSupplyStates()) {
            if (ss.isOpened()) continue;
            var l = ss.getLocation();
            for (int i = 0 ; i < 100 ; i++) {
                var pl = l.clone().add(0, i + 1, 0);
                pl.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, pl, 1, 0, 0, 0, 0);
            }
        }
    }

    public void spawnPendingSupply() {
        var state = this.getState();
        var bm = Games.battleFromState(state);
        for (Integer time : bm.getSupplyModel().getAppearTime()) {
            var currentTime = state.getTime();
            int remain = time - currentTime;
            if (remain <= 10 && !supplySpawned.contains(time)) {
                supplySpawned.add(time);
                var ss = Games.randomizeSupply(this.getState());
                if (ss == null) {
                    SkyBattle.get().getLogger().info("Can't find any appropriate locations for supply time: " + time);
                    return;
                }
                var spt = new SupplyPendingTask(state,
                        remain * 1000L,
                        "§6§lXuất hiện Hòm tiếp tế (x: " + ss.getLocation().getBlockX() + ", z: " +ss.getLocation().getBlockZ() + ")",
                        ss);
                spt.start();
            }
        }
    }

}
