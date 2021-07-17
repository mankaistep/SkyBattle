package manaki.plugin.skybattle.config.model.battle;

import java.util.List;

public class SupplyModel {

    private List<Integer> appearTime;
    private List<ChestGroupItemModel> itemList;

    public SupplyModel(List<Integer> appearTime, List<ChestGroupItemModel> itemList) {
        this.appearTime = appearTime;
        this.itemList = itemList;
    }

    public List<Integer> getAppearTime() {
        return appearTime;
    }

    public List<ChestGroupItemModel> getItemList() {
        return itemList;
    }
}
