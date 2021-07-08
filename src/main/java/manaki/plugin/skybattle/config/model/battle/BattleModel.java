package manaki.plugin.skybattle.config.model.battle;

import manaki.plugin.skybattle.area.AreaType;
import manaki.plugin.skybattle.util.command.Command;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public class BattleModel {

    private final String id;
    private final String mapId;
    private final int time;
    private final String bossId;
    private final SettingModel setting;
    private final MobModel mobModel;
    private final Map<AreaType, ChestModel> chest;
    private final SupplyModel supplyChest;
    private final Map<Material, List<Command>> blockCommands;
    private final List<Command> winCommands;

    public BattleModel(String id, String mapId, String bossId, int time, SettingModel setting, MobModel mobModel, Map<AreaType, ChestModel> chest, SupplyModel supplyChest, Map<Material, List<Command>> blockCommands, List<Command> winCommands) {
        this.id = id;
        this.mapId = mapId;
        this.bossId = bossId;
        this.time = time;
        this.setting = setting;
        this.mobModel = mobModel;
        this.chest = chest;
        this.supplyChest = supplyChest;
        this.blockCommands = blockCommands;
        this.winCommands = winCommands;
    }

    public String getId() {
        return id;
    }

    public String getMapId() {
        return mapId;
    }

    public String getBossId() {
        return bossId;
    }

    public int getTime() {
        return time;
    }

    public SettingModel getSetting() {
        return setting;
    }

    public MobModel getMobModel() {
        return mobModel;
    }

    public Map<AreaType, ChestModel> getChests() {
        return chest;
    }

    public SupplyModel getSupplyModel() {
        return supplyChest;
    }

    public Map<Material, List<Command>> getBlockCommands() {
        return blockCommands;
    }

    public List<Command> getWinCommands() {
        return winCommands;
    }

}
