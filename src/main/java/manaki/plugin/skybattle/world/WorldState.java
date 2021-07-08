package manaki.plugin.skybattle.world;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldState {

    private final int id;
    private final String worldSource;

    public WorldState(int id, String worldSource) {
        this.id = id;
        this.worldSource = worldSource;
    }

    public int getId() {
        return id;
    }

    public String getWorldSource() {
        return worldSource;
    }

    public String toWorldName() {
        return worldSource + "_" + id;
    }

    public World toWorld() {
        return Bukkit.getWorld(toWorldName());
    }

    public static WorldState parse(String s) {
        var worldSource = s.split(" ")[0];
        var id = Integer.parseInt(s.split(" ")[1]);
        return new WorldState(id, worldSource);
    }

}
