package manaki.plugin.skybattle.config.model.battle;

import manaki.plugin.skybattle.area.Area;
import manaki.plugin.skybattle.util.command.Command;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public class BattleModel {

    private final String id;
    private final String mapId;
    private final int time;
    private final SettingModel setting;
    private final Map<Area, ChestModel> chest;
    private final ChestModel supplyChest;
    private final Map<Material, List<Command>> blockCommands;
    private final List<Command> winCommands;
    private final List<Command> loseCommands;

    public BattleModel(String id, String mapId, int time, SettingModel setting, Map<Area, ChestModel> chest, ChestModel supplyChest, Map<Material, List<Command>> blockCommands, List<Command> winCommands, List<Command> loseCommands) {
        this.id = id;
        this.mapId = mapId;
        this.time = time;
        this.setting = setting;
        this.chest = chest;
        this.supplyChest = supplyChest;
        this.blockCommands = blockCommands;
        this.winCommands = winCommands;
        this.loseCommands = loseCommands;
    }

    public String getId() {
        return id;
    }

    public String getMapId() {
        return mapId;
    }

    public int getTime() {
        return time;
    }

    public SettingModel getSetting() {
        return setting;
    }

    public Map<Area, ChestModel> getChests() {
        return chest;
    }

    public ChestModel getSupplyChest() {
        return supplyChest;
    }

    public Map<Material, List<Command>> getBlockCommands() {
        return blockCommands;
    }

    public List<Command> getWinCommands() {
        return winCommands;
    }

    public List<Command> getLoseCommands() {
        return loseCommands;
    }
}
