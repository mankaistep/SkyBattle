package manaki.plugin.skybattle.game.task.supply;

import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.Games;

import java.util.ArrayList;
import java.util.List;

public class SupplyManagerTask extends ATask {

    private final List<Integer> supplySpawned;

    public SupplyManagerTask(GameState state) {
        super(state, 5);
        this.supplySpawned = new ArrayList<>();
    }

    @Override
    public void run() {
        this.spawnPendingSupply();
    }

    public void spawnPendingSupply() {
        var state = this.getState();
        var bm = Games.battleFromState(state);
        for (Integer time : bm.getSupplyModel().getAppearTime()) {
            var currentTime = state.getTime();
            int remain = time - currentTime;
            if (remain <= 30 && !supplySpawned.contains(time)) {
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
