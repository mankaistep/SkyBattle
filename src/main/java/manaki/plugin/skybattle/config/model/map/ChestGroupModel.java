package manaki.plugin.skybattle.config.model.map;

import manaki.plugin.skybattle.util.MinMax;

import java.util.List;

public class ChestGroupModel {

    private final String id;
    private final List<String> locations;
    private final MinMax random;

    public ChestGroupModel(String id, List<String> locations, MinMax random) {
        this.id = id;
        this.locations = locations;
        this.random = random;
    }

    public String getId() {
        return id;
    }

    public List<String> getLocations() {
        return locations;
    }

    public MinMax getRandom() {
        return random;
    }
}
