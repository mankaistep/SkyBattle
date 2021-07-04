package manaki.plugin.skybattle.config.model.battle;

import manaki.plugin.skybattle.util.MinMax;

import java.util.List;

public class ChestModel {

    private final MinMax random;
    private final List<ChestItemModel> items;

    public ChestModel(MinMax random, List<ChestItemModel> items) {
        this.random = random;
        this.items = items;
    }

    public MinMax getRandom() {
        return random;
    }

    public List<ChestItemModel> getItems() {
        return items;
    }

}
