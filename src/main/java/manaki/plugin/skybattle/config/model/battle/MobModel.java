package manaki.plugin.skybattle.config.model.battle;

import manaki.plugin.skybattle.area.AreaType;

import java.util.List;
import java.util.Map;

public class MobModel {

    private final Map<AreaType, List<String>> mobTypes;
    private final int limitPerPlayer;
    private final float spawnRate;
    private final Map<String, List<MobDropModel>> drops;
    private final Map<String, List<KillCmdModel>> killCmds;

    public MobModel(Map<AreaType, List<String>> mobTypes, int limitPerPlayer, float spawnRate, Map<String, List<MobDropModel>> drops, Map<String, List<KillCmdModel>> killCmds) {
        this.mobTypes = mobTypes;
        this.limitPerPlayer = limitPerPlayer;
        this.spawnRate = spawnRate;
        this.drops = drops;
        this.killCmds = killCmds;
    }

    public Map<String, List<KillCmdModel>> getKillCmds() {
        return killCmds;
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
