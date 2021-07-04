package manaki.plugin.skybattle.config.model.map;

import manaki.plugin.skybattle.area.Area;
import manaki.plugin.skybattle.config.model.BorderModel;

import java.util.Map;

public class MapModel {

    private final String worldName;
    private final String bossLocation;
    private final String centerLocation;
    private final Map<Area, Double> areaRadius;
    private final Map<String, BorderModel> borders;
    private final Map<String, ChestGroupModel> chestGroups;

    public MapModel(String worldName, String bossLocation, String centerLocation, Map<Area, Double> areaRadius, Map<String, BorderModel> borders, Map<String, ChestGroupModel> chestGroups) {
        this.worldName = worldName;
        this.bossLocation = bossLocation;
        this.centerLocation = centerLocation;
        this.areaRadius = areaRadius;
        this.borders = borders;
        this.chestGroups = chestGroups;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getBossLocation() {
        return bossLocation;
    }

    public String getCenterLocation() {
        return centerLocation;
    }

    public Map<Area, Double> getAreaRadius() {
        return areaRadius;
    }

    public Map<String, BorderModel> getBorders() {
        return borders;
    }

    public Map<String, ChestGroupModel> getChestGroups() {
        return chestGroups;
    }

}
