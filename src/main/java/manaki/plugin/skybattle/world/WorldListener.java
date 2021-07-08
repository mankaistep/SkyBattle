package manaki.plugin.skybattle.world;

import manaki.plugin.skybattle.SkyBattle;
import org.bukkit.GameRule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {

    private final SkyBattle plugin;

    public WorldListener(SkyBattle plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        var w = e.getWorld();
        if (plugin.getWorldManager().isBattleWorld(w.getName())) {
            plugin.getLogger().warning("Detect temporary world load " + w.getName());
            w.setKeepSpawnInMemory(false);
            w.setAutoSave(false);
            w.setGameRule(GameRule.DISABLE_RAIDS, true);
            w.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
            w.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        }
    }

}
