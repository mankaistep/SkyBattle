package manaki.plugin.skybattle.game.task.supply;

import com.google.common.collect.Lists;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.task.a.APendingTask;
import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.util.Utils;
import org.bukkit.Material;
import org.bukkit.block.Chest;

import java.util.List;

public class SupplyPendingTask extends APendingTask {

    public SupplyPendingTask(GameState state, long period, String message) {
        super(state, "supplyPending", period, message);
    }

    @Override
    public Runnable getStarter() {
        return () -> {
            var ss = Games.randomizeSupply(this.getState());
            var l = ss.getLocation();

            // Spawn chest
            var b = l.getBlock();
            l.getWorld().strikeLightningEffect(l);
            b.setType(Material.CHEST);

            // Add items
            Chest chest = (Chest) b.getState();
            var inv = chest.getInventory();
            List<Integer> slots = Lists.newArrayList();
            int amount = ss.getItems().size();
            for (int i = 0 ; i < inv.getSize() ; i++) slots.add(i);
            Utils.random(slots, amount);
            for (int i = 0; i < slots.size(); i++) {
                var is = ss.getItems().get(i);
                inv.setItem(slots.get(i), is);
            }

            // Set
            this.getState().addSupply(ss);
        };
    }

}
