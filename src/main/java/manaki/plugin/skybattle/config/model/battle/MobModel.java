package manaki.plugin.skybattle.config.model.battle;

import manaki.plugin.skybattle.area.AreaType;

import java.util.List;
import java.util.Map;

public class MobModel {

    private final Map<AreaType, List<String>> mobTypes;
    private final int limitPerPlayer;
    private final float spawnRate;
    private final Map<String, List<MobDropModel>> drops;

    public MobModel(Map<AreaType, List<String>> mobTypes, int limitPerPlayer, float spawnRate, Map<String, List<MobDropModel>> drops) {
        this.mobTypes = mobTypes;
        this.limitPerPlayer = limitPerPlayer;
        this.spawnRate = spawnRate;
        this.drops = drops;
    }

    public Map<AreaType, List<String>> getMobTypes() {
        return mobTypes;
    }

    public int getLimitPerPlayer() {
        return limitPerPlayer;
    }

    public float getSpawnRate() {
        return spawnRate;
    }

    public Map<String, List<MobDropModel>> getDrops() {
        return drops;
    }

}
