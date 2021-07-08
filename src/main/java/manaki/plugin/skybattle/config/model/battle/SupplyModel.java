package manaki.plugin.skybattle.config.model.battle;

import manaki.plugin.skybattle.util.MinMax;

import java.util.List;

public class SupplyModel extends ChestModel {

    private List<Integer> appearTime;

    public SupplyModel(List<Integer> appearTime, MinMax random, List<ChestItemModel> items) {
        super(random, items);
        this.appearTime = appearTime;
    }

    public List<Integer> getAppearTime() {
        return appearTime;
    }
}
