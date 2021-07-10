package manaki.plugin.skybattle.game.task.supply;

import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.state.SupplyState;
import manaki.plugin.skybattle.game.task.a.APendingTask;
import manaki.plugin.skybattle.game.util.Games;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;

public class SupplyPendingTask extends APendingTask {

    private SupplyState ss;

    public SupplyPendingTask(GameState state, long period, String message, SupplyState ss) {
        super(state, "supplyPending", period, message);
        this.ss = ss;
    }

    @Override
    public Runnable getStarter() {
        return () -> {
            var l = ss.getLocation();
            // Spawn
            Shulker shulker = (Shulker) l.getWorld().spawnEntity(l.getBlock().getLocation().add(0.5, 0, 0.5), EntityType.SHULKER);
            shulker.setAI(false);
            shulker.setGlowing(true);
            shulker.setCustomName("§cĐánh em đi");
            shulker.setCustomNameVisible(true);

            ss.setShulker(shulker);
            Games.setSpecialEntity(shulker);

            // Set
            this.getState().addSupply(ss);
        };
    }

}
