package manaki.plugin.skybattle.config.model.map;

import manaki.plugin.skybattle.area.AreaType;
import manaki.plugin.skybattle.config.model.BorderModel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapModel {

    private final String id;
    private final String worldName;
    private final String centerLocation;
    private final Map<AreaType, Double> areaRadius;
    private final LinkedHashMap<String, BorderModel> borders;
    private final List<String> supplyLocations;
    private final List<String> teamLocations;
    private final Map<String, ChestGroupModel> chestGroups;

    private final Map<String, LocationModel> locations;

    public MapModel(String id, String worldName, String centerLocation, Map<AreaType, Double> areaRadius, LinkedHashMap<String, BorderModel> borders, List<String> supplyLocations, List<String> teamLocations, Map<String, ChestGroupModel> chestGroups, Map<String, LocationModel> locations) {
        this.id = id;
        this.worldName = worldName;
        this.centerLocation = centerLocation;
        this.areaRadius = areaRadius;
        this.borders = borders;
        this.supplyLocations = supplyLocations;
        this.teamLocations = teamLocations;
        this.chestGroups = chestGroups;
        this.locations = locations;
    }

    public String getId() {
        return id;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getCenterLocation() {
        return centerLocation;
    }

    public Map<AreaType, Double> getAreaRadius() {
        return areaRadius;
    }

    public LinkedHashMap<String, BorderModel> getBorders() {
        return borders;
    }

    public List<String> getSupplyLocations() {
        return supplyLocations;
    }

    public List<String> getTeamLocations() {
        return teamLocations;
    }

    public Map<String, ChestGroupModel> getChestGroups() {
        return chestGroups;
    }

    public Map<String, LocationModel> getLocations() {
        return locations;
    }

    public LocationModel getLocation(String id) {
        return this.locations.getOrDefault(id, null);
    }

}
