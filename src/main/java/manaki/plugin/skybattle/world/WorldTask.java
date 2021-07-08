package manaki.plugin.skybattle.world;

import manaki.plugin.skybattle.SkyBattle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class WorldTask extends BukkitRunnable {

    public static void start(SkyBattle plugin) {
        var wt = new WorldTask(plugin);
        wt.runTaskTimerAsynchronously(plugin, 0, 20);
    }

    private final SkyBattle plugin;

    private WorldTask(SkyBattle plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        var worlds = plugin.getWorldLoader().getPendingCaches();
        for (Map.Entry<WorldState, Long> e : worlds.entrySet()) {
            var k = e.getKey();
            var v = e.getValue();

            // Delete world after 30 mins with no-using
            if ((System.currentTimeMillis() - v) >= (1800 * 1000L)) {
                plugin.getWorldManager().removeActiveWorld(k);
                worlds.remove(k);
                plugin.getWorldLoader().unload(k.toWorldName(), true);
                plugin.getLogger().warning("Removed temporary world " + k.toWorldName());
            }
        }
    }

}
