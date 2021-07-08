package manaki.plugin.skybattle.world;

import manaki.plugin.skybattle.SkyBattle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorldManager {

    private final SkyBattle plugin;

    private final List<WorldState> activeWorlds;

    public WorldManager(SkyBattle plugin) {
        this.plugin = plugin;
        this.activeWorlds = new ArrayList<>();
    }

    public boolean isBattleWorld(String worldName) {
        for (var mm : plugin.getMainConfig().getMapModels().values()) {
            if (worldName.startsWith(mm.getWorldName() + "_")) {
                return true;
            }
        }
        return false;
    }

    public List<String> getActiveWorldNames() {
        return activeWorlds.stream().map(WorldState::toWorldName).collect(Collectors.toList());
    }

    public void addActiveWorld(WorldState cache) {
        activeWorlds.add(cache);
    }

    public void removeActiveWorld(WorldState cache) { ;
        activeWorlds.remove(cache);
    }

    public List<String> getActiveWorlds() {
        return activeWorlds.stream().map(WorldState::toWorldName).collect(Collectors.toList());
    }

}
